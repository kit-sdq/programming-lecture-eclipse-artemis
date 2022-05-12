/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.view.languages;

public class GermanLanguage implements I18N {

	@Override
	public String settingsLanguage() {
		return "Sprache";
	}

	@Override
	public String languageDisplayName() {
		return "German";
	}

	@Override
	public boolean isDefault() {
		return false;
	}

}
