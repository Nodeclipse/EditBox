package pm.eclipse.editbox.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;

import pm.eclipse.editbox.EditBox;
import pm.eclipse.editbox.IBoxDecorator;
import pm.eclipse.editbox.IBoxProvider;
import pm.eclipse.editbox.IBoxSettings;

/**
 * Registry keeps Providers list and initialize them or restores from preferences
 * @author Piotr Metel
 * @author Paul Verest : added "RainbowDrops" and ALL_THEMES_LIST to be used in every category
 */
public class BoxProviderRegistry {

	//used to get Preferences, if this misspelled value is changed, user preferences are gone after update
	private static final String PROVIDERS = "proivders";  
	// always added. Is it useless?
	private static final String PROVIDER_ID_ = "pm.eclipse.editbox.provider.";
	//+ {
	public static final String[] ALL_THEMES_ARRAY = {"Default", "Whitebox", "OnClick", "GreyGradient", "Java_v_20",
		"RainbowDrops", "RainbowDropsLine", "RainbowDropsLineFill", 
		"BlueToDeepBlue", "OrangeToRed", "BlueGradient22WithDarkBoldLeftBorder", "BlueLight", "PaleBlue"}; 
	private static final List<String> ALL_THEMES_LIST = Arrays.asList(ALL_THEMES_ARRAY);
	public static final String DEFAULT_THEME = "PaleBlue";
	
	/** return index within ALL_THEMES_ARRAY or -1 if not found */
	public static int getThemeIndex(String preferedThemeName) {
		for (int i = 0; i<ALL_THEMES_ARRAY.length; i++){
			if (ALL_THEMES_ARRAY[i].equalsIgnoreCase(preferedThemeName)){
				return i;
			}
		}
		return -1; 
	}
	//}
	
	//{ like in BoxSettingsStoreImpl
	protected IPreferenceStore store = EditBox.getDefault().getPreferenceStore();
//	protected IPreferenceStore store{
//		if (store == null)
//			store = EditBox.getDefault().getPreferenceStore();
//		return store;
//	}
	//}
	
	protected Collection<IBoxProvider> providers;
	protected Map<IWorkbenchPart, IBoxDecorator> decorators;
	protected Map<IPartService, IPartListener2> partListeners;

	// 
	public Collection<IBoxProvider> getBoxProviders() {
		if (providers == null){
			providers = loadProvidersFromPreferences();
		}	
		if (providers == null){
			providers = defaultProviders();
		}
		return providers;
	}

	// Preferences have string like
	// proivders=java,python,markup,text,js
	// calls createProvider(name)
	protected Collection<IBoxProvider> loadProvidersFromPreferences() {
		List<IBoxProvider> result = null;
		String pSetting = store.getString(PROVIDERS);
		if (pSetting != null && pSetting.length() > 0) {
			String[] split = pSetting.split(",");
			if (split.length > 0)
				result = new ArrayList<IBoxProvider>();
			for (String s : split){
				String name = s.trim();
				if (name.length() > 0)
					result.add(createProvider(name));
			}	
		}
		return result;
	}

	public void setProvideres(Collection<IBoxProvider> newProviders){
		providers = newProviders;
	}
	
	public void storeProviders(){
		if (providers!=null){
			StringBuilder sb = new StringBuilder();
			for(IBoxProvider p : providers) {
				if (sb.length()!=0) sb.append(",");
				sb.append(p.getName());
			}
			store.setValue(PROVIDERS,sb.toString());
		}
	}
	
	//{ defaults :
	protected Collection<IBoxProvider> defaultProviders() {
		List<IBoxProvider> result = new ArrayList<IBoxProvider>();
		// order important (see BoxProviderImpl.supports())
		// refactored to use this more generic method
		result.add(createProviderForNameAndExtentions("c++",	Arrays.asList("*.c", "*.cpp", "*.h", "*.hpp", "*.go") ) ); 
		result.add(createProviderForNameAndExtentions("java",	Arrays.asList("*.java", "*.class", "*.gradle", "*.groovy", "*.scala") ) );
		result.add(createProviderForNameAndExtentions("js",		Arrays.asList("*.js", "*.jjs", "*.jshintrc", "*.mjs", "*.njs", "*.pjs", "*.vjs", "*.ts", "*.coffee", "*.dart") ) );
		result.add(createProviderForNameAndExtentions("lua",	Arrays.asList("*.lua") ) );
		result.add(createProviderForNameAndExtentions("markup", Arrays.asList("*.*ml", "*.jsp", "*.html", "*.hjs", "*.jade", "*.css", "*.less") ) );
		result.add(createProviderForNameAndExtentions("php",	Arrays.asList("*.php") ) );
		result.add(createProviderForNameAndExtentions("python", Arrays.asList("*.py") ) );
		result.add(createProviderForNameAndExtentions("ruby",	Arrays.asList("*.rb", "*.ruby") ) );
		result.add(createProviderForNameAndExtentions("text",	Arrays.asList("*.txt", "*.") ) );
		result.add(createProviderForNameAndExtentions("xml",	Arrays.asList("*.xml", "*.launch", "*.project", "*.classpath") ) );
		result.add(createProviderForNameAndExtentionsDisabled("exclude", Arrays.asList("*.ascii") ) );
		result.add(createProviderForNameAndExtentions("others",	Arrays.asList("*.*") ) ); // "*.*" makes default to every file
		return result;
	}

