package edu.kit.kastel.sdq.eclipse.grading.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.FeedbackType;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExerciseGroup;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ParticipationDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.client.AbstractArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.AbstractController;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.IArtemisController;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IRatingGroup;
import edu.kit.kastel.sdq.eclipse.grading.client.git.GitException;
import edu.kit.kastel.sdq.eclipse.grading.client.git.GitHandler;
import edu.kit.kastel.sdq.eclipse.grading.client.rest.ArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.AnnotationMapper;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.WorkspaceUtil;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.calculation.DefaultPenaltyCalculationStrategy;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.calculation.IPenaltyCalculationStrategy;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.calculation.ZeroedPenaltyCalculationStrategy;

public class ArtemisController extends AbstractController implements IArtemisController {

	private final SystemwideController systemwideController;
	private final AbstractArtemisClient artemisClient;

	private String username;
	private String password;
	private final Map<Integer, ILockResult> lockResults;

	protected ArtemisController(final SystemwideController systemwideController, final String host,
			final String username, final String password) {
		this.username = username;
		this.password = password;
		this.artemisClient = new ArtemisClient(username, password, host);
		this.systemwideController = systemwideController;
		this.lockResults = new HashMap<>();
	}

	@Override
	public boolean downloadExerciseAndSubmission(ICourse course, IExercise exercise, ISubmission submission,
			IProjectFileNamingStrategy projectNaming) {
		final File eclipseWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();

		// abort if directory already exists.
		if (this.existsAndNotify(projectNaming.getProjectFileInWorkspace(eclipseWorkspaceRoot, exercise, submission))) {
			return false;
		}

		try {
			this.artemisClient.downloadExerciseAndSubmission(exercise, submission, eclipseWorkspaceRoot, projectNaming);
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

	@Override
	public boolean loadExerciseInWorkspaceForStudent(ICourse course, IExercise exercise,
			IProjectFileNamingStrategy projectNaming) {
		final File eclipseWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();

		// abort if directory already exists.
		if (this.existsAndNotify(projectNaming.getProjectFileInWorkspace(eclipseWorkspaceRoot, exercise, null))) {
			return false;
		}
		Optional<ParticipationDTO> optParticipation = getparticipationForExercise(course, exercise);

		ParticipationDTO participation;

		if (optParticipation.isEmpty()) {
			try {
				participation = artemisClient.startParticipationForExercise(course, exercise);
			} catch (ArtemisClientException e) {
				this.error("The selected Exercise can not be startet.", e);
				return false;
			}
		} else {
			participation = optParticipation.get();
		}
		try {
			this.artemisClient.downloadExercise(exercise, eclipseWorkspaceRoot, projectNaming,
					participation.getRepositoryUrl());
		} catch (ArtemisClientException e) {
			this.error(e.getMessage(), e);
			return false;
		}
		try {
			WorkspaceUtil.createEclipseProject(
					projectNaming.getProjectFileInWorkspace(eclipseWorkspaceRoot, exercise, null));
		} catch (CoreException e) {
			this.error("Project could not be created: " + e.getMessage(), null);
		}
		return true;
	}

	private Optional<ParticipationDTO> getparticipationForExercise(ICourse course, IExercise exercise) {
		try {
			return Optional.of(artemisClient.getParticipationForExercise(course, exercise));
		} catch (ArtemisClientException e) {
			return Optional.empty();
		}
	}

	private boolean existsAndNotify(File file) {
		if (file.exists()) {
			this.warn("Project " + file.getName() + " could not be cloned since the workspace "
					+ "already contains a project with that name. Please delete it and retry.");
			return true;
		}
		return false;
	}

	@Override
	public List<Feedback> getAllFeedbacksGottenFromLocking(ISubmission submission) {
		ILockResult lockResult = this.lockResults.get(submission.getSubmissionId());
		if (lockResult == null) {
			this.error("No Lock found for submissionID=" + submission.getSubmissionId(), null);
			return List.of();
		}
		return lockResult.getLatestFeedback();
	}

	@Override
	public List<ISubmission> getBegunSubmissions(IExercise exercise) {
		try {
			return this.artemisClient.getSubmissions(exercise);
		} catch (Exception e) {
			this.error(e.getMessage(), e);
			return List.of();
		}
	}

	private ICourse getCourseByShortName(final String courseShortName) {
		List<ICourse> filteredCourses = this.getCourses().stream()
				.filter(course -> course.getShortName().equals(courseShortName)).collect(Collectors.toList());
		if (filteredCourses.isEmpty()) {
			this.error("No course found for courseShortName=" + courseShortName, null);
			return null;
		}
		if (filteredCourses.size() > 1) {
			this.error("Multiple courses found for courseShortName=" + courseShortName, null);
			return null;
		}
		return filteredCourses.iterator().next();
	}

	private ICourse getCourseFromCourses(List<ICourse> courses, int courseID) {
		final List<ICourse> coursesWithCorrectID = courses.stream().filter(course -> (course.getCourseId() == courseID))
				.collect(Collectors.toList());
		if (coursesWithCorrectID.isEmpty()) {
			this.error("No course found for courseID=" + courseID, null);
			return null;
		}
		if (coursesWithCorrectID.size() > 1) {
			this.error("Multiple courses found for courseID=" + courseID, null);
			return null;
		}
		return coursesWithCorrectID.iterator().next();

	}

	@Override
	public List<ICourse> getCourses() {
		if (!this.artemisClient.isReady()) {
			return List.of();
		}
		try {
			return this.artemisClient.getCourses();
		} catch (final Exception e) {
			this.error(e.getMessage(), e);
			return List.of();
		}
	}

	@Override
	public List<String> getCourseShortNames() {
		return this.getCourses().stream().map(ICourse::getShortName).collect(Collectors.toList());
	}

	@Override
	public List<String> getExamTitles(final String courseShortName) {
		try {
			ICourse course = this.getCourseByShortName(courseShortName);
			if (course == null) {
				return List.of();
			}

			return course.getExams().stream().map(IExam::getTitle).collect(Collectors.toList());
		} catch (final Exception e) {
			this.error(e.getMessage(), e);
			return List.of();
		}
	}

	@Override
	public IExercise getExerciseFromCourses(List<ICourse> courses, int courseID, int exerciseID) {
		ICourse course = this.getCourseFromCourses(courses, courseID);
		if (course == null) {
			this.error("No course found for courseID=" + courseID, null);
			return null;
		}
		final List<IExercise> filteredExercises = this.getExercises(course, true).stream()
				.filter(exercise -> (exercise.getExerciseId() == exerciseID)).collect(Collectors.toList());
		if (filteredExercises.isEmpty()) {
			this.error("No exercise found for courseID=" + courseID + " and exerciseID=" + exerciseID, null);
			return null;
		}
		if (filteredExercises.size() > 1) {
			this.error("Multiple submissions found for courseID=" + courseID + " and exerciseID=" + exerciseID, null);
			return null;
		}
		return filteredExercises.iterator().next();
	}

	@Override
	public List<IExercise> getExercises(final ICourse course, boolean withExamExercises) {
		if (course == null) {
			return List.of();
		}
		try {
			List<IExercise> allExercises = new ArrayList<>(course.getExercises());
			if (withExamExercises) {

				for (IExam e : course.getExams()) {
					for (IExerciseGroup g : e.getExerciseGroups()) {
						allExercises.addAll(g.getExercises());
					}
				}

			}
			return allExercises;
		} catch (final Exception e) {
			this.error(e.getMessage(), e);
			return List.of();
		}
	}

	@Override
	public List<IExercise> getExercisesFromExam(final String examTitle) {
		return this.getExercisesFromExam(examTitle, this.getCourses());
	}

	private List<IExercise> getExercisesFromExam(final String examTitle, List<ICourse> courses) {
		IExam foundExam = null;
		for (ICourse course : courses) {
			List<IExam> filteredExams;
			try {
				filteredExams = course.getExams().stream().filter(exam -> exam.getTitle().equals(examTitle))
						.collect(Collectors.toList());
			} catch (final Exception e) {
				this.error(e.getMessage(), e);
				continue;
			}
			if (filteredExams.size() == 1) {
				IExam exam = filteredExams.iterator().next();
				if (exam.getTitle().equals(examTitle)) {
					foundExam = exam;
				}
			}
		}
		if (foundExam == null) {
			this.error("No exam found for examTitle=" + examTitle, null);
			return List.of();
		}
		try {
			return foundExam.getExerciseGroups().stream().map(IExerciseGroup::getExercises).flatMap(Collection::stream)
					.collect(Collectors.toList());
		} catch (final Exception e) {
			this.error(e.getMessage(), e);
			return List.of();
		}

	}

	@Override
	public List<String> getExerciseShortNames(final String courseShortName) {
		ICourse course = this.getCourseByShortName(courseShortName);
		if (course == null) {
			return List.of();
		}

		try {
			return course.getExercises().stream().map(IExercise::getShortName).collect(Collectors.toList());
		} catch (ArtemisClientException e) {
			this.error(e.getMessage(), e);
			return List.of();
		}
	}

	@Override
	public List<String> getExerciseShortNamesFromExam(final String examTitle) {
		return this.getExercisesFromExam(examTitle).stream().map(IExercise::getShortName).collect(Collectors.toList());
	}

	@Override
	public List<Feedback> getPrecalculatedAutoFeedbacks(ISubmission submission) {
		return this.lockResults.get(submission.getSubmissionId()).getLatestFeedback().stream()
				.filter(feedback -> FeedbackType.AUTOMATIC.equals(feedback.getFeedbackType()))
				.collect(Collectors.toList());
	}

	@Override
	public boolean saveAssessment(IExercise exercise, ISubmission submission, boolean submit,
			boolean invalidSubmission) {
		final IAssessmentController assessmentController = this.systemwideController.getCurrentAssessmentController();
		if (!this.lockResults.containsKey(submission.getSubmissionId())) {
			throw new IllegalStateException("Assessment not started, yet!");
		}
		final ILockResult lock = this.lockResults.get(submission.getSubmissionId());
		final ParticipationDTO participation = lock.getParticipation();

		final List<IAnnotation> annotations = assessmentController.getAnnotations();
		final List<IMistakeType> mistakeTypes = assessmentController.getMistakes();
		final List<IRatingGroup> ratingGroups = assessmentController.getRatingGroups();

		IPenaltyCalculationStrategy calculator = invalidSubmission //
				? new ZeroedPenaltyCalculationStrategy()
				: new DefaultPenaltyCalculationStrategy(annotations, mistakeTypes);
		try {
			AnnotationMapper mapper = //
					new AnnotationMapper(exercise, submission, annotations, mistakeTypes, ratingGroups,
							this.artemisClient.getAssessor(), calculator, lock);
			this.artemisClient.saveAssessment(participation, submit, mapper.createAssessmentResult());
		} catch (IOException e) {
			this.error("Local backend failed to format the annotations: " + e.getMessage(), e);
			return false;
		} catch (ArtemisClientException e) {
			this.error("Assessor could not be retrieved from Artemis or Authentication to Artemis failed:"
					+ e.getMessage(), e);
			return false;
		}

		if (submit) {
			this.lockResults.remove(submission.getSubmissionId());
		}
		return true;
	}

	@Override
	public void startAssessment(ISubmission submissionID) {
		try {
			this.lockResults.put(submissionID.getSubmissionId(), this.artemisClient.startAssessment(submissionID));
		} catch (Exception e) {
			this.error(Messages.ASSESSMENT_COULD_NOT_BE_STARTED_MESSAGE + e.getMessage(), e);
		}
	}

	@Override
	public Optional<ISubmission> startNextAssessment(IExercise exercise) {
		try {
			return this.startNextAssessment(exercise, 0);
		} catch (Exception e) {
			this.error(Messages.ASSESSMENT_COULD_NOT_BE_STARTED_MESSAGE + e.getMessage(), e);
			return Optional.empty();
		}
	}

	@Override
	public Optional<ISubmission> startNextAssessment(IExercise exercise, int correctionRound) {
		Optional<ILockResult> lockResultOptional;
		try {
			lockResultOptional = this.artemisClient.startNextAssessment(exercise, correctionRound);
		} catch (Exception e) {
			this.error(Messages.ASSESSMENT_COULD_NOT_BE_STARTED_MESSAGE + e.getMessage(), e);
			return Optional.empty();
		}
		if (lockResultOptional.isEmpty()) {
			return Optional.empty();
		}
		final ILockResult lockResult = lockResultOptional.get();

		final int submissionID = lockResult.getSubmissionId();
		this.lockResults.put(submissionID, lockResult);
		try {
			return Optional.of(exercise.getSubmission(submissionID));
		} catch (ArtemisClientException e) {
			this.error(Messages.ASSESSMENT_COULD_NOT_BE_STARTED_MESSAGE + e.getMessage(), e);
			return Optional.empty();
		}
	}

	
	@Override
	public boolean submitSolution(ICourse course, IExercise exercise, IProjectFileNamingStrategy projectNaming) {
		final File eclipseWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
 		File exeriseRepo =  projectNaming.getProjectFileInWorkspace(eclipseWorkspaceRoot, exercise, null);
		File gitFileInRepo = projectNaming.getGitFileInProjectDirectory(exeriseRepo);
		try {
			GitHandler.commitExercise(username, username + "@student,kit,edu", "Test Commit Artemis", gitFileInRepo);
		} catch (GitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		try {
			GitHandler.pushExercise(username, password, gitFileInRepo);
		} catch (GitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public Optional<Set<String>> cleanWorkspace(ICourse course, IExercise exercise, IProjectFileNamingStrategy projectNaming) {
		final File eclipseWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
 		File exeriseRepo =  projectNaming.getProjectFileInWorkspace(eclipseWorkspaceRoot, exercise, null);
		File gitFileInRepo = projectNaming.getGitFileInProjectDirectory(exeriseRepo);
		try {
			return Optional.of(GitHandler.cleanRepo(gitFileInRepo));
		} catch (GitException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

}
