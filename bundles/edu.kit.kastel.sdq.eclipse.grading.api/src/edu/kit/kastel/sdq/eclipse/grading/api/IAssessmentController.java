package edu.kit.kastel.sdq.eclipse.grading.api;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

/**
 * TODO Zwischenberechnung der mistakes (pro Klasse e.g.) zurück an die GUI.
 *
 */
public interface IAssessmentController {

	/**
	 * Add an annotation to the current assessment.
	 *
	 * TODO
	 * <li> additional param: mistake type!
	 * <li> maybe return int (id)
	 *
	 * @param startLine annotation start
	 * @param endLine	annotation end
	 * @param className	unique name of the respective Class to be annotated TODO (maybe) path/ "workspace URI"
	 * 		 instead of class name (other langs etc)
	 * @param customMessage	custom message set by tutor
	 * @param customPenalty This may or may not have an effekt, depending on the MistakeType's PenaltyRule!
	 * E.g. a ThresholdPenaltyRule will not consider custom penalties while a (thinkable) "AggregatedPenaltyThresholdPenaltyRule" would do so.
	 */
	void addAnnotation(IMistakeType mistakeType, int startLine, int endLine, String fullyClassifiedClassName, String customMessage, Double customPenalty);

	/**
	 *
	 * @param className
	 * @return all annotations already made for the given class.
	 */
	Collection<IAnnotation> getAnnotations(String className);

	/**
	 *
	 * @return all mistake types.
	 * @throws IOException
	 */
	Collection<IMistakeType> getMistakes() throws IOException;

	/**
	 *
	 * @return all rating groups.
	 * @throws IOException
	 */
	Collection<IRatingGroup> getRatingGroups() throws IOException;

	/**
	 * Modify an existent annotation
	 * @param annatationId	unique annotation identifier
	 * @param customMessage	new custom message
	 * @param customPenalty new custom penalty. This may or may not have an effekt, depending on the MistakeType's PenaltyRule!
	 * E.g. a ThresholdPenaltyRule will not consider custom penalties while a (thinkable) "AggregatedPenaltyThresholdPenaltyRule" would do so.
	 */
	void modifyAnnotation(int annatationId, Optional<String> customMessage, Optional<Double> customPenalty);

	/**
	 * Remove an existent annotation
	 * @param annotationId	unique annotation identifier
	 */
	void removeAnnotation(int annotationId);




}
