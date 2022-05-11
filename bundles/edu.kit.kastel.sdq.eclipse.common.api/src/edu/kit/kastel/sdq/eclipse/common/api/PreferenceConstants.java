/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.common.api;

/**
 * Constant definitions for plug-in preferences
 */
public final class PreferenceConstants {

	public static final String ARTEMIS_URL = "artemisUrl";
	public static final String ARTEMIS_USER = "artemisUser";
	public static final String ARTEMIS_PASSWORD = "artemisPassword";
	public static final String GIT_TOKEN = "gitToken";

	public static final String ABSOLUTE_CONFIG_PATH = "absoluteConfigPath";
	public static final String GRADING_BUTTONS_IN_COLUMN = "grading_buttons_in_column";
	public static final String PREFERS_LARGE_PENALTY_TEXT_PATH = "userPreferresLargePenaltyText";
	public static final String PREFERS_TEXT_WRAPPING_IN_PENALTY_TEXT_PATH = "userPrefersTextWrappingInPenaltyText";

	public static final String OVERRIDE_DEFAULT_PREFERENCES = "override_default_preferences";

	private PreferenceConstants() {
		throw new IllegalAccessError();
	}
}
