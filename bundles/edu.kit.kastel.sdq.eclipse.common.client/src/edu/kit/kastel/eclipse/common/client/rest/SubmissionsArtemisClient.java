/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.client.rest;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ISubmission;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.User;
import edu.kit.kastel.eclipse.common.api.client.ISubmissionsArtemisClient;
import edu.kit.kastel.eclipse.common.api.messages.Messages;
import edu.kit.kastel.eclipse.common.client.mappings.ArtemisSubmission;

public class SubmissionsArtemisClient extends AbstractArtemisClient implements ISubmissionsArtemisClient {
	private WebTarget endpoint;
	private String token;
	private User assesor;

	public SubmissionsArtemisClient(final String hostName, String token, User assessor) {
		super(hostName);

		this.endpoint = this.getEndpoint(this.getApiRootURL());
		this.token = token;
		this.assesor = assessor;
	}

	@Override
	public List<ISubmission> getSubmissions(IExercise exercise, int correctionRound) throws ArtemisClientException {
		boolean isInstructor = exercise.getCourse().isInstructor(this.assesor);

		final Response rsp = this.endpoint.path(EXERCISES_PATHPART).path(String.valueOf(exercise.getExerciseId())).path(PROGRAMMING_SUBMISSION_PATHPART) //
				.queryParam("assessedByTutor", !isInstructor) //
				.queryParam("correction-round", correctionRound) //
				.request().cookie(getAuthCookie(this.token)).buildGet().invoke();

		this.throwIfStatusUnsuccessful(rsp);

		final String rspEntity = rsp.readEntity(String.class);
		ArtemisSubmission[] submissionsArray = this.read(rspEntity, ArtemisSubmission[].class);

		for (ArtemisSubmission submission : submissionsArray) {
			submission.init(correctionRound);
		}

		return Arrays.asList(submissionsArray);
	}

	@Override
	public ISubmission getSubmissionById(IExercise artemisExercise, int submissionId) throws ArtemisClientException {
		List<ISubmission> submissions = this.getSubmissions(artemisExercise);
		ISubmission target = submissions.stream().filter(s -> s.getSubmissionId() == submissionId).findFirst().orElse(null);
		if (target == null) {
			throw new ArtemisClientException(String.format(Messages.CLIENT_NO_SUBMISSION_FOUND_FORMAT, submissionId));
		}
		return target;
	}

}
