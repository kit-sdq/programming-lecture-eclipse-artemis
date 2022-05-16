/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.api.controller;

import java.util.List;
import java.util.Optional;

import edu.kit.kastel.eclipse.common.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ICourse;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ISubmission;
import edu.kit.kastel.eclipse.common.api.model.IAnnotation;
import edu.kit.kastel.eclipse.common.api.model.IMistakeType;
import edu.kit.kastel.eclipse.common.api.model.IRatingGroup;

/**
 * The assessmentController handles everything that has to do with the
 * assessment of a single submission.
 */
public interface IAssessmentController extends IController {

	/**
	 * Add an annotation to the current assessment.<br>
	 * <b>e.g. a ThresholdPenaltyRule will not consider custom penalties</b>
	 *
	 * @param annotationUUID           a unique annotation ID.
	 * @param mistakeType              the mistake type
	 * @param startLine                annotation start
	 * @param endLine                  annotation end
	 * @param fullyClassifiedClassName fully classified class name of the respective
	 *                                 class to be annotated
	 * @param customMessage            custom message set by tutor
	 * @param customPenalty            This may or may not have an effect, depending
	 *                                 on the MistakeType's PenaltyRule!
	 *
	 *
	 */
	void addAnnotation(String annotationUUID, IMistakeType mistakeType, int startLine, int endLine, String fullyClassifiedClassName, String customMessage,
			Double customPenalty);

	/**
	 * Modify an existent annotation
	 *
	 * @param annatationUUID unique annotation identifier
	 * @param customMessage  new custom message
	 * @param customPenalty  new custom penalty. This may or may not have an effect,
	 *                       depending on the MistakeType's PenaltyRule
	 */
	void modifyAnnotation(String annatationUUID, String customMessage, Double customPenalty);

	/**
	 * Remove an existent annotation
	 *
	 * @param annatationUUID unique annotation identifier
	 */
	void removeAnnotation(String annatationUUID);

	/**
	 * All annotations already made with this AssessmentController.
	 */
	List<IAnnotation> getAnnotations();

	Optional<IAnnotation> getAnnotationByUUID(String uuid);

	/**
	 * All annotations already made for the given class.
	 */
	List<IAnnotation> getAnnotations(String className);

	/**
	 * Calculate a single penalty for a given mistakeType (uses one or many
	 * annotations)
	 */
	double calculateCurrentPenaltyForMistakeType(IMistakeType mistakeType);

	/**
	 * Sum up all penalties of annotations whose mistakeTypes belong to the given
	 * rating group. Takes into account the penaltyLimit of the given ratingGroup,
	 * if defined.
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

	List<IMistakeType> getMistakes();

	IRatingGroup getRatingGroupByDisplayName(String displayName);

	IRatingGroup getRatingGroupByShortName(String shortName);

	List<IRatingGroup> getRatingGroups();

	String getTooltipForMistakeType(IMistakeType mistakeType);

	/**
	 * Reset annotations by re-locking and reloading from Artemis state. Do so with
	 * {@link IArtemisController#startAssessment(int)}, with this
	 * {@link IAssessmentController#getSubmissionID()} as param.
	 */
	void resetAndRestartAssessment(IProjectFileNamingStrategy projectNaming);

	IViewInteraction getViewInteraction();
}
