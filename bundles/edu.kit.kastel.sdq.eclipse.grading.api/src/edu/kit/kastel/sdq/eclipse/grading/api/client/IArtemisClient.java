package edu.kit.kastel.sdq.eclipse.grading.api.client;

import java.util.Date;
import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;

public interface IArtemisClient {
	/**
	 * Returns current time of server.
	 * 
	 * @return current Date of server
	 * @throws ArtemisClientException
	 */
	Date getTime() throws ArtemisClientException;
	
	/**
	 * Login to Artemis.
	 * 
	 * @return security token
	 * @throws ArtemisClientException
	 */
	String login() throws ArtemisClientException;
}
