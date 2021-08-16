package edu.kit.kastel.sdq.eclipse.grading.client.rest;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.security.sasl.AuthenticationException;
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

import edu.kit.kastel.sdq.eclipse.grading.api.AbstractArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IAssessor;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExerciseGroup;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.client.git.EgitGitHandler;
import edu.kit.kastel.sdq.eclipse.grading.client.git.GitException;
import edu.kit.kastel.sdq.eclipse.grading.client.lockstuff.Assessor;
import edu.kit.kastel.sdq.eclipse.grading.client.lockstuff.LockResult;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.ArtemisCourse;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.ArtemisExercise;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.ArtemisSubmission;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.exam.ArtemisExam;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.exam.ArtemisExerciseGroup;

public class ArtemisRESTClient extends AbstractArtemisClient  {

	/**
	 * TODO review this. Dont know what usernames are to be expected...
	 * TODO nicht das verwenden. Ich habe den Benutzernamen!
	 */
	private static final String USERNAME_REGEX = "[0-9A-Za-z.\\-]+";

	//json fields
	private static final String TITLE_FIELD = "title";

	//paths
	private static final String PROGRAMMING_SUBMISSION_PATHPART = "programming-submissions";
	private static final String EXERCISES_PATHPART = "exercises";
	private static final String COURSES_PATHPART = "courses";
	private static final String EXAMS_PATHPART = "exams";

	private static final String AUTHORIZATION_NAME = "Authorization";

	private static final String JSON_PARSE_ERROR_MESSAGE = "Error parsing json: ";
	private static final String JSON_PARSE_ERROR_MESSAGE_CORRUPT_JSON_STRUCTURE = "Error parsing json: Corrupt Json Structure";

	private WebTarget rootApiTarget;
	private Optional<IDToken> idToken;

	/**
	 *
	 * @param username
	 * @param password
	 * @param hostName the artemis host name.
	 */
	public ArtemisRESTClient(final String username, final String password, final String hostName) {
		super(username, password, hostName);

		this.rootApiTarget = ClientBuilder.newBuilder()
			.build()
			.target(this.getApiRoot());
		this.idToken = Optional.empty();
	}

	private void checkAuthentication() throws AuthenticationException {
		if (this.idToken.isEmpty()) this.login();
	}

	/**
	 * Example:
	 * https://test-student@artemis-test-git.ipd.kit.edu/PRAKTIKUM21TESTAUFGABE1/praktikum21testaufgabe1-test-student.git
	 * ==>
	 * https://artemis-test-git.ipd.kit.edu/PRAKTIKUM21TESTAUFGABE1/praktikum21testaufgabe1-test-student.git
	 *
	 * @param repositoryURLWithStudentName
	 * @return
	 */
	private String convertRepositoryUrl(String repositoryURLWithStudentName) {
		return repositoryURLWithStudentName.replaceFirst(Constants.HTTPS_PREFIX + USERNAME_REGEX + "@", Constants.HTTPS_PREFIX);
	}

	@Override
	public void downloadExerciseAndSubmission(IExercise exercise, ISubmission submission,
			File directory, IProjectFileNamingStrategy projectFileNamingStrategy) throws ArtemisClientException {

		final File projectDirectory = projectFileNamingStrategy.getProjectFileInWorkspace(directory, exercise, submission);
		try {
			this.downloadTestRepo(exercise, projectDirectory);
			//download submission inside the exercise project directory
			this.downloadSubmission(submission, projectFileNamingStrategy.getAssignmentFileInProjectDirectory(projectDirectory));
		} catch (GitException e) {
			throw new ArtemisClientException("Unable to download exercise and submission: " + e.getMessage(), e);
		}


	}

	protected void downloadSubmission(ISubmission submission, File directory) throws GitException {
		new EgitGitHandler(this.convertRepositoryUrl(submission.getRepositoryUrl())).cloneRepo(directory, Constants.MASTER_BRANCH_NAME);
	}

