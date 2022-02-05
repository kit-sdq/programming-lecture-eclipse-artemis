package edu.kit.kastel.sdq.eclipse.grading.api.controller;

import java.io.File;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;

public interface IExerciseArtemisController {
	/**
	 * Clones exercise and a submission into one project.
	 */
	void downloadExerciseAndSubmission(IExercise exercise, ISubmission submission, File dir)
			throws ArtemisClientException;
	
	/**
	 * Clones exercise into local workspace.
	 */
	void downloadExercise(IExercise exercise, File dir, String repoUrl) throws ArtemisClientException;
	

}
