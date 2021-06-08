package testplugin_activateByShortcut.rest;

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

import org.eclipse.core.commands.ExecutionException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kit.kastel.sdq.eclipse.grading.api.AbstractArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.api.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.ISubmission;
import testplugin_activateByShortcut.git.EgitGitHandler;
import testplugin_activateByShortcut.mappings.ArtemisCourse;
import testplugin_activateByShortcut.mappings.ArtemisExercise;
import testplugin_activateByShortcut.mappings.ArtemisSubmission;

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
			.target(getApiRoot());
		this.id_token = Optional.empty();
	}
	
	private void login() throws AuthenticationException {
		final Response authenticationResponse = rootApiTarget.path("authenticate").request()
				.buildPost(getAuthenticationEntity())
				.invoke();
		
		checkStatusSuccessful(authenticationResponse);
		System.out.println("Tried to authenticate with status " + authenticationResponse.getStatus());
		final String authRspEntity = authenticationResponse.readEntity(String.class);
		try {
			id_token = Optional.of(new IDToken(new ObjectMapper().readTree(authRspEntity).get("id_token").asText()));
		} catch (IOException e1) {
			throw new AuthenticationException("Authentication to \"" + getApiRoot() + "\" failed: No token could be retrieved in server response.");
		}
	}
	
	public Collection<ICourse> getCourses() throws Exception {
		checkAuthentication();
		final Response rsp = rootApiTarget
				.path("courses")
//				.path("5")														// TestOfCourse
//				.path("with-exercises")
//				.path("with-exercises-and-relevant-participations")				// this should be best.
				.request().header("Authorization", id_token.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous variant
//				.submit(); // asynchronous variant
		checkStatusSuccessful(rsp);
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
					courses.add(getCoursefromJsonNode(courseJsonNode));
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
	
	private void checkStatusSuccessful(final Response authenticationResponse) throws AuthenticationException {
		if (!authenticationResponse.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
			throw new AuthenticationException("Authentication to \"" + getApiRoot() + "\" failed with status \"" 
					+ authenticationResponse.getStatus() 
					+ ": " + authenticationResponse.getStatusInfo().getReasonPhrase() + "\".");
		}
	}
	
	private ICourse getCoursefromJsonNode(JsonNode courseJsonNode) throws Exception {
		System.out.println("IN getCoursefromJsonNode:");
		final int courseId = courseJsonNode.get("id").intValue();
		final String title = courseJsonNode.get("title").textValue();
		final String shortName = courseJsonNode.get("shortName").textValue();
		
		checkAuthentication();
		final Response rsp = rootApiTarget
				.path("courses")
				.path(String.valueOf(courseId))	
				.path("with-exercises-and-relevant-participations")
				.request().header("Authorization", id_token.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous variant
//				.submit(); // asynchronous variant
		checkStatusSuccessful(rsp);
		
		JsonNode jsonNode = new ObjectMapper().readTree(rsp.readEntity(String.class));
		System.out.println("  Read jsonNode from response entity: \n  " + jsonNode.toString());
		
		JsonNode exercisesJsonArray = jsonNode.get("exercises");
		if (!exercisesJsonArray.isArray()) throw new Exception("Error parsing json.");
		
		Collection<IExercise> exercises = new LinkedList<IExercise>();
		exercisesJsonArray.forEach(exerciseJsonNode -> {
			try {
				exercises.add(getExercisefromJsonNode(exerciseJsonNode));
			} catch (Exception e) {
				//TODO handle exception!
			}
		});
		return new ArtemisCourse(courseId, title, shortName, exercises);
	}
	
	private IExercise getExercisefromJsonNode(JsonNode exerciseJsonNode) throws Exception {
		System.out.println("IN getExercisefromJsonNode:");
		
		final int exerciseId = exerciseJsonNode.get("id").intValue();
		final String title = exerciseJsonNode.get("title").textValue();
		final String shortName = exerciseJsonNode.get("shortName").textValue();
		final String testRepositoryUrl = exerciseJsonNode.get("testRepositoryUrl").textValue();
		
		checkAuthentication();
		final Response rsp = rootApiTarget
				.path("exercises")
				.path(String.valueOf(exerciseId))	
				.path("programming-submissions")
//				.queryParam("submittedOnly", true)		//TODO true?
				.request().header("Authorization", id_token.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous variant
		checkStatusSuccessful(rsp);
		
		JsonNode submissionsArrayJsonNode = new ObjectMapper().readTree(rsp.readEntity(String.class));
		System.out.println("  Read jsonNode from response entity: \n  " + submissionsArrayJsonNode.toString());

		final Collection<ISubmission> submissions = new LinkedList<ISubmission>();
		submissionsArrayJsonNode.forEach(submission -> {
			submissions.add(getSubmissionFromJsonNode(submission));
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
	
	protected void startAssessment(int submissionID) throws AuthenticationException {
		checkAuthentication();
		
		final Response rsp = rootApiTarget
				.path("programming-submissions")
				.path(String.valueOf(submissionID))
				.path("lock")				// this should be best.
				.request().header("Authorization", id_token.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous variant
		checkStatusSuccessful(rsp);
		
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
			return;
		}
		final String repositoryUrl = jsonNode.get("participation").get("repositoryUrl").asText();
		System.out.println("Read repositoryUrl from response entity: \n" + repositoryUrl);
		
		//TODO that should be done only, if submissions are not downloaded, yet!
		new EgitGitHandler(repositoryUrl).cloneRepo(new File("testPlugin_bookmarks/target/testEgitFromArtemisCall"), "master");
		
		//TODO implement starting the assessment
	}
	
	protected void downloadSubmission(ISubmission submission, File directory) {
		new EgitGitHandler(submission.getRepositoryUrl()).cloneRepo(directory, "master");
	}
	
	protected void submitAssessment(int submissionID) throws AuthenticationException {
		checkAuthentication();
		//TODO implement submitting
	}
	
	private String getApiRoot() {
		return new StringBuilder("https://").append(this.getArtemisHostname()).append("/api").toString();
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
	
	private void checkAuthentication() throws AuthenticationException {
		if (id_token.isEmpty()) login();
	}
	
	private void downloadExercise(IExercise exercise, File directory) {
		//TODO remove hardcoded
		new EgitGitHandler(exercise.getTestRepositoryUrl()).cloneRepo(directory, "master");
	}

	@Override
	public void downloadSubmissions(Collection<ISubmission> submissions, File directory) {
		// TODO IMplement
		for (ISubmission submission : submissions) {
			this.downloadSubmission(submission, directory);
		}
	}

	@Override
	public void startAssessments(Collection<ISubmission> submissions) throws Exception {
		for (ISubmission submissionID : submissions) {
			//TODO see whats best here
//			this.startAssessment(submissionID);
		}
	}

	@Override
	public void submitAssessments(Collection<Integer> submissionIDs) {
		// TODO Implement
		
	}

	@Override
	public void downloadExercises(Collection<IExercise> exercises, File directory) {
		exercises.forEach(exercise -> downloadExercise(exercise, new File(directory, exercise.getShortName())));
	}

}
