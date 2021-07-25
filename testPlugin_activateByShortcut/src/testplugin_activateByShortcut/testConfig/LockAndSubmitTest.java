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
import edu.kit.kastel.sdq.eclipse.grading.core.DefaultPenaltyCalculationStrategy;
import edu.kit.kastel.sdq.eclipse.grading.core.SystemwideController;
import edu.kit.kastel.sdq.eclipse.grading.core.annotation.Annotation;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.AnnotationMapper;
import edu.kit.kastel.sdq.eclipse.grading.core.config.ConfigDao;
import edu.kit.kastel.sdq.eclipse.grading.core.config.ExerciseConfig;
import edu.kit.kastel.sdq.eclipse.grading.core.config.JsonFileConfigDao;
import testplugin_activateByShortcut.ShortcutHandler;

public class LockAndSubmitTest {
	final private File eclipseWorkspaceRoot =  ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();

	final private AbstractArtemisClient artemisClient;
	final private ConfigDao configDao;

	private final String username;
	private final String password;
	private final String host;

	public LockAndSubmitTest(String username, String password, String host) {
		this.artemisClient = new ArtemisRESTClient(username, password, host);
		this.configDao = new JsonFileConfigDao(new File(this.eclipseWorkspaceRoot, ShortcutHandler.CONFIG_PATH));

		this.username = username;
		this.password = password;
		this.host = host;
	}


	private void addSomeFakeAssessments(ISystemwideController sysController)  {
		// add new annotations to the assessmentController
		int i = 1;
		for (IMistakeType mistakeType : sysController.getCurrentAssessmentController().getMistakes()) {
			if (i >= 10) break;
			i++;
			String customMsg = null;
			Double customPenalty = null;
			if (mistakeType.getButtonName().equals("Custom Penalty")) {
				customMsg = "my_CUSTOM_MESSAGE";
				customPenalty = 25D;
			}

			sysController.getCurrentAssessmentController().addAnnotation(
					i,
					mistakeType,
					i*2,
					i*2, "src/edu/kit/informatik/BubbleSort",
					customMsg,
					customPenalty,
					2000,
					2082);
		}

		System.out.println("++++++++++++++  Added the following annotations"
				+ sysController.getCurrentAssessmentController().getAnnotations());
	}

	private Collection<IAnnotation> getForgedAnnotations(final ExerciseConfig exerciseConfig) {
		final Collection<IAnnotation> forgedAnnotations = new LinkedList<IAnnotation>();
		int i = 1;
		for (IMistakeType mistakeType : exerciseConfig.getMistakeTypes()) {
			if (i >= 10) break;
			i++;
			forgedAnnotations.add(new Annotation(i, mistakeType, i*2, i*2, "src/edu/kit/informatik/BubbleSort", null, null, 2000, 2082));
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

		String mapped = new AnnotationMapper(forgedAnnotations, exerciseConfig.getIMistakeTypes(),
				exerciseConfig.getIRatingGroups(), assessor, lockResult, new DefaultPenaltyCalculationStrategy(forgedAnnotations, exerciseConfig.getIMistakeTypes()))
				.mapToJsonFormattedString();
		System.out.println("Got mapped config!\n" + mapped);

		this.artemisClient.saveAssessment(3, true, mapped);


	}

	public LockAndSubmitTest testNextAssessment() {
		final String exerciseConfigShortName = "Final Task 1";
		final ISystemwideController sysController = new SystemwideController(
				new File(this.eclipseWorkspaceRoot, ShortcutHandler.CONFIG_PATH),
				exerciseConfigShortName,
				this.host,
				this.username,
				this.password);
		final int exerciseID = 1;
		sysController.setCourseIdAndGetExerciseTitles("praktikum21");
		sysController.setExerciseId("testAufgabe1");

		System.out.println("Assessed, but unsubmitted submissions before assessment start " + sysController.getAssessedSubmissions(true));
		boolean startSuccessful = sysController.onStartAssessmentButton();

		if (!startSuccessful) {
			System.out.println("######################### NO MORE SUBMISSIONS FOUND ####");
			return this;
		}

		this.addSomeFakeAssessments(sysController);

		System.out.println("Assessed, but unsubmitted submissions before save" + sysController.getAssessedSubmissions(true));
		sysController.onSaveAssessmentButton();
		System.out.println("Assessed, but unsubmitted submissions before submit" + sysController.getAssessedSubmissions(true));
		sysController.onSubmitAssessmentButton();
		System.out.println("Assessed, but unsubmitted submissions after submit" + sysController.getAssessedSubmissions(true));


		return this;
	}


}
