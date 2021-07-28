package edu.kit.kastel.sdq.eclipse.grading.core.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import edu.kit.kastel.sdq.eclipse.grading.api.model.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.core.config.PenaltyRuleDeserializer;

/**
 * This class is used by an IMistakeType-Instance to calculate penalties.
 */
@JsonDeserialize(using = PenaltyRuleDeserializer.class)
public abstract class PenaltyRule {

	/**
	 * A util function.
	 * @return a map filled with "(annotation, 0.0)".
	 */
	protected static Map<IAnnotation, Double> createZeroedMap(List<IAnnotation> annotations) {
		Map<IAnnotation, Double> zeroedMap = new HashMap<>();
		annotations.forEach(annotation -> zeroedMap.put(annotation, 0.0));
		return zeroedMap;
	}

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

	public abstract String getTooltip(List<IAnnotation> annotations);
}