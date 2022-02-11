package edu.kit.kastel.sdq.eclipse.grading.api.controller;

import java.util.List;
import java.util.Map;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ResultsDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.client.websocket.WebsocketCallback;

public interface IStudentSystemwideController extends ISystemwideController {
	boolean submitSolution();
	
	boolean cleanWorkspace();
	
	Map<ResultsDTO, List<Feedback>> getFeedbackExcerise();
	
	boolean isSelectedExerciseExpired();
	
	IExercise getCurrentSelectedExercise();

	IExam setExam(String examName);

	void setExerciseIdWithSelectedExam(String exerciseShortName) throws ArtemisClientException;
	
	IExam getExam();
	
	IExam startExam();
	
	List<IExercise> getExerciseShortNamesFromExam(String examShortName);
	
	boolean loadExerciseForStudent();
	
	boolean connectToWebsocket(WebsocketCallback callback);

	void setExamToNull();

	String getExamUrlForCurrentExam();
}
