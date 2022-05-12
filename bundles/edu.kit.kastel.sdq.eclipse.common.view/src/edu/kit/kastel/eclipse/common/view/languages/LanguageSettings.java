/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.view.languages;

import java.util.List;
import java.util.NoSuchElementException;

import edu.kit.kastel.eclipse.common.view.activator.CommonActivator;
import edu.kit.kastel.sdq.eclipse.common.api.PreferenceConstants;

public final class LanguageSettings {

	private static I18N currentOverride = new DefaultLanguage();

	private static final List<I18N> availableLanguages = List.of(new DefaultLanguage(), new GermanLanguage());

	private LanguageSettings() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	public static I18N I18N() {
		return currentOverride;
	}

	public static String[][] getAvailableLocalesForComboField() {
		String[][] ret = new String[availableLanguages.size()][];
		for (int i = 0; i < availableLanguages.size(); i++) {
			ret[i] = new String[] { availableLanguages.get(i).languageDisplayName(), availableLanguages.get(i).languageDisplayName() };
		}
		return ret;
	}

	public static void updateI18N() {
		String languageString = CommonActivator.getDefault().getPreferenceStore().getString(PreferenceConstants.GENERAL_PREFERRED_LANGUAGE_PATH);

		currentOverride = availableLanguages.stream().filter(lang -> lang.languageDisplayName().equals(languageString)).findAny()
				.orElseGet(() -> availableLanguages.stream().filter(I18N::isDefault).findFirst()
						.orElseThrow(() -> new NoSuchElementException("No default language found!")));
	}

}
