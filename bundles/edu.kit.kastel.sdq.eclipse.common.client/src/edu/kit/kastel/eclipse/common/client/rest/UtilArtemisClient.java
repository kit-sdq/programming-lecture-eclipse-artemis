/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.client.rest;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.client.IUtilArtemisClient;
import edu.kit.kastel.eclipse.common.api.util.Version;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class UtilArtemisClient extends AbstractArtemisClient implements IUtilArtemisClient {
	private final OkHttpClient client;

	public UtilArtemisClient(final String hostname) {
		super(hostname);
		this.client = this.createClient(null);
	}

	@Override
	public LocalDateTime getTime() throws ArtemisClientException {
		Request request = new Request.Builder().url(this.path("public", "time")).get().build();
		return this.call(this.client, request, LocalDateTime.class);
	}

	@Override
	public Version getVersion() throws ArtemisClientException {
		Request request = new Request.Builder().url(this.getRootURL() + "/management/info").get().build();
		var info = this.call(this.client, request, Info.class);
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
