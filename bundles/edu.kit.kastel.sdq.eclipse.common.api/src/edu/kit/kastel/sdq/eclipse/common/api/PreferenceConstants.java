/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.common.api;

/**
 * Constant definitions for plug-in preferences
 */
public final class PreferenceConstants {

	public static final String IS_RELATIVE_CONFIG_PATH = "isRelativeConfigPath";
	public static final String ABSOLUTE_CONFIG_PATH = "absoluteConfigPath";
	public static final String RELATIVE_CONFIG_PATH = "relativeConfigPath";
	public static final String ARTEMIS_URL = "artemisUrl";
	public static final String ARTEMIS_USER = "artemisUser";
	public static final String ARTEMIS_PASSWORD = "artemisPassword";
	public static final String PREFERS_LARGE_PENALTY_TEXT_PATH = "userPreferresLargePenaltyText";
	public static final String PREFERS_TEXT_WRAPPING_IN_PENALTY_TEXT_PATH = "userPrefersTextWrappingInPenaltyText";
	public static final String PREFERRED_LANGUAGE_PATH = "preferredLanguageSelector";

	private PreferenceConstants() {
		throw new IllegalAccessError();
	}
}
