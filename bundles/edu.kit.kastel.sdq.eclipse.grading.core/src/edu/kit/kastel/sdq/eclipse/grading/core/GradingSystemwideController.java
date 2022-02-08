package edu.kit.kastel.sdq.eclipse.grading.core;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.PreferenceConstants;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.SubmissionFilter;
import edu.kit.kastel.sdq.eclipse.grading.api.backendstate.Transition;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.IArtemisController;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.IGradingSystemwideController;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.ISystemwideController;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.WorkspaceUtil;
import edu.kit.kastel.sdq.eclipse.grading.core.config.ConfigDAO;
import edu.kit.kastel.sdq.eclipse.grading.core.config.JsonFileConfigDao;

public class GradingSystemwideController extends SystemwideController implements  IGradingSystemwideController {

	private final Map<Integer, IAssessmentController> assessmentControllers  = new HashMap<>();

	private ConfigDAO configDao;

	private ISubmission submission;

	private BackendStateMachine backendStateMachine;

	public GradingSystemwideController(final IPreferenceStore preferenceStore) {
		super(preferenceStore.getString(PreferenceConstants.ARTEMIS_USER), //
				preferenceStore.getString(PreferenceConstants.ARTEMIS_PASSWORD) );
		createController(preferenceStore.getString(PreferenceConstants.ARTEMIS_URL), //
				preferenceStore.getString(PreferenceConstants.ARTEMIS_USER), //
				preferenceStore.getString(PreferenceConstants.ARTEMIS_PASSWORD) //
		);
		this.preferenceStore = preferenceStore;

		// initialize config
		this.updateConfigFile();
		
		this.initPreferenceStoreCallback(preferenceStore);
	}

	public GradingSystemwideController(final String artemisHost, final String username, final String password) {
		super(username, password);
		createController(artemisHost, username, password);
	}
	
	private void createController(final String artemisHost, final String username, final String password) {
		this.artemisGUIController = new ArtemisController(artemisHost, username, password);
		this.backendStateMachine = new BackendStateMachine();
		this.artemisGUIController = new ArtemisController(artemisHost, username, password);
	}

	private boolean applyTransitionAndNotifyIfNotAllowed(Transition transition) {
		try {
			this.backendStateMachine.applyTransition(transition);
		} catch (NoTransitionException e) {
			this.error(e.getMessage(), e);
			return true;
		}
		return false;
	}

	private IAssessmentController getAssessmentController(ISubmission submission, ICourse course, IExercise exercise) {
		// not equivalent to putIfAbsent!
		this.assessmentControllers.computeIfAbsent(submission.getSubmissionId(),
				submissionIDParam -> new AssessmentController(this, course, exercise, submission));
		return this.assessmentControllers.get(submission.getSubmissionId());
	}

	private List<ISubmission> getBegunSubmissions(SubmissionFilter submissionFilter) {
		if (this.nullCheckMembersAndNotify(true, true, false)) {
			return List.of();
		}

		return this.getArtemisGUIController().getBegunSubmissions(this.exercise).stream().filter(submissionFilter)
				.collect(Collectors.toList());
	}

	@Override
	public List<String> getBegunSubmissionsProjectNames(SubmissionFilter submissionFilter) {
		// sondercase: refresh
		if (this.course == null || this.exercise == null) {
			this.info("You need to choose a" + (this.course == null ? "course" : "")
					+ (this.course == null && this.exercise == null ? " and an " : "")
					+ (this.exercise == null ? "exercise" : "."));
			return List.of();
		}

		return this.getBegunSubmissions(submissionFilter).stream()
				.map(sub -> this.projectFileNamingStrategy
						.getProjectFileInWorkspace(WorkspaceUtil.getWorkspaceFile(), this.getCurrentExercise(), sub)
						.getName())
				.sorted().collect(Collectors.toList());
	}

	/**
	 *
	 * @return this system's configDao.
	 */
	public ConfigDAO getConfigDao() {
		return this.configDao;
	}

	@Override
	public IAssessmentController getCurrentAssessmentController() {
		if (this.nullCheckMembersAndNotify(true, true, true)) {
			return null;
		}
		return this.getAssessmentController(this.submission, this.course, this.exercise);
	}

	private IExercise getCurrentExercise() {
		if (this.nullCheckMembersAndNotify(true, true, false)) {
			return null;
		}
		return this.exercise;
	}

