/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.api.client;

import java.time.LocalDateTime;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.util.Version;

public interface IUtilArtemisClient {
	/**
	 * Returns current time of server.
	 *
	 * @return current Date of server
	 * @throws ArtemisClientException
	 */
	LocalDateTime getTime() throws ArtemisClientException;

	/**
	 * Get the current version of artemis.
	 * 
	 * @return the current version of artemis
	 * @throws ArtemisClientException
	 */
	Version getVersion() throws ArtemisClientException;
}
