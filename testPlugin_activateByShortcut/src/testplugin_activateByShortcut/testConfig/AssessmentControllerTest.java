package testplugin_activateByShortcut.testConfig;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import edu.kit.kastel.sdq.eclipse.grading.api.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.core.SystemwideController;

public class AssessmentControllerTest {

	private IAssessmentController assessmentController;

	public AssessmentControllerTest(File configFile, String exerciseName) {
		this.assessmentController = new SystemwideController(configFile, null, null, null).getAssessmentController(5555, exerciseName);
	}

	private void printAnnotations(Collection<IAnnotation> annos, String space) {
		annos.stream().forEach(annotation -> System.out.println(space + annotation));
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
	}
}
