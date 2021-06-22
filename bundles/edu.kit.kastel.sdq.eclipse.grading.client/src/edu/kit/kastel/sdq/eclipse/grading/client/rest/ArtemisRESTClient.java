package edu.kit.kastel.sdq.eclipse.grading.client.rest;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
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
import edu.kit.kastel.sdq.eclipse.grading.api.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IAssessor;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;
import edu.kit.kastel.sdq.eclipse.grading.client.git.EgitGitHandler;
import edu.kit.kastel.sdq.eclipse.grading.client.lockstuff.Assessor;
import edu.kit.kastel.sdq.eclipse.grading.client.lockstuff.LockResult;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.ArtemisCourse;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.ArtemisExercise;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.ArtemisSubmission;

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

	private void checkStatusSuccessful(final Response authenticationResponse) throws AuthenticationException {
		if (!authenticationResponse.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
			throw new AuthenticationException("Authentication to \"" + this.getApiRoot() + "\" failed with status \""
					+ authenticationResponse.getStatus()
					+ ": " + authenticationResponse.getStatusInfo().getReasonPhrase() + "\".");
		}
	}

	private void downloadExercise(IExercise exercise, File directory) {
		//TODO remove hardcoded
		new EgitGitHandler(exercise.getTestRepositoryUrl()).cloneRepo(directory, "master");
	}

	@Override
	public void downloadExerciseAndSubmissions(IExercise exercise, Collection<ISubmission> submissions,
			File directory) {
		//exercise-$EXERCISEID-$EXERCISENAME_submission-$SUBMISSIONID-$SUBMISSIONNAME
		submissions.forEach(submission -> {
			final File project = new File(directory, new StringBuilder()
					.append("exercise-").append(exercise.getExerciseId()).append("-").append(exercise.getShortName())
					.append("_submission-").append(submission.getSubmissionId()).append("-").append(submission.getParticipantIdentifier())
					.toString()
			);

			this.downloadExercise(exercise, project);

			//download submission inside
			this.downloadSubmission(submission, new File(project, "assignment"));

		});
	}

	@Override
	public void downloadExercises(Collection<IExercise> exercises, File directory) {
		exercises.forEach(exercise -> this.downloadExercise(exercise, new File(directory, exercise.getShortName())));
	}

	protected void downloadSubmission(ISubmission submission, File directory) {
		new EgitGitHandler(submission.getRepositoryUrl()).cloneRepo(directory, "master");
	}

	@Override
	public void downloadSubmissions(Collection<ISubmission> submissions, File directory) {
		// TODO IMplement
		for (ISubmission submission : submissions) {
			this.downloadSubmission(submission, directory);
		}
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
		this.checkStatusSuccessful(rsp);

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
		System.out.println("IN getCoursefromJsonNode:");
		final int courseId = courseJsonNode.get("id").intValue();
		final String title = courseJsonNode.get("title").textValue();
		final String shortName = courseJsonNode.get("shortName").textValue();

		this.checkAuthentication();
		final Response rsp = this.rootApiTarget
				.path("courses")
				.path(String.valueOf(courseId))
				.path("with-exercises-and-relevant-participations")
				.request().header("Authorization", this.id_token.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous variant
//				.submit(); // asynchronous variant
		this.checkStatusSuccessful(rsp);

		JsonNode jsonNode = new ObjectMapper().readTree(rsp.readEntity(String.class));
		System.out.println("  Read jsonNode from response entity: \n  " + jsonNode.toString());

		JsonNode exercisesJsonArray = jsonNode.get("exercises");
		if (!exercisesJsonArray.isArray()) throw new Exception("Error parsing json.");

		Collection<IExercise> exercises = new LinkedList<IExercise>();
		exercisesJsonArray.forEach(exerciseJsonNode -> {
			try {
				exercises.add(this.getExercisefromJsonNode(exerciseJsonNode));
			} catch (Exception e) {
				//TODO handle exception!
			}
		});
		return new ArtemisCourse(courseId, title, shortName, exercises);
	}

	@Override
	public Collection<ICourse> getCourses() throws Exception {
		this.checkAuthentication();
		final Response rsp = this.rootApiTarget
				.path("courses")
//				.path("5")														// TestOfCourse
//				.path("with-exercises")
//				.path("with-exercises-and-relevant-participations")				// this should be best.
				.request().header("Authorization", this.id_token.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous variant
//				.submit(); // asynchronous variant
		this.checkStatusSuccessful(rsp);
		String rspString = rsp.readEntity(String.class);
		System.out.println("Got entity from rest call: " + rspString );
		try {
			// Put the result into java objects
//			ArtemisCourses courses = objectMapper.readValue(rspString, ArtemisCourses.class);
//			System.out.println("Got parsed entity from rest call: " + courses );

			// Put the result into a JsonNode -> No need for java objects, but bad style.
			JsonNode jsonNode = new ObjectMapper().readTree(rspString);
			System.out.println("Read jsonNode from response entity: \n" + jsonNode.toString());
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

		return null;

	}

	private IExercise getExercisefromJsonNode(JsonNode exerciseJsonNode) throws Exception {
		System.out.println("IN getExercisefromJsonNode:");

		final int exerciseId = exerciseJsonNode.get("id").intValue();
		final String title = exerciseJsonNode.get("title").textValue();
		final String shortName = exerciseJsonNode.get("shortName").textValue();
		final String testRepositoryUrl = exerciseJsonNode.get("testRepositoryUrl").textValue();

		this.checkAuthentication();
		final Response rsp = this.rootApiTarget
				.path("exercises")
				.path(String.valueOf(exerciseId))
				.path("programming-submissions")
//				.queryParam("submittedOnly", true)		//TODO true?
				.request().header("Authorization", this.id_token.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous variant
		this.checkStatusSuccessful(rsp);

		JsonNode submissionsArrayJsonNode = new ObjectMapper().readTree(rsp.readEntity(String.class));
		System.out.println("  Read jsonNode from response entity: \n  " + submissionsArrayJsonNode.toString());

		final Collection<ISubmission> submissions = new LinkedList<ISubmission>();
		submissionsArrayJsonNode.forEach(submission -> {
			submissions.add(this.getSubmissionFromJsonNode(submission));
		});

		return new ArtemisExercise(exerciseId, title, shortName, testRepositoryUrl, submissions);
	}

	private ISubmission getSubmissionFromJsonNode(JsonNode submissionJsonNode) {
		System.out.println("IN getSubmissionFromJsonNode:");
		final JsonNode participationJsonNode = submissionJsonNode.get("participation");

		final int submissionId = submissionJsonNode.get("id").intValue();
		final String participantIdentifier = participationJsonNode.get("participantIdentifier").textValue();
		final String participantName = participationJsonNode.get("participantName").textValue();
		final String repositoryUrl = participationJsonNode.get("repositoryUrl").textValue();
		final String commitHash = submissionJsonNode.get("commitHash").textValue();

		System.out.println("COMPLETED getSubmissionFromJsonNode:");
		return new ArtemisSubmission(submissionId, participantIdentifier, participantName, repositoryUrl, commitHash);
	}

	private void login() throws AuthenticationException {
		final Response authenticationResponse = this.rootApiTarget.path("authenticate").request()
				.buildPost(this.getAuthenticationEntity())
				.invoke();

		this.checkStatusSuccessful(authenticationResponse);
		System.out.println("Tried to authenticate with status " + authenticationResponse.getStatus());
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
		System.out.println("######DEBUG: " + jsonString);
		return new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.readValue(jsonString, LockResult.class);
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
		this.checkStatusSuccessful(rsp);

		String rspString = rsp.readEntity(String.class);
		final JsonNode jsonNode;
		try {
			// Put the result into java objects
//			ArtemisCourses courses = objectMapper.readValue(rspString, ArtemisCourses.class);
//			System.out.println("Got parsed entity from rest call: " + courses );

			// Put the result into a JsonNode -> No need for java objects, but bad style.
			jsonNode = new ObjectMapper().readTree(rspString);
			System.out.println("Read jsonNode from response entity: \n" + jsonNode.toString());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		final String repositoryUrl = jsonNode.get("participation").get("repositoryUrl").asText();
		System.out.println("Read repositoryUrl from response entity: \n" + repositoryUrl);

		//TODO that should be done only, if submissions are not downloaded, yet!
		new EgitGitHandler(repositoryUrl).cloneRepo(new File("testPlugin_bookmarks/target/testEgitFromArtemisCall"), "master");

		//TODO implement returning ILockResult

		return this.parseLockResult(rspString);

	}

	@Override
	public void submitAssessment(int submissionID, String payload) throws AuthenticationException {
		this.checkAuthentication();
		this.checkAuthentication();

		// /api/users/{login}
		final Response rsp = this.rootApiTarget
				.path("participations")
				.path(Integer.toString(submissionID))
				.path("manual-results")
				.queryParam("submit", true)
				.request().header("Authorization", this.id_token.get().getHeaderString())
				.buildPut(this.toJsonStringEntity(payload))
				.invoke(); // synchronous variant
		this.checkStatusSuccessful(rsp);
	}

	private Entity<String> toJsonStringEntity(String jsonString) {

		return Entity.entity(
				jsonString,
				MediaType.APPLICATION_JSON_TYPE);
	}
}
