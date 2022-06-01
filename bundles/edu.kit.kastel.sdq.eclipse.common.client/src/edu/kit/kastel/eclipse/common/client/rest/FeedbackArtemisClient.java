/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.client.rest;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.Feedback;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ParticipationDTO;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ResultsDTO;
import edu.kit.kastel.eclipse.common.api.client.IFeedbackArtemisClient;

public class FeedbackArtemisClient extends AbstractArtemisClient implements IFeedbackArtemisClient {
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
		return this.read(exercisesRsp.readEntity(String.class), Feedback[].class);
	}

}
