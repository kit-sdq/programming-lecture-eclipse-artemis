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

/**
 * Works as an interface from backend to REST-clients. It handles specific tasks concerned with the student product.
 */
public interface IStudentArtemisController extends IArtemisController {

	/**
	 * Fetches the exercises of the exam with the title examTitle.
	 * 
	 * @param examTitle
	 * @return StudentExam with exam and all its exercises.
	 */
	IStudentExam getExercisesFromStudentExam(String examTitle);

	/**
	 * 
	 * @param course
	 * @param excerise
	 * @return
	 */
	Map<ResultsDTO, List<Feedback>> getFeedbackForExercise(ICourse course, IExercise excerise);

	/**
	 * 
	 * @param course
	 * @param exam
	 * @return
	 */
	IStudentExam startExam(ICourse course, IExam exam);

	/**
	 * 
	 * @param course
	 * @param exercise
	 * @return
	 */
	Optional<ParticipationDTO> getParticipation(ICourse course, IExercise exercise);
}
