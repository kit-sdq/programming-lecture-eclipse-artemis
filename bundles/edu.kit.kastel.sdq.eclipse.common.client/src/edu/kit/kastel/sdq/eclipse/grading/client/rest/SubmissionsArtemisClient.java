/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.grading.client.rest;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Assessor;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.api.client.ISubmissionsArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.ArtemisSubmission;

public class SubmissionsArtemisClient extends AbstractArtemisClient implements ISubmissionsArtemisClient {
	private WebTarget endpoint;
	private String token;
	private Assessor assesor;

	public SubmissionsArtemisClient(final String hostName, String token, Assessor assesor) {
		super(hostName);

		this.endpoint = getEndpoint(this.getApiRootURL());
		this.token = token;
		this.assesor = assesor;
	}

	@Override
	public List<ISubmission> getSubmissions(IExercise exercise, int correctionRound) throws ArtemisClientException {
		boolean isInstructor = exercise.getCourse().isInstructor(assesor);

		final Response rsp = this.endpoint.path(EXERCISES_PATHPART).path(String.valueOf(exercise.getExerciseId())).path(PROGRAMMING_SUBMISSION_PATHPART) //
				.queryParam("assessedByTutor", !isInstructor) //
				.queryParam("correction-round", correctionRound) //
				.request().header(AUTHORIZATION_NAME, this.token).buildGet().invoke();

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
			throw new ArtemisClientException("Submission " + submissionId + " not found!");
		}
		return target;
	}

}
