package edu.kit.kastel.eclipse.grading.view.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.kit.kastel.eclipse.grading.view.activator.Activator;
import edu.kit.kastel.sdq.eclipse.grading.api.PreferenceConstants;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.ABSOLUTE_CONFIG_PATH, "");
		store.setDefault(PreferenceConstants.RELATIVE_CONFIG_PATH, "");
		store.setDefault(PreferenceConstants.IS_RELATIVE_CONFIG_PATH, "false");
		store.setDefault(PreferenceConstants.ARTEMIS_URL, "");
		store.setDefault(PreferenceConstants.ARTEMIS_USER, "");
		store.setDefault(PreferenceConstants.ARTEMIS_PASSWORD, "");
		store.setDefault(PreferenceConstants.PREFERS_LARGE_PENALTY_TEXT_PATH, "false");
	}

}
