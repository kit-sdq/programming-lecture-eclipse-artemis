/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.client.rest;

import java.net.ConnectException;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ICourse;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ParticipationDTO;
import edu.kit.kastel.eclipse.common.api.client.IParticipationArtemisClient;

public class ParticipationArtemisClient extends AbstractArtemisClient implements IParticipationArtemisClient {

	private WebTarget endpoint;
	private String token;

	public ParticipationArtemisClient(final String hostName, String token) {
		super(hostName);

		this.endpoint = getEndpoint(this.getApiRootURL());
		this.token = token;
	}

	@Override
	public ParticipationDTO startParticipationForExercise(ICourse couse, IExercise exercise) throws ArtemisClientException, ConnectException {
		final Response exercisesRsp = this.endpoint.path(EXERCISES_PATHPART).path(String.valueOf(exercise.getExerciseId())).path("participations").request()
				.header(AUTHORIZATION_NAME, this.token).buildPost(null).invoke();

		this.throwIfStatusUnsuccessful(exercisesRsp);

		return this.read(exercisesRsp.readEntity(String.class), ParticipationDTO.class);
	}

	@Override
	public ParticipationDTO resumeParticipationForExercise(ICourse couse, IExercise exercise) throws ArtemisClientException {
		final Response exercisesRsp = this.endpoint.path(EXERCISES_PATHPART).path(String.valueOf(exercise.getExerciseId()))
				.path("resume-programming-participation").request().header(AUTHORIZATION_NAME, this.token).buildPut(null).invoke();

		this.throwIfStatusUnsuccessful(exercisesRsp);

		return this.read(exercisesRsp.readEntity(String.class), ParticipationDTO.class);
	}

	@Override
	public ParticipationDTO getParticipationForExercise(ICourse couse, IExercise exercise) throws ArtemisClientException {
		final Response exercisesRsp = this.endpoint.path(EXERCISES_PATHPART).path(String.valueOf(exercise.getExerciseId())).path("participation").request()
				.header(AUTHORIZATION_NAME, this.token).buildGet().invoke();

		this.throwIfStatusUnsuccessful(exercisesRsp);

		return this.read(exercisesRsp.readEntity(String.class), ParticipationDTO.class);
	}

	@Override
	public ParticipationDTO getParticipationWithLatestResultForExercise(int participationId) throws ArtemisClientException {
		final Response exercisesRsp = this.endpoint.path(PARTICIPATION_PATHPART).path(Integer.toString(participationId)).path("withLatestResult").request()
				.header(AUTHORIZATION_NAME, this.token).buildGet().invoke();

		this.throwIfStatusUnsuccessful(exercisesRsp);

		return this.read(exercisesRsp.readEntity(String.class), ParticipationDTO.class);
	}

}
