package gui.controllers;

import java.io.File;
import java.util.Collection;
import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextSelection;

import edu.kit.kastel.sdq.eclipse.grading.api.IArtemisGUIController;
import edu.kit.kastel.sdq.eclipse.grading.api.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission.Filter;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IRatingGroup;
import edu.kit.kastel.sdq.eclipse.grading.core.SystemwideController;
import gui.activator.Activator;
import gui.preferences.PreferenceConstants;
import gui.utilities.AssessmentUtilities;
import observers.ViewAlertObserver;

public class AssessmentViewController {

	private static String CONFIG_PATH;
	private static boolean IS_RELATIVE_PATH;
	private int submissionID;
	private IAssessmentController assessmentController;
	private IArtemisGUIController artemisGUIController;
	private SystemwideController systemwideController;

	public AssessmentViewController() {
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		this.systemwideController = new SystemwideController(
				new File(store.getString(PreferenceConstants.P_ABSOLUTE_CONFIG_PATH)),
				store.getString(PreferenceConstants.P_CONFIG_NAME), store.getString(PreferenceConstants.P_ARTEMIS_URL),
				store.getString(PreferenceConstants.P_ARTEMIS_USER),
				store.getString(PreferenceConstants.P_ARTEMIS_PASSWORD));
		this.artemisGUIController = this.systemwideController.getArtemisGUIController();
		this.systemwideController.getAlertObservable().addAlertObserver(new ViewAlertObserver());
		this.artemisGUIController.getAlertObservable().addAlertObserver(new ViewAlertObserver());
	}

