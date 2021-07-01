package testplugin_activateByShortcut.testConfig;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.core.resources.ResourcesPlugin;

import edu.kit.kastel.sdq.eclipse.grading.api.AbstractArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.api.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.ISystemwideController;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IAssessor;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;
import edu.kit.kastel.sdq.eclipse.grading.client.rest.ArtemisRESTClient;
import edu.kit.kastel.sdq.eclipse.grading.core.SystemwideController;
import edu.kit.kastel.sdq.eclipse.grading.core.annotation.Annotation;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.AnnotationMapper;
import edu.kit.kastel.sdq.eclipse.grading.core.config.ConfigDao;
import edu.kit.kastel.sdq.eclipse.grading.core.config.ExerciseConfig;
import edu.kit.kastel.sdq.eclipse.grading.core.config.JsonFileConfigDao;

public class LockAndSubmitTest {
	final private File eclipseWorkspaceRoot =  ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();

	final private AbstractArtemisClient artemisClient;
	final private ConfigDao configDao;

	private final String username;
	private final String password;
	private final String host;

	public LockAndSubmitTest(String username, String password, String host) {
		this.artemisClient = new ArtemisRESTClient(username, password, host);
		this.configDao = new JsonFileConfigDao(new File(this.eclipseWorkspaceRoot, "Lala/src/config_v2.json"));

		this.username = username;
		this.password = password;
		this.host = host;
	}


	private Collection<IAnnotation> getForgedAnnotations(final ExerciseConfig exerciseConfig) {
		final Collection<IAnnotation> forgedAnnotations = new LinkedList<IAnnotation>();
		int i = 1;
		for (IMistakeType mistakeType : exerciseConfig.getMistakeTypes()) {
			if (i >= 10) break;
			i++;
			forgedAnnotations.add(new Annotation(i, mistakeType, i*2, i*2, "src/edu/kit/informatik/BubbleSort", null, null));
		}
		return forgedAnnotations;
	}

	public void test() throws Exception {

		ILockResult lockResult = this.artemisClient.startAssessment(3);
		System.out.println("################################LOCK stuff###########################");
		System.out.println("Got Lock result\n" + lockResult);

		IAssessor assessor = this.artemisClient.getAssessor();
		System.out.println("Got ASSessor\n" + assessor);

		System.out.println("################################AnnotationMapper stufff###########################");
		ExerciseConfig exerciseConfig = this.configDao.getExerciseConfigs().stream()
				.filter(config -> config.getShortName().endsWith("1"))
				.findAny()
				.get();


		final Collection<IAnnotation> forgedAnnotations = this.getForgedAnnotations(exerciseConfig);

		String mapped = new AnnotationMapper(forgedAnnotations, exerciseConfig.getIMistakeTypes(), exerciseConfig.getIRatingGroups(), assessor, lockResult)
				.mapToJsonFormattedString();
		System.out.println("Got mapped config!\n" + mapped);

		this.artemisClient.submitAssessment(3, mapped);


	}

	public void testShowcase() throws Exception {
		final ISystemwideController sysController = new SystemwideController(
				new File(this.eclipseWorkspaceRoot, "Lala/src/config_v2.json"),
				this.host,
				this.username,
				this.password);
		final String exerciseConfigShortName = "Final Task 1";
		final int submissionID = 5;


		//THIS ID SEEMS TO BE THE PARTICIPATION ID !!!! It is gotten via LOCKing --> TODO einbauen!
		final int participationID = sysController.getArtemisGUIController().downloadHardcodedExerciseAndSubmissionExample();
		System.out.println("++++++++++++++ Downloaded hardcoded exercise and submission example with id: "
				+ participationID);

		// add new annotations to the assessmentController
		int i = 1;
		for (IMistakeType mistakeType : sysController.getAssessmentController(submissionID, exerciseConfigShortName).getMistakes()) {
			if (i >= 10) break;
			i++;
			sysController.getAssessmentController(submissionID, exerciseConfigShortName).addAnnotation(
					mistakeType,
					i*2,
					i*2, "src/edu/kit/informatik/BubbleSort",
					null,
					null);
		}

		System.out.println("++++++++++++++  Added the following annotations"
				+ sysController.getAssessmentController(submissionID, exerciseConfigShortName).getAnnotations());

		//start and submit the assessment
		sysController.getArtemisGUIController().startAssessment(submissionID);
		sysController.getArtemisGUIController().submitAssessment(submissionID);

	}
}
