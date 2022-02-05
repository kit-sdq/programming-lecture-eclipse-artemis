package edu.kit.kastel.sdq.eclipse.grading.api.client;

import java.io.File;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;

public interface IExerciseArtemisClient {
	/**
	 * Clones exercise and a submission into one project.
	 */
	void downloadExerciseAndSubmission(IExercise exercise, ISubmission submission, File dir, IProjectFileNamingStrategy namingStrategy)
			throws ArtemisClientException;
	void downloadExercise(IExercise exercise, File dir, IProjectFileNamingStrategy namingStrategy, String repoUrl) throws ArtemisClientException;
	

}
