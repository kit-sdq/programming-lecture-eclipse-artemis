package gui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import gui.activator.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_ABSOLUTE_CONFIG_PATH,
				"C:\\Users\\Paul\\git\\programming-lecture-eclipse-artemis-grading\\bundles\\edu.kit.kastel.sdq.eclipse.grading.view\\resources\\config_v2.json");
		store.setDefault(PreferenceConstants.P_RELATIVE_CONFIG_PATH, "");
		store.setDefault(PreferenceConstants.P_IS_RELATIVE_CONFIG_PATH, "false");
	}

}
