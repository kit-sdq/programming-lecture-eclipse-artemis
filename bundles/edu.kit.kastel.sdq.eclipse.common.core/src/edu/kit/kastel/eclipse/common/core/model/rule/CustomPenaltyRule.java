/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.core.model.rule;

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
	public double calculate(List<IAnnotation> annotations) {
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
		return this.calculate(annotations) + " points [" + annotations.size() + " annotations made]";
	}

	@Override
	public boolean isCustomPenalty() {
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		return this.getClass() == obj.getClass();
	}

	@Override
	public int hashCode() {
		return this.getClass().hashCode();
	}
}
