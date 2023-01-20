/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.client.rest;

import java.time.LocalDateTime;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.client.IUtilArtemisClient;
import edu.kit.kastel.eclipse.common.api.util.Version;

public class UtilArtemisClient extends AbstractArtemisClient implements IUtilArtemisClient {
	private WebTarget endpoint;

	public UtilArtemisClient(final String hostName) {
		super(hostName);

		this.endpoint = getEndpoint(this.getRootURL());
	}

	@Override
	public LocalDateTime getTime() throws ArtemisClientException {
		final Response response = this.endpoint.path("time").request().buildGet().invoke();
		this.throwIfStatusUnsuccessful(response);

		return this.read(response.readEntity(String.class), LocalDateTime.class);
	}

	@Override
	public Version getVersion() throws ArtemisClientException {
		final Response response = this.endpoint.path("management/info").request().buildGet().invoke();
		this.throwIfStatusUnsuccessful(response);
		var info = this.read(response.readEntity(String.class), Info.class);
		return Version.fromString(info.build.version);
	}

	private static class Info {
		@JsonProperty
		private Build build;

		private static class Build {
			@JsonProperty
			private String version;
		}
	}
}
