package edu.kit.kastel.sdq.eclipse.grading.core;

import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.IRatingGroup;

public class ZeroedPenaltyCalculationStrategy implements IPenaltyCalculationStrategy {

	@Override
	public double calculatePenaltyForMistakeType(IMistakeType mistakeType) {
		return 0;
	}

	@Override
	public double calcultatePenaltyForRatingGroup(IRatingGroup ratingGroup) {
		return 0;
	}

	@Override
	public boolean penaltyLimitIsHitForRatingGroup(IRatingGroup ratingGroup) {
		return true;
	}

	@Override
	public boolean submissionIsInvalid() {
		return true;
	}

}
