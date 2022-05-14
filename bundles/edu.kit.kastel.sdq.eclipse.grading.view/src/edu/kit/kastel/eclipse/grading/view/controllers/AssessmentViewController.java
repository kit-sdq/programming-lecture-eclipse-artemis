/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.grading.view.controllers;

import java.util.List;
import java.util.Optional;

import edu.kit.kastel.eclipse.common.view.controllers.AbstractArtemisViewController;
import edu.kit.kastel.eclipse.common.view.utilities.AssessmentUtilities;
import edu.kit.kastel.eclipse.grading.view.activator.Activator;
import edu.kit.kastel.sdq.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.SubmissionFilter;
import edu.kit.kastel.sdq.eclipse.common.api.controller.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.common.api.controller.IGradingSystemwideController;
import edu.kit.kastel.sdq.eclipse.common.api.model.IAnnotation;
import edu.kit.kastel.sdq.eclipse.common.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.common.api.model.IRatingGroup;

/**
 * This class is the controller for the grading view. It creates the marker for
 * the annotations and holds all controller for the backend calls.
 *
 * @see {@link ArtemisStudentView}
 */
public class AssessmentViewController extends AbstractArtemisViewController<IGradingSystemwideController> {
	private IAssessmentController assessmentController;

	public AssessmentViewController() {
		super(Activator.getDefault().createNewSystemwideController());
		this.initializeControllersAndObserver();
	}

	/**
	 * creates markers for current annotations in the backend
	 */
	public void createAnnotationsMarkers() {
		this.getAnnotations().stream().filter(
				annotation -> AssessmentUtilities.findPresentAnnotation(annotation, this.systemwideController.getCurrentProjectName(), "assignment/") == null)
				.forEach(annatoation -> {
					try {
						AssessmentUtilities.createMarkerByAnnotation(annatoation, this.systemwideController.getCurrentProjectName(), "assignment/");
					} catch (ArtemisClientException e) {
						this.viewObserver.error("Unable to create marker for annotation", e);
					}
				});
	}

	/**
	 * Deletes an annotation on the backend
	 *
	 * @param id (of the annotation)
	 */
	public void deleteAnnotation(String uuid) {
		if (this.assessmentController != null) {
			this.assessmentController.removeAnnotation(uuid);
		}
	}

	/**
	 * @return all annotations for the current assessment
	 */
	public List<IAnnotation> getAnnotations() {
		return this.assessmentController.getAnnotations();
	}

	/**
	 * @param ratingGroup
	 * @return the current penalty for the given rating group
	 */
	public double getCurrentPenaltyForRatingGroup(IRatingGroup ratingGroup) {
		return this.assessmentController.calculateCurrentPenaltyForRatingGroup(ratingGroup);
	}

	/**
	 * @return the mistake types of the current config file
	 */
	public List<IMistakeType> getMistakeTypes() {
		return this.assessmentController.getMistakes();
	}

	public IRatingGroup getRatingGroupByDisplayName(String displayName) {
		return this.assessmentController.getRatingGroupByDisplayName(displayName);
	}

	/**
	 * @return the rating groups of the current config file
	 */
	public List<IRatingGroup> getRatingGroups() {
		return this.assessmentController.getRatingGroups();
	}

	/**
	 * @return all submissions for the given filter
	 */
	public List<String> getSubmissionsForBacklog(SubmissionFilter filter) {
		return this.systemwideController.getBegunSubmissionsProjectNames(filter);
	}

	/**
	 * @param mistakeType (of the certain button)
	 * @return tooltip for the mistake type button
	 */
	public String getToolTipForMistakeType(IMistakeType mistakeType) {
		return this.assessmentController.getTooltipForMistakeType(mistakeType);

	}

	/**
	 * Loads the selected assessment from the backlog combo
	 */
	public void onLoadAgain() {
		this.systemwideController.loadAgain();
	}

	/**
	 * reloads the current assessment and creates the marker for the given
	 * annotations
	 */
	public void onReloadAssessment() {
		this.systemwideController.reloadAssessment();
	}

	/**
	 * Saves the current assessment
	 */
	public void onSaveAssessment() {
		this.systemwideController.saveAssessment();
	}

	/**
	 * Starts the first correction round of an exam
	 */
	public boolean onStartCorrectionRound1() {
		return this.systemwideController.startCorrectionRound1();
	}

	/**
	 * Starts the second correction round of an exam
	 */
	public boolean onStartCorrectionRound2() {
		return this.systemwideController.startCorrectionRound2();
	}

	/**
	 * Submits the current assessment
	 */
	public void onSubmitAssessment() {
		this.systemwideController.submitAssessment();
	}

	/**
	 * @param projectName (of the selected assessment)
	 */
	public void setAssessedSubmission(String projectName) {
		this.systemwideController.setAssessedSubmissionByProjectName(projectName);
	}

	/**
	 * creates a new assessment controller (if needed) and adds a observer for error
	 * handling
	 */
	public void setCurrentAssessmentController() {
		this.assessmentController = this.systemwideController.getCurrentAssessmentController();
		this.assessmentController.setViewInteractionHandler(this.viewObserver);
	}

	public IAssessmentController getAssessmentController() {
		return assessmentController;
	}

	public boolean isAssessmentStarted() {
		return this.systemwideController.isAssessmentStarted();
	}

	public Optional<IExercise> getSelectedExercise() {
		return this.systemwideController.getSelectedExercise();
	}
}
