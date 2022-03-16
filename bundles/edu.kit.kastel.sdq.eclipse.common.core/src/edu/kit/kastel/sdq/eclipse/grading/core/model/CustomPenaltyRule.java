package edu.kit.kastel.sdq.eclipse.grading.core.model;

import java.util.List;

import edu.kit.kastel.sdq.eclipse.grading.api.model.IAnnotation;

public class CustomPenaltyRule extends PenaltyRule {

	private static final String DISPLAY_NAME = "Custom Penalty";
	public static final String SHORT_NAME = "customPenalty";

	public CustomPenaltyRule() {
		// a comment explaining why this method is empty
	}

	@Override
	public double calculatePenalty(List<IAnnotation> annotations) {
		if (annotations != null) {
			return annotations.stream().mapToDouble(annotation -> annotation.getCustomPenalty().orElse(0.D)).sum();
		}
		return 0.D;
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
		return new StringBuilder().append(this.calculatePenalty(annotations)).append(" points [").append(annotations.size()).append(" annotations made]")
				.toString();
	}

	@Override
	public String toString() {
		return "CustomPenaltyRule";
	}

	@Override
	protected boolean isCustomPenalty() {
		return true;
	}

}
