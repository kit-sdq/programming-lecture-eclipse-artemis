package edu.kit.kastel.sdq.eclipse.grading.api.client;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ParticipationDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ResultsDTO;

public interface IFeedbackArtemisClient {
	Feedback[] getFeedbackForResult(ParticipationDTO particiaptionId, ResultsDTO resultId) throws ArtemisClientException;
}
