package edu.kit.kastel.sdq.eclipse.grading.client.rest;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;

import com.fasterxml.jackson.databind.JsonNode;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ParticipationDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.client.IParticipationArtemisClient;

public class ParticipationArtemisClient extends AbstractArtemisClient implements IParticipationArtemisClient {
	private static final ILog log = Platform.getLog(ParticipationArtemisClient.class);

	private WebTarget endpoint;
	private String token;

	public ParticipationArtemisClient(final String hostName, String token) {
		super(hostName);

		this.endpoint = getEndpoint(this.getApiRootURL());
		this.token = token;
	}

	@Override
	public ParticipationDTO startParticipationForExercise(ICourse couse, IExercise exercise) throws ArtemisClientException {
		final Response exercisesRsp = this.endpoint.path(EXERCISES_PATHPART).path(String.valueOf(exercise.getExerciseId())).path("participations").request()
				.header(AUTHORIZATION_NAME, this.token).buildPost(null).invoke();

		this.throwIfStatusUnsuccessful(exercisesRsp);

		// get the part of the json that we want to deserialize
		final JsonNode exercisesAndParticipationsJsonNode = this.readTree(exercisesRsp.readEntity(String.class));
		return this.read(exercisesAndParticipationsJsonNode.toString(), ParticipationDTO.class);
	}

	@Override
	public ParticipationDTO resumeParticipationForExercise(ICourse couse, IExercise exercise) throws ArtemisClientException {
		final Response exercisesRsp = this.endpoint.path(EXERCISES_PATHPART).path(String.valueOf(exercise.getExerciseId()))
				.path("resume-programming-participation").request().header(AUTHORIZATION_NAME, this.token).buildPut(null).invoke();

		this.throwIfStatusUnsuccessful(exercisesRsp);

		// get the part of the json that we want to deserialize
		final JsonNode exercisesAndParticipationsJsonNode = this.readTree(exercisesRsp.readEntity(String.class));
		return this.read(exercisesAndParticipationsJsonNode.toString(), ParticipationDTO.class);
	}

	@Override
	public ParticipationDTO getParticipationForExercise(ICourse couse, IExercise exercise) throws ArtemisClientException {
		final Response exercisesRsp = this.endpoint.path(EXERCISES_PATHPART).path(String.valueOf(exercise.getExerciseId())).path("participation").request()
				.header(AUTHORIZATION_NAME, this.token).buildGet().invoke();

		this.throwIfStatusUnsuccessful(exercisesRsp);

		// get the part of the json that we want to deserialize
		final JsonNode exercisesAndParticipationsJsonNode = this.readTree(exercisesRsp.readEntity(String.class));
		return this.read(exercisesAndParticipationsJsonNode.toString(), ParticipationDTO.class);
	}

	@Override
	public ParticipationDTO getParticipationWithLatestResultForExercise(int participationId) throws ArtemisClientException {
		final Response exercisesRsp = this.endpoint.path(PARTICIPATION_PATHPART).path(Integer.toString(participationId)).path("withLatestResult").request()
				.header(AUTHORIZATION_NAME, this.token).buildGet().invoke();

		this.throwIfStatusUnsuccessful(exercisesRsp);

		// get the part of the json that we want to deserialize
		final JsonNode exercisesAndParticipationsJsonNode = this.readTree(exercisesRsp.readEntity(String.class));
		return this.read(exercisesAndParticipationsJsonNode.toString(), ParticipationDTO.class);
	}

}
