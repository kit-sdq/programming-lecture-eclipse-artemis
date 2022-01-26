package edu.kit.kastel.eclipse.student.view.controllers;

import java.util.List;
import java.util.Map;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ResultsDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.client.websocket.WebsocketCallback;

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
	
	public List<String> getCourseShortNames() {
		return getArtemisController().getCourseShortNames();
	}
	
	public void fetchCourses() {
		getArtemisController().fetchCourses();
	}
	
	public boolean canSubmit() {
		return !getSystemwideController().isSelectedExerciseExpired();
	}
	
	public boolean canClean() {
		return true;
	}
	
	public boolean connectToWebsocket(WebsocketCallback callBack) {
		return getArtemisController().connectToWebsocket(callBack);
	}
	
	public boolean canFetchFeedback() {
		return true;
	}
	
	public IExercise getCurrentSelectedExercise() {
		return getSystemwideController().getCurrentSelectedExercise();
	}
 }
