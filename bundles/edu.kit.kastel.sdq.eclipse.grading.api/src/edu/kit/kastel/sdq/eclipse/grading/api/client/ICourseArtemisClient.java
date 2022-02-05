package edu.kit.kastel.sdq.eclipse.grading.api.client;

import java.util.List;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;

public interface ICourseArtemisClient {
	/**
	 *
	 * @return all available courses, containing exercises and available submissions
	 * @throws ArtemisClientException if some errors occur while parsing the result.
	 */
	List<ICourse> getCourses() throws ArtemisClientException;
}
