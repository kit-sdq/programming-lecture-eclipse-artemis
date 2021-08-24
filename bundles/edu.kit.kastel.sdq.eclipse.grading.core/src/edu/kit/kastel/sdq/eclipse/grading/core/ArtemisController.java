package edu.kit.kastel.sdq.eclipse.grading.core;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.security.sasl.AuthenticationException;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import edu.kit.kastel.sdq.eclipse.grading.api.AbstractArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.IArtemisController;
import edu.kit.kastel.sdq.eclipse.grading.api.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.alerts.IAlertObservable;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExerciseGroup;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IFeedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IFeedback.FeedbackType;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.client.rest.ArtemisRESTClient;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.AnnotationMapper;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.DefaultPenaltyCalculationStrategy;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.DefaultProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.WorkspaceUtil;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.ZeroedPenaltyCalculationStrategy;

public class ArtemisController implements IArtemisController {

	private final SystemwideController systemwideController;
	private final AbstractArtemisClient artemisClient;

	private final Map<Integer, ILockResult> lockResults;

	private AlertObservable alertObservable;

	protected ArtemisController(final SystemwideController systemwideController, final String host, final String username, final String password) {
		this.artemisClient = new ArtemisRESTClient(username, password, host);
		this.systemwideController = systemwideController;
		this.lockResults = new HashMap<>();

		this.alertObservable = new AlertObservable();
	}

	@Override
	public void downloadExerciseAndSubmission(int courseID, int exerciseID, int submissionID) {
		final File eclipseWorkspaceRoot =  ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
		final IProjectFileNamingStrategy defaultProjectFileNamingStrategy = new DefaultProjectFileNamingStrategy();

		final Collection<ICourse> courses = this.getCourses();
		final IExercise exercise = this.getExerciseFromCourses(courses, courseID, exerciseID);
		final ISubmission submission = this.getSubmissionFromExercise(exercise, submissionID);

		try {
		this.artemisClient.downloadExerciseAndSubmission(exercise, submission, eclipseWorkspaceRoot,
				defaultProjectFileNamingStrategy);
		} catch (ArtemisClientException e) {
			this.alertObservable.error(e.getMessage(), e);
			return;
		}
		try {
		WorkspaceUtil.createEclipseProject(
				defaultProjectFileNamingStrategy.getProjectFileInWorkspace(eclipseWorkspaceRoot, exercise, submission));
		} catch (CoreException e) {
			this.alertObservable.error("Project could not be created: " + e.getMessage(), null);
		}

	}

	@Override
	public IAlertObservable getAlertObservable() {
		return this.alertObservable;
	}

	@Override
	public Collection<IFeedback> getAllFeedbacksGottenFromLocking(int submissionID) {
		ILockResult lockResult = this.lockResults.get(submissionID);
		if (lockResult == null) {
			this.alertObservable.error("No Lock found for submissionID=" + submissionID, null);
			return List.of();
		}
		return lockResult.getPreexistentFeedbacks();
	}

	@Override
	public Collection<ISubmission> getBegunSubmissions(int exerciseID) {
		try {
			return this.artemisClient.getSubmissions(exerciseID, true);
		} catch (Exception e) {
			this.alertObservable.error(e.getMessage(), e);
			return List.of();
		}
	}

	private ICourse getCourseByShortName(final String courseShortName) {

		Collection<ICourse> filteredCourses = this.getCourses().stream()
				.filter(course -> course.getShortName().equals(courseShortName))
				.collect(Collectors.toList());
		if (filteredCourses.isEmpty()) {
			this.alertObservable.error("No course found for courseShortName=" + courseShortName, null);
			return null;
		}
		if (filteredCourses.size() > 1) {
			this.alertObservable.error("Multiple courses found for courseShortName=" + courseShortName, null);
			return null;
		}
		return filteredCourses.iterator().next();
	}

	private ICourse getCourseFromCourses(Collection<ICourse> courses, int courseID) {
		final Collection<ICourse> coursesWithCorrectID = courses.stream()
				.filter(course -> (course.getCourseId() == courseID)).collect(Collectors.toList());
		if (coursesWithCorrectID.isEmpty()) {
			this.alertObservable.error("No course found for courseID=" + courseID, null);
			return null;
		}
		if (coursesWithCorrectID.size() > 1) {
			this.alertObservable.error("Multiple courses found for courseID=" + courseID, null);
			return null;
		}
		return coursesWithCorrectID.iterator().next();

	}