	private void downloadTestRepo(IExercise exercise, File directory) throws GitException {
		new EgitGitHandler(exercise.getTestRepositoryUrl()).cloneRepo(directory, Constants.MASTER_BRANCH_NAME);
	}

	private String getApiRoot() {
		return new StringBuilder(Constants.HTTPS_PREFIX).append(this.getArtemisHostname()).append("/api").toString();
	}

	@Override
	public IAssessor getAssessor() throws ArtemisClientException, AuthenticationException {
		this.checkAuthentication();

		final Response rsp = this.rootApiTarget
				.path("users")
				.path(this.getArtemisUsername())
				.request().header(AUTHORIZATION_NAME, this.idToken.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous variant
		this.throwIfStatusUnsuccessful(rsp);

		try {
			return this.parseAssessorResult(rsp.readEntity(String.class));
		} catch (JsonProcessingException e) {
			throw new ArtemisClientException("Error parsing assessor json: " + e.getMessage(), e);
		}
	}


	private Entity<String> getAuthenticationEntity() {
		//TODO use a json lib!
		final String entityString = new StringBuilder().append("{")
				.append("\"username\":\"").append(this.getArtemisUsername()).append("\",")
				.append("\"password\":\"").append(this.getArtemisPassword()).append("\",")
				.append("\"rememberMe\":true")
				.append("}")
				.toString();

		return Entity.entity(
				entityString,
				MediaType.APPLICATION_JSON_TYPE);
	}

	private ICourse getCoursefromJsonNode(JsonNode courseJsonNode) throws AuthenticationException, JsonProcessingException, ArtemisClientException  {
		final int courseId = courseJsonNode.get("id").intValue();
		final String title = courseJsonNode.get(TITLE_FIELD).textValue();
		final String shortName = courseJsonNode.get("shortName").textValue();

		this.checkAuthentication();
		final Response exercisesAndParticipationsRsp = this.rootApiTarget
				.path(COURSES_PATHPART)
				.path(String.valueOf(courseId))
				.path("with-exercises-and-relevant-participations")
				.request().header(AUTHORIZATION_NAME, this.idToken.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous call
		this.throwIfStatusUnsuccessful(exercisesAndParticipationsRsp);


		// handle exercises
		final JsonNode exercisesAndParticipationsJsonNode = new ObjectMapper()
				.readTree(exercisesAndParticipationsRsp.readEntity(String.class));

		JsonNode exercisesJsonArray = exercisesAndParticipationsJsonNode.get(EXERCISES_PATHPART);
		if (!exercisesJsonArray.isArray()) throw new ArtemisClientException(JSON_PARSE_ERROR_MESSAGE_CORRUPT_JSON_STRUCTURE);

		Collection<IExercise> exercises = new LinkedList<>();
		for (JsonNode exerciseJsonNode : exercisesJsonArray) {
			exercises.add(this.getExercisefromJsonNode(exerciseJsonNode));
		}

		//handle exams TODO --> new method
		final Response examsRsp = this.rootApiTarget
				.path(COURSES_PATHPART)
				.path(String.valueOf(courseId))
				.path(EXAMS_PATHPART)
				.request().header(AUTHORIZATION_NAME, this.idToken.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous call
		this.throwIfStatusUnsuccessful(examsRsp);
		final JsonNode examsJsonArray = new ObjectMapper()
				.readTree(examsRsp.readEntity(String.class));

		if (!examsJsonArray.isArray()) throw new ArtemisClientException(JSON_PARSE_ERROR_MESSAGE_CORRUPT_JSON_STRUCTURE);

		Collection<IExam> exams = new LinkedList<>();
		for (JsonNode examJsonNode : examsJsonArray) {
			exams.add(this.getExamFromJsonNode(examJsonNode, courseId));
		}

		return new ArtemisCourse(courseId, title, shortName, exercises, exams);
	}

	@Override
	public Collection<ICourse> getCourses() throws ArtemisClientException, AuthenticationException {
		this.checkAuthentication();
		final Response rsp = this.rootApiTarget
				.path(COURSES_PATHPART)
				.request().header(AUTHORIZATION_NAME, this.idToken.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous variant
		this.throwIfStatusUnsuccessful(rsp);
		String rspString = rsp.readEntity(String.class);

		Collection<ArtemisCourse> courses;
		try {
			ArtemisCourse[] coursesArray = new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.readValue(rspString, ArtemisCourse[].class);
			courses = Arrays.asList(coursesArray);
			for (ArtemisCourse course : courses) {
				course.init(
						this.getExercisesForCourse(course),
						this.getExamsForCourse(course));
			}
		} catch (JsonProcessingException e) {
			throw new ArtemisClientException(JSON_PARSE_ERROR_MESSAGE + e.getMessage(), e);
		}
		return courses.stream()
				.map(ICourse.class::cast)
				.collect(Collectors.toList());
	}

	private IExam getExamFromJsonNode(JsonNode examJsonNode, int courseId)  throws ArtemisClientException, AuthenticationException, JsonProcessingException {

		final int examId = examJsonNode.get("id").intValue();
		final String title = examJsonNode.get(TITLE_FIELD).textValue();

		this.checkAuthentication();
		final Response rsp = this.rootApiTarget
				.path(COURSES_PATHPART)
				.path(String.valueOf(courseId))
				.path(EXAMS_PATHPART)
				.path(String.valueOf(examId))
				.path("exam-for-assessment-dashboard") // web client does it that way..
				.request().header(AUTHORIZATION_NAME, this.idToken.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous variant
		this.throwIfStatusUnsuccessful(rsp);

		JsonNode detailledExamJsonNode = new ObjectMapper().readTree(rsp.readEntity(String.class));

		JsonNode exerciseGroupsJsonArray = detailledExamJsonNode.get("exerciseGroups");
		if (!exerciseGroupsJsonArray.isArray()) throw new ArtemisClientException(JSON_PARSE_ERROR_MESSAGE_CORRUPT_JSON_STRUCTURE);

		final Collection<IExerciseGroup> exerciseGroups = new LinkedList<>();

		exerciseGroupsJsonArray.forEach(exerciseGroupJsonNode -> {
			try {
				exerciseGroups.add(this.getExerciseGroupFromJsonNode(exerciseGroupJsonNode));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return new ArtemisExam(exerciseGroups, examId, title);
	}

	private Collection<IExam> getExamsForCourse(ArtemisCourse course) throws AuthenticationException, JsonProcessingException, ArtemisClientException {
		final Response examsRsp = this.rootApiTarget
				.path(COURSES_PATHPART)
				.path(String.valueOf(course.getCourseId()))
				.path(EXAMS_PATHPART)
				.request().header(AUTHORIZATION_NAME, this.idToken.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous call
		this.throwIfStatusUnsuccessful(examsRsp);

		Collection<ArtemisExam> exams;

		ArtemisExam[] examsArray = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.readValue(examsRsp.readEntity(String.class), ArtemisExam[].class);
		exams = Arrays.asList(examsArray);

		for (ArtemisExam exam : exams) {
			exam.init(this.getExerciseGroupsForExam(exam, course.getCourseId()));
		}

		return exams.stream()
				.map(IExam.class::cast)
				.collect(Collectors.toList());
	}

	private IExercise getExercisefromJsonNode(JsonNode exerciseJsonNode)  throws AuthenticationException, JsonProcessingException {

		final int exerciseId = exerciseJsonNode.get("id").intValue();
		final String title = exerciseJsonNode.get(TITLE_FIELD).textValue();
		final String shortName = exerciseJsonNode.get("shortName").textValue();
		final String testRepositoryUrl = exerciseJsonNode.get("testRepositoryUrl").textValue();

		this.checkAuthentication();
		final Response rsp = this.rootApiTarget
				.path(EXERCISES_PATHPART)
				.path(String.valueOf(exerciseId))
				.path(PROGRAMMING_SUBMISSION_PATHPART)
//				.queryParam("submittedOnly", true)		//TODO auf true setzen!
				.request().header(AUTHORIZATION_NAME, this.idToken.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous variant
		this.throwIfStatusUnsuccessful(rsp);

		JsonNode submissionsArrayJsonNode = new ObjectMapper().readTree(rsp.readEntity(String.class));

		final Collection<ISubmission> submissions = new LinkedList<>();
		submissionsArrayJsonNode.forEach(submission ->
			submissions.add(this.getSubmissionFromJsonNode(submission))
		);

		return new ArtemisExercise(exerciseId, title, shortName, testRepositoryUrl, submissions);
	}

	// for exams
	private IExerciseGroup getExerciseGroupFromJsonNode(JsonNode exerciseGroupJsonNode) throws ArtemisClientException, AuthenticationException, JsonProcessingException {

		final int exerciseGroupId = exerciseGroupJsonNode.get("id").intValue();
		final String title = exerciseGroupJsonNode.get(TITLE_FIELD).asText();
		final boolean isMandatory = exerciseGroupJsonNode.get("isMandatory").booleanValue();

		JsonNode exercisesJsonArray = exerciseGroupJsonNode.get(EXERCISES_PATHPART);
		if (!exercisesJsonArray.isArray()) throw new ArtemisClientException(JSON_PARSE_ERROR_MESSAGE_CORRUPT_JSON_STRUCTURE);

		Collection<IExercise> exercises = new LinkedList<>();
		for (JsonNode exerciseJsonNode : exercisesJsonArray) {
			if (exerciseJsonNode.get("type").asText().equals("programming")) {
				exercises.add(this.getExercisefromJsonNode(exerciseJsonNode));
			}
		}
		return new ArtemisExerciseGroup(exerciseGroupId, exercises, title, isMandatory);
	}

	private Collection<IExerciseGroup> getExerciseGroupsForExam(ArtemisExam exam, int courseId) throws AuthenticationException, JsonProcessingException, ArtemisClientException {
		this.checkAuthentication();
		final Response rsp = this.rootApiTarget
				.path(COURSES_PATHPART)
				.path(String.valueOf(courseId))
				.path(EXAMS_PATHPART)
				.path(String.valueOf(exam.getExamId()))
				.path("exam-for-assessment-dashboard") // web client does it that way..
				.request().header(AUTHORIZATION_NAME, this.idToken.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous variant
		this.throwIfStatusUnsuccessful(rsp);

		JsonNode detailledExamJsonNode = new ObjectMapper().readTree(rsp.readEntity(String.class));

		JsonNode exerciseGroupsJsonArray = detailledExamJsonNode.get("exerciseGroups");
		if (!exerciseGroupsJsonArray.isArray()) throw new ArtemisClientException(JSON_PARSE_ERROR_MESSAGE_CORRUPT_JSON_STRUCTURE);

		final Collection<IExerciseGroup> exerciseGroups = new LinkedList<>();

		exerciseGroupsJsonArray.forEach(exerciseGroupJsonNode -> {
			try {
				exerciseGroups.add(this.getExerciseGroupFromJsonNode(exerciseGroupJsonNode));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return exerciseGroups;
	}

	private Collection<IExercise> getExercisesForCourse(ArtemisCourse course) throws AuthenticationException, JsonProcessingException, ArtemisClientException {

		this.checkAuthentication();
		final Response exercisesAndParticipationsRsp = this.rootApiTarget
				.path(COURSES_PATHPART)
				.path(String.valueOf(course.getCourseId()))
				.path("with-exercises-and-relevant-participations")
				.request().header(AUTHORIZATION_NAME, this.idToken.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous call
		this.throwIfStatusUnsuccessful(exercisesAndParticipationsRsp);

		Collection<ArtemisExercise> exercises;

		ArtemisExercise[] exercisesArray = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.readValue(exercisesAndParticipationsRsp.readEntity(String.class), ArtemisExercise[].class);
		exercises = Arrays.asList(exercisesArray);

		for (ArtemisExercise exercise : exercises) {
			exercise.init(this.getSubmissionsForExercise(exercise));
		}

		return exercises.stream()
				.map(IExercise.class::cast)
				.collect(Collectors.toList());
	}

	private ISubmission getSubmissionFromJsonNode(JsonNode submissionJsonNode) {
		final JsonNode participationJsonNode = submissionJsonNode.get("participation");

		final int submissionId = submissionJsonNode.get("id").intValue();
		final String participantIdentifier = participationJsonNode.get("participantIdentifier").textValue();
		final String participantName = participationJsonNode.get("participantName").textValue();
		final String repositoryUrl = participationJsonNode.get("repositoryUrl").textValue();
		final String commitHash = submissionJsonNode.get("commitHash").textValue();

		// get info about whether this submission has an assessment which was submitted already
		final JsonNode resultsJsonNode = submissionJsonNode.get("results");

		boolean hasSubmittedAssessment = false;
		boolean hasSavedAssessment = false;

		if (resultsJsonNode.size() > 0) {
			final JsonNode lastResultJsonNode = resultsJsonNode.get(resultsJsonNode.size()-1);
			hasSubmittedAssessment = lastResultJsonNode.get("completionDate") != null;

			final JsonNode lastResultHasFeedbackJsonNode = lastResultJsonNode.get("hasFeedback");
			hasSavedAssessment = lastResultHasFeedbackJsonNode != null && lastResultHasFeedbackJsonNode.asBoolean();
		}



		return new ArtemisSubmission(submissionId, participantIdentifier, participantName, repositoryUrl, commitHash,
				hasSubmittedAssessment, hasSavedAssessment);
	}

	@Override
	public Collection<ISubmission> getSubmissions(int exerciseID, boolean assessedByTutor) throws ArtemisClientException, AuthenticationException {
		this.checkAuthentication();
		final Response rsp = this.rootApiTarget
				.path(EXERCISES_PATHPART)
				.path(String.valueOf(exerciseID))
				.path(PROGRAMMING_SUBMISSION_PATHPART)
//				.queryParam("submittedOnly", submittedOnly) TODO change to true
				.queryParam("assessedByTutor", assessedByTutor)
				.request().header(AUTHORIZATION_NAME, this.idToken.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous variant
		this.throwIfStatusUnsuccessful(rsp);

		JsonNode submissionsArrayJsonNode = null;
		try {
			submissionsArrayJsonNode = new ObjectMapper().readTree(rsp.readEntity(String.class));
		} catch (JsonProcessingException e) {
			throw new ArtemisClientException(JSON_PARSE_ERROR_MESSAGE + e.getMessage(), e);
		}

		final Collection<ISubmission> submissions = new LinkedList<>();
		submissionsArrayJsonNode.forEach(submission ->
			submissions.add(this.getSubmissionFromJsonNode(submission))
		);

		return submissions;

	}

	private Collection<ISubmission> getSubmissionsForExercise(ArtemisExercise exercise) throws AuthenticationException, JsonProcessingException {
		this.checkAuthentication();
		final Response rsp = this.rootApiTarget
				.path(EXERCISES_PATHPART)
				.path(String.valueOf(exercise.getExerciseId()))
				.path(PROGRAMMING_SUBMISSION_PATHPART)
//				.queryParam("submittedOnly", true)		//TODO auf true setzen!
				.request().header(AUTHORIZATION_NAME, this.idToken.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous variant
		this.throwIfStatusUnsuccessful(rsp);

		JsonNode submissionsArrayJsonNode = new ObjectMapper().readTree(rsp.readEntity(String.class));

		final Collection<ISubmission> submissions = new LinkedList<>();
		submissionsArrayJsonNode.forEach(submission ->
			submissions.add(this.getSubmissionFromJsonNode(submission))
		);
		return submissions;
	}

	private boolean isStatusSuccessful(final Response response) {
		return response.getStatusInfo().getFamily().equals(Family.SUCCESSFUL);
	}

	private void login() throws AuthenticationException {
		final Response authenticationResponse = this.rootApiTarget.path("authenticate").request()
				.buildPost(this.getAuthenticationEntity())
				.invoke();

		this.throwIfStatusUnsuccessful(authenticationResponse);
		final String authRspEntity = authenticationResponse.readEntity(String.class);
		try {
			this.idToken = Optional.of(new IDToken(new ObjectMapper().readTree(authRspEntity).get("id_token").asText()));
		} catch (IOException e1) {
			throw new AuthenticationException("Authentication to \"" + this.getApiRoot() + "\" failed: No token could be retrieved in server response.");
		}
	}

	private IAssessor parseAssessorResult(final String jsonString) throws JsonProcessingException {
		return new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.readValue(jsonString, Assessor.class);
	}

	private ILockResult parseLockResult(final String jsonString) throws JsonProcessingException {
		return new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.readValue(jsonString, LockResult.class);
	}

	@Override
	public void saveAssessment(int participationID, boolean submit, String payload) throws AuthenticationException {
		this.checkAuthentication();

		final Response rsp = this.rootApiTarget
				.path("participations")
				.path(Integer.toString(participationID))
				.path("manual-results")
				.queryParam("submit", submit)
				.request().header(AUTHORIZATION_NAME, this.idToken.get().getHeaderString())
				.buildPut(this.toJsonStringEntity(payload))
				.invoke(); // synchronous variant
		this.throwIfStatusUnsuccessful(rsp);
	}

	@Override
	public ILockResult startAssessment(int submissionID) throws AuthenticationException, ArtemisClientException {
		this.checkAuthentication();

		final Response rsp = this.rootApiTarget
				.path(PROGRAMMING_SUBMISSION_PATHPART)
				.path(String.valueOf(submissionID))
				.path("lock")				// this should be best.
				.request().header(AUTHORIZATION_NAME, this.idToken.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous variant
		this.throwIfStatusUnsuccessful(rsp);

		try {
			return this.parseLockResult(rsp.readEntity(String.class));
		} catch (JsonProcessingException e) {
			throw new ArtemisClientException("Error parsing lock result json: " + e.getMessage(), e);
		}

	}

	@Override
	public Optional<ILockResult> startNextAssessment(int exerciseID, int correctionRound) throws AuthenticationException, ArtemisClientException {
		this.checkAuthentication();

		final Response rsp = this.rootApiTarget
				.path(EXERCISES_PATHPART)
				.path(String.valueOf(exerciseID))
				.path("programming-submission-without-assessment")
				.queryParam("correction-round", correctionRound)
				.queryParam("lock", true)
				.request().header(AUTHORIZATION_NAME, this.idToken.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous variant
		if (!this.isStatusSuccessful(rsp)) {
			//no assessment left!
			return Optional.empty();
		}

		try {
			return Optional.of(this.parseLockResult(rsp.readEntity(String.class)));
		} catch (JsonProcessingException e) {
			throw new ArtemisClientException("Error parsing lock result json: " + e.getMessage(), e);
		}
	}

	private void throwIfStatusUnsuccessful(final Response response) throws AuthenticationException {
		if (!this.isStatusSuccessful(response)) {
			throw new AuthenticationException("Communication with \"" + this.getApiRoot() + "\" failed with status \""
					+ response.getStatus()
					+ ": " + response.getStatusInfo().getReasonPhrase() + "\".");
		}
	}

	private Entity<String> toJsonStringEntity(String jsonString) {

		return Entity.entity(
				jsonString,
				MediaType.APPLICATION_JSON_TYPE);
	}
}
