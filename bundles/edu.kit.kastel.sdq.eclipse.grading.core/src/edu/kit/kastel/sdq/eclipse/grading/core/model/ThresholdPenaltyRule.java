package edu.kit.kastel.sdq.eclipse.grading.core.model;

import java.util.List;

import edu.kit.kastel.sdq.eclipse.grading.api.model.IAnnotation;

/**
 * A Penalty which returns only one Annotation.
 *
 */
public class ThresholdPenaltyRule extends PenaltyRule {

	private static final String DISPLAY_NAME = "Threshold Penalty";
	public static final String SHORT_NAME = "thresholdPenalty";

	private int threshold;
	private double penalty;

	public ThresholdPenaltyRule(int threshold, double penalty) {
		this.threshold = threshold;
		this.penalty = penalty;
	}

	@Override
	public double calculatePenalty(List<IAnnotation> annotations) {
		return Math.abs(annotations.size() >= this.threshold ? this.penalty : 0.D);
	}

	@Override
	public String getDisplayName() {
		return DISPLAY_NAME;
	}

	@Override
	public String getShortName() {
		return SHORT_NAME;
	}

	@Override
	public String getTooltip(List<IAnnotation> annotations) {
		return new StringBuilder().append(this.calculatePenalty(annotations)).append(" points [").append(annotations.size()).append(" of at least ")
				.append(this.threshold).append(" annotations made]").toString();
	}

	@Override
	public String toString() {
		return "ThresholdPenaltyRule [threshold=" + this.threshold + ", penalty=" + this.penalty + "]";
	}

}
