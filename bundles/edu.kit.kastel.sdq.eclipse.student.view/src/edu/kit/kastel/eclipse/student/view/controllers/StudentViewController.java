package edu.kit.kastel.eclipse.student.view.controllers;

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

}
