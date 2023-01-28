/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.client.rest;

import java.net.ConnectException;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ICourse;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ParticipationDTO;
import edu.kit.kastel.eclipse.common.api.client.IParticipationArtemisClient;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ParticipationArtemisClient extends AbstractArtemisClient implements IParticipationArtemisClient {

	private final OkHttpClient client;

	public ParticipationArtemisClient(final String hostName, String token) {
		super(hostName);
		this.client = this.createClient(token);
	}

	@Override
	public ParticipationDTO startParticipation(ICourse couse, IExercise exercise) throws ArtemisClientException, ConnectException {
		Request request = new Request.Builder() //
				.url(this.path(EXERCISES_PATHPART, exercise.getExerciseId(), "participations")).post(RequestBody.create("", JSON)).build();
		return this.call(this.client, request, ParticipationDTO.class);
	}

	@Override
	public ParticipationDTO getParticipation(ICourse couse, IExercise exercise) throws ArtemisClientException {
		Request request = new Request.Builder() //
				.url(this.path(EXERCISES_PATHPART, exercise.getExerciseId(), "participation")).get().build();
		return this.call(this.client, request, ParticipationDTO.class);
	}

	@Override
	public ParticipationDTO getParticipationWithLatestResult(int participationId) throws ArtemisClientException {
		Request request = new Request.Builder() //
				.url(this.path(PARTICIPATION_PATHPART, participationId, "withLatestResult")).get().build();
		return this.call(this.client, request, ParticipationDTO.class);
	}

}
