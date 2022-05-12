/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.view.languages;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.eclipse.jface.preference.ComboFieldEditor;

import edu.kit.kastel.eclipse.common.view.activator.CommonActivator;
import edu.kit.kastel.sdq.eclipse.common.api.PreferenceConstants;

public final class LanguageSettings {

	private static I18N currentOverride = new DefaultLanguage();

	private static final List<I18N> availableLanguages = List.of(new DefaultLanguage(), new GermanLanguage());

	private LanguageSettings() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	/**
	 * provides an {@link I18N}-instance to use for the translations Normally this
	 * method should be statically imported
	 * 
	 * @return the {@link I18N}-instance
	 */
	public static I18N I18N() {
		return currentOverride;
	}

	/**
	 * Creates a 2D-String-Array required for {@link ComboFieldEditor} containing
	 * all languages
	 * 
	 * @return the array
	 */
	public static String[][] getAvailableLocalesForComboField() {
		String[][] ret = new String[availableLanguages.size()][];
		for (int i = 0; i < availableLanguages.size(); i++) {
			ret[i] = new String[] { availableLanguages.get(i).languageDisplayName(), availableLanguages.get(i).languageDisplayName() };
		}
		return ret;
	}

	/**
	 * Reloads the internal {@link I18N} to match the selected language. Note: A
	 * restart of the IDE might be required for all changes to take effect.
	 */
	public static void updateI18N() {
		String languageString = CommonActivator.getDefault().getPreferenceStore().getString(PreferenceConstants.GENERAL_PREFERRED_LANGUAGE_PATH);

		// Load language with name from config
		Optional<I18N> optionalLanguage = availableLanguages.stream().filter(lang -> lang.languageDisplayName().equals(languageString)).findFirst();

		if (optionalLanguage.isEmpty()) {
			// fallback to default language if none found
			optionalLanguage = availableLanguages.stream().filter(I18N::isDefault).findFirst();
		}
		currentOverride = optionalLanguage.orElseThrow(() -> new NoSuchElementException("No default language found!"));
	}

}
