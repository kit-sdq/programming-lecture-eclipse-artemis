/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.view.utilities;

import java.util.Locale;
import java.util.ResourceBundle;

public final class ResourceBundleProvider {

	private static ResourceBundle resourceBundle;

	public static ResourceBundle getResourceBundle() {
		if (resourceBundle == null) {
			updateResourceBundle();
		}
		return resourceBundle;
	}

	public static void updateResourceBundle() {
		// TODO dynamically load locale from config
		resourceBundle = ResourceBundle.getBundle("lang.translations", new Locale("de_DE"));
	}

}
