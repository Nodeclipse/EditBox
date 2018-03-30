package pm.eclipse.editbox.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.jface.preference.IPreferenceStore;

import pm.eclipse.editbox.EditBox;
import pm.eclipse.editbox.IBoxSettings;
import pm.eclipse.editbox.IBoxSettingsStore;


/** ProviderStore
 * Saving to pm.eclipse.editbox.prefs just ONE provider:
 * gives mapping to serialize to Eclipse IPreferenceStore
<pre>
pm.eclipse.editbox.provider.java_Default=\#COMMENT\r\n\#Mon Jun 30 17\:24\:47 CST 2014\r\nHighlightOne\=false\r\nFillGradient\=false\r\nFillSelected\=true\r\nRoundBox\=true\r\nBorderColorType\=1\r\nName\=Default\r\nExpandBox\=false\r\nBorderDrawLine\=false\r\nFillOnMove\=true\r\nAlpha\=0\r\nHighlightWidth\=1\r\nBorderWidth\=1\r\nHighlightColor\=acb3b7\r\nBorderColor\=c0c0c0\r\nFillKeyModifier\=Alt\r\nHighlightColorType\=3\r\nFillGradientColor\=dadcc2\r\nBuilder\=Java\r\nHighlightDrawLine\=false\r\nFillSelectedColor\=ffffff\r\nBorderLineStyle\=2\r\nColors\=ffffff-c5d0ac-d9e3b7-e8ecd9\r\nHighlightLineStyle\=0\r\nNoBackground\=false\r\nCirculateLevelColors\=false\r\n
pm.eclipse.editbox.provider.java_RainbowDrops=\#COMMENT\r\n\#Wed Jul 09 17\:06\:50 CST 2014\r\nHighlightOne\=true\r\nFillGradient\=false\r\nFillSelected\=false\r\nRoundBox\=false\r\nBorderColorType\=0\r\nName\=RainbowDrops\r\nExpandBox\=false\r\nBorderDrawLine\=true\r\nFillOnMove\=false\r\nAlpha\=0\r\nHighlightWidth\=1\r\nBorderWidth\=1\r\nHighlightColor\=00ff00\r\nBorderColor\=00bbbb\r\nFillKeyModifier\=Alt\r\nHighlightColorType\=0\r\nFillGradientColor\=null\r\nBuilder\=Java\r\nHighlightDrawLine\=true\r\nFillSelectedColor\=202020\r\nBorderLineStyle\=1\r\nColors\=202020-null\r\nHighlightLineStyle\=0\r\nNoBackground\=false\r\nCirculateLevelColors\=false\r\n
pm.eclipse.editbox.provider.java_catalog=Default,Whitebox,OnClick,GreyGradient,Java_v_20,RainbowDropsLine,RainbowDropsLineFill,BlueToDeepBlue,OrangeToRed,BlueGradient22WithDarkBoldLeftBorder,BlueLight,Java_PaleBlue,RainbowDrops
pm.eclipse.editbox.provider.java_default=RainbowDrops
pm.eclipse.editbox.provider.java_enabled=true
pm.eclipse.editbox.provider.java_fileNames=*.java,*.class,*.gradle,*.groovy,*.scala
pm.eclipse.editbox.provider.js_Default=\#COMMENT\r\n\#Fri Jul 04 11\:28\:13 CST 2014\r\nHighlightOne\=true\r\nFillGradient\=false\r\nFillSelected\=true\r\nRoundBox\=true\r\nBorderColorType\=0\r\nName\=Default\r\nExpandBox\=false\r\nBorderDrawLine\=false\r\nFillOnMove\=false\r\nAlpha\=0\r\nHighlightWidth\=1\r\nBorderWidth\=1\r\nHighlightColor\=000000\r\nBorderColor\=00bbbb\r\nFillKeyModifier\=Alt\r\nHighlightColorType\=0\r\nFillGradientColor\=null\r\nBuilder\=Text\r\nHighlightDrawLine\=false\r\nFillSelectedColor\=ffffc4\r\nBorderLineStyle\=0\r\nColors\=null-d0dd9b-cdd8b9-e9f58b\r\nHighlightLineStyle\=0\r\nNoBackground\=false\r\nCirculateLevelColors\=false\r\n
pm.eclipse.editbox.provider.js_RainbowDropsLineFill=\#COMMENT\r\n\#Wed Jul 09 17\:06\:50 CST 2014\r\nHighlightOne\=true\r\nFillGradient\=false\r\nFillSelected\=true\r\nRoundBox\=false\r\nBorderColorType\=0\r\nName\=RainbowDropsLineFill\r\nExpandBox\=false\r\nBorderDrawLine\=true\r\nFillOnMove\=true\r\nAlpha\=0\r\nHighlightWidth\=1\r\nBorderWidth\=1\r\nHighlightColor\=00ff00\r\nBorderColor\=00bbbb\r\nFillKeyModifier\=\r\nHighlightColorType\=0\r\nFillGradientColor\=null\r\nBuilder\=Java\r\nHighlightDrawLine\=true\r\nFillSelectedColor\=000080\r\nBorderLineStyle\=1\r\nColors\=202020-null\r\nHighlightLineStyle\=1\r\nNoBackground\=false\r\nCirculateLevelColors\=false\r\n
pm.eclipse.editbox.provider.js_default=RainbowDropsLineFill
pm.eclipse.editbox.provider.js_enabled=true
pm.eclipse.editbox.provider.js_fileNames=*.js,*.jjs,*.jshintrc,*.mjs,*.njs,*.pjs,*.vjs,*.ts,*.coffee,*.dart
</pre>
 * where java or js is providerId
 * keys starting with Capital are actually themes names
 */
