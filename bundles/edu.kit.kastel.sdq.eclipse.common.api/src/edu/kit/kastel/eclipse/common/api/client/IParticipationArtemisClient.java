/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.api.client;

import java.net.ConnectException;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ICourse;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ParticipationDTO;

/**
 * REST-Client to execute calls concerning participations of exercises.
 */
public interface IParticipationArtemisClient {
	/**
	 * Start the participation for given exercise for the current user.
	 */
	ParticipationDTO startParticipation(ICourse course, IExercise exercise) throws ArtemisClientException, ConnectException;

	/**
	 * Returns the participation for the current user.
	 */
	ParticipationDTO getParticipation(ICourse course, IExercise exercise) throws ArtemisClientException;

	/**
	 * Returns the participation for the given participationId including its latest
	 * result.
	 */
	ParticipationDTO getParticipationWithLatestResult(int participationId) throws ArtemisClientException;
}
