package edu.kit.kastel.sdq.eclipse.grading.api.client;

import java.io.Serializable;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.Constants;

/**
 * Encapsulates methods to get data from and send data to Artemis
 */
public abstract class AbstractArtemisClient implements IArtemisClient {

	// paths
	protected static final String PROGRAMMING_SUBMISSION_PATHPART = "programming-submissions";
	protected static final String EXERCISES_PATHPART = "exercises";
	protected static final String COURSES_PATHPART = "courses";
	protected static final String EXAMS_PATHPART = "exams";
	protected static final String USERS_PATHPART = "users";
	protected static final String PARTICIPATION_PATHPART = "participations";
	protected static final String RESULT_PATHPART = "results";
	protected static final String STUDENT_EXAM_PATH = "student-exams";
	protected static final String JSON_PARSE_ERROR_MESSAGE_CORRUPT_JSON_STRUCTURE = "Error parsing json: Corrupt Json Structure";
	protected static final String AUTHORIZATION_NAME = "Authorization";

	private String artemisUsername;
	private String artemisPassword;
	private String artemisHostname;
	private ObjectMapper orm;

	/**
	 *
	 * @param artemisUsername for login to artemis
	 * @param artemisPassword for login to artemis
	 * @param artemisHostname the hostname, only! (e.g. "test.kit.edu")
	 */
	protected AbstractArtemisClient(String artemisUsername, String artemisPassword, String artemisHostname) {
		this.artemisUsername = artemisUsername;
		this.artemisPassword = artemisPassword;
		this.artemisHostname = artemisHostname;
		this.orm = createObjectMapper();
	}

	public boolean isReady() {
		return !(this.artemisHostname.isBlank() && this.artemisUsername.isBlank() && this.artemisPassword.isBlank());
	}

	protected final String getRootURL() {
		String endpoint = this.artemisHostname;
		if (!endpoint.startsWith(Constants.HTTPS_PREFIX)) {
			endpoint = Constants.HTTPS_PREFIX + endpoint;
		}

		if (endpoint.endsWith("/")) {
			endpoint = endpoint.substring(0, endpoint.length() - 1);
		}

		return endpoint;
	}
	
	protected final String getApiRootURL() {
		return getRootURL() + "/api";
	}

	public String getArtemisUsername() {
		return this.artemisUsername;
	}

	protected final AuthenticationEntity getAuthenticationEntity() {
		AuthenticationEntity entity = new AuthenticationEntity();
		entity.username = this.artemisUsername;
		entity.password = this.artemisPassword;
		return entity;
	}

	protected static final class AuthenticationEntity implements Serializable {
		private static final long serialVersionUID = -6291795795865534155L;
		@JsonProperty
		private String username;
		@JsonProperty
		private String password;
		@JsonProperty
		private boolean rememberMe = true;
	}
	
	protected void checkAuthentication() throws ArtemisClientException {
		if (this.token == null) {
			this.login();
		}
	}
	
	protected String getToken() {
		return token;
	}
	
	protected void throwIfStatusUnsuccessful(final Response response) throws ArtemisClientException {
		if (!this.isStatusSuccessful(response)) {
			throw new ArtemisClientException("Communication with \"" + this.getApiRootURL() + "\" failed with status \"" + response.getStatus() + ": "
					+ response.getStatusInfo().getReasonPhrase() + "\".");
		}
	}
	
	protected <E> String payload(E rspEntity) throws ArtemisClientException {
		try {
			return this.orm.writeValueAsString(rspEntity);
		} catch (Exception e) {
			throw new ArtemisClientException(e.getMessage(), e);
		}
	}
	
	protected <E> E read(String rspEntity, Class<E> clazz) throws ArtemisClientException {
		try {
			return this.orm.readValue(rspEntity, clazz);
		} catch (Exception e) {
			throw new ArtemisClientException(e.getMessage(), e);
		}
	}

	protected JsonNode readTree(String readEntity) throws ArtemisClientException {
		try {
			return this.orm.readTree(readEntity);
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

}
