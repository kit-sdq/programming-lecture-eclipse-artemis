/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.api.controller;

import java.io.File;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ISubmission;

/**
 * Controls tasks concerned with load, saving and cleaning exercises in the
 * local workspace.
 */
public interface IExerciseArtemisController extends IController {
	/**
	 * Clones exercise and a submission into one project.
	 */
	void downloadExerciseAndSubmission(IExercise exercise, ISubmission submission, File dir, IProjectFileNamingStrategy namingStrategy)
			throws ArtemisClientException;
}
