package edu.kit.kastel.eclipse.grading.gui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.kit.kastel.eclipse.grading.gui.activator.Activator;
import edu.kit.kastel.sdq.eclipse.grading.api.PreferenceConstants;

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
		store.setDefault(PreferenceConstants.ABSOLUTE_CONFIG_PATH,
				"C:\\Users\\Ruessmann\\Desktop\\programming-lecture-eclipse-artemis-grading\\bundles\\edu.kit.kastel.sdq.eclipse.grading.view\\resources\\config_v4.json");
		store.setDefault(PreferenceConstants.RELATIVE_CONFIG_PATH, "");
		store.setDefault(PreferenceConstants.IS_RELATIVE_CONFIG_PATH, "false");
		store.setDefault(PreferenceConstants.ARTEMIS_URL, "artemis-test.ipd.kit.edu");
		store.setDefault(PreferenceConstants.ARTEMIS_USER, "uyduk");
		store.setDefault(PreferenceConstants.ARTEMIS_PASSWORD, "arTem155");
	}

}
