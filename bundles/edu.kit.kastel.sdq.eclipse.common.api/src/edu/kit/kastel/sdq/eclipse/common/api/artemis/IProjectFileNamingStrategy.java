/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.common.api.artemis;

import java.io.File;

import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.ISubmission;

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
	File getProjectFileInWorkspace(File workspace, IExercise exercise, ISubmission submission);

	/**
	 * 
	 * @param projectDirectory
	 * @return returns the unique .git file in File
	 */
	File getGitFileInProjectDirectory(File projectDirectory);
}
