package edu.kit.kastel.sdq.eclipse.grading.core;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.core.resources.ResourcesPlugin;

import edu.kit.kastel.sdq.eclipse.grading.api.AbstractArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.api.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.IArtemisGUIController;
import edu.kit.kastel.sdq.eclipse.grading.api.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IFeedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IFeedback.FeedbackType;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.client.rest.ArtemisRESTClient;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.AnnotationMapper;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.DefaultProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.WorkspaceUtil;

public class ArtemisGUIController implements IArtemisGUIController {

	private final String host;
	private final SystemwideController systemwideController;
	private final AbstractArtemisClient artemisClient;

	private final Map<Integer, ILockResult> lockResults;

	protected ArtemisGUIController(final SystemwideController systemwideController, final String host, final String username, final String password) {
		this.host = host;
		this.artemisClient = new ArtemisRESTClient(username, password, host);
		this.systemwideController = systemwideController;
		this.lockResults = new HashMap<Integer, ILockResult>();
	}

	private void checkOneElementInCollection(Collection collection) throws Exception {
		if (collection.size() != 1) throw new Exception("More ore less than one element: " + collection);
	}

	@Override
	public void downloadExerciseAndSubmission(int courseID, int exerciseID, int submissionID) throws Exception {
		final File eclipseWorkspaceRoot =  ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
		final IProjectFileNamingStrategy defaultProjectFileNamingStrategy = new DefaultProjectFileNamingStrategy();

		final Collection<ICourse> courses = this.getCourses();
		final IExercise exercise = this.getExerciseFromCourses(courses, courseID, exerciseID);
		final ISubmission submission = this.getSubmissionFromExercise(exercise, submissionID);

		this.artemisClient.downloadExerciseAndSubmission(exercise, submission, eclipseWorkspaceRoot,
				defaultProjectFileNamingStrategy);
		WorkspaceUtil.createEclipseProject(
				defaultProjectFileNamingStrategy.getProjectFileInWorkspace(eclipseWorkspaceRoot, exercise, submission));

	}

	//TODO hardcoded download of exercise and submission (and maybe give back submissionID)
	@Override
	public int downloadHardcodedExerciseAndSubmissionExample() {

		final File eclipseWorkspaceRoot =  ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
		final Collection<ICourse> courses = this.getCourses();
		final int exerciseId = 1;
		final int courseId = 1;
		final int submissionId = 5;

		try {
			this.downloadExerciseAndSubmission(courseId, exerciseId, submissionId);
		} catch (Exception e) {
			System.out.println("Caught exception in downloadHardcodedExerciseAndSubmissionExample: ");
			e.printStackTrace();
		}
		System.out.println("Download Done!");
		return 3;
	}

	@Override
	public Collection<IFeedback> getAllFeedbacksGottenFromLocking(int submissionID) {
		return this.lockResults.get(submissionID).getPreexistentFeedbacks();
	}

	private ICourse getCourseFromCourses(Collection<ICourse> courses, int courseID) throws Exception {
		final Collection<ICourse> coursesWithCorrectID = courses.stream()
				.filter(course -> (course.getCourseId() == courseID)).collect(Collectors.toList());
		this.checkOneElementInCollection(coursesWithCorrectID);
		return coursesWithCorrectID.iterator().next();

	}

	@Override
	public Collection<ICourse> getCourses() {
		try {
			return this.artemisClient.getCourses();
		} catch (final Exception e) {
			//TODO exception handling!
			e.printStackTrace();
			throw new RuntimeException("Underlying " + e.getClass() + ": " + e.getMessage());

		}
	}

	private IExercise getExerciseFromCourses(Collection<ICourse> courses, int courseID, int exerciseID) throws Exception {
		final Collection<IExercise> filteredExercises = this.getCourseFromCourses(courses, courseID).getExercises().stream()
				.filter(exercise -> (exercise.getExerciseId() == exerciseID))
				.collect(Collectors.toList());
		this.checkOneElementInCollection(filteredExercises);
		return filteredExercises.iterator().next();
	}

	@Override
	public Collection<IFeedback> getPrecalculatedAutoFeedbacks(int submissionID) {
		System.out.println("DBUG IN ArtemisGUICONTROLLER::getPrecalculatedAutoFeedbacks: " + this.lockResults.get(submissionID));
		return this.lockResults.get(submissionID)
				.getPreexistentFeedbacks().stream()
				.filter(feedback -> feedback.getFeedbackType().equals(FeedbackType.AUTOMATIC))
				.collect(Collectors.toList());
	}

	private ISubmission getSubmissionFromExercise(IExercise exercise, int submissionID) throws Exception {
		final Collection<ISubmission> filteredSubmissions = exercise.getSubmissions().stream()
				.filter(submission -> (submission.getSubmissionId() == submissionID))
				.collect(Collectors.toList());
		this.checkOneElementInCollection(filteredSubmissions);
		return filteredSubmissions.iterator().next();
	}

	@Override
	public void startAssessment(int submissionID) throws Exception {
		this.lockResults.put(submissionID, this.artemisClient.startAssessment(submissionID));
	}

	@Override
	public Optional<Integer> startNextAssessment(int exerciseID) throws Exception {
		final Optional<ILockResult> lockResultOptional = this.artemisClient.startNextAssessment(exerciseID);
		if (lockResultOptional.isEmpty()) return Optional.empty();
		final ILockResult lockResult = lockResultOptional.get();

		final int submissionID = lockResult.getSubmissionID();
		this.lockResults.put(submissionID, lockResult);
		return Optional.of(submissionID);
	}

	@Override
	public void submitAssessment(int submissionID) throws Exception {
		final IAssessmentController assessmentController = this.systemwideController.getAssessmentController(submissionID, null);
		if (!this.lockResults.containsKey(submissionID))
			throw new IllegalStateException("Assessment not started, yet!");
		final ILockResult lockResult = this.lockResults.get(submissionID);
		final int participationID = lockResult.getParticipationID();

		final Collection<IAnnotation> annotations = assessmentController.getAnnotations();
		final Collection<IMistakeType> mistakeTypes = assessmentController.getMistakes();

		this.artemisClient.submitAssessment(participationID,
			new AnnotationMapper(
					annotations,
					mistakeTypes,
					assessmentController.getRatingGroups(),
					this.artemisClient.getAssessor(),
					lockResult,
					new DefaultPenaltyCalculationStrategy(annotations, mistakeTypes))
			.mapToJsonFormattedString());

		//TODO only if successful!
		this.lockResults.remove(submissionID);
	}
}
