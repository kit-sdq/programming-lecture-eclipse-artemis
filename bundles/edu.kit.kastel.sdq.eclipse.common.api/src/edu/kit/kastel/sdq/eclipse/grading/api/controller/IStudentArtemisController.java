/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.grading.api.controller;

import java.util.List;
import java.util.Optional;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IStudentExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ParticipationDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ResultsDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.util.Pair;

/**
 * Works as an interface from backend to REST-clients. It handles specific tasks
 * concerned with the student product.
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
	 * Fetches the feedback of all results from the given exercise.
	 *
	 * @param course
	 * @param excerise
	 * @return Map from result to its feedbacks.
	 */
	Pair<ResultsDTO, List<Feedback>> getFeedbackForExercise(ICourse course, IExercise excerise);

	/**
	 * Starts an exam.
	 *
	 * @param course
	 * @param exam
	 * @return the exam and its exercises.
	 */
	IStudentExam startExam(ICourse course, IExam exam, boolean alreadyStarted);

	/**
	 * Returns participation if exists of given exercise and current user.
	 *
	 * @param course
	 * @param exercise
	 * @return
	 */
	Optional<ParticipationDTO> getParticipation(ICourse course, IExercise exercise);
}