	@Override
	public Set<Transition> getCurrentlyPossibleTransitions() {
		boolean secondCorrectionRoundEnabled = this.exercise != null && this.exercise.isSecondCorrectionEnabled();

		return this.backendStateMachine.getCurrentlyPossibleTransitions().stream().filter(
				transition -> !Transition.START_CORRECTION_ROUND_2.equals(transition) || secondCorrectionRoundEnabled)
				.collect(Collectors.toSet());
	}

	@Override
	public String getCurrentProjectName() {
		if (this.nullCheckMembersAndNotify(true, true, true)) {
			return null;
		}

		return this.projectFileNamingStrategy.getProjectFileInWorkspace(WorkspaceUtil.getWorkspaceFile(),
				this.getCurrentExercise(), this.getCurrentSubmission()).getName();
	}

	private ISubmission getCurrentSubmission() {
		if (this.nullCheckMembersAndNotify(true, true, true)) {
			return null;
		}
		return this.submission;
	}

	@Override
	public void loadAgain() {
		if (this.applyTransitionAndNotifyIfNotAllowed(Transition.LOAD_AGAIN)) {
			return;
		}
		if (this.nullCheckMembersAndNotify(true, true, true)) {
			return;
		}

		this.artemisGUIController.startAssessment(this.submission);
		this.downloadExerciseAndSubmission(this.course, this.exercise, this.submission,
				this.projectFileNamingStrategy);
	}

	@Override
	public void onZeroPointsForAssessment() {
		if (this.applyTransitionAndNotifyIfNotAllowed(Transition.ON_ZERO_POINTS_FOR_ASSESSMENT)) {
			return;
		}

		if (this.artemisGUIController.saveAssessment(getCurrentAssessmentController(), exercise, this.submission, true, true)) {
			this.getCurrentAssessmentController().deleteEclipseProject(this.projectFileNamingStrategy);
			this.submission = null;
		}
	}

	@Override
	public void reloadAssessment() {
		if (this.applyTransitionAndNotifyIfNotAllowed(Transition.RELOAD_ASSESSMENT)) {
			return;
		}
		if (this.nullCheckMembersAndNotify(true, true, true)) {
			return;
		}
		this.updateConfigFile();

		this.getCurrentAssessmentController().resetAndRestartAssessment(this.projectFileNamingStrategy);
	}

	@Override
	public void saveAssessment() {
		if (this.applyTransitionAndNotifyIfNotAllowed(Transition.SAVE_ASSESSMENT)) {
			return;
		}
		if (this.nullCheckMembersAndNotify(true, true, true)) {
			return;
		}

		this.artemisGUIController.saveAssessment(getCurrentAssessmentController(), this.exercise, this.submission, false, false);
	}

	public void setArtemisController(IArtemisController artemisController) {
		this.artemisGUIController = artemisController;
	}

	@Override
	public void setAssessedSubmissionByProjectName(String projectName) {
		if (this.applyTransitionAndNotifyIfNotAllowed(Transition.SET_ASSESSED_SUBMISSION_BY_PROJECT_NAME)) {
			return;
		}

		boolean[] found = { false };
		this.getBegunSubmissions(SubmissionFilter.ALL).forEach(sub -> {
			String currentProjectName = this.projectFileNamingStrategy
					.getProjectFileInWorkspace(WorkspaceUtil.getWorkspaceFile(), this.getCurrentExercise(), sub)
					.getName();

			if (currentProjectName.equals(projectName)) {
				this.submission = sub;
				found[0] = true;
			}
		});
		if (found[0]) {
			return;
		}
		this.error("Assessed submission with projectName=\"" + projectName + "\" not found!", null);
	}

	@Override
	public void setConfigFile(File newConfigFile) {
		this.configDao = new JsonFileConfigDao(newConfigFile);
	}

	@Override
	public List<String> setCourseIdAndGetExerciseShortNames(final String courseShortName)
			throws ArtemisClientException {
		if (this.applyTransitionAndNotifyIfNotAllowed(Transition.SET_COURSE_ID_AND_GET_EXERCISE_SHORT_NAMES)) {
			return List.of();
		}

		for (ICourse c : this.getArtemisGUIController().getCourses()) {
			if (c.getShortName().equals(courseShortName)) {
				this.course = c;
				return c.getExercises().stream().map(IExercise::getShortName).collect(Collectors.toList());
			}
		}
		this.error("No Course with the given shortName \"" + courseShortName + "\" found.", null);
		return List.of();
	}


