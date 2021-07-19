package edu.kit.kastel.sdq.eclipse.grading.core.model;

import java.util.List;

import edu.kit.kastel.sdq.eclipse.grading.api.IAnnotation;

public class CustomPenaltyRule extends PenaltyRule {

	private static final String DISPLAY_NAME = "Custom Penalty";
	public static final String SHORT_NAME = "customPenalty";

	public CustomPenaltyRule() { }

	@Override
	public double calculatePenalty(List<IAnnotation> annotations) {
		if (annotations != null) {
			return annotations.stream()
					.map(annotation -> annotation.getCustomPenalty().orElseGet(() -> 0.D))
					.reduce(0.D, Double::sum);
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
		return new StringBuilder()
				.append(this.calculatePenalty(annotations))
				.append(" points [")
				.append(annotations.size())
				.append(" annotations made]")
				.toString();
	}

	@Override
	public String toString() {
		return "CustomPenaltyRule";
	}



}
