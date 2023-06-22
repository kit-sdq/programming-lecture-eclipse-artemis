/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.client.rest;

import java.io.IOException;
import java.util.Optional;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.artemis.AssessmentResult;
import edu.kit.kastel.eclipse.common.api.artemis.ILockResult;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.Feedback;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ISubmission;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.LongFeedbackText;
import edu.kit.kastel.eclipse.common.api.client.IAssessmentArtemisClient;
import edu.kit.kastel.eclipse.common.api.controller.ExerciseStats;
import edu.kit.kastel.eclipse.common.client.mappings.ArtemisSubmission;
import edu.kit.kastel.eclipse.common.client.mappings.lock.LockResult;
import edu.kit.kastel.eclipse.common.client.mappings.stats.Stats;
import edu.kit.kastel.eclipse.common.client.mappings.stats.Timing;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AssessmentArtemisClient extends AbstractArtemisClient implements IAssessmentArtemisClient {
	private static final ILog log = Platform.getLog(AssessmentArtemisClient.class);

	private static final String PROGRAMMING_SUBMISSION_WIHOUT_ASSESSMENT_PATH = "programming-submission-without-assessment";
	private static final String PARTICIPATIONS_PATHPART = "participations";
	private static final String MANUAL_RESULTS_PATHPART = "manual-results";
	private static final String CORRECTION_ROUND_QUERY_PARAM = "correction-round";
	private static final String LOCK_QUERY_PARAM = "lock";
	private static final String SUBMIT_QUERY_PARAM = "submit";
	protected static final String STATS_FOR_ASSESSMENT_DASHBOARD_PATH = "stats-for-assessment-dashboard";

	private final OkHttpClient client;

	public AssessmentArtemisClient(final String hostname, String token) {
		super(hostname);
		this.client = this.createClient(token);
	}

	@Override
	public void saveAssessment(int participationId, boolean submit, AssessmentResult assessment) throws ArtemisClientException {
		String assessmentPayload = this.payload(assessment);
		log.info(String.format("Saving assessment for submission %s with json: %s", assessment.id, assessmentPayload));

		Request request = new Request.Builder() //
				.url(this.path(PARTICIPATIONS_PATHPART, participationId, MANUAL_RESULTS_PATHPART).newBuilder()
						.addQueryParameter(SUBMIT_QUERY_PARAM, String.valueOf(submit)).build())
				.put(RequestBody.create(assessmentPayload, JSON)).build();

		this.call(this.client, request, null);
	}

	@Override
	public ILockResult startAssessment(ISubmission submission) throws ArtemisClientException {
		Request request = new Request.Builder() //
				.url(this.path(PROGRAMMING_SUBMISSIONS_PATHPART, submission.getSubmissionId(), LOCK_QUERY_PARAM).newBuilder()
						.addQueryParameter(CORRECTION_ROUND_QUERY_PARAM, String.valueOf(submission.getCorrectionRound())).build())
				.get().build();

		LockResult result = this.call(this.client, request, LockResult.class);
		result.init(this);
		return result;
	}

	@Override
	public Optional<ILockResult> startNextAssessment(IExercise exercise, int correctionRound) throws ArtemisClientException {
		Request request = new Request.Builder() //
				.url(this.path(EXERCISES_PATHPART, exercise.getExerciseId(), PROGRAMMING_SUBMISSION_WIHOUT_ASSESSMENT_PATH).newBuilder()
						.addQueryParameter(CORRECTION_ROUND_QUERY_PARAM, String.valueOf(correctionRound))
						.addQueryParameter(LOCK_QUERY_PARAM, String.valueOf(true)).build())
				.get().build();

		try (Response response = this.client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				return Optional.empty();
			}
			return Optional.of(this.read(response.body().string(), LockResult.class));
		} catch (IOException e) {
			throw new ArtemisClientException(e.getMessage(), e);
		}

	}

	@Override
	public ExerciseStats getStats(IExercise exercise) throws ArtemisClientException {
		Request request = new Request.Builder() //
				.url(this.path(EXERCISES_PATHPART, exercise.getExerciseId(), STATS_FOR_ASSESSMENT_DASHBOARD_PATH)).get().build();

		Stats stats = null;
		try (Response response = this.client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				return null;
			}
			stats = this.read(response.body().string(), Stats.class);
		} catch (IOException e) {
			throw new ArtemisClientException(e.getMessage(), e);
		}

		int submissionsInRound1 = this.countSubmissions(exercise, 0);
		int submissionsInRound2 = 0;
		if (exercise.hasSecondCorrectionRound()) {
			submissionsInRound2 = this.countSubmissions(exercise, 1);
		}

		return new ExerciseStats( //
				this.countInRounds(stats.numberOfAssessmentsOfCorrectionRounds()), //
				stats.numberOfSubmissions().inTime(), //
				stats.totalNumberOfAssessmentLocks(), //
				submissionsInRound1 + submissionsInRound2 //
		);

	}

	private int countInRounds(Timing[] rounds) {
		int countInTime = 0;
		for (var round : rounds) {
			countInTime += round.inTime();
		}
		return countInTime;
	}

	private int countSubmissions(IExercise exercise, int correctionRound) throws ArtemisClientException {
		Request request = new Request.Builder() //
				.url(this.path(EXERCISES_PATHPART, exercise.getExerciseId(), PROGRAMMING_SUBMISSIONS_PATHPART).newBuilder()
						.addQueryParameter("assessedByTutor", String.valueOf(true)).addQueryParameter("correction-round", String.valueOf(correctionRound))
						.build())
				.get().build();

		try (Response response = this.client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				return 0;
			}
			ArtemisSubmission[] submissionsArray = this.read(response.body().string(), ArtemisSubmission[].class);
			return submissionsArray.length;
		} catch (IOException e) {
			throw new ArtemisClientException(e.getMessage(), e);
		}
	}

	@Override
	public LongFeedbackText getLongFeedback(int resultId, Feedback feedback) throws ArtemisClientException {
		Request request = new Request.Builder()//
				.url(this.path("results", resultId, "feedbacks", feedback.getId(), "long-feedback")).get().build();

		return this.call(client, request, LongFeedbackText.class);
	}
}
