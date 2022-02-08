package edu.kit.kastel.sdq.eclipse.grading.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.MessageDialog;

import edu.kit.kastel.eclipse.common.view.utilities.AssessmentUtilities;
import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.FeedbackType;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExerciseGroup;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IStudentExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ParticipationDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ResultsDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.client.websocket.ArtemisWebsocketException;
import edu.kit.kastel.sdq.eclipse.grading.api.client.websocket.IWebsocketClient;
import edu.kit.kastel.sdq.eclipse.grading.api.client.websocket.WebsocketCallback;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.AbstractController;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.IArtemisController;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.IWebsocketController;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IRatingGroup;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.exam.ArtemisExam;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.exam.ArtemisStudentExam;
import edu.kit.kastel.sdq.eclipse.grading.client.rest.RestClientManager;
import edu.kit.kastel.sdq.eclipse.grading.client.websocket.ArtemisFeedbackWebsocket;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.AnnotationMapper;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.calculation.DefaultPenaltyCalculationStrategy;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.calculation.IPenaltyCalculationStrategy;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.calculation.ZeroedPenaltyCalculationStrategy;

public class ArtemisController extends AbstractController implements IArtemisController, IWebsocketController {

	private final IWebsocketClient websocketClient;
	private final Map<Integer, ILockResult> lockResults;
	private final RestClientManager clientManager;
	private List<ICourse> courses;

	protected ArtemisController(final String host, final String username, final String password) {
		this.clientManager = new RestClientManager(host, username, password);
		this.lockResults = new HashMap<>();
		this.websocketClient = new ArtemisFeedbackWebsocket(host);

		loginOrNotify();
	}

	private void loginOrNotify() {
		try {
			this.clientManager.login();
		} catch (ArtemisClientException e) {
			this.error(e.getMessage(), e);
		}
	}