	/**
	 * is used in createProviderForNameAndExtentions(), providerForName() 
	 * and loadProviders() to load from Preferences 
	 */
	protected BoxProviderImpl createProvider(String name) {
		BoxProviderImpl provider = new BoxProviderImpl();
		provider.setId(PROVIDER_ID_ + name);
		provider.setName(name);
		provider.setBuilders(defaultBuilders());
		provider.setDefaultSettingsCatalog(ALL_THEMES_LIST); // was Arrays.asList("Default")
		return provider;
	}

	// refactored to use this more generic method
	protected BoxProviderImpl createProviderForNameAndExtentions(String name, List<String> extentions) {
		BoxProviderImpl provider = createProvider(name);
		provider.setDefaultSettingsCatalog(ALL_THEMES_LIST);
		if (provider.getEditorsBoxSettings().getFileNames() == null)
			provider.getEditorsBoxSettings().setFileNames(extentions);
		return provider;
	}
	protected BoxProviderImpl createProviderForNameAndExtentionsDisabled(String name, List<String> extentions) {
		BoxProviderImpl provider = createProviderForNameAndExtentions(name, extentions);
		// no effect :-(
		//		IBoxSettings theme = provider.getEditorsBoxSettings();
		//		theme.setEnabled(false); //this fires PropertyChangeListener
		return provider;		
	}

	protected Map<String, Class> defaultBuilders() {
		Map<String, Class> result = new HashMap<String, Class>();
		result.put("Text", BoxBuilderImpl.class);
		result.put("Java", JavaBoxBuilder.class);
		result.put("Markup", MarkupBuilder2.class);
		result.put("Text2", TextBoxBuilder.class);
		return result;
	}

	public IBoxDecorator getDecorator(IWorkbenchPart part) {
		return getDecorators().get(part);
	}

	protected Map<IWorkbenchPart, IBoxDecorator> getDecorators() {
		if (decorators == null)
			decorators = new HashMap<IWorkbenchPart, IBoxDecorator>();
		return decorators;
	}

	public IBoxDecorator removeDecorator(IWorkbenchPart part) {
		return getDecorators().remove(part);
	}

	public void addDecorator(IBoxDecorator decorator, IWorkbenchPart part) {
		getDecorators().put(part, decorator);
	}

	public void releaseDecorators() {
		if (decorators != null) {
			for (Map.Entry<IWorkbenchPart, IBoxDecorator> e : decorators.entrySet())
				e.getValue().getProvider().releaseDecorator(e.getValue());
			decorators.clear();
		}
	}

	public IBoxProvider getBoxProvider(IWorkbenchPart part) {
		for (IBoxProvider p : getBoxProviders())
			if (p.supports(part))
				return p;
		return null;
	}

	public IBoxProvider providerForName(String name) {
		Collection<IBoxProvider> providers = getBoxProviders();
		for (IBoxProvider provider : providers) {
			if (provider.getName().equals(name))
				return provider;
		}
		IBoxProvider provider = createProvider(name);
		providers.add(provider);
		return provider;
	}

	public void removeProvider(String name) {
		for (Iterator<IBoxProvider> it = getBoxProviders().iterator(); it.hasNext();) {
			if (it.next().getName().equals(name))
				it.remove();
		}
	}

	public void setPartListener(IPartService partService, IPartListener2 listener) {
		if (partService == null)
			return;
		if (partListeners == null)
			partListeners = new HashMap<IPartService, IPartListener2>();
		IPartListener2 oldListener = partListeners.get(partService);
		if (oldListener != null)
			partService.removePartListener(oldListener);
		partService.addPartListener(listener);
		partListeners.put(partService, listener);
	}

	public void removePartListener(IPartService partService) {
		if (partService == null || partListeners == null)
			return;
		IPartListener2 oldListener = partListeners.remove(partService);
		if (oldListener != null)
			partService.removePartListener(oldListener);
	}

}
