package pm.eclipse.editbox;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import pm.eclipse.editbox.impl.BoxProviderRegistry;

public class EditBox extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "pm.eclipse.editbox";
	public static final String PREF_ENABLED = "enabled";
	public static final String PREF_DEFAULT_THEME = "default_theme";

	private static EditBox plugin;

	private BoxProviderRegistry registry;
	
	public EditBox() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static EditBox getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public BoxProviderRegistry getProviderRegistry(){
		return registry == null ? (registry = new BoxProviderRegistry()) : registry;
	}

	/** check if enabled from PreferenceStore */
	public boolean isEnabled() {
		//if (getPreferenceStore().contains(ENABLED))
			return getPreferenceStore().getBoolean(PREF_ENABLED);
		//return false;
	}
	
	public void setEnabled(boolean flag){
		getPreferenceStore().setValue(PREF_ENABLED, flag);
	}
	
	//+ {
	//TODO move to utils class?
	/** called from EditboxPreferencePage 
	 * TODO still can't toggle on main window from Preferences
	 * */
	public static void toggleToolBarItemInAllWindows(boolean enabled) {
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (IWorkbenchWindow window: windows){
			toggleToolBarItem(window, enabled);			
		}
	}
	/** called from EditBoxStartup */
	public static void toggleToolBarItem(IWorkbenchWindow window, boolean enabled) {
		// any better way to toggle toolbar button in 3.2?
		if (window instanceof ApplicationWindow) {
			CoolBarManager coolBarManager = ((ApplicationWindow) window).getCoolBarManager();
			if (coolBarManager != null) {
				IContributionItem item = coolBarManager.find("pm.eclipse.editbox.ActionSetId");
				if (item instanceof ToolBarContributionItem) {
					IToolBarManager tbMgr2 = ((ToolBarContributionItem) item).getToolBarManager();
					if (tbMgr2 != null) {
						IContributionItem item2 = tbMgr2.find("pm.eclipse.editbox.EnableEditboxActionId");
						if (item2 instanceof ActionContributionItem) {
							((ActionContributionItem) item2).getAction().setChecked(enabled);
						}
					}
				}
			}
		}
	}
	//}
	
	
	public static void logError(Object source, String msg, Throwable error) {
		String src = "";
		if (source instanceof Class)
			src = ((Class) source).getName();
		else if (source != null)
			src = source.getClass().getName();
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, 1, "[" + src + "] " + msg, error));
	}

}
