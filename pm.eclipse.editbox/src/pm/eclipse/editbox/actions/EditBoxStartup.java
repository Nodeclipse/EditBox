package pm.eclipse.editbox.actions;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

import pm.eclipse.editbox.EditBox;

public class EditBoxStartup implements IStartup {

	@Override
	public void earlyStartup() {
		if (!EditBox.getDefault().isEnabled())
			return;

		EditBox.getDefault().setEnabled(false);

		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {

			public void run() {
				ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
				Command command = commandService.getCommand(EnableEditBox.COMMAND_ID);
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window != null) {
					IHandlerService handlerService = (IHandlerService) window.getWorkbench().getService(IHandlerService.class);
					if (handlerService != null)
						try {
							handlerService.executeCommand(new ParameterizedCommand(command, null), null);
							EditBox.toggleToolBarItem(window, true);
							//EditBox.toggleToolBarItemInAllWindows(enabled); //no need as there is to be only one window
						} catch (Exception e) {
							EditBox.logError(this, "Failed to enable EditBox at startup", e);
						}
				}
			}

		});
	}

}
