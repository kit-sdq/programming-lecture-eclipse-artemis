/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.client.rest;

import java.util.Arrays;
import java.util.List;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ISubmission;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.User;
import edu.kit.kastel.eclipse.common.api.client.ISubmissionsArtemisClient;
import edu.kit.kastel.eclipse.common.api.messages.Messages;
import edu.kit.kastel.eclipse.common.client.mappings.ArtemisSubmission;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SubmissionsArtemisClient extends AbstractArtemisClient implements ISubmissionsArtemisClient {
	private final OkHttpClient client;
	private User assessor;

	public SubmissionsArtemisClient(final String hostName, String token, User assessor) {
		super(hostName);
		this.client = this.createClient(token);
		this.assessor = assessor;
	}

	@Override
	public List<ISubmission> getSubmissions(IExercise exercise, int correctionRound) throws ArtemisClientException {
		boolean isInstructor = exercise.getCourse().isInstructor(this.assessor);
		Request request = new Request.Builder() //
				.url(this.path(EXERCISES_PATHPART, exercise.getExerciseId(), PROGRAMMING_SUBMISSION_PATHPART).newBuilder()
						.addQueryParameter("assessedByTutor", String.valueOf(!isInstructor))
						.addQueryParameter("correction-round", String.valueOf(correctionRound)).build())
				.get().build();

		ArtemisSubmission[] submissionsArray = this.call(this.client, request, ArtemisSubmission[].class);
		for (ArtemisSubmission submission : submissionsArray) {
			submission.init(correctionRound);
		}

		return Arrays.asList(submissionsArray);
	}

	@Override
	public ISubmission getSubmissionById(IExercise artemisExercise, int submissionId) throws ArtemisClientException {
		List<ISubmission> submissions = this.getSubmissions(artemisExercise);
		ISubmission target = submissions.stream().filter(s -> s.getSubmissionId() == submissionId).findFirst().orElse(null);
		if (target == null) {
			throw new ArtemisClientException(String.format(Messages.CLIENT_NO_SUBMISSION_FOUND_FORMAT, submissionId));
		}
		return target;
	}

}
