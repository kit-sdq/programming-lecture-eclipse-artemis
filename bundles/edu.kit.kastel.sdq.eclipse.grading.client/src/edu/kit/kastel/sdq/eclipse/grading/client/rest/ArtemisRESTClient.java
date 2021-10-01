package edu.kit.kastel.sdq.eclipse.grading.client.rest;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.kit.kastel.sdq.eclipse.grading.api.AbstractArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IAssessor;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExerciseGroup;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IParticipation;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.client.git.EgitGitHandler;
import edu.kit.kastel.sdq.eclipse.grading.client.git.GitException;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.ArtemisCourse;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.ArtemisExercise;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.ArtemisSubmission;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.IMappingLoader;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.exam.ArtemisExam;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.exam.ArtemisExerciseGroup;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.lock.Assessor;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.lock.LockResult;

public class ArtemisRESTClient extends AbstractArtemisClient implements IMappingLoader {

	// paths
	private static final String PROGRAMMING_SUBMISSION_PATHPART = "programming-submissions";
	private static final String EXERCISES_PATHPART = "exercises";
	private static final String COURSES_PATHPART = "courses";
	private static final String EXAMS_PATHPART = "exams";

	private static final String AUTHORIZATION_NAME = "Authorization";

	private static final String JSON_PARSE_ERROR_MESSAGE_CORRUPT_JSON_STRUCTURE = "Error parsing json: Corrupt Json Structure";

	private WebTarget rootApiTarget;
	private String token;

	private ObjectMapper deserializingObjectMapper;
	private ObjectMapper unconfiguredObjectMapper;

	/**
	 *
	 * @param username
	 * @param password
	 * @param hostName the artemis host name.
	 */
	public ArtemisRESTClient(final String username, final String password, final String hostName) {
		super(username, password, hostName);

		this.rootApiTarget = ClientBuilder.newBuilder().build().target(this.getApiRoot());
		this.token = null;
		this.deserializingObjectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		this.unconfiguredObjectMapper = new ObjectMapper();
	}

	private void checkAuthentication() throws ArtemisClientException {
		if (this.token == null) {
			this.login();
		}
	}

	@Override
	public void downloadExerciseAndSubmission(IExercise exercise, ISubmission submission, File directory, IProjectFileNamingStrategy projectFileNamingStrategy)
			throws ArtemisClientException {

		final File projectDirectory = projectFileNamingStrategy.getProjectFileInWorkspace(directory, exercise, submission);
		try {
			if (projectDirectory.exists()) {
				throw new ArtemisClientException("Could not clone project " + projectDirectory.getName() + ", " + "directory already exists!");
			}

			this.downloadTestRepo(exercise, projectDirectory);

			// download submission inside the exercise project directory
			this.downloadSubmission(submission, projectFileNamingStrategy.getAssignmentFileInProjectDirectory(projectDirectory));
		} catch (GitException e) {
			throw new ArtemisClientException("Unable to download exercise and submission: " + e.getMessage(), e);
		}

	}

	protected void downloadSubmission(ISubmission submission, File directory) throws GitException {
		new EgitGitHandler(submission.getRepositoryUrl()).cloneRepo(directory, Constants.MASTER_BRANCH_NAME);
	}

	private void downloadTestRepo(IExercise exercise, File directory) throws GitException {
		new EgitGitHandler(exercise.getTestRepositoryUrl()).cloneRepo(directory, Constants.MASTER_BRANCH_NAME);
	}

	private String getApiRoot() {
		return new StringBuilder(Constants.HTTPS_PREFIX).append(this.getArtemisHostname()).append("/api").toString();
	}

	@Override
	public IAssessor getAssessor() throws ArtemisClientException {
		this.checkAuthentication();

		final Response rsp = this.rootApiTarget.path("users").path(this.getArtemisUsername()).request().header(AUTHORIZATION_NAME, this.token).buildGet()
				.invoke(); // synchronous variant
		this.throwIfStatusUnsuccessful(rsp);

		try {
			return this.parseAssessorResult(rsp.readEntity(String.class));
		} catch (JsonProcessingException e) {
			throw new ArtemisClientException("Error parsing assessor json: " + e.getMessage(), e);
		}
	}

	private Entity<String> getAuthenticationEntity() {
		final ObjectNode authenticationNode = this.unconfiguredObjectMapper.createObjectNode();
		authenticationNode.put("username", this.getArtemisUsername());
		authenticationNode.put("password", this.getArtemisPassword());
		authenticationNode.put("rememberMe", true);

		return Entity.entity(authenticationNode.toString(), MediaType.APPLICATION_JSON_TYPE);
	}

	@Override
	public List<ICourse> getCourses() throws ArtemisClientException {
		this.checkAuthentication();
		final Response rsp = this.rootApiTarget.path(COURSES_PATHPART).request().header(AUTHORIZATION_NAME, this.token).buildGet().invoke(); // synchronous
																																				// variant
		this.throwIfStatusUnsuccessful(rsp);
		String rspString = rsp.readEntity(String.class);

		ArtemisCourse[] coursesArray = this.read(rspString, ArtemisCourse[].class);
		for (ArtemisCourse course : coursesArray) {
			course.init(this);
		}
		return Arrays.asList(coursesArray);
	}

