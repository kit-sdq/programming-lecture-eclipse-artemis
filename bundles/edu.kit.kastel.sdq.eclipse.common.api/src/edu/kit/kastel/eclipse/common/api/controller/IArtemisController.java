/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.api.controller;

import java.util.List;

import edu.kit.kastel.sdq.artemis4j.api.artemis.Course;
import edu.kit.kastel.sdq.artemis4j.api.artemis.Exercise;
import edu.kit.kastel.sdq.artemis4j.api.artemis.User;
import edu.kit.kastel.sdq.artemis4j.api.artemis.assessment.Feedback;
import edu.kit.kastel.sdq.artemis4j.api.artemis.assessment.Submission;

/**
 * Works as an interface from backend to REST-clients
 *
 */
public interface IArtemisController extends IController {
	/**
	 *
	 * @return all IFeedbacks that were gotten in the process of locking the given
	 *         submission.
	 */
	List<Feedback> getAllFeedbacksGottenFromLocking(Submission submission);

	/**
	 *
	 * @return all submissions of the given @link {@link IExercise}, that have been
	 *         started, saved or submitted by the caller.
	 */
	List<Submission> getBegunSubmissions(Exercise exercise);

	/**
	 *
	 * @return all available courses (contains exercices and available submissions
	 */
	List<Course> getCourses();

	/**
	 *
	 * @return the {@link ICourse#getShortName()} of all available courses
	 */
	List<String> getCourseShortNames();

	/**
	 *
	 * @return the {@link IExam#getTitle()} of all available exams in the given
	 *         {@link ICourse}
	 */
	List<String> getExamTitles(String courseShortName);

	/**
	 * Returns all exercises of exasm with title examTitle.
	 *
	 * @param examTitle exam title of the exam
	 * @return a list of all exercises of the exam
	 */
	List<Exercise> getExercisesFromExam(String examTitle);

	/**
	 *
	 * @return the {@link IExercise#getShortName()}s of the given {@link IExam}
	 */
	List<String> getExerciseShortNamesFromExam(String examTitle);

	User getUserLogin();
}