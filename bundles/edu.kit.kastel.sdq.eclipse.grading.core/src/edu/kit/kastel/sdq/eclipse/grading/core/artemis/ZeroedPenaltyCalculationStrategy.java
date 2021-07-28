package edu.kit.kastel.sdq.eclipse.grading.core.artemis;

import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IRatingGroup;

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
