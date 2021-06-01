package edu.kit.kastel.sdq.eclipse.grading.core.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.kit.kastel.sdq.eclipse.grading.api.IAnnotation;

/**
 * A Penalty which returns only one Annotation.
 *
 */
public class ThresholdPenaltyRule extends PenaltyRule {
	
	//TODO localize
	private static final String DISPLAY_NAME = "Threshold Penalty";
	public static final String SHORT_NAME = "thresholdPenalty";

	private int threshold;
	private double penalty;
	
	
	public ThresholdPenaltyRule(int threshold, double penalty) {
		this.threshold = threshold;
		this.penalty = penalty;
	}
	
	@Override
	public Map<IAnnotation, Double> calculatePenalty(List<IAnnotation> annotations) {
		return (annotations.size() >= this.threshold) 
			? Map.of(annotations.get(0), this.penalty)
			: Map.of();
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
	public boolean isCollectiveType() {
		return true;
	}

	@Override
	public String toString() {
		return "ThresholdPenaltyRule [threshold=" + threshold + ", penalty=" + penalty + "]";
	}

	
}
