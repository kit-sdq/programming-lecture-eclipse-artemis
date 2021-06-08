package testplugin_activateByShortcut.testConfig;

import java.io.File;
import java.io.IOException;

import edu.kit.kastel.sdq.eclipse.grading.core.AssessmentController;

public class AssessmentControllerTest {

	private AssessmentController assessmentController;
	
	public AssessmentControllerTest(File configFile, String exerciseName) {
		this.assessmentController = new AssessmentController(configFile, exerciseName);
		
	}
	
	public void run() {
		try {
			System.out.println("mistakes from ass controller: " +assessmentController.getMistakes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
