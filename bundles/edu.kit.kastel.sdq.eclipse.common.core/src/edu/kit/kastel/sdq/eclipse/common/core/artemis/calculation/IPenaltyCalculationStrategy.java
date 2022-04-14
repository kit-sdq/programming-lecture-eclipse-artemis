/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.common.core.artemis.calculation;

import edu.kit.kastel.sdq.eclipse.common.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.common.api.model.IRatingGroup;

public interface IPenaltyCalculationStrategy {

	/**
	 *
	 * @param mistakeType
	 * @return the penalty considering all annotations belonging to the given
	 *         mistaketype.
	 */
	double calculatePenaltyForMistakeType(IMistakeType mistakeType);

	/**
	 *
	 * @param ratingGroup
	 * @return the penalty considering all annotations belonging to the given
	 *         ratingGroup'. Capping may or may not be applied, based on the
	 *         ratingGroup's {@link IRatingGroup#getPenaltyLimit()}.
	 */
	double calcultatePenaltyForRatingGroup(IRatingGroup ratingGroup);

	/**
	 *
	 * @param ratingGroup
	 * @return whether calculating the penalty for rating group
	 *         {@link #calcultatePenaltyForRatingGroup(IRatingGroup)} hits the
	 *         ratingGroup's {@link IRatingGroup#getPenaltyLimit()}
	 */
	boolean penaltyLimitIsHitForRatingGroup(IRatingGroup ratingGroup);

	/**
	 *
	 * @return whether submissions are treated as invalid.
	 */
	boolean submissionIsInvalid();

}
