/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.common.api.client;

import java.net.ConnectException;

import edu.kit.kastel.sdq.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.ParticipationDTO;

/**
 * REST-Client to execute calls concerning participations of exercises.
 */
public interface IParticipationArtemisClient {
	/**
	 * Start the participation for given exercise for the current user.
	 *
	 * @param course
	 * @param exercise
	 * @return the participation
	 * @throws ArtemisClientException
	 * @throws ConnectException
	 */
	ParticipationDTO startParticipationForExercise(ICourse course, IExercise exercise) throws ArtemisClientException, ConnectException;

	/**
	 * Resume the participation of the current user in the given exercise.
	 *
	 * @param course
	 * @param exercise
	 * @return updated participation
	 * @throws ArtemisClientException
	 */
	ParticipationDTO resumeParticipationForExercise(ICourse course, IExercise exercise) throws ArtemisClientException;

	/**
	 * Returns the participation for the current user.
	 *
	 * @param course
	 * @param exercise
	 * @return participation of exercise and current user.
	 * @throws ArtemisClientException
	 */
	ParticipationDTO getParticipationForExercise(ICourse course, IExercise exercise) throws ArtemisClientException;

	/**
	 * Returns the participation for the given participationId including its latest
	 * result.
	 *
	 * @param participationId
	 * @return the participation with its latest result.
	 * @throws ArtemisClientException
	 */
	ParticipationDTO getParticipationWithLatestResultForExercise(int participationId) throws ArtemisClientException;
}
