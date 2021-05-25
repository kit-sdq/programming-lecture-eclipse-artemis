package testplugin_activateByShortcut.rest;

import java.io.File;
import java.io.IOException;
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

import testplugin_activateByShortcut.git.EgitGitHandler;

public class ArtemisRESTClient {
	
	
	private String username;
	private String password;
	private String hostName;
	private WebTarget rootApiTarget;
	
	private Optional<IDToken> id_token;
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @param hostName the artemis host name.
	 */
	public ArtemisRESTClient(final String username, final String password, final String hostName) {
		this.username = username;
		this.password = password;
		this.hostName = hostName;
		this.rootApiTarget = ClientBuilder.newBuilder()
			.build()
			.target(getApiRoot());
		this.id_token = Optional.empty();
	}
	
	private void login() throws AuthenticationException {
		final Response authenticationResponse = rootApiTarget.path("authenticate").request()
				.buildPost(getAuthenticationEntity())
				.invoke();
		
		if (!authenticationResponse.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
			throw new AuthenticationException("Authentication to \"" + getApiRoot() + "\" failed with status code " 
					+ authenticationResponse.getStatus() 
					+ "and reason phrase \"" + authenticationResponse.getStatusInfo().getReasonPhrase() + "\".");
		}
		System.out.println("Tried to authenticate with status " + authenticationResponse.getStatus());
		final String authRspEntity = authenticationResponse.readEntity(String.class);
		try {
			id_token = Optional.of(new IDToken(new ObjectMapper().readTree(authRspEntity).get("id_token").asText()));
		} catch (IOException e1) {
			throw new AuthenticationException("Authentication to \"" + getApiRoot() + "\" failed: No token could be retrieved in server response.");
		}
	}
	
	public void getCourses() throws AuthenticationException {
		checkAuthentication();
		final Response rsp = rootApiTarget
				.path("courses")
				.path("5")														// TestOfCourse
//				.path("with-exercises")
				.path("with-exercises-and-relevant-participations")				// this should be best.
				.request().header("Authorization", id_token.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous variant
//				.submit(); // asynchronous variant
		String rspString = rsp.readEntity(String.class);
		System.out.println("Got entity from rest call: " + rspString );
		try {
			// Put the result into java objects
//			ArtemisCourses courses = objectMapper.readValue(rspString, ArtemisCourses.class);
//			System.out.println("Got parsed entity from rest call: " + courses );

			// Put the result into a JsonNode -> No need for java objects, but bad style.
			JsonNode jsonNode = new ObjectMapper().readTree(rspString);
			System.out.println("Read jsonNode from response entity: \n" + jsonNode.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void startAssessment(int submissionID) throws AuthenticationException {
		checkAuthentication();
		
		final Response rsp = rootApiTarget
				.path("programming-submissions")
				.path(String.valueOf(submissionID))
				.path("lock")				// this should be best.
				.request().header("Authorization", id_token.get().getHeaderString())
				.buildGet()
				.invoke(); // synchronous variant
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
		
		new EgitGitHandler(repositoryUrl).cloneRepo(new File("testPlugin_bookmarks/target/testEgitFromArtemisCall"), "master");
	}
	
	private String getApiRoot() {
		return new StringBuilder("https://").append(this.hostName).append("/api").toString();
	}
	
	private Entity getAuthenticationEntity() {
		//TODO use a json lib!
		final String entityString = new StringBuilder().append("{")
				.append("\"username\":\"").append(this.username).append("\",")
				.append("\"password\":\"").append(this.password).append("\",")
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

}
