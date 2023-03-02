/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.api.client;

import java.util.List;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ICourse;

/**
 * REST-Client to execute calls concerning courses.
 */
public interface ICourseArtemisClient {

	/**
	 * Returns all courses for current user. Needs extra rights to be called.
	 *
	 * @return all available courses, containing exercises and available submissions
	 * @throws ArtemisClientException if some errors occur while parsing the result.
	 */
	List<ICourse> getCoursesForAssessment() throws ArtemisClientException;
}
