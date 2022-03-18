package edu.kit.kastel.sdq.eclipse.grading.api.client;

import java.time.LocalDateTime;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;

public interface IUtilArtemisClient {
	/**
	 * Returns current time of server.
	 *
	 * @return current Date of server
	 * @throws ArtemisClientException
	 */
	LocalDateTime getTime() throws ArtemisClientException;
}
