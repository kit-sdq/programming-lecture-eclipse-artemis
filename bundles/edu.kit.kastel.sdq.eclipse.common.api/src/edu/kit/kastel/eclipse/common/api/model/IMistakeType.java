/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.api.model;

import java.util.List;

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
	String getId();

	/**
	 *
	 * @return a more elaborate explanation of what the mistake is.
	 */
	String getMessage();

	/**
	 *
	 * @return what should be shown on the button.
	 */
	String getButtonText();

	/**
	 *
	 * @return the name of the penalty this MistakeType uses.
	 */
	String getPenaltyName();

	/**
	 *
	 * @return the {@link IRatingGroup} this {@link IMistakeType} belongs to, which
	 *         may introduce a {@link IRatingGroup#getPenaltyLimit()}!
	 */
	IRatingGroup getRatingGroup();

	/**
	 *
	 * @param annotations
	 * @return tooltip for hovering over the button. Shows rating status information
	 *         based on the given annotation.
	 */
	String getTooltip(List<IAnnotation> annotations);

	/**
	 * Indicates whether this is a custom penalty.
	 *
	 * @return indicator for custom penalties
	 */
	boolean isCustomPenalty();
}
