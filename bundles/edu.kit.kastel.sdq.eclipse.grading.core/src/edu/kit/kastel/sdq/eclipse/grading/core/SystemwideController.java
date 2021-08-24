package edu.kit.kastel.sdq.eclipse.grading.core;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.jface.preference.IPreferenceStore;

import edu.kit.kastel.sdq.eclipse.grading.api.IArtemisController;
import edu.kit.kastel.sdq.eclipse.grading.api.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.ISystemwideController;
import edu.kit.kastel.sdq.eclipse.grading.api.PreferenceConstants;
import edu.kit.kastel.sdq.eclipse.grading.api.alerts.IAlertObservable;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExerciseGroup;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission.Filter;
import edu.kit.kastel.sdq.eclipse.grading.api.backendstate.Transition;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.DefaultProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.WorkspaceUtil;
import edu.kit.kastel.sdq.eclipse.grading.core.config.ConfigDao;
import edu.kit.kastel.sdq.eclipse.grading.core.config.JsonFileConfigDao;

public class SystemwideController implements ISystemwideController {

	private final Map<Integer, IAssessmentController> assessmentControllers;
	private final IArtemisController artemisGUIController;

	private ConfigDao configDao;

	private Integer courseID;
	private Integer exerciseID;
	private Integer submissionID;

	private AlertObservable alertObservable;

	private IProjectFileNamingStrategy projectFileNamingStrategy;

	private BackendStateMachine backendStateMachine;

	public SystemwideController(final File configFile, final String artemisHost, final String username, final String password) {
		this.setConfigFile(configFile);
		this.assessmentControllers = new HashMap<>();
		this.alertObservable = new AlertObservable();

		this.artemisGUIController = new ArtemisController(this, artemisHost, username, password);
		this.projectFileNamingStrategy = new DefaultProjectFileNamingStrategy();		//TODO durch das ganze projekt durchreichen! NUR hier instanziieren!
		this.backendStateMachine = new BackendStateMachine();
	}

	public SystemwideController(final IPreferenceStore preferenceStore) {
		this(new File(
				preferenceStore.getString(PreferenceConstants.ABSOLUTE_CONFIG_PATH)),
				preferenceStore.getString(PreferenceConstants.ARTEMIS_URL),
				preferenceStore.getString(PreferenceConstants.ARTEMIS_USER),
				preferenceStore.getString(PreferenceConstants.ARTEMIS_PASSWORD));
	}

	private boolean checkTransitionNotAllowedAndNotify(Transition transition) {
		try {
			this.backendStateMachine.applyTransition(transition);
		} catch (NoTransitionException e) {
			this.alertObservable.error(e.getMessage(), e);
			return true;
		}
		return false;
	}

	@Override
	public IAlertObservable getAlertObservable() {
		return this.alertObservable;
	}

	@Override
	public IArtemisController getArtemisGUIController() {
		return this.artemisGUIController;
	}


	private IAssessmentController getAssessmentController(int submissionID, int courseID,
			int exerciseID) {
		//not equivalent to putIfAbsent!
		this.assessmentControllers.computeIfAbsent(submissionID, submissionIDParam -> new AssessmentController(this, courseID, exerciseID, submissionID));
		return this.assessmentControllers.get(submissionID);
	}

	private Collection<ISubmission> getBegunSubmissions(ISubmission.Filter submissionFilter) {
		if (this.nullCheckMembersAndNotify(true, true, false)) return List.of();

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
		return this.getAssessmentController(this.submissionID, this.courseID, this.exerciseID);
	}

	private IExercise getCurrentExercise() {
		if (this.nullCheckMembersAndNotify(true, true, false)) return null;
		return this.getArtemisGUIController()
				.getExerciseFromCourses(this.getArtemisGUIController().getCourses(), this.courseID, this.exerciseID);
	}

	@Override
	public Set<Transition> getCurrentlyPossibleTransitions() {
		return this.backendStateMachine.getCurrentlyPossibleTransitions();
	}

	@Override
	public String getCurrentProjectName() {
		if (this.nullCheckMembersAndNotify(true, true, true)) return null;

		return this.projectFileNamingStrategy.getProjectFileInWorkspace(
				WorkspaceUtil.getWorkspaceFile(),
				this.getCurrentExercise(),
				this.getCurrentSubmission()).getName();
	}

	private ISubmission getCurrentSubmission() {
		if (this.nullCheckMembersAndNotify(true, true, true)) return null;
		return this.getArtemisGUIController().getSubmissionFromExercise(this.getCurrentExercise(), this.submissionID);
	}

