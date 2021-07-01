package testplugin_activateByShortcut;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.kit.kastel.sdq.eclipse.grading.api.AbstractArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.api.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.client.git.AbstractGitHandler;
import edu.kit.kastel.sdq.eclipse.grading.client.git.EgitGitHandler;
import edu.kit.kastel.sdq.eclipse.grading.client.git.JGitGitHandler;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.ArtemisExercise;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.ArtemisSubmission;
import edu.kit.kastel.sdq.eclipse.grading.client.rest.ArtemisRESTClient;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.DefaultProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.WorkspaceUtil;
import testplugin_activateByShortcut.testConfig.AssessmentControllerTest;
import testplugin_activateByShortcut.testConfig.LockAndSubmitTest;

//import org.glassfish.jersey.jackson.



public class ShortcutHandler extends AbstractHandler {

	public void artemisTest() {
		Pair<String, String> credentials = CredentialsGetter.getCredentials();
		AbstractArtemisClient artemisClient = new ArtemisRESTClient(credentials.L, credentials.R, "artemis-test.ipd.kit.edu");
		List<Integer> submissionIds = new LinkedList<Integer>();
		submissionIds.add(79);
		try {
			Collection<ICourse> courses = artemisClient.getCourses();
//			this.coursesTest(courses);
//			this.downloadExerciseTest(artemisClient, courses, 1, 1);


//			new LockAndSubmitTest(credentials.L, credentials.R, "artemis-test.ipd.kit.edu").test();
//			new LockAndSubmitTest(credentials.L, credentials.R, "artemis-test.ipd.kit.edu").testShowcase();
			new LockAndSubmitTest(credentials.L, credentials.R, "artemis-test.ipd.kit.edu").testNextAssessment();



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
		final File eclipseWorkspaceRoot =  ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();

		Collection<IExercise> exercises = courses.stream().filter(course -> (course.getCourseId() == courseId)).findAny().get()
				.getExercises().stream().filter(exercise -> (exercise.getExerciseId() == exerciseId)).collect(Collectors.toList());
		final IExercise exercise = exercises.iterator().next();


		artemisClient.downloadExerciseAndSubmission(exercise, exercise.getSubmissions().iterator().next(),
				eclipseWorkspaceRoot, new DefaultProjectFileNamingStrategy());
		WorkspaceUtil.createEclipseProject(new DefaultProjectFileNamingStrategy().getProjectFileInWorkspace(
				eclipseWorkspaceRoot,
				exercise,
				exercise.getSubmissions().iterator().next()
		));

		System.out.println("Download Done!");
	}

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


		this.artemisTest();
		// you need to import the file into a new Lala-Project.
//		new ConfigDaoTest(new JsonFileConfigDao(new File(eclipseWorkspaceRoot, "Lala/src/config_v2.json"))).run();

		System.out.println("#####AssessmentControllerTest#####");
		AssessmentControllerTest act = new AssessmentControllerTest(new File(eclipseWorkspaceRoot, "Lala/src/config_v2.json"), "Final Task 1");
		act.testConfigLoading();
		act.testMistakesEtc();
		return null;
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

	public void gitCloneWithEgit(String repoURL, String destination) throws ExecutionException {
		this.gitCloneTest(new EgitGitHandler(repoURL), destination);
	}

	public void gitCloneWithJgit(String repoURL, String destination) throws ExecutionException {
		this.gitCloneTest(new JGitGitHandler(repoURL), destination);
	}

}
