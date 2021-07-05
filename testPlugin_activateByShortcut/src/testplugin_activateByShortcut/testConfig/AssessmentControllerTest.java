package testplugin_activateByShortcut.testConfig;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import edu.kit.kastel.sdq.eclipse.grading.api.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.core.SystemwideController;
import edu.kit.kastel.sdq.eclipse.grading.core.model.MistakeType;

public class AssessmentControllerTest {

	private IAssessmentController assessmentController;

	public AssessmentControllerTest(File configFile, String exerciseName) {
		this.assessmentController = new SystemwideController(configFile, null, null, null).getAssessmentController(5555, exerciseName);
	}

	private void printAnnotations(Collection<IAnnotation> annos, String space) {
		annos.stream().forEach(annotation -> System.out.println(space + annotation));
	}

	private void printCalculatedPenaltiesForMistakesAndRatingGroups() {
		try {
			System.out.println("-- Rating Groups caluclated Penalties --");
			this.assessmentController.getRatingGroups()
			.forEach(ratingGroup -> {
					try {
						System.out.println( "[" + ratingGroup + "]\n    " +
								this.assessmentController.calculateCurrentPenaltyForRatingGroup(ratingGroup));
					} catch (IOException e) {}
				});

			System.out.println("-- Mistakes caluclated Penalties --");

			this.assessmentController.getMistakes().forEach(mistakeType -> {
				try {
					System.out.println( "[" + mistakeType + "]\n   " +
							this.assessmentController.calculateCurrentPenaltyForMistakeType(mistakeType));
				} catch (IOException e) {}
			});
		} catch (IOException e) { }
	}

	public void testConfigLoading() {
		// test config stuf
		try {
			System.out.println("mistakes from ass controller: " +this.assessmentController.getMistakes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testMistakesEtc() {
		System.out.println("Testing mistake handling in core");
		try {
			Collection<IMistakeType> mistakeTypes = this.assessmentController.getMistakes();
			mistakeTypes.forEach(mistakeType -> {
				System.out.println("  Testing mistakeType " + mistakeType.toString());

				this.assessmentController.addAnnotation(mistakeType, 0, 22, "class.name", null, null);
			});
		} catch (IOException e) { }

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
			this.assessmentController.addAnnotation(jdEmpty, 0, 44, "class2.name", null, null);
			this.assessmentController.addAnnotation(jdEmpty2, 45, 46, "class2.name", null, null);
		} catch (Exception e) {}

		this.printCalculatedPenaltiesForMistakesAndRatingGroups();

	}
}
