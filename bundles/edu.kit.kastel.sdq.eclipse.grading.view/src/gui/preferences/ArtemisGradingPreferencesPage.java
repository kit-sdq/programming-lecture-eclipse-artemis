package gui.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import gui.activator.Activator;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class ArtemisGradingPreferencesPage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	
	BooleanFieldEditor isRelativeConfigPath;
	StringFieldEditor relativeConfigPath;
	FileFieldEditor absoluteConfigPath;
	StringFieldEditor artemisUrl;

	public ArtemisGradingPreferencesPage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Set preferences for the Artemis Grading");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		
		 absoluteConfigPath = new FileFieldEditor(
				PreferenceConstants.P_ABSOLUTE_CONFIG_PATH, 
				"Absolute config path: ", 
				getFieldEditorParent());
		
		 isRelativeConfigPath = new BooleanFieldEditor(
				PreferenceConstants.P_IS_RELATIVE_CONFIG_PATH, 
				"Use relative config path", getFieldEditorParent());
		
		 relativeConfigPath = new StringFieldEditor(
				PreferenceConstants.P_RELATIVE_CONFIG_PATH, 
				"Relative config path: ",
				getFieldEditorParent());
		 
		 artemisUrl = new StringFieldEditor(
				 PreferenceConstants.P_ARTEMIS_URL, 
				 "Artemis URL: ", 
				 getFieldEditorParent());
		
				
		addField(absoluteConfigPath);
		addField(relativeConfigPath);
		addField(isRelativeConfigPath);
		addField(artemisUrl);
		
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		isRelativeConfigPath.setPropertyChangeListener(new IPropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				
					boolean isRelative = ((Boolean) event.getNewValue()).booleanValue();
					if(isRelative) {
						relativeConfigPath.setEnabled(true, getFieldEditorParent());
						absoluteConfigPath.setEnabled(false, getFieldEditorParent());
					} else {
						relativeConfigPath.setEnabled(false, getFieldEditorParent());
						absoluteConfigPath.setEnabled(true, getFieldEditorParent());
					}
			
			}
		});
		
		boolean isRelativeSelected = isRelativeConfigPath.getBooleanValue();
		if(isRelativeSelected) {
			absoluteConfigPath.setEnabled(false, getFieldEditorParent());
		}
	}
			

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		
	}
	
}