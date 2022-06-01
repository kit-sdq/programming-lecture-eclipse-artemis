/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.client.rest;

import java.time.LocalDateTime;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.client.IUtilArtemisClient;

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

		return this.read(exercisesRsp.readEntity(String.class), LocalDateTime.class);
	}

}