	private Optional<ParticipationDTO> getParticipationForExercise(ICourse course, IExercise exercise) {
		try {
			return Optional
					.of(clientManager.getParticipationArtemisClient().getParticipationForExercise(course, exercise));
		} catch (ArtemisClientException e) {
			return Optional.empty();
		}
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
			return this.clientManager.getSubmissionArtemisClient().getSubmissions(exercise);
		} catch (Exception e) {
			this.error(e.getMessage(), e);
			return List.of();
		}
	}

	protected ICourse getCourseByShortName(final String courseShortName) {
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
	public List<ICourse> fetchCourses() {
		if (!this.clientManager.isReady()) {
			return List.of();
		}
		try {
			return this.clientManager.getCourseArtemisClient().getCourses();
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
	public IStudentExam getExercisesFromExam(final String examTitle) {
		return this.getExercisesFromExam(examTitle, this.getCourses());
	}

	@Override
	public Date getCurrentDate() {
		try {
			return this.clientManager.getUtilArtemisClient().getTime();
		} catch (ArtemisClientException e) {
			return new Date();
		}
	}

	private IStudentExam getExercisesFromExam(final String examTitle, List<ICourse> courses) {
		Entry<ICourse, IExam> foundEntry = filterGetExamObjectFromLoadedCourses(examTitle, courses);
		if (foundEntry == null) {
			this.error("No exam found for examTitle=" + examTitle, null);
			return new ArtemisStudentExam();
		}
		try {
			return this.clientManager.getExamArtemisClient().findExamForSummary(foundEntry.getKey(),
					foundEntry.getValue());
		} catch (ArtemisClientException e) {
			this.error("The exam has not been submitted yet. \n"
					+ "You can only view results after the exam was submitted. \n"
					+ "To submit the exam you have to submit the exam in the Artemis webclient!. It is not possible in Eclipse!. \n"
					+ "To load exercises for the exam in to your local workspace you have to start the exam first! \n"
					+ "After starting the exam you can load exercises in the workspace und submit solutions \n "
					+ "After submitting solutions you can view results in the Result-Tab.", e);
		}
		if (this.confirm("Do you want to start the exam now?")) {
			return this.startExam(foundEntry.getKey(), foundEntry.getValue());
		}
		return new ArtemisStudentExam();
	}

	private Entry<ICourse, IExam> filterGetExamObjectFromLoadedCourses(String examTitle, List<ICourse> courses) {
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
					return Map.entry(course, exam);
				}
			}
		}
		return null;
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
		return this.getExercisesFromExam(examTitle).getExercises().stream().map(IExercise::getShortName)
				.collect(Collectors.toList());
	}

	@Override
	public List<Feedback> getPrecalculatedAutoFeedbacks(ISubmission submission) {
		return this.lockResults.get(submission.getSubmissionId()).getLatestFeedback().stream()
				.filter(feedback -> FeedbackType.AUTOMATIC.equals(feedback.getFeedbackType()))
				.collect(Collectors.toList());
	}

	@Override
	public boolean saveAssessment(IAssessmentController assessmentController, IExercise exercise,
			ISubmission submission, boolean submit, boolean invalidSubmission) {
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
							this.clientManager.getAuthenticationClient().getAssessor(), calculator, lock);
			this.clientManager.getAssessmentArtemisClient().saveAssessment(participation, submit,
					mapper.createAssessmentResult());
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
			this.lockResults.put(submissionID.getSubmissionId(),
					this.clientManager.getAssessmentArtemisClient().startAssessment(submissionID));
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
			lockResultOptional = this.clientManager.getAssessmentArtemisClient().startNextAssessment(exercise,
					correctionRound);
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
	public Map<ResultsDTO, List<Feedback>> getFeedbackExcerise(ICourse course, IExercise exercise) {
		Optional<ParticipationDTO> participationOpt = getParticipationForExercise(course, exercise);
		if (participationOpt.isEmpty()) {
			return new HashMap<>();
		}

		ParticipationDTO participationWithResults;
		try {
			participationWithResults = this.clientManager.getParticipationArtemisClient()
					.getParticipationWithLatestResultForExercise(participationOpt.get().getParticipationID());
		} catch (ArtemisClientException e) {
			this.error("Can't load results for selected exercise " + exercise.getShortName() //
					+ ".\n No results found. Please check if a solution was submitted.", e);
			return new HashMap<>();
		}

		if (participationWithResults.getResults() == null) {
			return new HashMap<>();
		}

		Map<ResultsDTO, List<Feedback>> resultFeedbackMap = new HashMap<>();

		for (var result : participationWithResults.getResults()) {
			if (result.hasFeedback) {
				Feedback[] feedbacks = {};
				try {
					feedbacks = this.clientManager.getFeedbackArtemisClient()
							.getFeedbackForResult(participationOpt.get(), result);
				} catch (ArtemisClientException e) {
					e.printStackTrace();
					break;
				}
				resultFeedbackMap.put(result, Arrays.asList(feedbacks));
			}
		}

		if (resultFeedbackMap.isEmpty()) {
			this.error("Can't load any feedback for selected exercise " + exercise.getShortName() //
					+ ".\n No feedback found. Please check if a solution was submitted.", null);
		}

		return resultFeedbackMap;
	}

	public List<ICourse> getCourses() {
		if (courses == null) {
			courses = this.fetchCourses();
		}

		return courses;
	}

	@Override
	public boolean connectToWebsocket(WebsocketCallback callback) {
		try {
			this.websocketClient.connect(callback, this.clientManager.getAuthenticationClient().getRawToken());
			return true;
		} catch (ArtemisWebsocketException e) {
			System.out.println("Can not connect to websocket.");
			return false;
		}
	}

	@Override
	public IStudentExam startExam(ICourse course, IExam exam) {
		try {
			return this.clientManager.getExamArtemisClient().conductExam(course, exam);
		} catch (ArtemisClientException e) {
			this.error("Error, can not start the exam: " + exam.getTitle(), e);
			return new ArtemisStudentExam();
		}
	}

	@Override
	public Optional<ParticipationDTO> getParticipation(ICourse course, IExercise exercise) {
		Optional<ParticipationDTO> participation = getParticipationForExercise(course, exercise);

		if (participation.isEmpty()) {
			try {
				participation = Optional.of(
						clientManager.getParticipationArtemisClient().startParticipationForExercise(course, exercise));
			} catch (ArtemisClientException e) {
				return Optional.empty();
			}
		}
		return participation;
	}
}
