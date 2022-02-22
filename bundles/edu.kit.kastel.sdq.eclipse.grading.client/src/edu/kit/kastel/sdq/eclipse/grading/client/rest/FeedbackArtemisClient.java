package edu.kit.kastel.sdq.eclipse.grading.client.rest;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;

import com.fasterxml.jackson.databind.JsonNode;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ParticipationDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ResultsDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.client.IFeedbackArtemisClient;

public class FeedbackArtemisClient extends AbstractArtemisClient implements IFeedbackArtemisClient {
	private static final ILog log = Platform.getLog(FeedbackArtemisClient.class);

	private WebTarget endpoint;
	private String token;

	public FeedbackArtemisClient(final String hostName, String token) {
		super(hostName);

		this.endpoint = getEndpoint(this.getApiRootURL());
		this.token = token;
	}

	@Override
	public Feedback[] getFeedbackForResult(ParticipationDTO participation, ResultsDTO result) throws ArtemisClientException {
		final Response exercisesRsp = this.endpoint.path(PARTICIPATION_PATHPART).path(Integer.toString(participation.getParticipationID()))
				.path(RESULT_PATHPART).path(Integer.toString(result.id)).path("details").request().header(AUTHORIZATION_NAME, this.token).buildGet().invoke();

		this.throwIfStatusUnsuccessful(exercisesRsp);

		// get the part of the json that we want to deserialize
		final JsonNode exercisesAndParticipationsJsonNode = this.readTree(exercisesRsp.readEntity(String.class));
		return this.read(exercisesAndParticipationsJsonNode.toString(), Feedback[].class);
	}

}