	@Override
	public List<IExam> getExamsForCourse(ICourse course) throws ArtemisClientException {
		final Response examsRsp = this.rootApiTarget.path(COURSES_PATHPART).path(String.valueOf(course.getCourseId())).path(EXAMS_PATHPART).request()
				.header(AUTHORIZATION_NAME, this.token).buildGet().invoke(); // synchronous call
		this.throwIfStatusUnsuccessful(examsRsp);

		ArtemisExam[] examsArray = this.read(examsRsp.readEntity(String.class), ArtemisExam[].class);
		for (ArtemisExam exam : examsArray) {
			exam.init(this, course);
		}

		return Arrays.asList(examsArray);
	}

	@Override
	public List<IExerciseGroup> getExerciseGroupsForExam(IExam exam, ICourse course) throws ArtemisClientException {
		this.checkAuthentication();
		final Response rsp = this.rootApiTarget.path(COURSES_PATHPART).path(String.valueOf(course.getCourseId())).path(EXAMS_PATHPART)
				.path(String.valueOf(exam.getExamId())).path("exam-for-assessment-dashboard") // web client does it that way..
				.request().header(AUTHORIZATION_NAME, this.token).buildGet().invoke(); // synchronous variant
		this.throwIfStatusUnsuccessful(rsp);

		// need to retrieve the exerciseGroups array root node to deserialize it!
		JsonNode detailledExamJsonNode = this.readTree(rsp.readEntity(String.class));
		JsonNode exerciseGroupsJsonArray = detailledExamJsonNode.get("exerciseGroups");
		if (exerciseGroupsJsonArray == null) {
			// exam has no exercise groups!
			return List.of();
		}
		if (!exerciseGroupsJsonArray.isArray()) {
			throw new ArtemisClientException(JSON_PARSE_ERROR_MESSAGE_CORRUPT_JSON_STRUCTURE);
		}

		ArtemisExerciseGroup[] exerciseGroupsArray = this.read(exerciseGroupsJsonArray.toString(), ArtemisExerciseGroup[].class);
		for (ArtemisExerciseGroup exerciseGroup : exerciseGroupsArray) {
			exerciseGroup.init(this);
		}
		return Arrays.asList(exerciseGroupsArray);
	}

	// private Collection

	@Override
	public List<IExercise> getExercisesForCourse(ICourse course) throws ArtemisClientException {
		this.checkAuthentication();
		final Response exercisesAndParticipationsRsp = this.rootApiTarget.path(COURSES_PATHPART).path(String.valueOf(course.getCourseId()))
				.path("with-exercises-and-relevant-participations").request().header(AUTHORIZATION_NAME, this.token).buildGet().invoke(); // synchronous
																																			// call
		this.throwIfStatusUnsuccessful(exercisesAndParticipationsRsp);

		// get the part of the json that we want to deserialize
		final JsonNode exercisesAndParticipationsJsonNode = this.readTree(exercisesAndParticipationsRsp.readEntity(String.class));
		JsonNode exercisesJsonArray = exercisesAndParticipationsJsonNode.get(EXERCISES_PATHPART);
		if (exercisesJsonArray == null) {
			// course has no exercises!
			return List.of();
		}
		if (!exercisesJsonArray.isArray()) {
			throw new ArtemisClientException(JSON_PARSE_ERROR_MESSAGE_CORRUPT_JSON_STRUCTURE);
		}

		// deserialize
		ArtemisExercise[] exercisesArray = this.read(exercisesJsonArray.toString(), ArtemisExercise[].class);
		for (ArtemisExercise exercise : exercisesArray) {
			exercise.init(this);
		}

		return Arrays.stream(exercisesArray).filter(exercise -> "programming".equals(exercise.getType())).collect(Collectors.toList());
	}

	@Override
	public List<ISubmission> getSubmissions(int exerciseID, boolean assessedByTutor) throws ArtemisClientException {
		this.checkAuthentication();
		final Response rsp = this.rootApiTarget.path(EXERCISES_PATHPART).path(String.valueOf(exerciseID)).path(PROGRAMMING_SUBMISSION_PATHPART)
				// .queryParam("submittedOnly", submittedOnly)
				.queryParam("assessedByTutor", assessedByTutor).request().header(AUTHORIZATION_NAME, this.token).buildGet().invoke(); // synchronous
																																		// variant
		this.throwIfStatusUnsuccessful(rsp);

		final String rspEntity = rsp.readEntity(String.class);
		ArtemisSubmission[] submissionsArray = this.read(rspEntity, ArtemisSubmission[].class);

		for (ArtemisSubmission submission : submissionsArray) {
			submission.init();
		}

		return Arrays.asList(submissionsArray);
	}

