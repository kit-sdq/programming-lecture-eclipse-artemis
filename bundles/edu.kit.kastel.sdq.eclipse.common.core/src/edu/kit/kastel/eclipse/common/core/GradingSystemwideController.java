/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.core;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.kit.kastel.eclipse.common.api.PreferenceConstants;
import edu.kit.kastel.eclipse.common.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.eclipse.common.api.controller.IArtemisController;
import edu.kit.kastel.eclipse.common.api.controller.IAssessmentController;
import edu.kit.kastel.eclipse.common.api.controller.IGradingArtemisController;
import edu.kit.kastel.eclipse.common.api.controller.IGradingSystemwideController;
import edu.kit.kastel.eclipse.common.api.controller.IViewInteraction;
import edu.kit.kastel.eclipse.common.core.artemis.WorkspaceUtil;
import edu.kit.kastel.sdq.artemis4j.api.ArtemisClientException;
import edu.kit.kastel.sdq.artemis4j.api.artemis.Course;
import edu.kit.kastel.sdq.artemis4j.api.artemis.Exercise;
import edu.kit.kastel.sdq.artemis4j.api.artemis.ExerciseStats;
import edu.kit.kastel.sdq.artemis4j.api.artemis.assessment.Submission;
import edu.kit.kastel.sdq.artemis4j.api.artemis.exam.Exam;

public class GradingSystemwideController extends SystemwideController implements IGradingSystemwideController {
	private final Map<Integer, IAssessmentController> assessmentControllers = new HashMap<>();
	private IGradingArtemisController artemisController;

	private Submission submission;

	public GradingSystemwideController(final IPreferenceStore preferenceStore, IViewInteraction handler) {
		super(preferenceStore, handler);
		this.preferenceStore = preferenceStore;
	}

	@Override
	protected IArtemisController createController(IPreferenceStore preferenceStore, IViewInteraction handler) {
		this.artemisController = new GradingArtemisController(preferenceStore.getString(PreferenceConstants.GENERAL_ARTEMIS_URL),
				preferenceStore.getString(PreferenceConstants.GENERAL_ADVANCED_ARTEMIS_USER),
				preferenceStore.getString(PreferenceConstants.GENERAL_ADVANCED_ARTEMIS_PASSWORD), handler);
		return this.artemisController;
	}

	private IAssessmentController getAssessmentController(Submission submission, Course course, Exercise exercise) {
		// not equivalent to putIfAbsent!
		this.assessmentControllers.computeIfAbsent(submission.getSubmissionId(),
				submissionIDParam -> new AssessmentController(this, course, exercise, submission));
		return this.assessmentControllers.get(submission.getSubmissionId());
	}

	private List<Submission> getBegunSubmissions() {
		if (this.nullCheckMembersAndNotify(true, true, false)) {
			return List.of();
		}

		return this.getArtemisController().getBegunSubmissions(this.exercise);
	}

	@Override
	public ExerciseStats getStats() {
		if (this.nullCheckMembersAndNotify(true, true, false)) {
			return new ExerciseStats(0, 0, 0, 0);
		}
		try {
			return this.getArtemisController().getStats(this.exercise);
		} catch (ArtemisClientException e) {
			this.error(e.getMessage(), e);
			return new ExerciseStats(0, 0, 0, 0);
		}
	}

	@Override
	public List<String> getBegunSubmissionsProjectNames() {
		// Special Case: refresh
		if (this.course == null || this.exercise == null) {
			this.info("You need to choose a" + (this.course == null ? "course" : "") + (this.course == null && this.exercise == null ? " and an " : "")
					+ (this.exercise == null ? "exercise" : "."));
			return List.of();
		}

		return this.getBegunSubmissions().stream().map(
				sub -> this.projectFileNamingStrategy.getProjectFileInWorkspace(WorkspaceUtil.getWorkspaceFile(), this.getCurrentExercise(), sub).getName())
				.sorted().toList();
	}

	@Override
	public IAssessmentController getCurrentAssessmentController() {
		if (this.nullCheckMembersAndNotify(true, true, true)) {
			return null;
		}
		return this.getAssessmentController(this.submission, this.course, this.exercise);
	}

	private Exercise getCurrentExercise() {
		if (this.nullCheckMembersAndNotify(true, true, false)) {
			return null;
		}
		return this.exercise;
	}

	@Override
	public String getCurrentProjectName() {
		if (this.nullCheckMembersAndNotify(true, true, true)) {
			return null;
		}

		return this.projectFileNamingStrategy
				.getProjectFileInWorkspace(WorkspaceUtil.getWorkspaceFile(), this.getCurrentExercise(), this.getCurrentSubmission()).getName();
	}

	private Submission getCurrentSubmission() {
		if (this.nullCheckMembersAndNotify(true, true, true)) {
			return null;
		}
		return this.submission;
	}

	@Override
	public void loadAgain() {
		if (this.nullCheckMembersAndNotify(true, true, true)) {
			return;
		}

		this.artemisController.startAssessment(this.submission);
		this.downloadExerciseAndSubmission(this.course, this.exercise, this.submission, this.projectFileNamingStrategy);
	}

