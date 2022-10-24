/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.client.rest;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.messages.Messages;

/**
 * Encapsulates methods to get data from and send data to Artemis
 */
public abstract class AbstractArtemisClient {

	private static final String PROTOCOL_PREFIX = "https://";

	// paths
	protected static final String PROGRAMMING_SUBMISSION_PATHPART = "programming-submissions";
	protected static final String EXERCISES_PATHPART = "exercises";
	protected static final String COURSES_PATHPART = "courses";
	protected static final String EXAMS_PATHPART = "exams";
	protected static final String PARTICIPATION_PATHPART = "participations";
	protected static final String RESULT_PATHPART = "results";
	protected static final String STUDENT_EXAM_PATH = "student-exams";

	private static final String COOKIE_NAME_JWT = "jwt";

	protected final String hostname;
	private ObjectMapper orm;

	/**
	 * @param artemisUsername for login to artemis
	 * @param artemisPassword for login to artemis
	 * @param artemisHostname the hostname, only! (e.g. "test.kit.edu")
	 */
	protected AbstractArtemisClient(String artemisHostname) {
		this.hostname = artemisHostname;
		this.orm = this.createObjectMapper();
	}

	protected final String getRootURL() {
		String endpoint = this.hostname;
		if (!endpoint.startsWith(PROTOCOL_PREFIX)) {
			endpoint = PROTOCOL_PREFIX + endpoint;
		}

		if (endpoint.endsWith("/")) {
			endpoint = endpoint.substring(0, endpoint.length() - 1);
		}

		return endpoint;
	}

	protected final String getApiRootURL() {
		return this.getRootURL() + "/api";
	}

	protected WebTarget getEndpoint(String url) {
		return ClientBuilder.newBuilder().build().target(url);
	}

	protected void throwIfStatusUnsuccessful(final Response response) throws ArtemisClientException {
		if (!this.isStatusSuccessful(response)) {
			throw new ArtemisClientException(String.format(Messages.CLIENT_COMMUNICATION_ERROR_FORMAT, this.getApiRootURL(), response.getStatus(),
					response.getStatusInfo().getReasonPhrase()));
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

	protected boolean isStatusSuccessful(final Response response) {
		return Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily());
	}

	private ObjectMapper createObjectMapper() {
		return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).setSerializationInclusion(Include.NON_NULL);
	}

	protected final Cookie getAuthCookie(String jwtToken) {
		return new Cookie(COOKIE_NAME_JWT, jwtToken);
	}

}
