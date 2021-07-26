package edu.kit.kastel.sdq.eclipse.grading.client.rest;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.security.sasl.AuthenticationException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kit.kastel.sdq.eclipse.grading.api.AbstractArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.api.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.IExerciseGroup;
import edu.kit.kastel.sdq.eclipse.grading.api.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IAssessor;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.client.git.EgitGitHandler;
import edu.kit.kastel.sdq.eclipse.grading.client.lockstuff.Assessor;
import edu.kit.kastel.sdq.eclipse.grading.client.lockstuff.LockResult;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.ArtemisCourse;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.ArtemisExercise;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.ArtemisSubmission;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.exam.ArtemisExam;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.exam.ArtemisExerciseGroup;

public class ArtemisRESTClient extends AbstractArtemisClient  {


	private WebTarget rootApiTarget;
	private Optional<IDToken> id_token;

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
		this.id_token = Optional.empty();
	}

	private void checkAuthentication() throws AuthenticationException {
		if (this.id_token.isEmpty()) this.login();
	}

	private void downloadExercise(IExercise exercise, File directory) {
		//TODO remove hardcoded
		new EgitGitHandler(exercise.getTestRepositoryUrl()).cloneRepo(directory, "master");
	}

	@Override
	public void downloadExerciseAndSubmission(IExercise exercise, ISubmission submission,
			File directory, IProjectFileNamingStrategy projectFileNamingStrategy) {

		final File projectDirectory = projectFileNamingStrategy.getProjectFileInWorkspace(directory, exercise, submission);
		this.downloadExercise(exercise, projectDirectory);

		//download submission inside the exercise project directory
		this.downloadSubmission(submission, projectFileNamingStrategy.getAssignmentFileInProjectDirectory(projectDirectory));

	}

	protected void downloadSubmission(ISubmission submission, File directory) {
		new EgitGitHandler(submission.getRepositoryUrl()).cloneRepo(directory, "master");
	}

	private String getApiRoot() {
		return new StringBuilder("https://").append(this.getArtemisHostname()).append("/api").toString();
	}

	@Override
	public IAssessor getAssessor() throws Exception {
		this.checkAuthentication();

		// /api/users/{login}
		final Response rsp = this.rootApiTarget
				.path("users")
				.path(this.getArtemisUsername())
				.request().header("Authorization", this.id_token.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous variant
		this.throwIfStatusUnsuccessful(rsp);

		return this.parseAssessorResult(rsp.readEntity(String.class));
	}


	private Entity<String> getAuthenticationEntity() {
		//TODO use a json lib!
		final String entityString = new StringBuilder().append("{")
				.append("\"username\":\"").append(this.getArtemisUsername()).append("\",")
				.append("\"password\":\"").append(this.getArtemisPassword()).append("\",")
				.append("\"rememberMe\":true")
				.append("}")
				.toString();
		//TODO remove that!
//		System.out.println("Trying to authenticate with entity " + entityString);

		return Entity.entity(
				entityString,
				MediaType.APPLICATION_JSON_TYPE);
	}

	private ICourse getCoursefromJsonNode(JsonNode courseJsonNode) throws Exception {
		final int courseId = courseJsonNode.get("id").intValue();
		final String title = courseJsonNode.get("title").textValue();
		final String shortName = courseJsonNode.get("shortName").textValue();

		this.checkAuthentication();
		final Response exercisesAndParticipationsRsp = this.rootApiTarget
				.path("courses")
				.path(String.valueOf(courseId))
				.path("with-exercises-and-relevant-participations")
				.request().header("Authorization", this.id_token.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous call
		this.throwIfStatusUnsuccessful(exercisesAndParticipationsRsp);

		//handle exercises TODO --> new method
		final JsonNode exercisesAndParticipationsJsonNode = new ObjectMapper()
				.readTree(exercisesAndParticipationsRsp.readEntity(String.class));
//		System.out.println("  Read jsonNode from exercisesAndParticipations response entity: \n  " + exercisesAndParticipationsJsonNode.toString());
		JsonNode exercisesJsonArray = exercisesAndParticipationsJsonNode.get("exercises");
		if (!exercisesJsonArray.isArray()) throw new Exception("Error parsing json.");

		Collection<IExercise> exercises = new LinkedList<IExercise>();
		exercisesJsonArray.forEach(exerciseJsonNode -> {
			try {
				exercises.add(this.getExercisefromJsonNode(exerciseJsonNode));
			} catch (Exception e) {
				//TODO handle exception!
			}
		});

		//handle exams TODO --> new method
		final Response examsRsp = this.rootApiTarget
				.path("courses")
				.path(String.valueOf(courseId))
				.path("exams")
				.request().header("Authorization", this.id_token.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous call
		this.throwIfStatusUnsuccessful(examsRsp);
		final JsonNode examsJsonArray = new ObjectMapper()
				.readTree(examsRsp.readEntity(String.class));
//		System.out.println("  Read jsonNode from exams-for-user response entity: \n  " + examsJsonArray.toString());
		if (!examsJsonArray.isArray()) throw new Exception("Error parsing json.");

		Collection<IExam> exams = new LinkedList<IExam>();
		examsJsonArray.forEach(examJsonNode -> {
			try {
				exams.add(this.getExamFromJsonNode(examJsonNode, courseId));
			} catch (Exception e) {
				//TODO handle exception!
			}
		});


		return new ArtemisCourse(courseId, title, shortName, exercises, exams);
	}

	@Override
	public Collection<ICourse> getCourses() throws Exception {
		this.checkAuthentication();
		final Response rsp = this.rootApiTarget
				.path("courses")
				.request().header("Authorization", this.id_token.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous variant
//				.submit(); // asynchronous variant
		this.throwIfStatusUnsuccessful(rsp);
		String rspString = rsp.readEntity(String.class);
		try {
			// Put the result into a JsonNode -> No need for java objects, but bad style.
			JsonNode jsonNode = new ObjectMapper().readTree(rspString);
//			System.out.println("Read jsonNode from response entity: \n" + jsonNode.toString());
			if (!jsonNode.isArray()) throw new Exception("Error parsing json.");

			Collection<ICourse> courses = new LinkedList();
			jsonNode.forEach(courseJsonNode -> {
				try {
					courses.add(this.getCoursefromJsonNode(courseJsonNode));
				} catch (Exception e) {
					//TODO handle exception!
				}
			});
			return courses;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return List.of();
	}

	private IExam getExamFromJsonNode(JsonNode examJsonNode, int courseId) throws Exception {

		final int examId = examJsonNode.get("id").intValue();
		final String title = examJsonNode.get("title").textValue();

		this.checkAuthentication();
		final Response rsp = this.rootApiTarget
				.path("courses")
				.path(String.valueOf(courseId))
				.path("exams")
				.path(String.valueOf(examId))
				.path("exam-for-assessment-dashboard") // web client does it that way..
				.request().header("Authorization", this.id_token.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous variant
		this.throwIfStatusUnsuccessful(rsp);

		JsonNode detailledExamJsonNode = new ObjectMapper().readTree(rsp.readEntity(String.class));
//		System.out.println("  Read jsonNode from exam response entity: \n  " + detailledExamJsonNode.toString());

		JsonNode exerciseGroupsJsonArray = detailledExamJsonNode.get("exerciseGroups");
		if (!exerciseGroupsJsonArray.isArray()) throw new Exception("Error parsing json.");

		final Collection<IExerciseGroup> exerciseGroups = new LinkedList<IExerciseGroup>();

		exerciseGroupsJsonArray.forEach(exerciseGroupJsonNode -> {
			try {
				exerciseGroups.add(this.getExerciseGroupFromJsonNode(exerciseGroupJsonNode));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		return new ArtemisExam(exerciseGroups, examId, title);
	}

	private IExercise getExercisefromJsonNode(JsonNode exerciseJsonNode) throws Exception {
//		System.out.println("IN getExercisefromJsonNode:");

		final int exerciseId = exerciseJsonNode.get("id").intValue();
		final String title = exerciseJsonNode.get("title").textValue();
		final String shortName = exerciseJsonNode.get("shortName").textValue();
		final String testRepositoryUrl = exerciseJsonNode.get("testRepositoryUrl").textValue();

		this.checkAuthentication();
		final Response rsp = this.rootApiTarget
				.path("exercises")
				.path(String.valueOf(exerciseId))
				.path("programming-submissions")
//				.queryParam("submittedOnly", true)		//TODO auf true setzen!
				.request().header("Authorization", this.id_token.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous variant
		this.throwIfStatusUnsuccessful(rsp);

		JsonNode submissionsArrayJsonNode = new ObjectMapper().readTree(rsp.readEntity(String.class));
//		System.out.println("  Read jsonNode from response entity: \n  " + submissionsArrayJsonNode.toString());

		final Collection<ISubmission> submissions = new LinkedList<ISubmission>();
		submissionsArrayJsonNode.forEach(submission -> {
			submissions.add(this.getSubmissionFromJsonNode(submission));
		});

		return new ArtemisExercise(exerciseId, title, shortName, testRepositoryUrl, submissions);
	}

	// for exams
	private IExerciseGroup getExerciseGroupFromJsonNode(JsonNode exerciseGroupJsonNode) throws Exception {

		final int exerciseGroupId = exerciseGroupJsonNode.get("id").intValue();
		final String title = exerciseGroupJsonNode.get("title").asText();
		final boolean isMandatory = exerciseGroupJsonNode.get("isMandatory").booleanValue();

		JsonNode exercisesJsonArray = exerciseGroupJsonNode.get("exercises");
		if (!exercisesJsonArray.isArray()) throw new Exception("Error parsing json.");

		Collection<IExercise> exercises = new LinkedList<IExercise>();
		exercisesJsonArray.forEach(exerciseJsonNode -> {
			try {
				exercises.add(this.getExercisefromJsonNode(exerciseJsonNode));
			} catch (Exception e) {
				//TODO handle exception!
			}
		});
		return new ArtemisExerciseGroup(exerciseGroupId, exercises, title, isMandatory);

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
	public Collection<ISubmission> getSubmissions(int exerciseID, boolean assessedByTutor) throws Exception {
		this.checkAuthentication();
		final Response rsp = this.rootApiTarget
				.path("exercises")
				.path(String.valueOf(exerciseID))
				.path("programming-submissions")
//				.queryParam("submittedOnly", submittedOnly) TODO change to true
				.queryParam("assessedByTutor", assessedByTutor)
				.request().header("Authorization", this.id_token.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous variant
		this.throwIfStatusUnsuccessful(rsp);

		JsonNode submissionsArrayJsonNode = new ObjectMapper().readTree(rsp.readEntity(String.class));
		final Collection<ISubmission> submissions = new LinkedList<ISubmission>();
		submissionsArrayJsonNode.forEach(submission -> {
			submissions.add(this.getSubmissionFromJsonNode(submission));
		});

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
//		System.out.println("Tried to authenticate with status " + authenticationResponse.getStatus());
		final String authRspEntity = authenticationResponse.readEntity(String.class);
		try {
			this.id_token = Optional.of(new IDToken(new ObjectMapper().readTree(authRspEntity).get("id_token").asText()));
		} catch (IOException e1) {
			throw new AuthenticationException("Authentication to \"" + this.getApiRoot() + "\" failed: No token could be retrieved in server response.");
		}
	}

	private IAssessor parseAssessorResult(final String jsonString) throws JsonMappingException, JsonProcessingException {
		return new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.readValue(jsonString, Assessor.class);
	}

	private ILockResult parseLockResult(final String jsonString) throws JsonMappingException, JsonProcessingException {
		//TODO impl
		System.out.println("######ArtemisRESTClient::parseLockResult DEBUG: " + jsonString);
		return new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.readValue(jsonString, LockResult.class);
	}

	@Override
	public void saveAssessment(int participationID, boolean submit, String payload) throws AuthenticationException {
		this.checkAuthentication();

		System.out.println("######ArtemisRESTClient::saveAssessment DEBUG: " + payload);


		// /api/users/{login}
		final Response rsp = this.rootApiTarget
				.path("participations")
				.path(Integer.toString(participationID))
				.path("manual-results")
				.queryParam("submit", submit)
				.request().header("Authorization", this.id_token.get().getHeaderString())
				.buildPut(this.toJsonStringEntity(payload))
				.invoke(); // synchronous variant
		this.throwIfStatusUnsuccessful(rsp);
	}

	@Override
	public ILockResult startAssessment(int submissionID) throws Exception {
		this.checkAuthentication();

		final Response rsp = this.rootApiTarget
				.path("programming-submissions")
				.path(String.valueOf(submissionID))
				.path("lock")				// this should be best.
				.request().header("Authorization", this.id_token.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous variant
		this.throwIfStatusUnsuccessful(rsp);

		return this.parseLockResult(rsp.readEntity(String.class));

	}

	@Override
	public Optional<ILockResult> startNextAssessment(int exerciseID, int correctionRound) throws Exception {
		this.checkAuthentication();

		final Response rsp = this.rootApiTarget
				.path("exercises")
				.path(String.valueOf(exerciseID))
				.path("programming-submission-without-assessment")
				.queryParam("correction-round", correctionRound)
				.queryParam("lock", true)
				.request().header("Authorization", this.id_token.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous variant
		if (!this.isStatusSuccessful(rsp)) {
			//no assessment left!
			return Optional.empty();
		}

		return Optional.of(this.parseLockResult(rsp.readEntity(String.class)));
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
