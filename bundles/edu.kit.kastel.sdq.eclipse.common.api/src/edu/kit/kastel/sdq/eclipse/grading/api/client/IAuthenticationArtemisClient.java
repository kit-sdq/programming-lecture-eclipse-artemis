package edu.kit.kastel.sdq.eclipse.grading.api.client;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Assessor;

/**
 * REST-Client to execute calls concerning login and authentication.
 */
public interface IAuthenticationArtemisClient {
	/**
	 * Returns raw token as String. The token can be used to authenticate for
	 * REST-calls.
	 * 
	 * @return security token
	 */
	String getRawToken();

	/**
	 * Returns raw token as String. The token can be used to authenticate for
	 * REST-calls.
	 * 
	 * @return security token with Bearer prefix
	 */
	String getBearerToken();

	/**
	 *
	 * @return the artemis "assessor" object (needed for submitting the assessment).
	 * @throws ArtemisClientException if some errors occur while parsing the result.
	 */
	Assessor getAssessor();

	void init() throws ArtemisClientException;
}