	@Override
	public Collection<ICourse> getCourses() {
		try {
			return this.artemisClient.getCourses();
		} catch (final Exception e) {
			this.alertObservable.error(e.getMessage(), e);
			return List.of();
		}
	}

	@Override
	public Collection<String> getCourseShortNames() {
		return this.getCourses().stream().map(ICourse::getShortName).collect(Collectors.toList());
	}

	@Override
	public Collection<String> getExamTitles(final String courseShortName) {

		ICourse course = this.getCourseByShortName(courseShortName);
		if (course == null) return List.of();

		return course.getExams().stream()
				.map(IExam::getTitle)
				.collect(Collectors.toList());
	}

	@Override
	public IExercise getExerciseFromCourses(Collection<ICourse> courses, int courseID, int exerciseID) {
		ICourse course = this.getCourseFromCourses(courses, courseID);
		if (course == null) {
			this.alertObservable.error("No course found for courseID=" + courseID, null);
			return null;
		}
		final Collection<IExercise> filteredExercises = course.getExercises().stream()
				.filter(exercise -> (exercise.getExerciseId() == exerciseID))
				.collect(Collectors.toList());
		if (filteredExercises.isEmpty()) {
			this.alertObservable.error("No exercise found for courseID=" + courseID + " and exerciseID=" + exerciseID, null);
			return null;
		}
		if (filteredExercises.size() > 1) {
			this.alertObservable.error("Multiple submissions found for courseID=" + courseID + " and exerciseID=" + exerciseID, null);
			return null;
		}
		return filteredExercises.iterator().next();
	}

	@Override
	public Collection<IExercise> getExercises(final int courseID, boolean withExamExercises) {
		ICourse course = this.getCourseFromCourses(this.getCourses(), courseID);

		if (course == null) return List.of();

		Collection<IExercise> allExercises = new LinkedList<>();
		allExercises.addAll(course.getExercises());
		if (withExamExercises) {
			allExercises.addAll(course.getExams().stream()
				.map(IExam::getExerciseGroups)
				.flatMap(Collection::stream)			//stream of Collections of Exercise Groups ==> stream of Exercise Groups
				.map(IExerciseGroup::getExercises)
				.flatMap(Collection::stream)			// stream of Collections of Exercises ==> stream of Exercises
				.collect(Collectors.toList()));
		}
		return allExercises;
	}

	@Override
	public Collection<IExercise> getExercisesFromExam(final String examTitle) {
		return this.getExercisesFromExam(examTitle, this.getCourses());
	}

	private  Collection<IExercise> getExercisesFromExam(final String examTitle, Collection<ICourse> courses) {
		IExam foundExam = null;
		for (ICourse course : courses) {
			final Collection<IExam> filteredExams = course.getExams().stream()
				.filter(exam -> exam.getTitle().equals(examTitle))
				.collect(Collectors.toList());
			if (filteredExams.size() == 1) {
				IExam exam = filteredExams.iterator().next();
				if (exam.getTitle().equals(examTitle)) {
					foundExam = exam;
				}
			}
		}
		if (foundExam == null) {
			this.alertObservable.error("No exam found for examTitle=" + examTitle, null);
			return List.of();
		}
		return foundExam.getExerciseGroups().stream()
			.map(IExerciseGroup::getExercises)
			.flatMap(Collection::stream)
			.collect(Collectors.toList());

	}

	@Override
	public Collection<String> getExerciseShortNames(final String courseShortName) {
		ICourse course = this.getCourseByShortName(courseShortName);
		if (course == null) return List.of();

		return course.getExercises().stream()
				.map(IExercise::getShortName)
				.collect(Collectors.toList());
	}

	@Override
	public Collection<String> getExerciseShortNamesFromExam(final String examTitle) {
		return this.getExercisesFromExam(examTitle).stream()
				.map(IExercise::getShortName)
				.collect(Collectors.toList());
	}

