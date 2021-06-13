package gui.controllers;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import edu.kit.kastel.sdq.eclipse.grading.api.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.core.AssessmentController;

public class AssessmentViewController {

	private static final String EXERCISE_NAME = "Final Task 2";
	private static final String CONFIG_PATH = "C:\\Users\\Ruessmann\\Desktop\\programming-lecture-eclipse-artemis-grading\\bundles\\edu.kit.kastel.sdq.eclipse.grading.view\\resources\\config_v2.json";
	private IAssessmentController assessmentController;

	public AssessmentViewController() {
		this.assessmentController = new AssessmentController(new File(AssessmentViewController.CONFIG_PATH),
				AssessmentViewController.EXERCISE_NAME);
	}

	public Collection<IMistakeType> getMistakeTypesForButtonView() throws IOException {
		return this.assessmentController.getMistakes();
	}

}
