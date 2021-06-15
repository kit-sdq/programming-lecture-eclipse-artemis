package testplugin_activateByShortcut.testConfig;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

import edu.kit.kastel.sdq.eclipse.grading.api.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.core.AssessmentController;

public class AssessmentControllerTest {

	private AssessmentController assessmentController;
	
	public AssessmentControllerTest(File configFile, String exerciseName) {
		this.assessmentController = new AssessmentController(configFile, exerciseName);
		
	}
	
	public void testConfigLoading() {
		// test config stuf
		try {
			System.out.println("mistakes from ass controller: " +assessmentController.getMistakes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testMistakesEtc() {
		System.out.println("Testing mistake handling in core");
		try {
			Collection<IMistakeType> mistakeTypes = assessmentController.getMistakes();
			mistakeTypes.forEach(mistakeType -> {
				System.out.println("  Testing mistakeType " + mistakeType.toString());
				
				assessmentController.addAnnotation(mistakeType, 0, 22, "class.name", Optional.empty(), Optional.empty());
			});
		} catch (IOException e) { }
		
		System.out.println("  Got Annotations: ");
		printAnnotations(assessmentController.getAnnotations("class.name"), "    ");
		
		assessmentController.removeAnnotation(1);
		System.out.println("  Got Annotations after remove: ");
		printAnnotations(assessmentController.getAnnotations("class.name"), "    ");
		

		assessmentController.modifyAnnotation(2, Optional.of("mmmmmmmmmmmmmmmmmmmmmmmMmmmmmmodified message"), Optional.empty());
		System.out.println("  Got Annotations after modifying (2): ");
		printAnnotations(assessmentController.getAnnotations("class.name"), "    ");
	}
	
	private void printAnnotations(Collection<IAnnotation> annos, String space) {
		annos.stream().forEach(annotation -> System.out.println(space + annotation));
	}
}
