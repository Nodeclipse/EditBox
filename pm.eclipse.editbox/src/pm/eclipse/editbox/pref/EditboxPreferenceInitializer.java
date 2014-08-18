package pm.eclipse.editbox.pref;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import pm.eclipse.editbox.EditBox;
import pm.eclipse.editbox.impl.BoxProviderRegistry;

public class EditboxPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = EditBox.getDefault().getPreferenceStore();
		store.setDefault(EditBox.PREF_ENABLED, true);
		store.setDefault(EditBox.PREF_DEFAULT_THEME, BoxProviderRegistry.DEFAULT_THEME);
		//TODO move defaults from BoxProviderRegistry into this EditboxPreferenceInitializer
	}	
}
