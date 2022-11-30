/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.core.model.rule;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;

import edu.kit.kastel.eclipse.common.api.model.IAnnotation;

/**
 * A Penalty which returns only one Annotation.
 *
 */
public class ThresholdPenaltyRule extends PenaltyRule {

	private static final String DISPLAY_NAME = "Threshold Penalty";
	public static final String SHORT_NAME = "thresholdPenalty";

	private int threshold;
	private double penalty;

	public ThresholdPenaltyRule(JsonNode penaltyRuleNode) {
		this.threshold = penaltyRuleNode.get("threshold").asInt();
		this.penalty = penaltyRuleNode.get("penalty").asDouble();
	}

	@Override
	public double calculate(List<IAnnotation> annotations) {
		return annotations.size() >= this.threshold ? -this.penalty : 0.D;
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
		double penaltyValue = this.calculate(annotations);
		return penaltyValue + " points [" + annotations.size() + " of at least " + this.threshold + " annotations made]";
	}

	@Override
	public String toString() {
		return "ThresholdPenaltyRule [threshold=" + this.threshold + ", penalty=" + this.penalty + "]";
	}

	@Override
	public boolean isCustomPenalty() {
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(penalty, threshold);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		ThresholdPenaltyRule other = (ThresholdPenaltyRule) obj;
		return Double.doubleToLongBits(penalty) == Double.doubleToLongBits(other.penalty) && threshold == other.threshold;
	}

}
