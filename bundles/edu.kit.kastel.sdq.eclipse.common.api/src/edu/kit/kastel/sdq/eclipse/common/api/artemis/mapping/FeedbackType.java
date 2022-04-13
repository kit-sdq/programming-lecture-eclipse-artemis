/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping;

/**
 * See also {@link IFeedback}.
 * <ul>
 * <li>{@link FeedbackType#MANUAL} represents single line annotations</li>
 * <li>{@link FeedbackType#MANUAL_UNREFERENCED} represents remarks that are
 * shown below the code</li>
 * <li>{@link FeedbackType#AUTOMATIC} represents e.g. unit test results</li>
 * </ul>
 *
 */
public enum FeedbackType {
	MANUAL, MANUAL_UNREFERENCED, AUTOMATIC;

	public static FeedbackType valueOfIgnoreCase(String str) {
		return str != null ? FeedbackType.valueOf(str.toUpperCase()) : null;
	}
}