	@Override
	public boolean startAssessment() {
		if (this.applyTransitionAndNotifyIfNotAllowed(Transition.START_ASSESSMENT)) {
			return false;
		}

		return this.startAssessment(0);
	}

	private boolean startAssessment(int correctionRound) {
		if (this.nullCheckMembersAndNotify(true, true, false)) {
			return false;
		}
		this.updateConfigFile();

		Optional<ISubmission> optionalSubmissionID = this.getArtemisGUIController().startNextAssessment(this.exercise,
				correctionRound);
		if (optionalSubmissionID.isEmpty()) {
			// revert!
			this.backendStateMachine.revertLatestTransition();
			this.info("No more submissions available for Correction Round " + (correctionRound + 1) + "!");
			return false;
		}
		this.submission = optionalSubmissionID.get();

		// perform download. Revert state if that fails.
		if (!this.downloadExerciseAndSubmission(this.course, this.exercise, this.submission,
				this.projectFileNamingStrategy)) {
			this.backendStateMachine.revertLatestTransition();
			return false;
		}
		return true;
	}

	@Override
	public boolean startCorrectionRound1() {
		if (this.applyTransitionAndNotifyIfNotAllowed(Transition.START_CORRECTION_ROUND_1)) {
			return false;
		}

		return this.startAssessment(0);
	}

	@Override
	public boolean startCorrectionRound2() {
		if (this.applyTransitionAndNotifyIfNotAllowed(Transition.START_CORRECTION_ROUND_2)) {
			return false;
		}

		return this.startAssessment(1);
	}

	@Override
	public void submitAssessment() {
		if (this.applyTransitionAndNotifyIfNotAllowed(Transition.SUBMIT_ASSESSMENT)) {
			return;
		}
		if (this.nullCheckMembersAndNotify(true, true, true)) {
			return;
		}

		if (this.artemisGUIController.saveAssessment(getCurrentAssessmentController(), this.exercise, this.submission, true, false)) {
			this.getCurrentAssessmentController().deleteEclipseProject(this.projectFileNamingStrategy);
			this.assessmentControllers.remove(this.submission.getSubmissionId());
			this.submission = null;
		}
	}

	private void updateConfigFile() {
		if (this.preferenceStore.getBoolean(PreferenceConstants.IS_RELATIVE_CONFIG_PATH)) {
			if (this.course != null && this.exercise != null && this.submission != null) {
				// not the case at startup with rel config path chosen!
				this.setConfigFile(
						new File(
								ResourcesPlugin.getWorkspace().getRoot().getProject(this.getCurrentProjectName())
										.getLocation().toFile(),
								this.preferenceStore.getString(PreferenceConstants.RELATIVE_CONFIG_PATH)));
			}
		} else {
			this.setConfigFile(new File(this.preferenceStore.getString(PreferenceConstants.ABSOLUTE_CONFIG_PATH)));

		}
	}
	
	private boolean nullCheckMembersAndNotify(boolean checkCourseID, boolean checkExerciseID, boolean checkSubmissionID) {
		boolean somethingNull = this.nullCheckMembersAndNotify(checkCourseID, checkExerciseID);
		if (checkSubmissionID && this.submission == null) {
			this.warn("Submission is not set ");
			somethingNull = true;
		}
		return somethingNull;
	}

	@Override
	void refreshArtemisController(String url, String user, String pass) {
		this.createController(url, user, pass);
	}
	
    @Override
    public boolean downloadExerciseAndSubmission(ICourse course, IExercise exercise, ISubmission submission,
            IProjectFileNamingStrategy projectNaming) {
        final File eclipseWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();

        try {
            this.exerciseController.downloadExerciseAndSubmission(exercise, submission, eclipseWorkspaceRoot, projectNaming);
        } catch (ArtemisClientException e) {
            this.error(e.getMessage(), e);
            return false;
        }
        try {
            WorkspaceUtil.createEclipseProject(
                    projectNaming.getProjectFileInWorkspace(eclipseWorkspaceRoot, exercise, submission));
        } catch (CoreException e) {
            this.error("Project could not be created: " + e.getMessage(), null);
        }
        return true;
    }
}