	@Override
	public List<ISubmission> getSubmissionsForExercise(IExercise exercise) throws ArtemisClientException {
		this.checkAuthentication();
		final Response rsp = this.rootApiTarget.path(EXERCISES_PATHPART).path(String.valueOf(exercise.getExerciseId())).path(PROGRAMMING_SUBMISSION_PATHPART)
				// .queryParam("submittedOnly", true)
				.request().header(AUTHORIZATION_NAME, this.token).buildGet().invoke(); // synchronous variant
		if (!this.isStatusSuccessful(rsp)) {
			// may happen sometimes
			return List.of();
		}

		final String rspEntity = rsp.readEntity(String.class);
		ArtemisSubmission[] submissionsArray = this.read(rspEntity, ArtemisSubmission[].class);
		for (ArtemisSubmission submission : submissionsArray) {
			submission.init();
		}

		return Arrays.asList(submissionsArray);
	}

	private boolean isStatusSuccessful(final Response response) {
		return Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily());
	}

	private void login() throws ArtemisClientException {
		final Response authenticationResponse = this.rootApiTarget.path("authenticate").request().buildPost(this.getAuthenticationEntity()).invoke();

		this.throwIfStatusUnsuccessful(authenticationResponse);
		final String authRspEntity = authenticationResponse.readEntity(String.class);
		try {
			String rawToken = this.unconfiguredObjectMapper.readTree(authRspEntity).get("id_token").asText();
			this.token = "Bearer " + rawToken;
		} catch (IOException e) {
			throw new ArtemisClientException("Authentication to \"" + this.getApiRoot() + "\" failed: No token could be retrieved in server response.");
		}
	}

	private IAssessor parseAssessorResult(final String jsonString) throws JsonProcessingException {
		return this.deserializingObjectMapper.readValue(jsonString, Assessor.class);
	}

	private ILockResult parseLockResult(final String jsonString) throws JsonProcessingException {
		return this.deserializingObjectMapper.readValue(jsonString, LockResult.class);
	}

	private <E> E read(String rspEntity, Class<E> clazz) throws ArtemisClientException {
		try {
			return this.deserializingObjectMapper.readValue(rspEntity, clazz);
		} catch (Exception e) {
			throw new ArtemisClientException(e.getMessage(), e);
		}
	}

	private JsonNode readTree(String readEntity) throws ArtemisClientException {
		try {
			return this.unconfiguredObjectMapper.readTree(readEntity);
		} catch (JsonProcessingException e) {
			throw new ArtemisClientException(e.getMessage(), e);
		}
	}

	@Override
	public void saveAssessment(IParticipation participation, boolean submit, String payload) throws ArtemisClientException {
		this.checkAuthentication();

		final Response rsp = this.rootApiTarget.path("participations").path(Integer.toString(participation.getParticipationID())) //
				.path("manual-results") //
				.queryParam("submit", submit) //
				.request().header(AUTHORIZATION_NAME, this.token).buildPut(this.toJsonStringEntity(payload)).invoke();
		this.throwIfStatusUnsuccessful(rsp);
	}

	@Override
	public ILockResult startAssessment(int submissionID) throws ArtemisClientException {
		this.checkAuthentication();

		final Response rsp = this.rootApiTarget.path(PROGRAMMING_SUBMISSION_PATHPART).path(String.valueOf(submissionID)).path("lock") // this should be best.
				.request().header(AUTHORIZATION_NAME, this.token).buildGet().invoke(); // synchronous variant
		this.throwIfStatusUnsuccessful(rsp);

		try {
			return this.parseLockResult(rsp.readEntity(String.class));
		} catch (JsonProcessingException e) {
			throw new ArtemisClientException("Error parsing lock result json: " + e.getMessage(), e);
		}

	}

	@Override
	public Optional<ILockResult> startNextAssessment(int exerciseID, int correctionRound) throws ArtemisClientException {
		this.checkAuthentication();

		final Response rsp = this.rootApiTarget.path(EXERCISES_PATHPART).path(String.valueOf(exerciseID)).path("programming-submission-without-assessment")
				.queryParam("correction-round", correctionRound).queryParam("lock", true).request().header(AUTHORIZATION_NAME, this.token).buildGet().invoke(); // synchronous
																																								// variant
		if (!this.isStatusSuccessful(rsp)) {
			// no assessment left!
			return Optional.empty();
		}

		try {
			return Optional.of(this.parseLockResult(rsp.readEntity(String.class)));
		} catch (JsonProcessingException e) {
			throw new ArtemisClientException("Error parsing lock result json: " + e.getMessage(), e);
		}
	}

	private void throwIfStatusUnsuccessful(final Response response) throws ArtemisClientException {
		if (!this.isStatusSuccessful(response)) {
			throw new ArtemisClientException("Communication with \"" + this.getApiRoot() + "\" failed with status \"" + response.getStatus() + ": "
					+ response.getStatusInfo().getReasonPhrase() + "\".");
		}
	}

	private Entity<String> toJsonStringEntity(String jsonString) {

		return Entity.entity(jsonString, MediaType.APPLICATION_JSON_TYPE);
	}

}
