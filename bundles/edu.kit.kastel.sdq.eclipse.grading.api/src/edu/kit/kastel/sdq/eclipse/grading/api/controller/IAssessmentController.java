package edu.kit.kastel.sdq.eclipse.grading.api.controller;

import java.util.List;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IRatingGroup;

/**
 * The assessmentController handles everything that has to do with the
 * assessment of a single submission.
 */
public interface IAssessmentController extends IController {

	/**
	 * Add an annotation to the current assessment.
	 *
	 * @param annotationUUID           a unique annotation ID.
	 * @param mistakeType
	 * @param startLine                annotation start
	 * @param endLine                  annotation end
	 * @param fullyClassifiedClassName fully classified class name of the respective
	 *                                 Class to be annotated
	 * @param customMessage            custom message set by tutor
	 * @param customPenalty            This may or may not have an effect, depending
	 *                                 on the MistakeType's PenaltyRule!
	 * @param marker_char_start        additional encoding of the start (counts from
	 *                                 file start, eclipse GUI requires this)
	 * @param marker_char_end          additional encoding of the start (counts from
	 *                                 file start, eclipse GUI requires this) E.g. a
	 *                                 ThresholdPenaltyRule will not consider custom
	 *                                 penalties while a (thinkable)
	 *                                 "AggregatedPenaltyThresholdPenaltyRule" would
	 *                                 do so.
	 */
	void addAnnotation(String annotationUUID, IMistakeType mistakeType, int startLine, int endLine, String fullyClassifiedClassName, String customMessage,
			Double customPenalty, int markerCharStart, int markerCharEnd);

	/**
	 * Modify an existent annotation
	 *
	 * @param annatationUUID unique annotation identifier
	 * @param customMessage  new custom message
	 * @param customPenalty  new custom penalty. This may or may not have an effekt,
	 *                       depending on the MistakeType's PenaltyRule! E.g. a
	 *                       ThresholdPenaltyRule will not consider custom penalties
	 *                       while a (thinkable)
	 *                       "AggregatedPenaltyThresholdPenaltyRule" would do so.
	 */
	void modifyAnnotation(String annatationUUID, String customMessage, Double customPenalty);

	/**
	 * Remove an existent annotation
	 *
	 * @param annatationUUID unique annotation identifier
	 */
	void removeAnnotation(String annatationUUID);

	/**
	 *
	 * @return all annotations already made with this AssessmentController.
	 */
	List<IAnnotation> getAnnotations();

	/**
	 *
	 * @param className
	 * @return all annotations already made for the given class.
	 */
	List<IAnnotation> getAnnotations(String className);

	/**
	 * Calculate a single penalty for a given mistakeType (uses one or many
	 * annotations)
	 *
	 * @param ratingGroup
	 * @return
	 */
	double calculateCurrentPenaltyForMistakeType(IMistakeType mistakeType);

	/**
	 * Sum up all penalties of annotations whose mistakeTypes belong to the given
	 * rating group. Takes into account the penaltyLimit of the given ratingGroup,
	 * if defined.
	 *
	 * @param ratingGroup
	 * @return
	 */
	double calculateCurrentPenaltyForRatingGroup(IRatingGroup ratingGroup);

	/**
	 * Deletes the eclipse project this assessment belongs to. Also deletes it on
	 * file system.
	 */
	void deleteEclipseProject(IProjectFileNamingStrategy projectNaming);

	ICourse getCourse();

	IExercise getExercise();

	ISubmission getSubmission();

	/**
	 *
	 * @return all mistake types.
	 */
	List<IMistakeType> getMistakes();

	IRatingGroup getRatingGroupByDisplayName(String displayName);

	IRatingGroup getRatingGroupByShortName(String shortName);

	/**
	 *
	 * @return all rating groups.
	 */
	List<IRatingGroup> getRatingGroups();

	// TODO Get Tooltip has to be updated on events of mistake types ..
	String getTooltipForMistakeType(IMistakeType mistakeType);

	/**
	 * Reset annotations by re-locking and reloading from Artemis state. Do so with
	 * {@link IArtemisController#startAssessment(int)}, with this
	 * {@link IAssessmentController#getSubmissionID()} as param.
	 */
	void resetAndRestartAssessment(IProjectFileNamingStrategy projectNaming);
}