	@Override
	public void loadAgain() {
		if (this.checkTransitionNotAllowedAndNotify(Transition.LOAD_AGAIN)) return;
		if (this.nullCheckMembersAndNotify(true, true, true)) return;

		this.getArtemisGUIController().startAssessment(this.submissionID);
		this.getArtemisGUIController().downloadExerciseAndSubmission(this.courseID, this.exerciseID, this.submissionID);
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
	public void onZeroPointsForAssessment() {
		if (this.checkTransitionNotAllowedAndNotify(Transition.ON_ZERO_POINTS_FOR_ASSESSMENT)) return;

		if (this.artemisGUIController.saveAssessment(this.submissionID, true, true)) {
			this.getCurrentAssessmentController().deleteEclipseProject();
			this.submissionID = null;
		}
	}

	@Override
	public void reloadAssessment() {
		if (this.checkTransitionNotAllowedAndNotify(Transition.RELOAD_ASSESSMENT)) return;
		if (this.nullCheckMembersAndNotify(true, true, true)) return;

		this.getCurrentAssessmentController().resetAndRestartAssessment();
	}

	@Override
	public void saveAssessment() {
		if (this.checkTransitionNotAllowedAndNotify(Transition.SAVE_ASSESSMENT)) return;
		if (this.nullCheckMembersAndNotify(true, true, true)) return;

		this.artemisGUIController.saveAssessment(this.submissionID, false, false);
	}

	@Override
	public void setAssessedSubmissionByProjectName(String projectName) {
		if (this.checkTransitionNotAllowedAndNotify(Transition.SET_ASSESSED_SUBMISSION_BY_PROJECT_NAME)) return;

		boolean[] found = {false};
		this.getBegunSubmissions(Filter.ALL).forEach(submission -> {
			String currentProjectName = this.projectFileNamingStrategy.getProjectFileInWorkspace(
					WorkspaceUtil.getWorkspaceFile(),
					this.getCurrentExercise(),
					submission).getName();

			if (currentProjectName.equals(projectName)) {
				this.submissionID = submission.getSubmissionId();
				found[0] = true;
			}
		});
		if (found[0]) {
			return;
		}
		this.alertObservable.error("Assessed submission with projectName=\"" + projectName +  "\" not found!", null);
	}

	@Override
	public void setConfigFile(File newConfigFile) {
		this.configDao = new JsonFileConfigDao(newConfigFile);
	}

	@Override
	public Collection<String> setCourseIdAndGetExerciseShortNames(final String courseShortName) {
		if (this.checkTransitionNotAllowedAndNotify(Transition.SET_COURSE_ID_AND_GET_EXERCISE_SHORT_NAMES)) return List.of();

		for (ICourse course : this.getArtemisGUIController().getCourses()) {
			if (course.getShortName().equals(courseShortName)) {
				this.courseID = course.getCourseId();
				return course.getExercises().stream()
						.map(IExercise::getShortName)
						.collect(Collectors.toList());
			}
		}
		this.alertObservable.error("No Course with the given shortName \"" + courseShortName + "\" found.", null);
		return List.of();
	}

	@Override
	public void setExerciseId(final String exerciseShortName) {
		if (this.checkTransitionNotAllowedAndNotify(Transition.SET_EXERCISE_ID)) return;

		for (ICourse course : this.getArtemisGUIController().getCourses()) {
			//normal exercises
			for (IExercise exercise : course.getExercises()) {
				if (exercise.getShortName().equals(exerciseShortName)) {
					this.exerciseID = exercise.getExerciseId();
					return;
				}
			}
			//exam exercises
			for (IExam exam: course.getExams()) {
				for (IExerciseGroup exerciseGroup: exam.getExerciseGroups()) {
					for ( IExercise exercise : exerciseGroup.getExercises()) {
						if (exercise.getShortName().equals(exerciseShortName)) {
							this.exerciseID = exercise.getExerciseId();
							return;
						}
					}
				}
			}
		}

		this.alertObservable.error("No Exercise with the given shortName \"" + exerciseShortName + "\" found.", null);
	}

	@Override
	public boolean startAssessment() {
		if (this.checkTransitionNotAllowedAndNotify(Transition.START_ASSESSMENT)) return false;

		return this.startAssessment(0);
	}

	private boolean startAssessment(int correctionRound) {
		if (this.nullCheckMembersAndNotify(true, true, false)) return false;

		Optional<Integer> optionalSubmissionID = this.getArtemisGUIController().startNextAssessment(this.exerciseID, correctionRound);
		if (optionalSubmissionID.isEmpty()) {
			//revert!
			this.backendStateMachine.revertLatestTransition();
			this.alertObservable.info("No more submissions available for Correction Round " + correctionRound + "!");
			return false;
		}
		this.submissionID = optionalSubmissionID.get();
		this.getArtemisGUIController().downloadExerciseAndSubmission(this.courseID, this.exerciseID, this.submissionID);
		return true;
	}

	@Override
	public boolean startCorrectionRound1() {
		if (this.checkTransitionNotAllowedAndNotify(Transition.START_CORRECTION_ROUND_1)) return false;

		return this.startAssessment(0);
	}

	@Override
	public boolean startCorrectionRound2() {
		if (this.checkTransitionNotAllowedAndNotify(Transition.START_CORRECTION_ROUND_2)) return false;

		return this.startAssessment(1);
	}

	@Override
	public void submitAssessment() {
		if (this.checkTransitionNotAllowedAndNotify(Transition.SUBMIT_ASSESSMENT)) return;
		if (this.nullCheckMembersAndNotify(true, true, true)) return;

		if (this.artemisGUIController.saveAssessment(this.submissionID, true, false)) {
			this.getCurrentAssessmentController().deleteEclipseProject();
			this.assessmentControllers.remove(this.submissionID);
			this.submissionID = null;
		}
	}
}