	@Override
	public void setExerciseId(final String exerciseShortName) throws ArtemisClientException {
		// Normal exercises
		List<Exercise> exercises = new ArrayList<>(this.course.getExercises());
		// exam exercises
		for (Exam ex : this.course.getExams()) {
			ex.getExerciseGroups().forEach(g -> exercises.addAll(g.getExercises()));
		}

		for (Exercise ex : exercises) {
			if (ex.getShortName().equals(exerciseShortName)) {
				this.exercise = ex;
				return;
			}
		}
		this.error("No Exercise with the given shortName \"" + exerciseShortName + "\" found.", null);
	}

	@Override
	public void reloadAssessment() {
		if (this.nullCheckMembersAndNotify(true, true, true)) {
			return;
		}
		this.getCurrentAssessmentController().resetAndRestartAssessment(this.projectFileNamingStrategy);
	}

	@Override
	public void saveAssessment() {
		if (this.nullCheckMembersAndNotify(true, true, true)) {
			return;
		}

		this.artemisController.saveAssessment(this.getCurrentAssessmentController(), this.exercise, this.submission, false);
	}

	@Override
	public void setAssessedSubmissionByProjectName(String projectName) {

		for (var submission : this.getBegunSubmissions()) {
			String currentProjectName = this.projectFileNamingStrategy
					.getProjectFileInWorkspace(WorkspaceUtil.getWorkspaceFile(), this.getCurrentExercise(), submission).getName();
			if (currentProjectName.equals(projectName)) {
				this.submission = submission;
				return;
			}
		}

		this.error("Assessed submission with projectName=\"" + projectName + "\" not found!", null);
	}

	@Override
	public List<String> setCourseIdAndGetExerciseShortNames(final String courseShortName) throws ArtemisClientException {

		for (Course c : this.getArtemisController().getCourses()) {
			if (c.getShortName().equals(courseShortName)) {
				this.course = c;
				return c.getExercises().stream().filter(it -> !it.isAutomaticAssessment()).map(Exercise::getShortName).toList();
			}
		}
		this.error("No Course with the given shortName \"" + courseShortName + "\" found.", null);
		return List.of();
	}

	private boolean startAssessment(int correctionRound) {
		if (this.nullCheckMembersAndNotify(true, true, false)) {
			return false;
		}

		Optional<Submission> optionalSubmission = this.artemisController.startNextAssessment(this.exercise, correctionRound);
		if (optionalSubmission.isEmpty()) {
			// revert!
			this.info("No more submissions available for Correction Round " + (correctionRound + 1) + "!");
			return false;
		}
		this.submission = optionalSubmission.get();

		// perform download. Revert state if that fails.
		if (!this.downloadExerciseAndSubmission(this.course, this.exercise, this.submission, this.projectFileNamingStrategy)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean startCorrectionRound1() {
		return this.startAssessment(0);
	}

	@Override
	public boolean startCorrectionRound2() {
		return this.startAssessment(1);
	}

	@Override
	public void submitAssessment() {
		if (this.nullCheckMembersAndNotify(true, true, true)) {
			return;
		}

		if (this.artemisController.saveAssessment(this.getCurrentAssessmentController(), this.exercise, this.submission, true)) {
			this.closeAssessment();
		}
	}

	@Override
	public void closeAssessment() {
		if (this.nullCheckMembersAndNotify(true, true, true)) {
			return;
		}

		this.getCurrentAssessmentController().deleteEclipseProject(this.projectFileNamingStrategy);
		this.assessmentControllers.remove(this.submission.getSubmissionId());
		this.submission = null;
	}

	private boolean nullCheckMembersAndNotify(boolean checkCourseId, boolean checkExerciseId, boolean checkSubmissionId) {
		boolean somethingNull = this.nullCheckMembersAndNotify(checkCourseId, checkExerciseId);
		if (checkSubmissionId && this.submission == null) {
			this.warn("Submission is not set ");
			somethingNull = true;
		}
		return somethingNull;
	}

	@Override
	public boolean downloadExerciseAndSubmission(Course course, Exercise exercise, Submission submission, IProjectFileNamingStrategy projectNaming) {
		final File eclipseWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();

		try {
			this.exerciseController.downloadExerciseAndSubmission(exercise, submission, eclipseWorkspaceRoot, projectNaming);
		} catch (ArtemisClientException e) {
			this.error(e.getMessage(), e);
			return false;
		}
		try {
			WorkspaceUtil.createEclipseProject(projectNaming.getProjectFileInWorkspace(eclipseWorkspaceRoot, exercise, submission),
					this.buildCompletedCallbacks);
		} catch (CoreException e) {
			this.error("Project could not be created: " + e.getMessage(), null);
		}
		return true;
	}

	@Override
	public IGradingArtemisController getArtemisController() {
		return this.artemisController;
	}

	@Override
	public boolean isAssessmentStarted() {
		return this.submission != null;
	}

	@Override
	public Optional<Exercise> getSelectedExercise() {
		return Optional.ofNullable(this.exercise);
	}

	public final IPreferenceStore getPreferences() {
		return this.preferenceStore;
	}

	@Override
	public Path getCurrentProjectPath() {
		return this.projectFileNamingStrategy.getProjectFileInWorkspace(WorkspaceUtil.getWorkspaceFile(), this.exercise, this.submission).toPath();
	}
}
