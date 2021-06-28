package edu.kit.kastel.sdq.eclipse.grading.core;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.resources.ResourcesPlugin;

import edu.kit.kastel.sdq.eclipse.grading.api.AbstractArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.api.IArtemisGUIController;
import edu.kit.kastel.sdq.eclipse.grading.api.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;
import edu.kit.kastel.sdq.eclipse.grading.client.rest.ArtemisRESTClient;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.AnnotationMapper;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.WorkspaceUtil;

public class ArtemisGUIController implements IArtemisGUIController {

	private final String host;
	private final SystemwideController systemwideController;
	private final AbstractArtemisClient artemisClient;

	private final Map<String, ILockResult> lockResults;

	protected ArtemisGUIController(final SystemwideController systemwideController, final String host, final String username, final String password) {
		this.host = host;
		this.artemisClient = new ArtemisRESTClient(username, password, host);
		this.systemwideController = systemwideController;
		this.lockResults = new HashMap<String, ILockResult>();
	}

	//TODO hardcoded download of exercise and submission (and maybe give back submissionID)
	@Override
	public int downloadHardcodedExerciseAndSubmissionExample() {

		final File eclipseWorkspaceRoot =  ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
		final Collection<ICourse> courses = this.getCourses();
		final int exerciseId = 1;
		final int courseId = 1;

		Collection<IExercise> exercises = courses.stream()
				.filter(course -> (course.getCourseId() == courseId)).findAny().get()
				.getExercises().stream()
				.filter(exercise -> (exercise.getExerciseId() == exerciseId))
				.collect(Collectors.toList());
		final IExercise exercise = exercises.iterator().next();

		this.artemisClient.downloadExerciseAndSubmissions(exercise, exercise.getSubmissions(), eclipseWorkspaceRoot);
		WorkspaceUtil.createEclipseProject("exercise-1-testAufgabe1_submission-5-uyduk");

		System.out.println("Download Done!");
		return exercise.getSubmissions().stream().findAny().get().getSubmissionId();
	}

	@Override
	public void downloadSubmissions(Collection<Integer> submissionIds, String courseName , String exerciseName) {
		final IAssessmentController assessmentController = this.systemwideController.getAssessmentController(exerciseName);
		/* TODO for each submission:
		 * * download via ArtemisClient
		 * * create a project
		 */
		//TODO get file
		final File eclipseWorkspaceRoot =  ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();

		Collection<ISubmission> submissions = this.getCourses().stream()
			.filter(course -> course.getShortName().equals(courseName))
			.findAny().get()
			.getExercises().stream()
			.filter(exercise -> exercise.getShortName().equals(exerciseName))
			.findAny().get()
			.getSubmissions().stream()
			.filter(submission -> submissionIds.contains(submission.getSubmissionId()))
			.collect(Collectors.toList());

		this.artemisClient.downloadSubmissions(submissions, eclipseWorkspaceRoot);


		WorkspaceUtil.createEclipseProject("exercise-1-testAufgabe1_submission-5-uyduk");
	}

	@Override
	public Collection<ICourse> getCourses() {
		try {
			return this.artemisClient.getCourses();
		} catch (final Exception e) {
			//TODO exception handling!
			throw new RuntimeException("..");

		}
	}

	@Override
	public void startAssessment(int submissionID, String exerciseName) throws Exception {
		// TODO check if submission is already in the workspace!
		this.lockResults.put(exerciseName, this.artemisClient.startAssessment(submissionID));
	}

	@Override
	public void submitAssessment(int submissionID, String exerciseName) throws Exception {
		final IAssessmentController assessmentController = this.systemwideController.getAssessmentController(exerciseName);
		/* TODO change signature of AbstractArtemisClient::submitAssessments to sth like
		 *      submitAssessment(int submissionID, ISubmission (or just a json formatted String))
		 */
		if (!this.lockResults.containsKey(exerciseName))
			throw new IllegalStateException("Assessment not started, yet!");

		//TODO exception only? or parse result?
		this.artemisClient.submitAssessment(submissionID,
			new AnnotationMapper(assessmentController.getAnnotations(exerciseName),
					assessmentController.getMistakes(),
					assessmentController.getRatingGroups(),
					this.artemisClient.getAssessor(),
					this.lockResults.get(exerciseName))
			.mapToJsonFormattedString());

		//TODO only if successful!
		this.lockResults.remove(exerciseName);
	}


}
