/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.common.core.model;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import edu.kit.kastel.sdq.eclipse.common.api.model.IAnnotation;

public class StackingPenaltyRule extends PenaltyRule {

	private static final String DISPLAY_NAME = "Stacking Penalty";
	public static final String SHORT_NAME = "stackingPenalty";

	// Penalty stored with decimal-point shifted one to the right (make sure no
	// rounding issues happen)
	private int penalty;

	private Integer maxUses = null; // null => no limit

	public StackingPenaltyRule(JsonNode penaltyRuleNode) {
		this.penalty = (int) (penaltyRuleNode.get("penalty").asDouble() * 10);

		if (penaltyRuleNode.hasNonNull("maxUses")) {
			maxUses = penaltyRuleNode.get("maxUses").asInt();
		}
	}

	@Override
	public double calculatePenalty(List<IAnnotation> annotations) {
		int multiplier = maxUses == null ? annotations.size() : Math.min(annotations.size(), maxUses);
		return (multiplier * this.penalty) / 10.0;
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
		double penaltyValue = this.calculatePenalty(annotations);
		String tooltip = penaltyValue + " points [" + annotations.size() + " annotations made";
		tooltip += maxUses != null ? " - capped to " + maxUses + " annotations" : "";
		tooltip += "]";
		return tooltip;
	}

	@Override
	public String toString() {
		String string = "StackingPenaltyRule [penalty=\" + this.penalty / 10.0 + \" per annotation";
		string += maxUses != null ? " capped to " + maxUses + " annotations" : "";
		string += "]";
		return string;
	}

	@Override
	protected boolean isCustomPenalty() {
		return false;
	}

}
