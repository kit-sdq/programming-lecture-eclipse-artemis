/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.view.utilities;

import java.util.Locale;
import java.util.ResourceBundle;

import edu.kit.kastel.eclipse.common.view.activator.Activator;
import edu.kit.kastel.sdq.eclipse.common.api.PreferenceConstants;

public final class ResourceBundleProvider {

	private static Locale locale = Locale.getDefault();
	private static ResourceBundle resourceBundle;

	public static ResourceBundle getResourceBundle() {
		if (resourceBundle == null) {
			updateResourceBundle();
		}
		return resourceBundle;
	}

	public static void updateResourceBundle() {
		String localeString = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.PREFERRED_LANGUAGE_PATH);
		if (localeString.isBlank()) {
			locale = new Locale("en_US");
		} else {
			locale = new Locale(Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.PREFERRED_LANGUAGE_PATH));
		}
		resourceBundle = ResourceBundle.getBundle("resources.lang.translations", locale);
	}

}
