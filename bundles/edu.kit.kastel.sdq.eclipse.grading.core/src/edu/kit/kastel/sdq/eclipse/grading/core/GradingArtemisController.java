package edu.kit.kastel.sdq.eclipse.grading.core;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ParticipationDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.IGradingArtemisController;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IRatingGroup;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.AnnotationMapper;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.calculation.DefaultPenaltyCalculationStrategy;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.calculation.IPenaltyCalculationStrategy;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.calculation.ZeroedPenaltyCalculationStrategy;
import edu.kit.kastel.sdq.eclipse.grading.core.messages.Messages;

public class GradingArtemisController extends ArtemisController implements IGradingArtemisController {

	public GradingArtemisController(String host, String username, String password) {
		super(host, username, password);
	}

	@Override
	public List<ICourse> getCourses() {
		if (courses == null) {
			courses = fetchCourses();
		}
		return courses;
	}

	@Override
	public List<String> getExerciseShortNamesFromExam(final String examTitle) {
		return this.getExercisesFromExam(examTitle).stream().map(IExercise::getShortName).collect(Collectors.toList());
	}

	@Override
	protected List<ICourse> fetchCourses() {
		if (!this.clientManager.isReady()) {
			return List.of();
		}
		try {
			return this.clientManager.getCourseArtemisClient().getCoursesForAssessment();
		} catch (final Exception e) {
			this.error(e.getMessage(), e);
			return List.of();
		}
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

}
