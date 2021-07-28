package edu.kit.kastel.sdq.eclipse.grading.core;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import edu.kit.kastel.sdq.eclipse.grading.api.IArtemisGUIController;
import edu.kit.kastel.sdq.eclipse.grading.api.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.ISystemwideController;
import edu.kit.kastel.sdq.eclipse.grading.api.alerts.IAlertObservable;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission.Filter;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.DefaultProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.WorkspaceUtil;
import edu.kit.kastel.sdq.eclipse.grading.core.config.ConfigDao;
import edu.kit.kastel.sdq.eclipse.grading.core.config.JsonFileConfigDao;

public class SystemwideController implements ISystemwideController {

	private final Map<Integer, IAssessmentController> assessmentControllers;
	private final IArtemisGUIController artemisGUIController;

	private ConfigDao configDao;

	private Integer courseID;
	private Integer exerciseID;
	private Integer submissionID;
	private String exerciseConfigName;

	private AlertObservable alertObservable;

	private IProjectFileNamingStrategy projectFileNamingStrategy;

	public SystemwideController(final File configFile, final String exerciseConfigName, final String artemisHost, final String username, final String password) {
		this.setConfigFile(configFile);
		this.exerciseConfigName = exerciseConfigName;
		this.assessmentControllers = new HashMap<>();

		this.alertObservable = new AlertObservable();

		this.artemisGUIController = new ArtemisGUIController(this, artemisHost, username, password);

		this.projectFileNamingStrategy = new DefaultProjectFileNamingStrategy();
	}

	@Override
	public IAlertObservable getAlertObservable() {
		return this.alertObservable;
	}

	@Override
	public IArtemisGUIController getArtemisGUIController() {
		return this.artemisGUIController;
	}

	private IAssessmentController getAssessmentController(int submissionID, String exerciseConfigName, int courseID,
			int exerciseID) {
		this.assessmentControllers.putIfAbsent(
				submissionID,
				new AssessmentController(this, courseID, exerciseID, submissionID, exerciseConfigName));
		return this.assessmentControllers.get(submissionID);
	}


	private Collection<ISubmission> getBegunSubmissions(ISubmission.Filter submissionFilter) {
		if (this.nullCheckMembersAndNotify(true, true, true)) return List.of();

		return this.getArtemisGUIController().getBegunSubmissions(this.exerciseID).stream()
				.filter(submissionFilter.getFilterPredicate())
				.collect(Collectors.toList());
	}

	@Override
	public Collection<String> getBegunSubmissionsProjectNames(ISubmission.Filter submissionFilter) {
		return this.getBegunSubmissions(submissionFilter)
				.stream()
				.map(submission -> this.projectFileNamingStrategy.getProjectFileInWorkspace(
						WorkspaceUtil.getWorkspaceFile(),
						this.getCurrentExercise(),
						submission).getName())
				.collect(Collectors.toList());
	}

	/**
	 *
	 * @return this system's configDao.
	 */
	protected ConfigDao getConfigDao() {
		return this.configDao;
	}

	@Override
	public IAssessmentController getCurrentAssessmentController() {
		if (this.nullCheckMembersAndNotify(true, true, true)) return null;
		return this.getAssessmentController(this.submissionID, this.exerciseConfigName, this.courseID, this.exerciseID);
	}

	private IExercise getCurrentExercise() {
		if (this.nullCheckMembersAndNotify(true, true, false)) return null;
		return this.getArtemisGUIController()
				.getExerciseFromCourses(this.getArtemisGUIController().getCourses(), this.courseID, this.exerciseID);
	}

	/**
	 *
	 * @return true if at least one of those three is null
	 */
	private boolean nullCheckMembersAndNotify(boolean checkCourseID, boolean checkExerciseID, boolean checkSubmissionID) {
		final StringBuilder alertMessageBuilder = new StringBuilder("[");
		boolean somethingNull = false;
		if (checkCourseID && this.courseID == null) {
			alertMessageBuilder.append("Course is not set ");
			somethingNull = true;
		}
		if (checkExerciseID && this.exerciseID == null) {
			alertMessageBuilder.append("Exercise is not set ");
			somethingNull = true;
		}
		if (checkSubmissionID && this.submissionID == null) {
			alertMessageBuilder.append("Submission is not set ");
			somethingNull = true;
		}
		if (somethingNull) {
			this.alertObservable.warn(alertMessageBuilder.append("]").toString());
		}
		return somethingNull;
	}

