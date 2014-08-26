package pm.eclipse.editbox.pref;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import pm.eclipse.editbox.EditBox;
import pm.eclipse.editbox.IBoxProvider;
import pm.eclipse.editbox.actions.EnableEditBox;
import pm.eclipse.editbox.impl.BoxProviderRegistry;


/**
 * @author Piotr Metel
 * @author Paul Verest
 */
public class EditboxPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Button pluginEnabled;
	private Combo themeCombo;
	private List categoryList;
	private TabFolder tabFolder;
	private Map<String, LinkedHashSet<String>> categoryFiles;
	private List namesList;
	private Button bAddFile;
	private boolean providersChanged;
	private IPreferenceStore store = EditBox.getDefault().getPreferenceStore();
	private BoxProviderRegistry providerRegistry = EditBox.getDefault().getProviderRegistry();
	
	public EditboxPreferencePage(){
		super("EditBox (Nodeclipse)", EditBox.getImageDescriptor("icons/editbox.png"));
	}
	
	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		noDefaultAndApplyButton();

		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(1, false));
		
		//+ {
		pluginEnabled = new Button(c, SWT.CHECK);
		GridData gd = new GridData();
		pluginEnabled.setLayoutData(gd);
		pluginEnabled.setText("Plugin enabled");
		pluginEnabled.setAlignment(SWT.RIGHT);
		pluginEnabled.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Boolean isEnabled = pluginEnabled.getSelection();
				//store.setValue(EditBox.PREF_ENABLED, isEnabled ); // no need
				// decorators are updated when state is toggled within Command Handler
				EnableEditBox toggleHandler = new EnableEditBox();
				try {
					toggleHandler.execute(null);
				} catch (ExecutionException ex) {
					EditBox.logError(this, ex.getLocalizedMessage(), ex);
				}
				// toolbar item
				EditBox.toggleToolBarItemInAllWindows(isEnabled);				
			}
		});
		pluginEnabled.setSelection(store.getBoolean(EditBox.PREF_ENABLED));
		//}		
		
		Link link = new Link(c, SWT.NONE);
		link.setText("Configure print margin and current line highlighting <A>here</A>.");
		FontData[] fontData = link.getFont().getFontData();
		for (FontData fd : fontData) {
			fd.setHeight(10);
			//-fd.setStyle(SWT.BOLD);
		}
		link.setFont(new Font(getShell().getDisplay(), fontData));
		link.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IWorkbenchPreferenceContainer container= (IWorkbenchPreferenceContainer) getContainer();
				container.openPage("org.eclipse.ui.preferencePages.GeneralTextEditor", null);
			}
		});		
		
		tabFolder = new TabFolder(c, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		TabItem ti = new TabItem(tabFolder, SWT.NONE);
		ti.setText("Categories");
		ti.setControl(createCategoryControl(tabFolder));
		tabFolder.pack();
		return c;
	}
	
	protected Control createCategoryControl(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, true));
		
		//+ apply theme globally {
		Label comboLabel = new Label(c, SWT.NONE);
		comboLabel.setText("Select one of bundled themes to apply to all categories (you can refine on respective Tab)");		
		
		themeCombo = new Combo(c, SWT.DROP_DOWN | SWT.READ_ONLY );
		GridData gd = new GridData(GridData.BEGINNING);
		gd.widthHint = 150;
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.LEFT;
		themeCombo.setLayoutData(gd);
		themeCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String s = themeCombo.getText();
				if (s != null && s.length() > 0) {
					store.setValue(EditBox.PREF_DEFAULT_THEME, s);
					updateAllTabsWithSelectedTheme(s);
				}
			}
		});
		themeCombo.setItems(BoxProviderRegistry.ALL_THEMES_ARRAY);
		String preferedThemeName = store.getString(EditBox.PREF_DEFAULT_THEME);
		themeCombo.select( BoxProviderRegistry.getThemeIndex(preferedThemeName) );
		//}

		Label categoryLabel = new Label(c, SWT.NONE);
		categoryLabel.setText("Categories");

		Label namesLabel = new Label(c, SWT.NONE);
		namesLabel.setText("Associated file names");
		namesLabel.setAlignment(SWT.RIGHT);

		categoryList = new List(c, SWT.V_SCROLL | SWT.BORDER);
		categoryList.setLayoutData(new GridData(GridData.FILL_BOTH));
		categoryList.addSelectionListener(new SelectCategory());
		namesList = new List(c, SWT.V_SCROLL | SWT.BORDER);
		namesList.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite cLeft = new Composite(c, SWT.NONE);
		cLeft.setLayout(new GridLayout(2, true));
		Button bAddCategory = new Button(cLeft, SWT.NONE);
		bAddCategory.setText("Add");
		bAddCategory.addSelectionListener(new AddCategory());
		bAddCategory.setLayoutData(new GridData(GridData.FILL_BOTH));
		Button bRemoveCategory = new Button(cLeft, SWT.NONE);
		bRemoveCategory.setText("Remove");
		bRemoveCategory.setLayoutData(new GridData(GridData.FILL_BOTH));
		bRemoveCategory.addSelectionListener(new RemoveCategory());

		Composite cRight = new Composite(c, SWT.NONE);
		cRight.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		cRight.setLayout(new GridLayout(2, true));
		bAddFile = new Button(cRight, SWT.NONE);
		bAddFile.setText("Add");
		bAddFile.setLayoutData(new GridData(GridData.FILL_BOTH));
		bAddFile.addSelectionListener(new AddFile());
		bAddFile.setEnabled(false);
		Button bRemoveFile = new Button(cRight, SWT.NONE);
		bRemoveFile.setText("Remove");
		bRemoveFile.setLayoutData(new GridData(GridData.FILL_BOTH));
		bRemoveFile.addSelectionListener(new RemoveFile());

		loadDataFromProviderRegistryAndCreateTabs();

		return c;
	}	

	private void updateAllTabsWithSelectedTheme(String theme) {
		//IBoxProvider provider = EditBox.getDefault().getProviderRegistry().providerForName(theme);		
		TabItem[] tabItemas = tabFolder.getItems();
		for (TabItem item: tabItemas){
			BoxSettingsTab bst = (BoxSettingsTab) item.getData();
			if (bst == null){
				continue;
			}
			//bst.setProvider(provider);
			bst.loadSettingsForName(theme);
		}		
	}

	protected void loadDataFromProviderRegistryAndCreateTabs() {
		Collection<IBoxProvider> boxProviders = providerRegistry.getBoxProviders();
		for (IBoxProvider provider : boxProviders){
			newTab(provider.getName());
		}
	}

	protected void newTab(String providerName) {
		categoryList.add(providerName);
		TabItem item = new TabItem(tabFolder, SWT.NONE);
		item.setText(providerName);
		BoxSettingsTab bst = new BoxSettingsTab();
		IBoxProvider provider = providerRegistry.providerForName(providerName);
		item.setControl(bst.createControlsWithContent(tabFolder, provider));			
		item.setData(bst);
		if (categoryFiles == null)
			categoryFiles = new LinkedHashMap<String, LinkedHashSet<String>>();
		Collection<String> fileNames = bst.getSettings().getFileNames(); //XXX should be from Provider
		if (fileNames == null)
			fileNames = Collections.emptyList();
		categoryFiles.put(providerName, new LinkedHashSet<String>(fileNames));
		namesList.setItems(fileNames.toArray(new String[0]));
		bAddFile.setEnabled(true);
	}

	public String[] namesArray(String name) {
		LinkedHashSet<String> set = categoryFiles.get(name);
		if (set == null || set.isEmpty())
			return new String[0];
		return set.toArray(new String[0]);
	}

	public void addFileName(String value) {
		int i = categoryList.getSelectionIndex();
		if (i > -1) {
			String categoryName = categoryList.getItem(i);
			LinkedHashSet<String> fileNames = categoryFiles.get(categoryName);
			fileNames.add(value);
			namesList.add(value);
			Object o = tabFolder.getItem(i + 1).getData();
			if (o instanceof BoxSettingsTab)
				((BoxSettingsTab) o).getSettings().setFileNames(fileNames);
		}
	}
	
	@Override
	public boolean performOk() {
		
		TabItem[] items = tabFolder.getItems();
		for (int i=1;i<items.length;i++) {
			Object o =items[i].getData();
			if (o instanceof BoxSettingsTab){
				BoxSettingsTab bst = (BoxSettingsTab)o;
				String msg = bst.validate();
				if (msg !=null){
					tabFolder.setSelection(i);
					setMessage(msg);
					return false;
				}
				bst.save();
			}
		}
		if (providersChanged){
			providerRegistry.storeProviders();
		}
		return true;
	}
	
	@Override
	public boolean performCancel() {
		TabItem[] items = tabFolder.getItems();
		for (int i=1;i<items.length;i++) {
			Object o =items[i].getData();
			if (o instanceof BoxSettingsTab){
				((BoxSettingsTab)o).cancel();
			}
		}

		if (providersChanged){
			providerRegistry.setProvideres(null);
		} 
		return true;
	}

	// used in AddCategory
	protected boolean contains(String[] items, String newText) {
		if (items == null || items.length == 0)
			return false;
		for (String s : items)
			if (s.equalsIgnoreCase(newText))
				return true;
		return false;
	}
	
	class AddCategory extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			InputDialog dialog = new InputDialog(getShell(), "New Category", "Name:", null, new IInputValidator() {
				public String isValid(String newText) {
					if (newText != null && newText.trim().length() > 0 && !contains(categoryList.getItems(), newText))
						return null;
					return "Unique name required";
				}
			});

			if (dialog.open() == InputDialog.OK) {
				String providerName = dialog.getValue();
				newTab(providerName);
				categoryList.setSelection(new String[] { providerName });
				providersChanged = true;
			}

		}

	}

	class RemoveCategory extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			int i = categoryList.getSelectionIndex();
			if (i > -1) {
				String name = categoryList.getItem(i);
				categoryList.remove(i);
				categoryFiles.remove(name);
				namesList.setItems(new String[0]);
				bAddFile.setEnabled(false);
				TabItem ti = tabFolder.getItem(i + 1);
				Object o = ti.getData();
				ti.dispose();
				if (o instanceof BoxSettingsTab) {
					((BoxSettingsTab) o).dispose();
				}
				providerRegistry.removeProvider(name);
				providersChanged = true;
			}
		}
	}

	class SelectCategory extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			int i = categoryList.getSelectionIndex();
			if (i > -1) {
				String name = categoryList.getItem(i);
				namesList.setItems(namesArray(name));
				bAddFile.setEnabled(true);
			} else {
				namesList.setItems(new String[0]);
				bAddFile.setEnabled(false);
			}
		}
	}

	class AddFile extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			InputDialog dialog = new InputDialog(getShell(), "New Name", "File name pattern like *.java, my.xml:", null, new IInputValidator() {

				public String isValid(String newText) {
					if (newText != null && newText.trim().length() > 0)
						return null;
					return "";
				}
			});

			if (dialog.open() == InputDialog.OK) {
				addFileName(dialog.getValue());
			}

		}
	}

	class RemoveFile extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			int i = namesList.getSelectionIndex();
			if (i > -1) {
				int n = categoryList.getSelectionIndex();
				if (n > -1) {
					String key = categoryList.getItem(n);
					String value = namesList.getItem(i);
					LinkedHashSet<String> fNames = categoryFiles.get(key);
					fNames.remove(value);
					namesList.remove(i);
					Object o = tabFolder.getItem(n+1).getData();
					if (o instanceof BoxSettingsTab)
						((BoxSettingsTab) o).getSettings().setFileNames(new ArrayList<String>(fNames));
				}
			}
		}
	}
}
