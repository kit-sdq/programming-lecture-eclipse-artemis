/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.api.controller;

import java.io.File;

import edu.kit.kastel.eclipse.common.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.artemis4j.api.ArtemisClientException;
import edu.kit.kastel.sdq.artemis4j.api.artemis.Exercise;
import edu.kit.kastel.sdq.artemis4j.api.artemis.assessment.Submission;

/**
 * Controls tasks concerned with load, saving and cleaning exercises in the
 * local workspace.
 */
public interface IExerciseArtemisController extends IController {
	/**
	 * Clones exercise and a submission into one project.
	 */
	void downloadExerciseAndSubmission(Exercise exercise, Submission submission, File dir, IProjectFileNamingStrategy namingStrategy)
			throws ArtemisClientException;
}
