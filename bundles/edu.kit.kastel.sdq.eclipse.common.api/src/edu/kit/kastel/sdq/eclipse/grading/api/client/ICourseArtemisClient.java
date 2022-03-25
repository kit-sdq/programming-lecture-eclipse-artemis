package edu.kit.kastel.sdq.eclipse.grading.api.client;

import java.util.List;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;

/**
 * REST-Client to execute calls concerning courses.
 */
public interface ICourseArtemisClient {
	/**
	 * Returns all courses for current user.
	 *
	 * @return all available courses, containing exercises and exams.
	 * @throws ArtemisClientException if some errors occur while parsing the result.
	 */
	List<ICourse> getCoursesForDashboard() throws ArtemisClientException;

	/**
	 * Returns all courses for current user. Needs extra rights to be called. If
	 * user is student please use above.
	 *
	 * @return all available courses, containing exercises and available submissions
	 * @throws ArtemisClientException if some errors occur while parsing the result.
	 */
	List<ICourse> getCoursesForAssessment() throws ArtemisClientException;
}
