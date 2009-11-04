package pm.eclipse.editbox;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import pm.eclipse.editbox.impl.BoxProviderRegistry;

public class EditBox extends AbstractUIPlugin {

	private static final String ENABLED = "ENABLED";

	public static final String PLUGIN_ID = "pm.eclipse.editbox";

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

	public boolean isEnabled() {
		if (getPreferenceStore().contains(ENABLED))
			return getPreferenceStore().getBoolean(ENABLED);
		return false;
	}
	
	public void setEnabled(boolean flag){
		getPreferenceStore().setValue(ENABLED, flag);
	}
	
	public static void logError(Object source, String msg, Throwable error) {
		String src = "";
		if (source instanceof Class)
			src = ((Class) source).getName();
		else if (source != null)
			src = source.getClass().getName();
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, "[" + src + "] " + msg, error));
	}

}
