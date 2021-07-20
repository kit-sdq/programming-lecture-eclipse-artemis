package testplugin_activateByShortcut.testConfig;

import java.io.File;
import java.util.Collection;

import edu.kit.kastel.sdq.eclipse.grading.api.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.alerts.IAlertObserver;
import edu.kit.kastel.sdq.eclipse.grading.core.SystemwideController;
import edu.kit.kastel.sdq.eclipse.grading.core.model.MistakeType;

public class AssessmentControllerTest {

	private IAssessmentController assessmentController;
	private IAlertObserver myAlertObserver = new IAlertObserver() {

		String prefix = "[MyAlertObserver] ";
		@Override
		public void error(String errorMsg, Throwable cause) {
			System.out.println(this.prefix + errorMsg);
			if (cause != null) cause.printStackTrace();
		}

		@Override
		public void info(String infoMsg) {
			System.out.println(this.prefix + infoMsg);
		}

		@Override
		public void warn(String warningMsg) {
			System.out.println(this.prefix + warningMsg);
		}
	};

	public AssessmentControllerTest(File configFile, String exerciseName) {
		SystemwideController sysC = new SystemwideController(configFile, null, null, null);
		this.assessmentController = sysC.getAssessmentController(5555, exerciseName, -1, -1);
		this.assessmentController.getAlertObservable().addAlertObserver(this.myAlertObserver);
		sysC.getArtemisGUIController().getAlertObservable().addAlertObserver(this.myAlertObserver);
	}

	private void printAnnotations(Collection<IAnnotation> annos, String space) {
		annos.stream().forEach(annotation -> System.out.println(space + annotation));
	}

	private void printCalculatedPenaltiesForMistakesAndRatingGroups() {
		System.out.println("-- Rating Groups caluclated Penalties --");
		this.assessmentController.getRatingGroups()
		.forEach(ratingGroup -> {
			System.out.println( "[" + ratingGroup + "]\n    " +
					this.assessmentController.calculateCurrentPenaltyForRatingGroup(ratingGroup));
		});
		System.out.println("-- Mistakes caluclated Penalties --");

		this.assessmentController.getMistakes().forEach(mistakeType -> {
			System.out.println( "[" + mistakeType + "]\n   " +
					this.assessmentController.calculateCurrentPenaltyForMistakeType(mistakeType));
		});
	}

	public void testConfigLoading() {
		System.out.println("mistakes from ass controller: " +this.assessmentController.getMistakes());
	}

	public void testMistakesEtc() {
		System.out.println("Testing mistake handling in core");
		try {
			Collection<IMistakeType> mistakeTypes = this.assessmentController.getMistakes();
			int idcount = 1;

			for (IMistakeType mistakeType : mistakeTypes) {
				System.out.println("  Testing mistakeType " + mistakeType.toString());
				//todo add custom shit
				if (mistakeType.getButtonName().equals("Custom Penalty")) {
					this.assessmentController.addAnnotation(idcount++, mistakeType, 0, 22, "class.name", "myCustomMessage", 25D, 2000, 2082);
				} else {
					this.assessmentController.addAnnotation(idcount++, mistakeType, 0, 22, "class.name", null, null, 2000, 2082);
				}
			}
		} catch (Exception e) { }

		System.out.println("  Got Annotations: ");
		this.printAnnotations(this.assessmentController.getAnnotations("class.name"), "    ");

		this.assessmentController.removeAnnotation(1);
		System.out.println("  Got Annotations after remove: ");
		this.printAnnotations(this.assessmentController.getAnnotations("class.name"), "    ");


		this.assessmentController.modifyAnnotation(2, "mmmmmmmmmmmmmmmmmmmmmmmMmmmmmmodified message", null);
		System.out.println("  Got Annotations after modifying (2): ");
		this.printAnnotations(this.assessmentController.getAnnotations("class.name"), "    ");


		try {
			IMistakeType jdEmpty = this.assessmentController.getMistakes().stream()
				.map(mistakeType -> ((MistakeType) mistakeType))
				.filter(mistakeType -> mistakeType.getShortName().equals("jdEmpty")).findAny().get();

			IMistakeType jdEmpty2 = this.assessmentController.getMistakes().stream()
					.map(mistakeType -> ((MistakeType) mistakeType))
					.filter(mistakeType -> mistakeType.getShortName().equals("jdEmpty2")).findAny().get();
			System.out.println("Adding a few annotations for " + jdEmpty);
			this.assessmentController.addAnnotation(123,jdEmpty, 0, 44, "class2.name", null, null, 2000, 2082);
			this.assessmentController.addAnnotation(133, jdEmpty2, 45, 46, "class2.name", null, null, 2000, 2082);
		} catch (Exception e) {}

		this.printCalculatedPenaltiesForMistakesAndRatingGroups();

	}
}
