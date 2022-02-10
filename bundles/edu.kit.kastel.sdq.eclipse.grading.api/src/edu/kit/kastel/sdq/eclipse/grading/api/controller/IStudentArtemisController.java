package edu.kit.kastel.sdq.eclipse.grading.api.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IStudentExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ParticipationDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ResultsDTO;

public interface IStudentArtemisController {
	IStudentExam getExercisesFromStudentExam(String examTitle);
	
	Map<ResultsDTO, List<Feedback>> getFeedbackExcerise(ICourse course, IExercise excerise);
	
	IStudentExam startExam(ICourse course,IExam exam);
	
	Optional<ParticipationDTO> getParticipation(ICourse course, IExercise exercise);

}
