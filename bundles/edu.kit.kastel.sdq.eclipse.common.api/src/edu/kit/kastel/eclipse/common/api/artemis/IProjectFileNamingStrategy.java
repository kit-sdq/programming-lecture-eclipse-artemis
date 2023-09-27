/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.api.artemis;

import java.io.File;

import edu.kit.kastel.sdq.artemis4j.api.artemis.Exercise;
import edu.kit.kastel.sdq.artemis4j.api.artemis.assessment.Submission;

/**
 * Strategy to determine how the downloaded projects (containing submission +
 * test code) are named.
 *
 */
public interface IProjectFileNamingStrategy {

	/**
	 *
	 * @param projectDirectory typically calculated by
	 *                         IProjectFileNamingStrategy::getProjectFileInWorkspace.
	 * @return the assignment directory file
	 */
	File getAssignmentFileInProjectDirectory(File projectDirectory);

	/**
	 *
	 * @param workspace  the root directory this project should be created in
	 * @param exercise   input for the calculation
	 * @param submission input for the calculation
	 * @return a exercise-and-submission-unique File inside the workspace.
	 */
	File getProjectFileInWorkspace(File workspace, Exercise exercise, Submission submission);

}
