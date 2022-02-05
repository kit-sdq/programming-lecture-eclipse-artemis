package edu.kit.kastel.sdq.eclipse.grading.api.client;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Assessor;

public interface IAuthenticationArtemisClient {
	/**
	 * Login to Artemis.
	 * 
	 * @return security token
	 * @throws ArtemisClientException
	 */
	String getToken();
	
	/**
	 *
	 * @return the artemis "assessor" object (needed for submitting the assessment).
	 * @throws ArtemisClientException if some errors occur while parsing the result.
	 */
	Assessor getAssessor();
}
