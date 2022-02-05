package edu.kit.kastel.sdq.eclipse.grading.api.client;

import java.util.Date;
import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;

public interface IUtilArtemisClient {
	/**
	 * Returns current time of server.
	 * 
	 * @return current Date of server
	 * @throws ArtemisClientException
	 */
	Date getTime() throws ArtemisClientException;
}
