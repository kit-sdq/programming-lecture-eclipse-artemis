package edu.kit.kastel.sdq.eclipse.grading.core.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import edu.kit.kastel.sdq.eclipse.grading.api.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.core.config.PenaltyRuleDeserializer;

/**
 * This class is used by an IMistakeType-Instance to calculate penalties.
 * There are two kinds of PenaltyTypes which subclasses might have:
 * <li> collective: All Annotations are considered for the result calculation and produce "one result", which is tagged to one Annotation.
 * <li> independent: The Calculation is done Annotation by Annotation, there is a result for each 
 * 
 * TODO might well be an interface, decide!
 *
 */
@JsonDeserialize(using = PenaltyRuleDeserializer.class)
public abstract class PenaltyRule {
	
	/**
	 * Calculate the penalty these Annotations add to the result.
	 * 
	 * @param annotations the annotations on which the calculation is based. List is used because in the "collective penalty" case, subclasses
	 * might want to apply the penalty to the first element, for example. Thus, the given list should be ordered in a way the caller sees fit.
	 * @return a mapping Annotations to annotation-wise penalty. That return type might seem strange, but makes the whole thing useable as both
	 * <li> collective and
	 * <li> independent 
	 * penalties. Since Artemis only knows independent penalties, this simplifies the translation, but makes it bound to this class...
	 * TODO if independent penalties are not desired, the return type can be changed to Double
	 */
	public abstract Map<IAnnotation, Double> calculatePenalty(List<IAnnotation> annotations);
	
	/**
	 * Returns whether the penalty type considers other lines in the result or not.
	 * 
	 * This might seem anti-OO but that way, result handling gets a lot easier:
	 * With a PenaltyResult Superclass (and two Subclasses CollectivePenaltyResult and IndependentPenaltyResult), 
	 * we'd always need to check instanceOf (and maybe even add more?) or need to add a "toMap" method which annihilates the "OO beauty".
	 * 
	 * @return whether the calculatePenalty will consider all annotations to calculate the results (collective) or only one (individual) 
	 */
	public abstract boolean isCollectiveType();
	
	/**
	 * A util function.
	 * @return a map filled with "(annotation, 0.0)".
	 */
	protected static Map<IAnnotation, Double> createZeroedMap(List<IAnnotation> annotations) {
		Map<IAnnotation, Double> zeroedMap = new HashMap<>();
		annotations.forEach(annotation -> zeroedMap.put(annotation, 0.0));
		return zeroedMap;
	}

	public abstract String getDisplayName();

	public abstract String getShortName();

}