/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.core.artemis.naming;

import java.io.File;

import edu.kit.kastel.eclipse.common.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.artemis4j.api.artemis.Exercise;
import edu.kit.kastel.sdq.artemis4j.api.artemis.assessment.Submission;

/**
 * A naming strategy that creates projects like this:
 *
 * <pre>
 * exercise-${EXERCISE_ID}-${EXERCISE_SHORTNAME}-${STUDENT_ID}-round-${round}-submission-${SUBMISSION_ID}${-locked}
 * </pre>
 *
 */
public class DefaultProjectFileNamingStrategy implements IProjectFileNamingStrategy {
	/**
	 * Create the Strategy.
	 */
	DefaultProjectFileNamingStrategy() {
	}

	@Override
	public File getAssignmentFileInProjectDirectory(File projectDirectory) {
		return new File(projectDirectory, "assignment");
	}

	@Override
	public File getProjectFileInWorkspace(File workspaceDirectory, Exercise exercise, Submission submission) {
		String projectName = "";
		projectName += "exercise-" + exercise.getExerciseId() + "-" + exercise.getShortName();
		if (submission != null) {
			var result = submission.getResult(submission.getCorrectionRound());

			projectName += "-" + submission.getParticipantIdentifier();
			projectName += "-round-" + (submission.getCorrectionRound() + 1);
			projectName += "-submission-" + submission.getSubmissionId();
			projectName += (result != null && result.completionDate != null) ? "" : "-locked";
		}
		return new File(workspaceDirectory, projectName);
	}

}
