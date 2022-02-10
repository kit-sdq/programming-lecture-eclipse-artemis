package edu.kit.kastel.sdq.eclipse.grading.client.rest;

import java.util.Optional;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.AssessmentResult;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ParticipationDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.client.IAssessmentArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.lock.LockResult;

public class AssessmentArtemisClient extends AbstractArtemisClient implements IAssessmentArtemisClient {

	private static final ILog log = Platform.getLog(AssessmentArtemisClient.class);

	private WebTarget endpoint;
	private String token;

	public AssessmentArtemisClient(final String hostName, String token) {
		super(hostName);

		this.endpoint = getEndpoint(this.getApiRootURL());
		this.token = token;
	}

	@Override
	public void saveAssessment(ParticipationDTO participation, boolean submit, AssessmentResult assessment)
			throws ArtemisClientException {
		String assessmentPayload = this.payload(assessment);
		log.info(String.format("Saving assessment for submission %s with json: %s", assessment.getId(), assessmentPayload));
		
		final Response rsp = this.endpoint.path("participations").path(String.valueOf(participation.getParticipationID())) //
				.path("manual-results") //
				.queryParam("submit", submit) //
				.request().header(AUTHORIZATION_NAME, this.token).buildPut(Entity.json(assessmentPayload)).invoke();
		this.throwIfStatusUnsuccessful(rsp);

	}

	@Override
	public ILockResult startAssessment(ISubmission submission) throws ArtemisClientException {
		final Response rsp = this.endpoint.path(PROGRAMMING_SUBMISSION_PATHPART).path(String.valueOf(submission.getSubmissionId())).path("lock").request()
				.header(AUTHORIZATION_NAME, this.token).buildGet().invoke(); // synchronous variant
		this.throwIfStatusUnsuccessful(rsp);
		return this.read(rsp.readEntity(String.class), LockResult.class);
	}

	@Override
	public Optional<ILockResult> startNextAssessment(IExercise exercise, int correctionRound)
			throws ArtemisClientException {
		final Response rsp = this.endpoint.path(EXERCISES_PATHPART).path(String.valueOf(exercise.getExerciseId()))
				.path("programming-submission-without-assessment").queryParam("correction-round", correctionRound).queryParam("lock", true).request()
				.header(AUTHORIZATION_NAME, this.token).buildGet().invoke();

		if (!this.isStatusSuccessful(rsp)) {
			// no assessment left!
			return Optional.empty();
		}

		return Optional.of(this.read(rsp.readEntity(String.class), LockResult.class));
	}

}
