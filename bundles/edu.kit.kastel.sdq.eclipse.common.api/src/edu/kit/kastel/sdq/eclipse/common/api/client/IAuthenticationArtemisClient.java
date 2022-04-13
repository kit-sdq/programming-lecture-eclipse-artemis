/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.common.api.client;

import edu.kit.kastel.sdq.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.User;

/**
 * REST-Client to execute calls concerning login and authentication.
 */
public interface IAuthenticationArtemisClient {
	String getArtemisUrl();

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
	 * @return the Artemis {@code Assessor} object (needed for submitting the
	 *         assessment).
	 */
	User getUser();

	void init() throws ArtemisClientException;
}