	@Override
	public Collection<IFeedback> getPrecalculatedAutoFeedbacks(int submissionID) {
		return this.lockResults.get(submissionID)
				.getPreexistentFeedbacks().stream()
				.filter(feedback -> feedback.getFeedbackType().equals(FeedbackType.AUTOMATIC))
				.collect(Collectors.toList());
	}

	@Override
	public ISubmission getSubmissionFromExercise(IExercise exercise, int submissionID) {
		final Collection<ISubmission> filteredSubmissions = exercise.getSubmissions().stream()
				.filter(submission -> (submission.getSubmissionId() == submissionID))
				.collect(Collectors.toList());
		if (filteredSubmissions.isEmpty()) {
			this.alertObservable.error("No submission found for exercise=" + exercise + " and submissionID=" + submissionID, null);
			return null;
		}
		if (filteredSubmissions.size() > 1) {
			this.alertObservable.error("Multiple submissions found for exercise=" + exercise + " and submissionID=" + submissionID, null);
			return null;
		}
		return filteredSubmissions.iterator().next();
	}

	@Override
	public boolean saveAssessment(int submissionID, boolean submit, boolean invalidSubmission) {
		final IAssessmentController assessmentController = this.systemwideController.getCurrentAssessmentController();
		if (!this.lockResults.containsKey(submissionID))
			throw new IllegalStateException("Assessment not started, yet!");
		final ILockResult lockResult = this.lockResults.get(submissionID);
		final int participationID = lockResult.getParticipationID();

		final Collection<IAnnotation> annotations = assessmentController.getAnnotations();
		final Collection<IMistakeType> mistakeTypes = assessmentController.getMistakes();

		try {
			this.artemisClient.saveAssessment(
				participationID,
				submit,
				new AnnotationMapper(
						annotations,
						mistakeTypes,
						assessmentController.getRatingGroups(),
						this.artemisClient.getAssessor(),
						lockResult,
						invalidSubmission
							? new ZeroedPenaltyCalculationStrategy()
							: new DefaultPenaltyCalculationStrategy(annotations, mistakeTypes))
				.mapToJsonFormattedString()
			);
		} catch (AuthenticationException e) {
			this.alertObservable.error("Authentication to Artemis failed: " + e.getMessage(), e);
			return false;
		} catch (IOException e) {
			this.alertObservable.error("Local backend failed to format the annotations: " + e.getMessage(), e);
			return false;
		} catch (ArtemisClientException e) {
			this.alertObservable.error("Assessor could not be retrieved from Artemis: " + e.getMessage(), e);
			return false;
		}

		if (submit) {
			this.lockResults.remove(submissionID);
		}
		return true;
	}

	@Override
	public void startAssessment(int submissionID) {
		try {
			this.lockResults.put(submissionID, this.artemisClient.startAssessment(submissionID));
		} catch (Exception e) {
			this.alertObservable.error(Messages.ASSESSMENT_COULD_NOT_BE_STARTED_MESSAGE + e.getMessage(), e);
		}
	}

	@Override
	public Optional<Integer> startNextAssessment(int exerciseID) {
		try {
			return this.startNextAssessment(exerciseID, 0);
		} catch (Exception e) {
			this.alertObservable.error(Messages.ASSESSMENT_COULD_NOT_BE_STARTED_MESSAGE + e.getMessage(), e);
			return Optional.empty();
		}
	}

	@Override
	public Optional<Integer> startNextAssessment(int exerciseID, int correctionRound) {
		Optional<ILockResult> lockResultOptional;
		try {
			lockResultOptional = this.artemisClient.startNextAssessment(exerciseID, correctionRound);
		} catch (Exception e) {
			this.alertObservable.error(Messages.ASSESSMENT_COULD_NOT_BE_STARTED_MESSAGE + e.getMessage(), e);
			return Optional.empty();
		}
		if (lockResultOptional.isEmpty()) return Optional.empty();
		final ILockResult lockResult = lockResultOptional.get();

		final int submissionID = lockResult.getSubmissionID();
		this.lockResults.put(submissionID, lockResult);
		return Optional.of(submissionID);
	}
}
