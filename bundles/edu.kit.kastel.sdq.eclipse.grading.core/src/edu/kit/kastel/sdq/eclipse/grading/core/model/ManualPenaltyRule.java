package edu.kit.kastel.sdq.eclipse.grading.core.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.kit.kastel.sdq.eclipse.grading.api.IAnnotation;

/**
 * A penalty type which sanctions each occurrence with the same penalty (individual type).
 *
 */
public class ManualPenaltyRule extends PenaltyRule {
	
	//TODO localize
	private static final String DISPLAY_NAME = "Manual Penalty";
	private static final String SHORT_NAME = "manualPenalty";

	private double penalty;
	
	public ManualPenaltyRule(double penalty) {
		this.penalty = penalty;
	}

	@Override
	public Map<IAnnotation, Double> calculatePenalty(List<IAnnotation> annotations) {
		Map<IAnnotation, Double> penaltyMap = new HashMap<IAnnotation, Double>();
		annotations.forEach(annotation -> penaltyMap.put(annotation, this.penalty));
		
		return penaltyMap;
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
		return false;
	}

}