	public void addAssessmentAnnotaion(IMistakeType mistake, String customMessage, Double customPenalty,
			String ratingGroupName) {

		final ITextSelection textSelection = AssessmentUtilities.getTextSelection();
		if (textSelection == null) {
			throw new RuntimeErrorException(null, "No text is selected in the editor");
		}
		final Integer startLine = textSelection.getStartLine();
		final Integer endLine = textSelection.getEndLine();
		final Integer lenght = textSelection.getLength();

		IMarker marker = null;
		try {
			marker = AssessmentUtilities.getCurrentFile().createMarker("gui.assessment.marker");
			marker.setAttribute(IMarker.CHAR_START, AssessmentUtilities.getLineOffSet(startLine));
			marker.setAttribute(IMarker.CHAR_END, AssessmentUtilities.getLineOffSet(startLine) + lenght + 10);
			if (mistake != null) {
				marker.setAttribute("errorTypeDescription", mistake.getMessage());
				marker.setAttribute("errorType", mistake.getButtonName());
			}
			marker.setAttribute("start", startLine + 1);
			marker.setAttribute("end", endLine + 1);
			marker.setAttribute("className", AssessmentUtilities.getClassNameForAnnotation());
			marker.setAttribute("ratingGroup", mistake == null ? ratingGroupName : mistake.getRatingGroupName());
			if (customMessage != null) {
				marker.setAttribute("customMessage", customMessage);
			}
			if (customPenalty != null) {
				marker.setAttribute("customPenalty", customPenalty);
			}
			if (mistake != null) {
				marker.setAttribute(IMarker.MESSAGE, AssessmentUtilities.createMarkerTooltip(startLine + 1, endLine + 1,
						mistake.getButtonName(), mistake.getRatingGroupName(), mistake.getMessage()));
			} else {
				marker.setAttribute(IMarker.MESSAGE, AssessmentUtilities
						.createMarkerTooltipForCustomButton(startLine + 1, endLine + 1, customMessage, customPenalty));
			}
			this.assessmentController.addAnnotation((int) marker.getId(), mistake, startLine + 1, endLine + 1,
					AssessmentUtilities.getPathForAnnotation(), customMessage, customPenalty,
					AssessmentUtilities.getLineOffSet(startLine),
					AssessmentUtilities.getLineOffSet(startLine) + lenght + 10);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void createAssessmentController() {
		this.assessmentController = this.systemwideController.getCurrentAssessmentController();
		this.assessmentController.getAlertObservable().addAlertObserver(new ViewAlertObserver());
	}

	private File createConfigFile() {
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		AssessmentViewController.IS_RELATIVE_PATH = store.getBoolean(PreferenceConstants.P_IS_RELATIVE_CONFIG_PATH);

		AssessmentViewController.CONFIG_PATH = AssessmentViewController.IS_RELATIVE_PATH
				? store.getString(PreferenceConstants.P_RELATIVE_CONFIG_PATH)
				: store.getString(PreferenceConstants.P_ABSOLUTE_CONFIG_PATH);

		return AssessmentViewController.IS_RELATIVE_PATH
				? new File(this.getEclipseWorkspaceRootFile(), AssessmentViewController.CONFIG_PATH)
				: new File(AssessmentViewController.CONFIG_PATH);
	}

	public double getCurrentPenaltyForRatingGroup(IRatingGroup ratingGroup) {
		return this.assessmentController.calculateCurrentPenaltyForRatingGroup(ratingGroup);
	}

	private File getEclipseWorkspaceRootFile() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
	}

	public Collection<IMistakeType> getMistakeTypesForButtonView() {
		return this.assessmentController.getMistakes();
	}

	public Collection<IRatingGroup> getRatingGroups() {
		return this.assessmentController.getRatingGroups();
	}

	public Collection<ICourse> getCourses() {
		return this.artemisGUIController.getCourses();
	}

	private void setConfigPathFromPreferenceStore() {
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		AssessmentViewController.IS_RELATIVE_PATH = store.getBoolean(PreferenceConstants.P_IS_RELATIVE_CONFIG_PATH);

		AssessmentViewController.CONFIG_PATH = AssessmentViewController.IS_RELATIVE_PATH
				? store.getString(PreferenceConstants.P_RELATIVE_CONFIG_PATH)
				: store.getString(PreferenceConstants.P_ABSOLUTE_CONFIG_PATH);
	}

	public int startAssessment(int exerciseID, int courseID) throws Exception {
		Optional<Integer> optionalSubmissonID = this.artemisGUIController.startNextAssessment(exerciseID);
		if (optionalSubmissonID.isPresent()) {
			this.artemisGUIController.downloadExerciseAndSubmission(courseID, exerciseID, optionalSubmissonID.get());
			this.setSubmissionID(optionalSubmissonID.get());
			return optionalSubmissonID.get();
		} else {
			return -1;
		}

	}

	public void startAssessment(int submissionID) {
		this.artemisGUIController.startAssessment(submissionID);
	}

	public void submitAssessment(int submissonId) {
		try {
			this.artemisGUIController.saveAssessment(submissonId, true, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MessageDialog.openInformation(null, "Submitted", "Submitted current assessment for selected exercise.");
	}

	public int getSubmissonID() {
		return this.submissionID;
	}

	public void setSubmissionID(int submissionID) {
		this.submissionID = submissionID;
	}

	public void deleteAnnotation(long id) {
		this.assessmentController.removeAnnotation((int) id);
	}

	public String getToolTipForMistakeType(IMistakeType mistakeType) {
		return this.assessmentController.getTooltipForMistakeType(mistakeType);

	}

	public Collection<IAnnotation> getAnnotations() {
		return this.assessmentController.getAnnotations();
	}

	public void createAnnotationsMarkers() {
		this.getAnnotations().forEach(this::createMarkerForAnnotation);
	}

	// TODO: handle customs annotations
	private void createMarkerForAnnotation(IAnnotation annotation) {

		int startLine = annotation.getStartLine();
		int endLine = annotation.getEndLine();
		IMistakeType mistake = annotation.getMistakeType();
		// String customMessage = annotation.getCustomMessage().orElse("");
		// Double customPenalty = annotation.getCustomPenalty().orElse(0.0);
		IMarker marker = null;
		try {
			marker = AssessmentUtilities.getFile(annotation.getClassFilePath()).createMarker("gui.assessment.marker");
			marker.setAttribute(IMarker.CHAR_START, annotation.getMarkerCharStart());
			marker.setAttribute(IMarker.CHAR_END, annotation.getMarkerCharEnd());
			if (mistake != null) {
				marker.setAttribute("errorTypeDescription", mistake.getMessage());
				marker.setAttribute("errorType", mistake.getButtonName());
			}
			marker.setAttribute("start", startLine + 1);
			marker.setAttribute("end", endLine + 1);
			marker.setAttribute("className", annotation.getClassFilePath());
			marker.setAttribute("ratingGroup", mistake.getRatingGroupName());
			// if (customMessage != null) {
			// marker.setAttribute("customMessage", customMessage);
			// }
			// if (customPenalty != null) {
			// marker.setAttribute("customPenalty", customPenalty);
			// }
			if (mistake != null) {
				marker.setAttribute(IMarker.MESSAGE, AssessmentUtilities.createMarkerTooltip(startLine + 1, endLine + 1,
						mistake.getButtonName(), mistake.getRatingGroupName(), mistake.getMessage()));
			}
			// else {
			// marker.setAttribute(IMarker.MESSAGE, AssessmentUtilities
			// .createMarkerTooltipForCustomButton(startLine + 1, endLine + 1,
			// customMessage, customPenalty));
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reloadAssessment(int courseID, int exerciseID, int submissionID) {
		this.assessmentController.deleteEclipseProject();
		try {
			this.artemisGUIController.downloadExerciseAndSubmission(courseID, exerciseID, submissionID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Optional<Integer> startNextAssessment(int exerciseID, int correctionRound, int courseID) {
		Optional<Integer> optSubmissionID = this.artemisGUIController.startNextAssessment(exerciseID, correctionRound);
		if (optSubmissionID.isPresent()) {
			this.artemisGUIController.downloadExerciseAndSubmission(courseID, exerciseID, optSubmissionID.get());
		}
		return optSubmissionID;
	}

	public void saveAssessment(int submissionID) {
		try {
			this.artemisGUIController.saveAssessment(submissionID, false, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean onStartAssessment() {
		return this.systemwideController.onStartAssessmentButton();
	}

	public Collection<String> getCourseShortNames() {
		return this.artemisGUIController.getCourseShortNames();
	}

	public Collection<String> getExerciseShortNames(String courseName) {
		return this.systemwideController.setCourseIdAndGetExerciseTitles(courseName);
	}

	public void onReloadAssessment() {
		this.systemwideController.onReloadAssessmentButton();
		this.getAnnotations().forEach(this::createMarkerForAnnotation);
	}

	public void onSaveAssessment() {
		this.systemwideController.onSaveAssessmentButton();
	}

	public void onSubmitAssessment() {
		this.systemwideController.onSubmitAssessmentButton();
	}

	public void onStartCorrectionRound1() {
		this.systemwideController.onStartCorrectionRound1Button();
	}

	public void onStartCorrectionRound2() {
		this.systemwideController.onStartCorrectionRound2Button();
	}

	public void setExerciseID(String exerciseShortName) {
		this.systemwideController.setExerciseId(exerciseShortName);
	}

	public Collection<String> getExamShortNames(String courseTitle) {
		return this.artemisGUIController.getExamTitles(courseTitle);
	}

	public Collection<String> getExercisesShortNamesForExam(String examShortName) {
		return this.artemisGUIController.getExerciseShortNamesFromExam(examShortName);
	}

	public Collection<String> getSubmissionsForBacklog() {
		return this.systemwideController.getBegunSubmissionsProjectNames(Filter.ALL);
	}

	public void onLoadAgain() {
		this.systemwideController.onLoadAgainButton();
	}

	public void setAssessedSubmission(String projectName) {
		this.systemwideController.setAssessedSubmissionByProjectName(projectName);
	}

}
