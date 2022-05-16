/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.core.model;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import edu.kit.kastel.eclipse.common.api.model.IAnnotation;

public class CustomPenaltyRule extends PenaltyRule {

	private static final String DISPLAY_NAME = "Custom Penalty";
	public static final String SHORT_NAME = "customPenalty";

	public CustomPenaltyRule(JsonNode penaltyRuleNode) {
		// No need for custom configurations.
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
