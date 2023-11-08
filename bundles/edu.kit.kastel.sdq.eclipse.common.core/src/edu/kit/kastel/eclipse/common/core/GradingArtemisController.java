/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.core;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;

import edu.kit.kastel.eclipse.common.api.controller.IAssessmentController;
import edu.kit.kastel.eclipse.common.api.controller.IGradingArtemisController;
import edu.kit.kastel.eclipse.common.api.controller.IViewInteraction;
import edu.kit.kastel.eclipse.common.api.messages.Messages;
import edu.kit.kastel.sdq.artemis4j.api.ArtemisClientException;
import edu.kit.kastel.sdq.artemis4j.api.artemis.Course;
import edu.kit.kastel.sdq.artemis4j.api.artemis.Exercise;
import edu.kit.kastel.sdq.artemis4j.api.artemis.ExerciseStats;
import edu.kit.kastel.sdq.artemis4j.api.artemis.assessment.LockResult;
import edu.kit.kastel.sdq.artemis4j.api.artemis.assessment.Submission;
import edu.kit.kastel.sdq.artemis4j.api.grading.IAnnotation;
import edu.kit.kastel.sdq.artemis4j.api.grading.IRatingGroup;
import edu.kit.kastel.sdq.artemis4j.grading.artemis.AnnotationMapper;

public class GradingArtemisController extends ArtemisController implements IGradingArtemisController {

	private static final ILog log = Platform.getLog(GradingArtemisController.class);

	public GradingArtemisController(String host, String username, String password, IViewInteraction handler) {
		super(host, username, password, handler);
	}

	@Override
	public List<String> getExerciseShortNamesFromExam(final String examTitle) {
		return this.getExercisesFromExam(examTitle).stream().map(Exercise::getShortName).toList();
	}

	@Override
	protected List<Course> fetchCourses() {
		if (!this.clientManager.isReady()) {
			return List.of();
		}
		try {
			return this.clientManager.getCourseArtemisClient().getCourses();
		} catch (ArtemisClientException e) {
			this.error(e.getMessage(), e);
			return List.of();
		}
	}

	@Override
	public boolean saveAssessment(IAssessmentController assessmentController, Exercise exercise, Submission submission, boolean submit) {
		if (!this.lockResults.containsKey(submission.getSubmissionId())) {
			throw new IllegalStateException("Assessment not started, yet!");
		}
		final LockResult lock = this.lockResults.get(submission.getSubmissionId());
		final int participationId = lock.getParticipationId();

		final List<IAnnotation> annotations = assessmentController.getAnnotations();
		final List<IRatingGroup> ratingGroups = assessmentController.getRatingGroups();

		try {
			AnnotationMapper mapper = //
					new AnnotationMapper(exercise, submission, annotations, ratingGroups, this.clientManager.getAuthenticationClient().getUser(), lock);
			this.clientManager.getAssessmentArtemisClient().saveAssessment(participationId, submit, mapper.createAssessmentResult());
		} catch (IOException e) {
			this.error("Local backend failed to format the annotations: " + e.getMessage(), e);
			return false;
		} catch (ArtemisClientException e) {
			this.error("Assessor could not be retrieved from Artemis or Authentication to Artemis failed:" + e.getMessage(), e);
			return false;
		}

		if (submit) {
			this.lockResults.remove(submission.getSubmissionId());
		}
		return true;
	}

	@Override
	public void startAssessment(Submission submission) {
		try {
			this.lockResults.put(submission.getSubmissionId(), this.clientManager.getAssessmentArtemisClient().startAssessment(submission));
		} catch (ArtemisClientException e) {
			this.error(Messages.ASSESSMENT_COULD_NOT_BE_STARTED_MESSAGE + e.getMessage(), e);
		}
	}

	@Override
	public Optional<Submission> startNextAssessment(Exercise exercise, int correctionRound) {
		LockResult lockResult;
		try {
			Optional<Integer> submissionId = this.clientManager.getAssessmentArtemisClient().startNextAssessment(exercise, correctionRound);
			if (submissionId.isEmpty()) {
				return Optional.empty();
			}

			lockResult = this.clientManager.getAssessmentArtemisClient().startAssessment(submissionId.orElseThrow(), correctionRound);
		} catch (ArtemisClientException e) {
			log.error(Messages.ASSESSMENT_COULD_NOT_BE_STARTED_MESSAGE + e.getMessage(), e);
			return Optional.empty();
		}

		this.lockResults.put(lockResult.getSubmissionId(), lockResult);
		try {
			return Optional.of(exercise.getSubmission(lockResult.getSubmissionId()));
		} catch (ArtemisClientException e) {
			this.error(Messages.ASSESSMENT_COULD_NOT_BE_STARTED_MESSAGE + e.getMessage(), e);
			return Optional.empty();
		}
	}

	@Override
	public ExerciseStats getStats(Exercise exercise) throws ArtemisClientException {
		return this.clientManager.getAssessmentArtemisClient().getStats(exercise);
	}

}
