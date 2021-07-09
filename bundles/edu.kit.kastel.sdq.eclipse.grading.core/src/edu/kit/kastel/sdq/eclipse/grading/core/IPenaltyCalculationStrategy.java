package edu.kit.kastel.sdq.eclipse.grading.core;

import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.IRatingGroup;

public interface IPenaltyCalculationStrategy {

	double calculatePenaltyForMistakeType(IMistakeType mistakeType);

	double calcultatePenaltyForRatingGroup(IRatingGroup ratingGroup);

	boolean penaltyLimitIsHitForRatingGroup(IRatingGroup ratingGroup);

}
