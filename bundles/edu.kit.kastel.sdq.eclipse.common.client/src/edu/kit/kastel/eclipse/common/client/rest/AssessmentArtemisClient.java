/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.client.rest;

import java.util.Optional;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.artemis.AssessmentResult;
import edu.kit.kastel.eclipse.common.api.artemis.ILockResult;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ISubmission;
import edu.kit.kastel.eclipse.common.api.client.IAssessmentArtemisClient;
import edu.kit.kastel.eclipse.common.api.controller.ExerciseStats;
import edu.kit.kastel.eclipse.common.client.mappings.ArtemisSubmission;
import edu.kit.kastel.eclipse.common.client.mappings.lock.LockResult;
import edu.kit.kastel.eclipse.common.client.mappings.stats.Stats;
import edu.kit.kastel.eclipse.common.client.mappings.stats.Timing;

public class AssessmentArtemisClient extends AbstractArtemisClient implements IAssessmentArtemisClient {
	private static final ILog log = Platform.getLog(AssessmentArtemisClient.class);

	private static final String SUBMISSION_WIHOUT_ASSESSMENT_PATH = "programming-submission-without-assessment";
	private static final String PARTICIPATION_PATHPART = "participations";
	private static final String MANUAL_RESULT_PATHPART = "manual-results";
	private static final String CORRECTION_ROUND_QUERY_PARAM = "correction-round";
	private static final String LOCK_QUERY_PARAM = "lock";
	private static final String SUBMIT_QUERY_PARAM = "submit";
	protected static final String STATS_PATH = "stats-for-assessment-dashboard";

	private WebTarget endpoint;
	private String token;

	public AssessmentArtemisClient(final String hostName, String token) {
		super(hostName);

		this.endpoint = this.getEndpoint(this.getApiRootURL());
		this.token = token;
	}

	@Override
	public void saveAssessment(int participationId, boolean submit, AssessmentResult assessment) throws ArtemisClientException {
		String assessmentPayload = this.payload(assessment);
		log.info(String.format("Saving assessment for submission %s with json: %s", assessment.id, assessmentPayload));

		final Response rsp = this.endpoint.path(PARTICIPATION_PATHPART).path(String.valueOf(participationId)) //
				.path(MANUAL_RESULT_PATHPART) //
				.queryParam(SUBMIT_QUERY_PARAM, submit) //
				.request().header(AUTHORIZATION_NAME, this.token).buildPut(Entity.json(assessmentPayload)).invoke();
		this.throwIfStatusUnsuccessful(rsp);

	}

	@Override
	public ILockResult startAssessment(ISubmission submission) throws ArtemisClientException {
		final Response rsp = this.endpoint.path(PROGRAMMING_SUBMISSION_PATHPART).path(String.valueOf(submission.getSubmissionId())).path(LOCK_QUERY_PARAM)
				.queryParam(CORRECTION_ROUND_QUERY_PARAM, submission.getCorrectionRound()).request().header(AUTHORIZATION_NAME, this.token).buildGet().invoke();

		this.throwIfStatusUnsuccessful(rsp);
		return this.read(rsp.readEntity(String.class), LockResult.class);
	}

	@Override
	public Optional<ILockResult> startNextAssessment(IExercise exercise, int correctionRound) throws ArtemisClientException {
		final Response rsp = this.endpoint.path(EXERCISES_PATHPART).path(String.valueOf(exercise.getExerciseId())).path(SUBMISSION_WIHOUT_ASSESSMENT_PATH)
				.queryParam(CORRECTION_ROUND_QUERY_PARAM, correctionRound).queryParam(LOCK_QUERY_PARAM, true).request().header(AUTHORIZATION_NAME, this.token)
				.buildGet().invoke();

		if (!this.isStatusSuccessful(rsp)) {
			// no assessment left!
			return Optional.empty();
		}
		return Optional.of(this.read(rsp.readEntity(String.class), LockResult.class));
	}

	@Override
	public ExerciseStats getStats(IExercise exercise) throws ArtemisClientException {
		final Response rsp = this.endpoint.path(EXERCISES_PATHPART).path(String.valueOf(exercise.getExerciseId())).path(STATS_PATH).request()
				.header(AUTHORIZATION_NAME, this.token).buildGet().invoke();
		if (!this.isStatusSuccessful(rsp)) {
			return null;
		}

		var stats = this.read(rsp.readEntity(String.class), Stats.class);

		int submissionsInRound1 = countSubmissions(exercise, 0);
		int submissionsInRound2 = 0;
		if (exercise.hasSecondCorrectionRound())
			submissionsInRound2 = countSubmissions(exercise, 1);

		return new ExerciseStats( //
				countInRounds(stats.numberOfAssessmentsOfCorrectionRounds()), //
				stats.numberOfSubmissions().inTime(), //
				stats.totalNumberOfAssessmentLocks(), //
				submissionsInRound1 + submissionsInRound2 //
		);

	}

	private int countInRounds(Timing[] rounds) {
		int countInTime = 0;
		for (var round : rounds)
			countInTime += round.inTime();
		return countInTime;
	}

	private int countSubmissions(IExercise exercise, int correctionRound) throws ArtemisClientException {
		final Response rsp = this.endpoint.path(EXERCISES_PATHPART).path(String.valueOf(exercise.getExerciseId())).path(PROGRAMMING_SUBMISSION_PATHPART) //
				.queryParam("assessedByTutor", true) //
				.queryParam("correction-round", correctionRound) //
				.request().header(AUTHORIZATION_NAME, this.token).buildGet().invoke();

		if (!this.isStatusSuccessful(rsp)) {
			return 0;
		}
		ArtemisSubmission[] submissionsArray = this.read(rsp.readEntity(String.class), ArtemisSubmission[].class);
		return submissionsArray.length;
	}

}
