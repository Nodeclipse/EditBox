package pm.eclipse.editbox.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;
//import org.eclipse.ui.texteditor.AbstractTextEditor;

import pm.eclipse.editbox.EditBox;
import pm.eclipse.editbox.IBoxBuilder;
import pm.eclipse.editbox.IBoxDecorator;
import pm.eclipse.editbox.IBoxProvider;
import pm.eclipse.editbox.IBoxSettings;

/**
 * Provider
 */
public class BoxProviderImpl implements IBoxProvider {

	protected String id;
	protected String name;
	protected IBoxSettings theme;
	protected BoxSettingsStoreImpl providerStore;
	protected Map<String,Class> builders;
	protected Collection<String> defaultSettingsCatalog;
	private ArrayList<Matcher> matchers;

	public BoxSettingsStoreImpl getSettingsStore() {
		if (providerStore == null) {
			providerStore = createSettingsStore();
			providerStore.setProviderId(id);
		}
		return providerStore;
	}

	public IBoxSettings getEditorsBoxSettings() {
		if (theme == null) {
			theme = createEmptySettings();
			getSettingsStore().loadDefaults(theme);
			theme.addPropertyChangeListener(new IPropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent event) {
					String p = event.getProperty();
					if (p!=null && (p.equals(IBoxSettings.PropertiesKeys.FileNames.name()) || 
							        p.equals(IBoxSettings.PropertiesKeys.ALL.name())))
						matchers = null;
				}});
		}
		return theme;
	}

	public IBoxDecorator decorate(IWorkbenchPart editorPart) {
		StyledText st = getStyledText(editorPart);
		if (st == null)
			return null;
		IBoxSettings settings = getEditorsBoxSettings();
		if (!settings.getEnabled())
			return null;
		IBoxDecorator result = createDecorator();
		result.setStyledText(st);
		result.setSettings(settings);
		result.decorate(false);
		return result;
	}

	protected StyledText getStyledText(final IWorkbenchPart editorPart) {
		if (editorPart != null) {
			Object obj = editorPart.getAdapter(Control.class);
			if (obj instanceof StyledText)
				return (StyledText) obj;
		}

		return null;
	}

	public boolean supports(IWorkbenchPart editorPart) {
		return editorPart.getAdapter(Control.class) instanceof StyledText &&
			   (supportsFile(editorPart.getTitle()) || supportsFile(editorPart.getTitleToolTip()));
	}

	protected boolean supportsFile(String fileName) {
		if (fileName != null)
			for (Matcher matcher : getMatchers()) {
				if (matcher.matches(fileName))
					return true;
			}
		return false;
	}

	protected Collection<Matcher> getMatchers() {
		if (matchers == null) {
			matchers = new ArrayList<Matcher>();
			Collection<String> fileNames = getEditorsBoxSettings().getFileNames();
			if (fileNames != null)
				for (String pattern : fileNames)
					matchers.add(new Matcher(pattern));
		}
		return matchers;
	}

	public void releaseDecorator(IBoxDecorator decorator) {
		decorator.undecorate();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}


	public void setId(String newId) {
		id = newId;
	}

	public void setName(String newName) {
		name = newName;
	}


	protected BoxSettingsStoreImpl createSettingsStore() {
		BoxSettingsStoreImpl pstore = new BoxSettingsStoreImpl();
		pstore.setDefaultSettingsCatalog(defaultSettingsCatalog);
		return pstore;
	}
	
	public void setDefaultSettingsCatalog(Collection<String> cat){
		defaultSettingsCatalog = cat;
	}
	
	public IBoxSettings createSettings() {
		BoxSettingsImpl theme = createEmptySettings();
		theme.copyFrom(getEditorsBoxSettings());
		return theme;
	}

	protected BoxSettingsImpl createEmptySettings() {
		return new BoxSettingsImpl();
	}

	public IBoxDecorator createDecorator() {
		BoxDecoratorImpl decorator = new BoxDecoratorImpl();
		decorator.setProvider(this);
		return decorator;
	}

	public Collection<String> getBuilders() {
		return builders!=null?builders.keySet():null;
	}

	public void setBuilders(Map<String,Class> newBuilders){
		builders = newBuilders;
	}
	
	public IBoxBuilder createBoxBuilder(String name){
		Class c = null;
		if (name != null && builders != null)
			c = builders.get(name);
		if (c == null)
			return new BoxBuilderImpl();
		try {
			return (IBoxBuilder) c.newInstance();
		} catch (Exception e) {
			EditBox.logError(this, "Cannot create box builder: "+name, e);
		}
		return null;
	}

	class Matcher {

		org.eclipse.ui.internal.misc.StringMatcher m;
		Matcher(String pattern) {
			m = new org.eclipse.ui.internal.misc.StringMatcher(pattern, true, false);
		}
		boolean matches(String text) {
			return m.match(text);
		}
	}
}
