/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.grading.view.controllers;

import java.util.List;
import java.util.Optional;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.SubmissionFilter;
import edu.kit.kastel.eclipse.common.api.controller.IAssessmentController;
import edu.kit.kastel.eclipse.common.api.controller.IGradingSystemwideController;
import edu.kit.kastel.eclipse.common.api.model.IAnnotation;
import edu.kit.kastel.eclipse.common.api.model.IMistakeType;
import edu.kit.kastel.eclipse.common.api.model.IRatingGroup;
import edu.kit.kastel.eclipse.common.view.controllers.AbstractArtemisViewController;
import edu.kit.kastel.eclipse.common.view.utilities.AssessmentUtilities;
import edu.kit.kastel.eclipse.grading.view.activator.Activator;

/**
 * This class is the controller for the grading view. It creates the marker for
 * the annotations and holds all controller for the backend calls.
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
	public void deleteAnnotation(String id) {
		if (this.assessmentController != null) {
			this.assessmentController.removeAnnotation(id);
		}
	}

	/**
	 * @return all annotations for the current assessment
	 */
	public List<IAnnotation> getAnnotations() {
		return this.assessmentController.getAnnotations();
	}

	/**
	 * @return the mistake types of the current config file
	 */
	public List<IMistakeType> getMistakeTypes() {
		return this.assessmentController.getMistakes();
	}

	public IRatingGroup getRatingGroupById(String id) {
		return this.assessmentController.getRatingGroupById(id);
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

	public String getToolTipForMistakeType(String languageKey, IMistakeType mistakeType) {
		return this.assessmentController.getTooltipForMistakeType(languageKey, mistakeType);
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
	 * Closes the current assessment
	 */
	public void onCloseAssessment() {
		this.systemwideController.closeAssessment();
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
