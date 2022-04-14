/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.common.core.model;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import edu.kit.kastel.sdq.eclipse.common.api.model.IAnnotation;
import edu.kit.kastel.sdq.eclipse.common.core.config.PenaltyRuleDeserializer;

/**
 * This class is used by an IMistakeType-Instance to calculate penalties.
 */
@JsonDeserialize(using = PenaltyRuleDeserializer.class)
public abstract class PenaltyRule {

	/**
	 * Calculate the penalty these Annotations add to the result.
	 *
	 * @param annotations the annotations on which the calculation is based.
	 *
	 * @return a reducing penalty
	 */
	public abstract double calculatePenalty(List<IAnnotation> annotations);

	public abstract String getDisplayName();

	public abstract String getShortName();

	/**
	 * @param annotations
	 * @return a tooltip String (e.g. for thresholdPenalty the score and how many
	 *         annotations were made.)
	 */
	public abstract String getTooltip(List<IAnnotation> annotations);

	protected abstract boolean isCustomPenalty();
}