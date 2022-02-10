package edu.kit.kastel.sdq.eclipse.grading.api.client;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ParticipationDTO;

public interface IParticipationArtemisClient {
	ParticipationDTO startParticipationForExercise(ICourse couse, IExercise exercise) throws ArtemisClientException;

	ParticipationDTO resumeParticipationForExercise(ICourse couse, IExercise exercise) throws ArtemisClientException;

	ParticipationDTO getParticipationForExercise(ICourse couse, IExercise exercise) throws ArtemisClientException;

	ParticipationDTO getParticipationWithLatestResultForExercise(int participationId) throws ArtemisClientException;

}
