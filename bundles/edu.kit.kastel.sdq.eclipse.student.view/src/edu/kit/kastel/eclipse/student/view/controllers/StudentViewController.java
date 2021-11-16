package edu.kit.kastel.eclipse.student.view.controllers;

public class StudentViewController extends AssessmentViewController {
	
	public StudentViewController() {
		super();
	}
	
	public void loadExerciseForUserInWorkspace() {
		getSystemwideController().loadExerciseForStudent();
	}

}
