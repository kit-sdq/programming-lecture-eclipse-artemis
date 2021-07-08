package edu.kit.kastel.sdq.eclipse.grading.api;

import java.io.IOException;
import java.util.Collection;

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
	 * @param annotationID a unique annotation ID.
	 * @param startLine annotation start
	 * @param endLine	annotation end
	 * @param className	unique name of the respective Class to be annotated TODO (maybe) path/ "workspace URI"
	 * 		 instead of class name (other langs etc)
	 * @param customMessage	custom message set by tutor
	 * @param customPenalty This may or may not have an effekt, depending on the MistakeType's PenaltyRule!
	 * E.g. a ThresholdPenaltyRule will not consider custom penalties while a (thinkable) "AggregatedPenaltyThresholdPenaltyRule" would do so.
	 */
	void addAnnotation(int annotationID, IMistakeType mistakeType, int startLine, int endLine, String fullyClassifiedClassName,
			String customMessage, Double customPenalty) throws Exception;

	/**
	 * Calculate a single penalty for a given mistakeType (uses one or many annotations)
	 * @param ratingGroup
	 * @return
	 */
	double calculateCurrentPenaltyForMistakeType(IMistakeType mistakeType) throws IOException ;

	/**
	 * Sum up all penalties of annotations whose mistakeTypes belong to the given rating group.
	 * Takes into account the penaltyLimit of the given ratingGroup, if defined.
	 * @param ratingGroup
	 * @return
	 */
	double calculateCurrentPenaltyForRatingGroup(IRatingGroup ratingGroup) throws IOException ;

	/**
	 *
	 * @return all annotations already made with this AssessmentController.
	 */
	Collection<IAnnotation> getAnnotations();

	/**
	 *
	 * @param className
	 * @return all annotations already made for the given class.
	 */
	Collection<IAnnotation> getAnnotations(String className);

	int getCourseID();

	int getExerciseID();

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

	String getTooltipForMistakeType(IMistakeType mistakeType);

	/**
	 * Modify an existent annotation
	 * @param annatationId	unique annotation identifier
	 * @param customMessage	new custom message
	 * @param customPenalty new custom penalty. This may or may not have an effekt, depending on the MistakeType's PenaltyRule!
	 * E.g. a ThresholdPenaltyRule will not consider custom penalties while a (thinkable) "AggregatedPenaltyThresholdPenaltyRule" would do so.
	 */
	void modifyAnnotation(int annatationId, String customMessage, Double customPenalty);

	/**
	 * Remove an existent annotation
	 * @param annotationId	unique annotation identifier
	 */
	void removeAnnotation(int annotationId);

}
