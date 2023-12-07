/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.api;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kit.kastel.sdq.artemis4j.util.Version;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class EclipseArtemisConstants {
	private EclipseArtemisConstants() {
		throw new IllegalAccessError();
	}

	public static final Version GITHUB_GRADING_TOOL_VERSION = loadVersion();

	public static final String GRADING_WIKI_URL = "https://github.com/kit-sdq/programming-lecture-eclipse-artemis/wiki";

	private static final String GITHUB_REST_ENDPOINT = "https://api.github.com/repos/kit-sdq/programming-lecture-eclipse-artemis/releases/latest";

	private static final ILog log = Platform.getLog(EclipseArtemisConstants.class);

	private static Version loadVersion() {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(GITHUB_REST_ENDPOINT).get().build();
		ObjectMapper orm = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		Version release = new Version(0, 0, 0);
		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful()) {
				String releaseVersionString = orm.readValue(response.body().string(), GitHubRelease.class).tagName;
				release = Version.fromString(releaseVersionString.substring(1));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return release;
	}

	private static final class GitHubRelease {
		@JsonProperty("tag_name")
		private String tagName;
	}
}
