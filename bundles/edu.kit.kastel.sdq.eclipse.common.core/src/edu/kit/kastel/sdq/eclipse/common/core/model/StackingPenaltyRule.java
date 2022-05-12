/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.common.core.model;

import java.util.List;

import edu.kit.kastel.sdq.eclipse.common.api.model.IAnnotation;

public class StackingPenaltyRule extends PenaltyRule {

	private static final String DISPLAY_NAME = "Stacking Penalty";
	public static final String SHORT_NAME = "stackingPenalty";

	// Penalty stored with decimal-point shifted one to the right (make sure no
	// rounding issues happen)
	private int penalty;

	public StackingPenaltyRule(double penalty) {
		this.penalty = (int) (penalty * 10);
	}

	@Override
	public double calculatePenalty(List<IAnnotation> annotations) {
		return (annotations.size() * this.penalty) / 10.0;
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
		return penaltyValue + " points [" + annotations.size() + " annotations made]";
	}

	@Override
	public String toString() {
		return "StackingPenaltyRule [penalty=" + this.penalty / 10.0 + " per annotation]";
	}

	@Override
	protected boolean isCustomPenalty() {
		return false;
	}

}
