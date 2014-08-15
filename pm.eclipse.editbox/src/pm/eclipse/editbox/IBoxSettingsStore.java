package pm.eclipse.editbox;

import java.util.Set;


/**
 * How this provider is saved into several lines of Eclipse preferences
 * see BoxSettingsStoreImpl
 */
public interface IBoxSettingsStore {

	void setProviderId(String id);

	void loadDefaults(IBoxSettings editorsSettings);
	void load(String name, IBoxSettings editorsSettings);

	void saveDefaults(IBoxSettings settings);

	public Set<String> getCatalog();

	void remove(String name);
}
