package edu.kit.kastel.sdq.eclipse.grading.core.artemis;

import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IRatingGroup;

public interface IPenaltyCalculationStrategy {

	double calculatePenaltyForMistakeType(IMistakeType mistakeType);

	double calcultatePenaltyForRatingGroup(IRatingGroup ratingGroup);

	boolean penaltyLimitIsHitForRatingGroup(IRatingGroup ratingGroup);

	boolean submissionIsInvalid();

}
