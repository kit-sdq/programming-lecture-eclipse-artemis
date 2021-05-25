package testplugin_activateByShortcut;

import java.io.File;
import java.io.IOException;

import javax.security.sasl.AuthenticationException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.glassfish.jersey.jackson.internal.FilteringJacksonJaxbJsonProvider;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import testplugin_activateByShortcut.git.AbstractGitHandler;
import testplugin_activateByShortcut.git.EgitGitHandler;
import testplugin_activateByShortcut.git.JGitGitHandler;
import testplugin_activateByShortcut.mappings.ArtemisCourses;
import testplugin_activateByShortcut.rest.ArtemisRESTClient;

import com.fasterxml.jackson.core.JsonFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
//import org.glassfish.jersey.jackson.



public class ShortcutHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		//System.out.println(event.toString());
		ITextSelection selection = (ITextSelection)HandlerUtil.getActiveSiteChecked(event).getSelectionProvider().getSelection();
		System.out.println(
				"  Selection: [startLine, endline, text] = "
				+ "[" 
					+ selection.getStartLine() + ", " 
					+ selection.getEndLine() + ", " 
					+ selection.getText() + ", "
				+ "]"
		);
		
		
		//TEST
//		gitCloneTestWithoutAuth();
//		gitCloneWithEgit("https://github.com/RobinRSchulz/sonntagsfrage.git", "testPlugin_bookmarks/target/testEgit");
//		gitCloneWithEgit("https://github.com/RobinRSchulz/testRepoPrivate.git", "testPlugin_bookmarks/target/testEgitWithAuth");
//		gitCloneWithJgit("https://github.com/RobinRSchulz/sonntagsfrage.git", "testPlugin_bookmarks/target/testJgit");
		
		
		artemisTest();
		return null;
	}
	
	public void artemisTest() {
		Pair<String, String> credentials = CredentialsGetter.getCredentials();
		ArtemisRESTClient artemisClient = new ArtemisRESTClient(credentials.L, credentials.R, "artemis-test.ipd.kit.edu");
		try {
			artemisClient.getCourses();
			artemisClient.startAssessment(79);
//			artemisClient.startAssessment(500000);
		} catch (AuthenticationException e) {
			System.out.println(e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
	public void gitCloneWithEgit(String repoURL, String destination) throws ExecutionException {
		gitCloneTest(new EgitGitHandler(repoURL), destination);
	}
	
	public void gitCloneWithJgit(String repoURL, String destination) throws ExecutionException {
		gitCloneTest(new JGitGitHandler(repoURL), destination);
	}
	
	public void gitCloneTest(AbstractGitHandler handler, String destination) throws ExecutionException {

		final File gitRepoDirectory = new File(destination);
//		try {
//			FileUtils.mkdirs(gitRepoDirectory);
//		} catch (Exception e) {
//			throw new ExecutionException(e.getLocalizedMessage());
//		} 		
		handler.cloneRepo(gitRepoDirectory, "master");
	}

}
