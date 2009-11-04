package pm.eclipse.editbox.actions;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

import pm.eclipse.editbox.EditBox;
import pm.eclipse.editbox.IBoxDecorator;
import pm.eclipse.editbox.IBoxProvider;
import pm.eclipse.editbox.impl.BoxProviderRegistry;

public class EnableEditBox extends AbstractHandler implements IElementUpdater{

	public static final String COMMAND_ID = "pm.eclipse.editbox.actions.EnableEditBoxCmd";
	
	private IWorkbenchWindow win;
	private BoxDecoratorPartListener listener;
	private BoxProviderRegistry registry;
	private boolean checked;
	
	public Object execute(ExecutionEvent ee) throws ExecutionException {
		win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (win == null)
			return null;

		Command command = ee.getCommand();
		checked  = !EditBox.getDefault().isEnabled();
		runCommand(checked);
		((ICommandService)win.getWorkbench().getService(ICommandService.class)).refreshElements(command.getId(), null);
		return null;
	}

	private void runCommand(boolean isChecked) {
		if (!isChecked)
			releaseDecorators();
		else {
			listener = new BoxDecoratorPartListener();
			win.getPartService().addPartListener(listener);
			IWorkbenchPart part = win.getActivePage().getActiveEditor();
			if (part != null)
				setVisible(part, true);
		}
		EditBox.getDefault().setEnabled(isChecked);
	}

	public void dispose() {
		releaseDecorators();
	}

	private void releaseDecorators() {
		if (listener != null && win != null) {
			IPartService partService = win.getPartService();
			if (partService != null)
				partService.removePartListener(listener);
			listener = null;
		}
		EditBox.getDefault().getProviderRegistry().releaseDecorators();
	}

	public void updateElement(UIElement element, Map parameters) {
		element.setChecked(checked);
	}

	protected void setVisible(IWorkbenchPartReference partRef, boolean visible) {
		IWorkbenchPart part = partRef.getPart(false);
		if (part != null) 
			setVisible(part, visible);
	}

	protected void setVisible(IWorkbenchPart part, boolean visible) {
		IBoxDecorator decorator = getRegistry().getDecorator(part);
		if (decorator == null && !visible)
			return;
		if (decorator == null) 
			decorator = decorate(part);
		if (decorator != null)
			decorator.enableUpdates(visible);
	}

	protected IBoxDecorator decorate(IWorkbenchPart part) {
		IBoxDecorator result = null;
		IBoxProvider provider = getRegistry().getBoxProvider(part);
		if (provider != null)
			result = provider.decorate(part);
		if (result != null)
			getRegistry().addDecorator(result, part);
		return result;
	}

	protected void undecorate(IWorkbenchPartReference partRef) {
		IWorkbenchPart part = partRef.getPart(false);
		if (part != null) {
			IBoxDecorator decorator = getRegistry().removeDecorator(part);
			if (decorator != null) {
				decorator.getProvider().releaseDecorator(decorator);
			}
		}
	}

	protected BoxProviderRegistry getRegistry() {
		if (registry == null)
			registry = EditBox.getDefault().getProviderRegistry();
		return registry;
	}

	class BoxDecoratorPartListener implements IPartListener2 {

		public void partActivated(IWorkbenchPartReference partRef) {
			setVisible(partRef, true);
		}

		public void partBroughtToTop(IWorkbenchPartReference partRef) {
			setVisible(partRef, true);
		}

		public void partClosed(IWorkbenchPartReference partRef) {
			undecorate(partRef);
		}

		public void partDeactivated(IWorkbenchPartReference partRef) {
		}

		public void partHidden(IWorkbenchPartReference partRef) {
			setVisible(partRef, false);
		}

		public void partInputChanged(IWorkbenchPartReference partRef) {
			setVisible(partRef, false);
			setVisible(partRef, true);
		}

		public void partOpened(IWorkbenchPartReference partRef) {
		}

		public void partVisible(IWorkbenchPartReference partRef) {
			setVisible(partRef, true);
		}
	}

}
