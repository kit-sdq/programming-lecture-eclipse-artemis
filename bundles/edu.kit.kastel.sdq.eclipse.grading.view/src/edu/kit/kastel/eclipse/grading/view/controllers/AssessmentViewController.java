/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.grading.view.controllers;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.ITextSelection;

import edu.kit.kastel.eclipse.common.view.controllers.AbstractArtemisViewController;
import edu.kit.kastel.eclipse.common.view.utilities.AssessmentUtilities;
import edu.kit.kastel.eclipse.grading.view.activator.Activator;
import edu.kit.kastel.sdq.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.SubmissionFilter;
import edu.kit.kastel.sdq.eclipse.common.api.backendstate.Transition;
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
	 * This method creates a marker for the annotation and add a new annotation to
	 * the backlog
	 *
	 * @param mistake         (the mistake type of the new annotation)
	 * @param customMessage   (for custom mistake type, else null)
	 * @param customPenalty   (for custom mistake, else null)
	 * @param ratingGroupName (the name of the rating group of the new annotation)
	 */
	public void addAssessmentAnnotation(IMistakeType mistake, String customMessage, Double customPenalty, String ratingGroupName) {
		final ITextSelection textSelection = AssessmentUtilities.getTextSelection();
		if (textSelection == null) {
			this.viewObserver.error("Text selection needed to add a new annotation", null);
			return;
		}
		final int startLine = textSelection.getStartLine();
		final int endLine = textSelection.getEndLine();
		final IFile file = AssessmentUtilities.getCurrentlyOpenFile();
		final String projectName = file.getProject().getName();
		final String srcPath = "assignment/src";
		final String className = file.getFullPath().makeRelative().toString().split("src", 2)[1];

		try {
			String uuid = IAnnotation.createUUID();
			IMarker marker = AssessmentUtilities.getCurrentlyOpenFile().createMarker(AssessmentUtilities.MARKER_NAME);
			marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_ANNOTATION_ID, uuid);
			AssessmentUtilities.setCharPositionsByLine(marker, projectName, srcPath, className, startLine, endLine);

			marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_ERROR_DESCRIPTION, mistake.isCustomPenalty() ? "" : mistake.getMessage());
			marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_ERROR, mistake.getButtonText());
			marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_START, startLine);
			marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_END, endLine);
			marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_CLASS_NAME, AssessmentUtilities.getClassNameForAnnotation());
			marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_RATING_GROUP, mistake.getRatingGroup().getDisplayName());
			if (customMessage != null) {
				marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_CUSTOM_MESSAGE, customMessage);
			}
			if (customPenalty != null) {
				marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_CUSTOM_PENALTY, customPenalty.toString());
			}
			if (!mistake.isCustomPenalty()) {
				marker.setAttribute(IMarker.MESSAGE, AssessmentUtilities.createMarkerTooltip(startLine, endLine, mistake.getButtonText(),
						mistake.getRatingGroup().getDisplayName(), AssessmentUtilities.formatCustomPenaltyMessage(mistake, customMessage), null));
			} else {
				marker.setAttribute(IMarker.MESSAGE, AssessmentUtilities.createMarkerTooltipForCustomButton(startLine, endLine, customMessage, customPenalty));
			}
			this.assessmentController.addAnnotation(uuid, mistake, startLine, endLine, AssessmentUtilities.getPathForAnnotation(), customMessage,
					customPenalty);
		} catch (Exception e) {

			/*
			 * Future Work: the error handling should be more specific (maybe for each
			 * setAttribute(...)) without getting a too messy code
			 */
			this.viewObserver.error("Unable to create marker for annotation: " + e.getMessage(), e);
		}

	}

	/**
	 * creates markers for current annotations in the backend
	 */
	public void createAnnotationsMarkers() {
		this.getAnnotations().stream().filter(
				annotation -> AssessmentUtilities.findPresentAnnotation(annotation, this.systemwideController.getCurrentProjectName(), "assignment/") == null)
				.forEach(annatoation -> {
					try {
						AssessmentUtilities.createMarkerForAnnotation(annatoation, this.systemwideController.getCurrentProjectName(), "assignment/");
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
	 * @return true, if a new assessment is started, else false
	 */
	public boolean onStartAssessment() {
		return this.systemwideController.startAssessment();
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

	/**
	 * Request all possible transitions of the current state
	 *
	 * @return the possible transitions
	 */
	public Set<Transition> getPossiblyTransitions() {
		return this.systemwideController.getCurrentlyPossibleTransitions();
	}
}
