package pm.eclipse.editbox.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;

import pm.eclipse.editbox.EditBox;
import pm.eclipse.editbox.IBoxDecorator;
import pm.eclipse.editbox.IBoxProvider;

public class BoxProviderRegistry {

	private static final String PROIVDERS = "proivders";
	private static final String PROVIDER_ID_ = "pm.eclipse.editbox.provider.";

	protected Collection<IBoxProvider> providers;
	protected Map<IWorkbenchPart, IBoxDecorator> decorators;
	protected Map<IPartService, IPartListener2> partListeners;

	public Collection<IBoxProvider> getBoxProviders() {
		if (providers == null)
			providers = loadProviders();
		if (providers == null)
			providers = defaultProviders();
		return providers;
	}

	protected Collection<IBoxProvider> loadProviders() {
		List<IBoxProvider> result = null;
		String pSetting = EditBox.getDefault().getPreferenceStore().getString(PROIVDERS);
		if (pSetting != null && pSetting.length() > 0) {
			String[] split = pSetting.split(",");
			if (split.length > 0)
				result = new ArrayList<IBoxProvider>();
			for (String s : split)
				if (s.trim().length() > 0)
					result.add(createProvider(s.trim()));
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
			EditBox.getDefault().getPreferenceStore().setValue(PROIVDERS,sb.toString());
		}
	}
	
	protected Collection<IBoxProvider> defaultProviders() {
		List<IBoxProvider> result = new ArrayList<IBoxProvider>();
		// order important (see supports())
		result.add(javaProvider());
		result.add(pythonProvider());
		result.add(markupProvider());
		result.add(textProvider());
		return result;
	}

	protected BoxProviderImpl createProvider(String name) {
		BoxProviderImpl provider = new BoxProviderImpl();
		provider.setId(PROVIDER_ID_ + name);
		provider.setName(name);
		provider.setBuilders(defaultBuilders());
		provider.setDefaultSettingsCatalog(Arrays.asList("Default"));
		return provider;
	}

	protected BoxProviderImpl markupProvider() {
		BoxProviderImpl provider = createProvider("markup");
		if (provider.getEditorsBoxSettings().getFileNames() == null)
			provider.getEditorsBoxSettings().setFileNames(Arrays.asList("*.*ml", "*.jsp"));
		return provider;
	}

	protected BoxProviderImpl javaProvider() {
		BoxProviderImpl provider = createProvider("java");
		provider.setDefaultSettingsCatalog(Arrays.asList("Default", "OnClick", "GreyGradient", "Java_v_20"));
		if (provider.getEditorsBoxSettings().getFileNames() == null)
			provider.getEditorsBoxSettings().setFileNames(Arrays.asList("*.java", "*.class"));
		return provider;
	}

	protected BoxProviderImpl pythonProvider() {
		BoxProviderImpl provider = createProvider("python");
		provider.setDefaultSettingsCatalog(Arrays.asList("Default", "Whitebox"));
		if (provider.getEditorsBoxSettings().getFileNames() == null)
			provider.getEditorsBoxSettings().setFileNames(Arrays.asList("*.py"));
		return provider;
	}
	
	protected BoxProviderImpl textProvider() {
		BoxProviderImpl provider = createProvider("text");
		provider.setDefaultSettingsCatalog(Arrays.asList("Default", "Whitebox"));
		if (provider.getEditorsBoxSettings().getFileNames() == null)
			provider.getEditorsBoxSettings().setFileNames(Arrays.asList("*.txt", "*.*"));
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
