package edu.kit.kastel.eclipse.grading.view.controllers;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.ITextSelection;

import edu.kit.kastel.eclipse.grading.view.activator.Activator;
import edu.kit.kastel.eclipse.grading.view.assessment.ArtemisGradingView;
import edu.kit.kastel.eclipse.grading.view.observers.ViewAlertObserver;
import edu.kit.kastel.eclipse.grading.view.utilities.AssessmentUtilities;
import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.SubmissionFilter;
import edu.kit.kastel.sdq.eclipse.grading.api.backendstate.Transition;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.IAlertObserver;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.IArtemisController;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.ISystemwideController;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IRatingGroup;

/**
 * This class is the controller for the grading view. It creates the marker for
 * the annotations and holds all controller for the backend calls.
 *
 * @see {@link ArtemisGradingView}
 *
 */
public class AssessmentViewController {

	private IAssessmentController assessmentController;
	private IArtemisController artemisGUIController;
	private ISystemwideController systemwideController;
	private IAlertObserver alertObserver;

	public AssessmentViewController() {
		Activator.getDefault().createSystemWideController();
		this.systemwideController = Activator.getDefault().getSystemwideController();
		this.initializeControllersAndObserver();
	}

	private void initializeControllersAndObserver() {
		this.alertObserver = new ViewAlertObserver();
		this.artemisGUIController = this.systemwideController.getArtemisGUIController();
		this.systemwideController.addAlertObserver(this.alertObserver);
		this.artemisGUIController.addAlertObserver(this.alertObserver);
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
			this.alertObserver.error("Text selection needed to add a new annotation", null);
			return;
		}
		final int startLine = textSelection.getStartLine() + 1;
		final int endLine = textSelection.getEndLine() + 1;
		final int charOffset = textSelection.getOffset();
		final int lenght = textSelection.getLength();
		final int charStart = charOffset;
		final int charEnd = charOffset + lenght;

		try {
			String uuid = IAnnotation.createUUID();
			IMarker marker = AssessmentUtilities.getCurrentlyOpenFile().createMarker(AssessmentUtilities.MARKER_NAME);
			marker.setAttribute("annotationID", uuid);
			marker.setAttribute(IMarker.CHAR_START, charStart);
			marker.setAttribute(IMarker.CHAR_END, charEnd);
			marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_ERROR_DESCRIPTION, mistake.isCustomPenalty() ? "" : mistake.getMessage());
			marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_ERROR, mistake.getName());
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
				marker.setAttribute(IMarker.MESSAGE, AssessmentUtilities.createMarkerTooltip(startLine, endLine, mistake.getName(),
						mistake.getRatingGroup().getDisplayName(), mistake.getMessage(), null));
			} else {
				marker.setAttribute(IMarker.MESSAGE, AssessmentUtilities.createMarkerTooltipForCustomButton(startLine, endLine, customMessage, customPenalty));
			}
			this.assessmentController.addAnnotation(uuid, mistake, startLine, endLine, AssessmentUtilities.getPathForAnnotation(), customMessage, customPenalty,
					charStart, charEnd);
		} catch (Exception e) {

			/*
			 * Future Work: the error handling should be more specific (maybe for each
			 * setAttribute(...)) without getting a too messy code
			 */
			e.printStackTrace();
			this.alertObserver.error("Unable to create marker for annotation", e);
		}

	}

	/**
	 * creates markers for current annotations in the backend
	 */
	public void createAnnotationsMarkers() {
		this.getAnnotations().forEach(this::createMarkerForAnnotation);
	}

	private void createMarkerForAnnotation(IAnnotation annotation) {

		int startLine = annotation.getStartLine();
		int endLine = annotation.getEndLine();
		IMistakeType mistake = annotation.getMistakeType();
		String customMessage = annotation.getCustomMessage().orElse(null);
		String customPenalty = annotation.getCustomPenalty().map(String::valueOf).orElse(null);
		try {
			IMarker marker = AssessmentUtilities.getFile(annotation.getClassFilePath(), this.systemwideController.getCurrentProjectName())
					.createMarker(AssessmentUtilities.MARKER_NAME);
			marker.setAttribute("annotationID", annotation.getUUID());
			marker.setAttribute(IMarker.CHAR_START, annotation.getMarkerCharStart());
			marker.setAttribute(IMarker.CHAR_END, annotation.getMarkerCharEnd());

			marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_START, startLine);
			marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_END, endLine);
			marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_CLASS_NAME, annotation.getClassFilePath());
			if (customMessage != null) {
				marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_CUSTOM_MESSAGE, customMessage);
			}
			if (customPenalty != null) {
				marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_CUSTOM_PENALTY, customPenalty);
			}
			if (mistake != null) {
				marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_ERROR_DESCRIPTION, mistake.getMessage());
				marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_ERROR, mistake.getName());
				marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_RATING_GROUP, mistake.getRatingGroup().getDisplayName());
				if (!"Custom Penalty".equals(mistake.getName())) {
					marker.setAttribute(IMarker.MESSAGE, AssessmentUtilities.createMarkerTooltip(startLine, endLine, mistake.getName(),
							mistake.getRatingGroup().getDisplayName(), mistake.getMessage(), annotation.getClassFilePath()));
				} else {
					marker.setAttribute(IMarker.MESSAGE,
							AssessmentUtilities.createMarkerTooltipForCustomButton(startLine, endLine, customMessage, Double.parseDouble(customPenalty)));
				}
			}

		} catch (Exception e) {
			this.alertObserver.error("Unable to create marker for given annotation:" + annotation.toString(), e);
		}
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
	 * @return all courses available at artemis
	 */
	public List<ICourse> getCourses() {
		return this.artemisGUIController.getCourses();
	}

	/**
	 * @return the name of all courses
	 */
	public List<String> getCourseShortNames() {
		return this.artemisGUIController.getCourseShortNames();
	}

	/**
	 * @param ratingGroup
	 * @return the current penalty for the given rating group
	 */
	public double getCurrentPenaltyForRatingGroup(IRatingGroup ratingGroup) {
		return this.assessmentController.calculateCurrentPenaltyForRatingGroup(ratingGroup);
	}

	/**
	 * @param courseTitle (of the selected course in the combo)
	 * @return all exams of the given course
	 */
	public List<String> getExamShortNames(String courseTitle) {
		return this.artemisGUIController.getExamTitles(courseTitle);
	}

	/**
	 * @param courseName (selected course in the combo)
	 * @return all exercises from the given course
	 */
	public List<String> getExerciseShortNames(String courseName) {
		try {
			return this.systemwideController.setCourseIdAndGetExerciseShortNames(courseName);
		} catch (ArtemisClientException e) {
			this.alertObserver.error(e.getMessage(), e);
			return List.of();
		}
	}

	/**
	 * @param examShortName (of the selected exam in the combo)
	 * @return all exercises of the given exam
	 */
	public List<String> getExercisesShortNamesForExam(String examShortName) {
		return this.artemisGUIController.getExerciseShortNamesFromExam(examShortName);
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
		this.assessmentController.addAlertObserver(this.alertObserver);
	}

	/**
	 * Sets the exercise ID of the selected exercise
	 *
	 * @param exerciseShortName (of the selected exercise in the combo)
	 */
	public void setExerciseID(String exerciseShortName) {
		try {
			this.systemwideController.setExerciseId(exerciseShortName);
		} catch (ArtemisClientException e) {
			this.alertObserver.error(e.getMessage(), e);
		}
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
