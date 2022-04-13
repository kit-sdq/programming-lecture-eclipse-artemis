/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.common.api.client;

import java.time.LocalDateTime;

import edu.kit.kastel.sdq.eclipse.common.api.ArtemisClientException;

public interface IUtilArtemisClient {
	/**
	 * Returns current time of server.
	 *
	 * @return current Date of server
	 * @throws ArtemisClientException
	 */
	LocalDateTime getTime() throws ArtemisClientException;
}
