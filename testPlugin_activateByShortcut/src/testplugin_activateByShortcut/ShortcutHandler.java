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
import org.eclipse.core.runtime.CoreException;

import edu.kit.kastel.sdq.eclipse.grading.api.AbstractArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.ArtemisExercise;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.ArtemisSubmission;
import edu.kit.kastel.sdq.eclipse.grading.client.rest.ArtemisRESTClient;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.DefaultProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.WorkspaceUtil;
import testplugin_activateByShortcut.testConfig.LockAndSubmitTest;

//import org.glassfish.jersey.jackson.



public class ShortcutHandler extends AbstractHandler {

	public static final String CONFIG_PATH = "Lala/src/config_v3.json";

	public void artemisTest() {
		Pair<String, String> credentials = CredentialsGetter.getCredentials();
		AbstractArtemisClient artemisClient = new ArtemisRESTClient(credentials.L, credentials.R, "artemis-test.ipd.kit.edu");
		List<Integer> submissionIds = new LinkedList<Integer>();
		submissionIds.add(79);

		try {
			Collection<ICourse> courses = artemisClient.getCourses();
		} catch (Exception e) {
			System.out.println("Got exception in getCourses: " + e.getMessage());
			return;
		}

		new LockAndSubmitTest(credentials.L, credentials.R, "artemis-test.ipd.kit.edu")
			.testNextAssessment()
//				.testAnnotationsDeserialized()
			;




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
		try {
			WorkspaceUtil.createEclipseProject(new DefaultProjectFileNamingStrategy().getProjectFileInWorkspace(
					eclipseWorkspaceRoot,
					exercise,
					exercise.getSubmissions().iterator().next()
			));
		} catch (CoreException e) {
			e.printStackTrace();
		}

		System.out.println("Download Done!");
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final File eclipseWorkspaceRoot =  ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
		final File configFile = new File(eclipseWorkspaceRoot, CONFIG_PATH);
		// you need to import the file into a new Lala-Project.
		System.out.println("##########--[                 CoursesTest                  ]--##########");
		System.out.println(
//				new CoursesTest(configFile, "artemis-test.ipd.kit.edu", "uyduk", "arTem155").getCoursesTest()
		);



		System.out.println("##########--[           AssessmentControllerTest           ]--##########");
//		AssessmentControllerTest act = new AssessmentControllerTest(new File(eclipseWorkspaceRoot, CONFIG_PATH), "Final Task 1");
//		act.testConfigLoading();
//		act.testMistakesEtc();

		System.out.println("##########--[                 ArtemisTest                  ]--##########");
		this.artemisTest();
		return null;
	}
}
