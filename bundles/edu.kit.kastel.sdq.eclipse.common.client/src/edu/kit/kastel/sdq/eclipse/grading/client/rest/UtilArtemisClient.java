package edu.kit.kastel.sdq.eclipse.grading.client.rest;

import java.time.LocalDateTime;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.client.IUtilArtemisClient;

public class UtilArtemisClient extends AbstractArtemisClient implements IUtilArtemisClient {
	private WebTarget endpoint;

	public UtilArtemisClient(final String hostName) {
		super(hostName);

		this.endpoint = getEndpoint(this.getRootURL());
	}

	@Override
	public LocalDateTime getTime() throws ArtemisClientException {
		final Response exercisesRsp = this.endpoint.path("time").request().buildGet().invoke();

		this.throwIfStatusUnsuccessful(exercisesRsp);

		final JsonNode exercisesAndParticipationsJsonNode = this.readTree(exercisesRsp.readEntity(String.class));
		return this.read(exercisesAndParticipationsJsonNode.toString(), LocalDateTime.class);
	}

}
