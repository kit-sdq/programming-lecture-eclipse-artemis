/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.view.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.kit.kastel.eclipse.common.view.activator.CommonActivator;
import edu.kit.kastel.sdq.eclipse.common.api.PreferenceConstants;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = CommonActivator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.ARTEMIS_URL, "");
		store.setDefault(PreferenceConstants.ARTEMIS_USER, "");
		store.setDefault(PreferenceConstants.ARTEMIS_PASSWORD, "");
		store.setDefault(PreferenceConstants.GIT_TOKEN, "");

		store.setDefault(PreferenceConstants.ABSOLUTE_CONFIG_PATH, "");
		store.setDefault(PreferenceConstants.GRADING_BUTTONS_IN_COLUMN, 3);
		store.setDefault(PreferenceConstants.PREFERS_LARGE_PENALTY_TEXT_PATH, false);
		store.setDefault(PreferenceConstants.PREFERS_TEXT_WRAPPING_IN_PENALTY_TEXT_PATH, false);
		store.setDefault(PreferenceConstants.OVERRIDE_DEFAULT_PREFERENCES, true);
	}

}
