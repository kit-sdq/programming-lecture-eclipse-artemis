package testplugin_activateByShortcut.testConfig;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.core.resources.ResourcesPlugin;

import edu.kit.kastel.sdq.eclipse.grading.api.AbstractArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.api.ISystemwideController;
import edu.kit.kastel.sdq.eclipse.grading.api.alerts.IAlertObservable;
import edu.kit.kastel.sdq.eclipse.grading.api.alerts.IAlertObserver;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IAssessor;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission.Filter;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.client.rest.ArtemisRESTClient;
import edu.kit.kastel.sdq.eclipse.grading.core.SystemwideController;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.AnnotationMapper;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.DefaultPenaltyCalculationStrategy;
import edu.kit.kastel.sdq.eclipse.grading.core.config.ConfigDao;
import edu.kit.kastel.sdq.eclipse.grading.core.config.ExerciseConfig;
import edu.kit.kastel.sdq.eclipse.grading.core.config.JsonFileConfigDao;
import edu.kit.kastel.sdq.eclipse.grading.core.model.annotation.Annotation;
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
			if (mistakeType.getName().equals("Custom Penalty")) {
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

	private void printBegunSubmissionState(ISystemwideController sysController, String text) {
		System.out.println("Begun Submissions [" + text + "]");
		System.out.println(" -- All submission: " +
				sysController.getBegunSubmissionsProjectNames(Filter.ALL));
		System.out.println(" -- Saved, but not submitted: " +
				sysController.getBegunSubmissionsProjectNames(Filter.SAVED_BUT_NOT_SUBMITTED));
		System.out.println(" -- Saved and submitted: " +
				sysController.getBegunSubmissionsProjectNames(Filter.SAVED_AND_SUBMITTED));

	}

	private void subscribeToAlertObservable(IAlertObservable alertObservable, String upFront) {
		alertObservable.addAlertObserver(new IAlertObserver() {

			@Override
			public void error(String errorMsg, Throwable cause) {
				System.out.println("==err==" + upFront + ":" + errorMsg);
				if (cause != null)
					cause.printStackTrace(System.out);
			}

			@Override
			public void info(String infoMsg) {
				System.out.println("==inf==" + upFront + ":" + infoMsg);
			}

			@Override
			public void warn(String warningMsg) {
				System.out.println("==wrn==" + upFront + ":" + warningMsg);

			}
		});
	}

	public void test() throws Exception {

		ILockResult lockResult = this.artemisClient.startAssessment(3);
		System.out.println("################################LOCK stuff###########################");
		System.out.println("Got Lock result\n" + lockResult);

		IAssessor assessor = this.artemisClient.getAssessor();
		System.out.println("Got ASSessor\n" + assessor);

		System.out.println("################################AnnotationMapper stufff###########################");
		ExerciseConfig exerciseConfig = this.configDao.getExerciseConfig();


		final Collection<IAnnotation> forgedAnnotations = this.getForgedAnnotations(exerciseConfig);

		String mapped = new AnnotationMapper(forgedAnnotations, exerciseConfig.getIMistakeTypes(),
				exerciseConfig.getIRatingGroups(), assessor, lockResult, new DefaultPenaltyCalculationStrategy(forgedAnnotations, exerciseConfig.getIMistakeTypes()))
				.mapToJsonFormattedString();
		System.out.println("Got mapped config!\n" + mapped);

		this.artemisClient.saveAssessment(3, true, mapped);


	}

	public LockAndSubmitTest testNextAssessment() {
		final ISystemwideController sysController = new SystemwideController(
				new File(this.eclipseWorkspaceRoot, ShortcutHandler.CONFIG_PATH),
				this.host,
				this.username,
				this.password);
		final int exerciseID = 1;
		this.subscribeToAlertObservable(sysController.getAlertObservable(), "SysController");
		this.subscribeToAlertObservable(sysController.getArtemisGUIController().getAlertObservable(), "ArtemisController");
		sysController.setCourseIdAndGetExerciseShortNames("praktikum21");
		sysController.setExerciseId("testAufgabe1");

		this.printBegunSubmissionState(sysController, "before assessment start");
		System.out.println("Possible Transitions before start: " + sysController.getCurrentlyPossibleTransitions());
		try {Thread.sleep(20000); } catch (InterruptedException e) {}
		boolean startSuccessful = sysController.startAssessment();

		if (!startSuccessful) {
			System.out.println("######################### NO MORE SUBMISSIONS FOUND ####");
			return this;
		}
		this.subscribeToAlertObservable(
				sysController.getCurrentAssessmentController().getAlertObservable(),
				"ArtemisController");


		this.addSomeFakeAssessments(sysController);

		this.printBegunSubmissionState(sysController, "before save");

		System.out.println("Before save. lockresults: ");
		System.out.println(sysController.getArtemisGUIController().getAllFeedbacksGottenFromLocking(sysController.getCurrentAssessmentController().getSubmissionID()));

		sysController.saveAssessment();
		this.printBegunSubmissionState(sysController, "before submit");
//		sysController.submitAssessment();



		//TODO this is debug
		sysController.reloadAssessment();
		System.out.println("After reload. lockresults: ");
		System.out.println(sysController.getArtemisGUIController().getAllFeedbacksGottenFromLocking(sysController.getCurrentAssessmentController().getSubmissionID()));


		sysController.submitAssessment();




//		// check BACKLOG and deserialization
//		sysController.setCourseIdAndGetExerciseShortNames("praktikum21");
//		sysController.setExerciseId("testAufgabe1");
//		this.printBegunSubmissionState(sysController, "after submit");
//		sysController.setAssessedSubmissionByProjectName("exercise-1-testAufgabe1_submission-89-test-student");
//		sysController.loadAgain();
//		this.subscribeToAlertObservable(
//				sysController.getCurrentAssessmentController().getAlertObservable(),
//				"ArtemisController");
//
//		System.out.println("Deserialized annotations:\n "
//				+ sysController.getCurrentAssessmentController().getAnnotations());

		return this;
	}


}
