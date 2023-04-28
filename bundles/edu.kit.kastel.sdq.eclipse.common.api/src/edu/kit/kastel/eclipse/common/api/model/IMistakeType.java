/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.api.model;

import java.util.List;

import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;

/**
 * Represents one type of mistakes from a rating group.
 *
 */
public interface IMistakeType {

	/**
	 * Calculate penalty using only the given annotations. Consider
	 * {@link #isDeduction()} for the calculation!
	 *
	 * @return a <i>positive or negative</> value denoting the penalty or points.
	 */
	double calculate(List<IAnnotation> annotations);

	/**
	 *
	 * @return what should be used as unique id.
	 */
	String getIdentifier();

	/**
	 * @param languageKey the key of the language that is selected. (e.g., "de").
	 *                    Can be null to use the default language.
	 * @return a more elaborate explanation of what the mistake is.
	 */
	String getMessage(String languageKey);

	/**
	 * @param languageKey the key of the language that is selected. (e.g., "de").
	 *                    Can be null to use the default language.
	 * @return what should be shown on the button.
	 */
	String getButtonText(String languageKey);

	/**
	 *
	 * @return the {@link IRatingGroup} this {@link IMistakeType} belongs to, which
	 *         may introduce a {@link IRatingGroup#getPenaltyLimit()}!
	 */
	IRatingGroup getRatingGroup();

	/**
	 * @param languageKey the key of the language that is selected. (e.g., "de").
	 *                    Can be null to use the default language.
	 * @return tooltip for hovering over the button. Shows rating status information
	 *         based on the given annotation.
	 */
	String getTooltip(String languageKey, List<IAnnotation> annotations);

	/**
	 * Indicates whether this is a custom penalty.
	 *
	 * @return indicator for custom penalties
	 */
	boolean isCustomPenalty();

	void initialize(IExercise exercise);

	boolean isEnabledButton();

	boolean isPenaltyEnabled();
}
