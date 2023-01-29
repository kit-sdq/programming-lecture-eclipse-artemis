/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.client.rest;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.Feedback;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ParticipationDTO;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ResultsDTO;
import edu.kit.kastel.eclipse.common.api.client.IFeedbackArtemisClient;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class FeedbackArtemisClient extends AbstractArtemisClient implements IFeedbackArtemisClient {
	private final OkHttpClient client;

	public FeedbackArtemisClient(final String hostname, String token) {
		super(hostname);
		this.client = this.createClient(token);
	}

	@Override
	public Feedback[] getFeedbackForResult(ParticipationDTO participation, ResultsDTO result) throws ArtemisClientException {
		Request request = new Request.Builder() //
				.url(this.path(PARTICIPATION_PATHPART, participation.getParticipationId(), RESULT_PATHPART, result.id, "details")).get().build();

		return this.call(this.client, request, Feedback[].class);
	}

}
