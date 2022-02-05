package edu.kit.kastel.eclipse.student.view.controllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.kit.kastel.eclipse.common.view.controllers.AArtemisViewController;
import edu.kit.kastel.eclipse.student.view.activator.Activator;
import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ResultsDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.client.websocket.WebsocketCallback;

public class StudentViewController extends AArtemisViewController {
	
	public StudentViewController() {
		super();
		Activator.getDefault().createSystemWideController();
		setSystemwideController(Activator.getDefault().getSystemwideController());
		this.initializeControllersAndObserver();
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
	
	public void fetchCourses() {
		getArtemisGUIController().fetchCourses();
	}
	
	public boolean canSubmit() {
		return !getSystemwideController().isSelectedExerciseExpired();
	}
	
	public boolean canClean() {
		return true;
	}
	
	public boolean connectToWebsocket(WebsocketCallback callBack) {
		return getArtemisGUIController().connectToWebsocket(callBack);
	}
	
	public boolean canFetchFeedback() {
		return true;
	}
	
	public IExercise getCurrentSelectedExercise() {
		return getSystemwideController().getCurrentSelectedExercise();
	}
	
	@Override
	public void setExerciseID(final String exerciseShortName) {
		try {
			getSystemwideController().setExerciseIdWithSelectedExam(exerciseShortName);
		} catch (ArtemisClientException e) {
			getAlertObserver().error(e.getMessage(), e);
		}
	}
	
	@Override
	public List<String> getExercisesShortNamesForExam(String examShortName) {
		return getSystemwideController().getExerciseShortNamesFromExam(examShortName).stream().map(IExercise::getShortName).collect(Collectors.toList());
	}
	
	public IExam setExam(String examName) {
		return getSystemwideController().setExam(examName);
	}
	
	public IExam getCurrentlySelectedExam() {
		return getSystemwideController().getExam();
	}
	
	public IExam startExam() {
		return getSystemwideController().startExam();
	}
 }
