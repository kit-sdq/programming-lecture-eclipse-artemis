package edu.kit.kastel.sdq.eclipse.grading.core.artemis.calculation;

import java.util.List;
import java.util.stream.Collectors;

import edu.kit.kastel.sdq.eclipse.grading.api.model.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IRatingGroup;

public class DefaultPenaltyCalculationStrategy implements IPenaltyCalculationStrategy {

	private List<IAnnotation> annotations;
	private List<IMistakeType> mistakeTypes;

	public DefaultPenaltyCalculationStrategy(List<IAnnotation> annotations, List<IMistakeType> mistakeTypes) {
		this.annotations = annotations;
		this.mistakeTypes = mistakeTypes;
	}

	@Override
	public double calculatePenaltyForMistakeType(IMistakeType mistakeType) {
		return mistakeType.calculatePenalty(this.annotations.stream() //
				.filter(annotation -> annotation.getMistakeType().equals(mistakeType)) //
				.collect(Collectors.toList()));
	}

	private double calculatePenaltyForRatingGroupWithoutLimit(IRatingGroup ratingGroup) {
		return this.mistakeTypes.stream() //
				.filter(mistakeType -> mistakeType.getRatingGroup().equals(ratingGroup)) //
				.mapToDouble(this::calculatePenaltyForMistakeType).sum();
	}

	@Override
	public double calcultatePenaltyForRatingGroup(IRatingGroup ratingGroup) {
		double calculatedPenalty = this.calculatePenaltyForRatingGroupWithoutLimit(ratingGroup);

		return ratingGroup.hasPenaltyLimit()
				// both are positive
				? Math.min(calculatedPenalty, ratingGroup.getPenaltyLimit())
				: calculatedPenalty;

	}

	@Override
	public boolean penaltyLimitIsHitForRatingGroup(IRatingGroup ratingGroup) {
		return this.calculatePenaltyForRatingGroupWithoutLimit(ratingGroup) > ratingGroup.getPenaltyLimit();
	}

	@Override
	public boolean submissionIsInvalid() {
		return false;
	}

}
