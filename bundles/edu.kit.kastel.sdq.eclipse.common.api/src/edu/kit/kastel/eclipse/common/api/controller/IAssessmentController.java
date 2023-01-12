/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.api.controller;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import edu.kit.kastel.eclipse.common.api.artemis.IProjectFileNamingStrategy;
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
	 * @param annotationId             a unique annotation ID.
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
	void addAnnotation(String annotationId, IMistakeType mistakeType, int startLine, int endLine, String fullyClassifiedClassName, String customMessage,
			Double customPenalty);

	/**
	 * Modify an existent annotation
	 *
	 * @param annatationId  unique annotation identifier
	 * @param customMessage new custom message
	 * @param customPenalty new custom penalty. This may or may not have an effect,
	 *                      depending on the MistakeType's PenaltyRule
	 */
	void modifyAnnotation(String annatationId, String customMessage, Double customPenalty);

	/**
	 * Remove an existent annotation
	 *
	 * @param annatationId unique annotation identifier
	 */
	void removeAnnotation(String annatationId);

	/**
	 * All annotations already made with this AssessmentController.
	 */
	List<IAnnotation> getAnnotations();

	Optional<IAnnotation> getAnnotationById(String annotationId);

	/**
	 * Deletes the eclipse project this assessment belongs to. Also deletes it on
	 * file system.
	 */
	void deleteEclipseProject(IProjectFileNamingStrategy projectNaming);

	IExercise getExercise();

	ISubmission getSubmission();

	List<IMistakeType> getMistakes();

	IRatingGroup getRatingGroupByDisplayName(String displayName);

	List<IRatingGroup> getRatingGroups();

	String getTooltipForMistakeType(Locale locale, IMistakeType mistakeType);

	/**
	 * Reset annotations by re-locking and reloading from Artemis state. Do so with
	 * {@link IArtemisController#startAssessment(int)}, with this
	 * {@link IAssessmentController#getSubmissionID()} as param.
	 */
	void resetAndRestartAssessment(IProjectFileNamingStrategy projectNaming);

	double getCurrentPenaltyForRatingGroup(IRatingGroup ratingGroup);

	boolean isPositiveFeedbackAllowed();
}
