package testplugin_activateByShortcut;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.security.sasl.AuthenticationException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.glassfish.jersey.jackson.internal.FilteringJacksonJaxbJsonProvider;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kit.kastel.sdq.eclipse.grading.api.AbstractArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.api.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.ISubmission;
import testplugin_activateByShortcut.git.AbstractGitHandler;
import testplugin_activateByShortcut.git.EgitGitHandler;
import testplugin_activateByShortcut.git.JGitGitHandler;
import testplugin_activateByShortcut.mappings.ArtemisCourses;
import testplugin_activateByShortcut.mappings.ArtemisExercise;
import testplugin_activateByShortcut.mappings.ArtemisSubmission;
import testplugin_activateByShortcut.rest.ArtemisRESTClient;
import testplugin_activateByShortcut.testConfig.ConfigDaoTest;

import com.fasterxml.jackson.core.JsonFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
//import org.glassfish.jersey.jackson.



public class ShortcutHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final File eclipseWorkspaceRoot =  ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
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
		
		
//		artemisTest();
		// you need to import the file into a new Lala-Project.
		new ConfigDaoTest(new File(eclipseWorkspaceRoot, "Lala/src/config.json")).run();
		return null;
	}
	
	public void artemisTest() {
		Pair<String, String> credentials = CredentialsGetter.getCredentials();
		AbstractArtemisClient artemisClient = new ArtemisRESTClient(credentials.L, credentials.R, "artemis-test.ipd.kit.edu");
		List<Integer> submissionIds = new LinkedList<Integer>();
		submissionIds.add(79);
		try {
			Collection<ICourse> courses = artemisClient.getCourses();
			coursesTest(courses);
			downloadExerciseTest(artemisClient, courses, 5, 16);
//			artemisClient.startAssessments(assessments);
//			artemisClient.startAssessment(500000);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
	public void coursesTest(Collection<ICourse> courses) {
		System.out.println("-----Courses-----");
		for (ICourse course : courses) {
			System.out.println("  Course " + course.toString());
			for (IExercise exercise : course.getExercises()) {
				System.out.println("  |--Exercise " + ((ArtemisExercise)exercise).toDebugString());
				for (ISubmission submission : exercise.getSubmissions()) {
					System.out.println("    |--Submission " + ((ArtemisSubmission)submission).toDebugString());
					
				}				
			}
		}
	}
	
	public void downloadExerciseTest(AbstractArtemisClient artemisClient, Collection<ICourse> courses, int courseId, int exerciseId) {
		//TODO
		artemisClient.downloadExercises(
			courses.stream().filter(course -> (course.getCourseId() == courseId)).findAny().get()
				.getExercises().stream().filter(exercise -> (exercise.getExerciseId() == exerciseId)).collect(Collectors.toList()),
			new File("testPlugin_bookmarks/target/testExercise16/")
		);
		System.out.println("Download Done!");
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
