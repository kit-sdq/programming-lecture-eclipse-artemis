package edu.kit.kastel.sdq.eclipse.grading.client.rest;

import java.time.LocalDateTime;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;

import com.fasterxml.jackson.databind.JsonNode;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.client.IUtilArtemisClient;

public class UtilArtemisClient extends AbstractArtemisClient implements IUtilArtemisClient {
	private static final ILog log = Platform.getLog(UtilArtemisClient.class);

	private WebTarget endpoint;
	private String token;

	public UtilArtemisClient(final String hostName, String token) {
		super(hostName);

		this.endpoint = getEndpoint(this.getRootURL());
		this.token = token;
	}

	@Override
	public LocalDateTime getTime() throws ArtemisClientException {
		final Response exercisesRsp = this.endpoint.path("time").request().buildGet().invoke();

		this.throwIfStatusUnsuccessful(exercisesRsp);

		final JsonNode exercisesAndParticipationsJsonNode = this.readTree(exercisesRsp.readEntity(String.class));
		return this.read(exercisesAndParticipationsJsonNode.toString(), LocalDateTime.class);
	}

}