public class BoxSettingsStoreImpl implements IBoxSettingsStore {

	private static final String FILE_NAMES = "fileNames";
	private static final String TXT_POSTFIX = "$txt";
	private static final String DEFAULT = "default"; //what theme to use as default
	private static final String ENABLED = "enabled";
	private static final String CATALOG = "catalog";
	protected String providerId;
	protected IPreferenceStore store = EditBox.getDefault().getPreferenceStore();
	private Set<String> catalog;
	private Collection<String> defaultCatalog;

//	protected IPreferenceStore store{
//		if (store == null)
//			store = EditBox.getDefault().getPreferenceStore();
//		return store;
//	}
	
	protected String providerKey(String postfix){
		return providerId+"_"+postfix;
	}
	
	@Override
	public void setProviderId(String id) {
		this.providerId = id;
	}
	
	@Override
	public void loadDefaults(IBoxSettings editorsSettings) {
		String defaultThemeName = store.getString(providerKey(DEFAULT));
		if (isEmpty(defaultThemeName)){
			defaultThemeName = providerId;
		} 
		load(defaultThemeName, editorsSettings);
	}

	@Override
	public void load(String themeName, IBoxSettings editorsSettings) {
		String themeInsideString = store.getString(providerKey(themeName));
		if (!isEmpty(themeInsideString))
			editorsSettings.load(themeInsideString);
		else
			try {
				editorsSettings.load(getClass().getResourceAsStream("/"+themeName + ".eb"));
			} catch (Exception e) {
				EditBox.logError(this, "Error loading settings: "+themeName, e);
			}
		editorsSettings.setEnabled(getIsEnabled()); //XXX smell: is Enabled for Provider, not theme!
		editorsSettings.setFileNames(getFileNames());
	}

	protected static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

	protected boolean getIsEnabled() {
		String key = providerKey(ENABLED);
		if (store.contains(key))
			return store.getBoolean(key);
		return true;
	}

	@Override
	public void saveDefaults(IBoxSettings settings) {
		store.setValue(providerKey(ENABLED), settings.getEnabled()?"true":"false");
		store.setValue(providerKey(DEFAULT), settings.getName());
		store(settings);
	}

	public void store(IBoxSettings settings) {
		String name = settings.getName();
		store.setValue(providerKey(name),settings.export());
		setFileNames(settings.getFileNames());
		addToCatalog(name);
		EditBox.getDefault().savePluginPreferences();
	}

	protected void addToCatalog(String name) {
		Set<String> cat = getCatalog();
		if (!cat.contains(name)){
			cat.add(name);
			storeCatalog(cat);
		}
	}

	private void storeCatalog(Set<String> cat) {
		StringBuilder sb = new StringBuilder();
		for(String c: cat){
			if (sb.length()>0) sb.append(",");
			sb.append(c);
		}
		store.setValue(providerKey(CATALOG), sb.toString());
	}

	@Override
	public Set<String> getCatalog() {
		if (catalog == null){
			catalog = new LinkedHashSet<String>();
			String cstr = store.getString(providerKey(CATALOG));
			if (!isEmpty(cstr))
				for (String s : cstr.split(",")) 
					catalog.add(s);
				
		}
		if (defaultCatalog != null && catalog !=null)
			catalog.addAll(defaultCatalog);
		return catalog;
	}
	
	public void setDefaultSettingsCatalog(Collection<String> cat){
		defaultCatalog = cat;
	}
	
	@Override
	public void remove(String name) {
		if (getCatalog().remove(name))
			storeCatalog(getCatalog());
		store.setValue(providerKey(name), "");
		store.setValue(providerKey(name+TXT_POSTFIX), "");
		EditBox.getDefault().savePluginPreferences();
	}

	protected void setFileNames(Collection<String> fileNames) {
		StringBuilder sb = new StringBuilder();
		if (fileNames != null) {
			boolean first = true;
			for (String s : fileNames) {
				if (!first)
					sb.append(",");
				sb.append(s);
				first = false;
			}
		}
		store.setValue(providerKey(FILE_NAMES), sb.toString());
	}

	/*
	 * @return null if settings never stored before
	 */
	protected Collection<String> getFileNames() {
		String fileNames = store.getString(providerKey(FILE_NAMES));
		if (fileNames == null || fileNames.equals("")) {
			return null;
		}		
		List<String> list = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(fileNames, ",");
		while (st.hasMoreTokens()) {
			String t = st.nextToken().trim();
			if (t.length() > 0)
				list.add(t);
		}

		return list;
	}

}
