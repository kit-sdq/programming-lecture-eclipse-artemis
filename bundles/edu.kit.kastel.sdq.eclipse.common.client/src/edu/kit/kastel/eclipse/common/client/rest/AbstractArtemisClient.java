/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.client.rest;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.messages.Messages;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Encapsulates methods to get data from and send data to Artemis
 */
public abstract class AbstractArtemisClient {
	private static final ILog log = Platform.getLog(AbstractArtemisClient.class);
	private static final String DEFAULT_PROTOCOL_PREFIX = "https://";

	// paths
	protected static final String PROGRAMMING_SUBMISSIONS_PATHPART = "programming-submissions";
	protected static final String EXERCISES_PATHPART = "exercises";
	protected static final String COURSES_PATHPART = "courses";
	protected static final String EXAMS_PATHPART = "exams";
	protected static final String PARTICIPATIONS_PATHPART = "participations";
	protected static final String RESULT_PATHPART = "results";

	protected static final String COOKIE_NAME_JWT = "jwt";

	protected static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
	protected final String hostname;
	// e.g., https://
	private final String protocol;
	private ObjectMapper orm;

	/**
	 * @param artemisUsername for login to artemis
	 * @param artemisPassword for login to artemis
	 * @param hostname        the hostname of the artemis system. Will be
	 *                        transformed to domain-name:port
	 */
	protected AbstractArtemisClient(String hostname) {
		this.protocol = extractProtocol(hostname.trim());
		this.hostname = cleanupHostnameString(hostname.trim(), this.protocol);
		this.orm = this.createObjectMapper();
	}

	protected final OkHttpClient createClient(String token) {
		OkHttpClient.Builder builder = new OkHttpClient.Builder() //
				.connectTimeout(5, TimeUnit.SECONDS) //
				.callTimeout(20, TimeUnit.SECONDS)//
				.readTimeout(20, TimeUnit.SECONDS)//
				.writeTimeout(20, TimeUnit.SECONDS);

		if (token != null && !token.isBlank()) {
			builder = builder.cookieJar(new CookieJar() {
				@Override
				public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
					// NOP
				}

				@Override
				public List<Cookie> loadForRequest(HttpUrl url) {
					return List.of(new Cookie.Builder().domain(AbstractArtemisClient.this.hostname).path("/").name(COOKIE_NAME_JWT).value(token).httpOnly()
							.secure().build());
				}
			});
		}
		return builder.build();
	}

	protected final <R> R call(OkHttpClient client, Request request, Class<R> resultClass) throws ArtemisClientException {
		try (Response response = client.newCall(request).execute()) {
			this.throwIfStatusUnsuccessful(response);
			if (resultClass == null) {
				return null;
			}
			return this.read(response.body().string(), resultClass);
		} catch (IOException e) {
			throw new ArtemisClientException(e.getMessage(), e);
		}
	}

	protected final HttpUrl path(Object... path) {
		String requestPath = this.getApiRootURL();
		for (Object segment : path) {
			requestPath += "/" + segment;
		}
		return HttpUrl.parse(requestPath);
	}

	protected final String getRootURL() {
		return this.protocol + this.hostname;
	}

	private static String cleanupHostnameString(String hostname, String protocol) {
		String finalHostname = hostname;
		if (hostname.startsWith(protocol)) {
			finalHostname = finalHostname.substring(protocol.length());
		}

		if (finalHostname.contains("/")) {
			finalHostname = finalHostname.split("/", 2)[0];
		}

		log.info("Using " + finalHostname + " as hostname of artemis. Protocol is " + protocol);
		return finalHostname;

	}

	private static String extractProtocol(String hostname) {
		if (!hostname.contains("://")) {
			return DEFAULT_PROTOCOL_PREFIX;
		}
		return hostname.split("://", 2)[0] + "://";
	}

	protected final String getApiRootURL() {
		return this.getRootURL() + "/api";
	}

	protected void throwIfStatusUnsuccessful(final Response response) throws ArtemisClientException {
		if (!response.isSuccessful()) {
			throw new ArtemisClientException(String.format(Messages.CLIENT_COMMUNICATION_ERROR_FORMAT, this.getApiRootURL(), response.code(), "HTTP_ERROR"));
		}
	}

	protected <E> String payload(E rspEntity) throws ArtemisClientException {
		try {
			return this.orm.writeValueAsString(rspEntity);
		} catch (JsonProcessingException e) {
			throw new ArtemisClientException(e.getMessage(), e);
		}
	}

	protected <E> E read(String rspEntity, Class<E> clazz) throws ArtemisClientException {
		try {
			return this.orm.readValue(rspEntity, clazz);
		} catch (JsonProcessingException e) {
			throw new ArtemisClientException(e.getMessage(), e);
		}
	}

	private ObjectMapper createObjectMapper() {
		return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).setSerializationInclusion(Include.NON_NULL);
	}

}