	@Override
	public void onLoadAgainButton() {
		if (this.nullCheckMembersAndNotify(true, true, true)) return;
		this.getArtemisGUIController().startAssessment(this.submissionID);
		this.getArtemisGUIController().downloadExerciseAndSubmission(this.courseID, this.exerciseID, this.submissionID);
	}

	@Override
	public void onReloadAssessmentButton() {
		if (this.nullCheckMembersAndNotify(true, true, true)) return;

		this.getCurrentAssessmentController().deleteEclipseProject();

		this.getArtemisGUIController().startAssessment(this.submissionID);
		this.getArtemisGUIController().downloadExerciseAndSubmission(this.courseID, this.exerciseID, this.submissionID);

		this.getCurrentAssessmentController().resetAndReload();
	}

	@Override
	public void onSaveAssessmentButton() {
		if (this.nullCheckMembersAndNotify(true, true, true)) return;
		this.artemisGUIController.saveAssessment(this.submissionID, false, false);
	}

	@Override
	public boolean onStartAssessmentButton() {
		return this.startAssessment(0);
	}

	@Override
	public boolean onStartCorrectionRound1Button() {
		return this.startAssessment(0);
	}

	@Override
	public boolean onStartCorrectionRound2Button() {
		return this.startAssessment(1);
	}

	@Override
	public void onSubmitAssessmentButton() {
		if (this.nullCheckMembersAndNotify(true, true, true)) return;
		if (this.artemisGUIController.saveAssessment(this.submissionID, true, false)) {
			this.getCurrentAssessmentController().deleteEclipseProject();
			this.submissionID = null;
		}
	}

	public void onZeroPointsForAssessment() {
		if (this.artemisGUIController.saveAssessment(this.submissionID, true, true)) {
			this.getCurrentAssessmentController().deleteEclipseProject();
			this.submissionID = null;
		}
	}

	@Override
	public void setAssessedSubmissionByProjectName(String projectName) {
		this.getBegunSubmissions(Filter.ALL).forEach(submission -> {
			String currentProjectName = this.projectFileNamingStrategy.getProjectFileInWorkspace(
					WorkspaceUtil.getWorkspaceFile(),
					this.getCurrentExercise(),
					submission).getName();
			if (currentProjectName.equals(projectName)) {
				this.submissionID = submission.getSubmissionId();
				return;
			}
		});

		this.alertObservable.error("Assessed submission with projectName=\"" + projectName +  "\" not found!", null);
	}

	@Override
	public void setConfigFile(File newConfigFile) {
		this.configDao = new JsonFileConfigDao(newConfigFile);
	}

	@Override
	public Collection<String> setCourseIdAndGetExerciseTitles(final String courseShortName) {
		for (ICourse course : this.getArtemisGUIController().getCourses()) {
			if (course.getShortName().equals(courseShortName)) {
				this.courseID = course.getCourseId();
				return course.getExercises().stream().map(IExercise::getShortName).collect(Collectors.toList());
			}
		}
		this.alertObservable.error("No Course with the given shortName \"" + courseShortName + "\" found.", null);
		return List.of();
	}

	@Override
	public void setExerciseId(final String exerciseShortName) {
		for (ICourse course : this.getArtemisGUIController().getCourses()) {
			for (IExercise exercise : course.getExercises()) {
				if (exercise.getShortName().equals(exerciseShortName)) {
					this.exerciseID = exercise.getExerciseId();
					return;
				}
			}
		}
		this.alertObservable.error("No Exercise with the given shortName \"" + exerciseShortName + "\" found.", null);
	}

	private boolean startAssessment(int correctionRound) {
		if (this.nullCheckMembersAndNotify(true, true, false)) return false;
		Optional<Integer> optionalSubmissionID = this.getArtemisGUIController().startNextAssessment(this.exerciseID, correctionRound);
		if (optionalSubmissionID.isEmpty()) {
			return false;
		}
		this.submissionID = optionalSubmissionID.get();
		this.getArtemisGUIController().downloadExerciseAndSubmission(this.courseID, this.exerciseID, this.submissionID);
		return true;

	}
}
