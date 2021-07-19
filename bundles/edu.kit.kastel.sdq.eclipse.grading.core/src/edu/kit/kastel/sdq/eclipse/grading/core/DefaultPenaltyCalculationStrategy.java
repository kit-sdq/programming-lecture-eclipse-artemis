package edu.kit.kastel.sdq.eclipse.grading.core;

import java.util.Collection;
import java.util.stream.Collectors;

import edu.kit.kastel.sdq.eclipse.grading.api.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.IRatingGroup;

public class DefaultPenaltyCalculationStrategy implements IPenaltyCalculationStrategy {

	private Collection<IAnnotation> annotations;
	private Collection<IMistakeType> mistakeTypes;

	public DefaultPenaltyCalculationStrategy(Collection<IAnnotation> annotations, Collection<IMistakeType> mistakeTypes) {
		this.annotations = annotations;
		this.mistakeTypes = mistakeTypes;
	}

	@Override
	public double calculatePenaltyForMistakeType(IMistakeType mistakeType) {
		return mistakeType.calculatePenalty(
			this.annotations.stream()
				.filter(annotation -> annotation.getMistakeType().equals(mistakeType))
				.collect(Collectors.toList())
		);
	}

	private double calculatePenaltyForRatingGroupWithoutLimit(IRatingGroup ratingGroup) {
		return this.mistakeTypes.stream()
			.filter(mistakeType -> mistakeType.getRatingGroup().equals(ratingGroup))
			.map(this::calculatePenaltyForMistakeType)
			.collect(Collectors.summingDouble(Double::doubleValue));
	}

	@Override
	public double calcultatePenaltyForRatingGroup(IRatingGroup ratingGroup) {
		double calculatedPenalty = this.calculatePenaltyForRatingGroupWithoutLimit(ratingGroup);

		return ratingGroup.hasPenaltyLimit()
				//both are positive
				? Math.min(calculatedPenalty, ratingGroup.getPenaltyLimit())
				: calculatedPenalty;

	}

	@Override
	public boolean penaltyLimitIsHitForRatingGroup(IRatingGroup ratingGroup) {
		return (this.calculatePenaltyForRatingGroupWithoutLimit(ratingGroup) > ratingGroup.getPenaltyLimit());
	}

	@Override
	public boolean submissionIsInvalid() {
		return false;
	}

}
