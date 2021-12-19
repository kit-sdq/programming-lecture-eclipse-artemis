package edu.kit.kastel.eclipse.student.view.controllers;

import java.util.List;
import java.util.Map;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ResultsDTO;

public class StudentViewController extends AssessmentViewController {
	
	public StudentViewController() {
		super();
	}
	
	public void startExercise() {
		getSystemwideController().loadExerciseForStudent();
	}
	
	public void onSubmitSolution() {
		getSystemwideController().submitSolution();
	}
	
	public void cleanWorkspace() {
		getSystemwideController().cleanWorkspace();
	}
	
	public Map<ResultsDTO, List<Feedback>> getFeedbackExcerise() {
		return getSystemwideController().getFeedbackExcerise();
	}
	
	/**
	 * @return the name of all courses
	 */
	public List<String> getCourseShortNames() {
		return getArtemisController().getCourseShortNames();
	}

}
