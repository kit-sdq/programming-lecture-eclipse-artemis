package edu.kit.kastel.sdq.eclipse.grading.core.artemis.naming;

import java.io.File;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;

/**
 * A naming strategy that creates projects like this:
 *
 * <pre>
 * exercise-${EXERCISE_ID}-${EXERCISE_SHORTNAME}_submission-${SUBMISSION_ID}-$PARTICIPANT_ID}
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
	public File getProjectFileInWorkspace(File workspaceDirectory, IExercise exercise, ISubmission submission) {
		return new File(workspaceDirectory,
				new StringBuilder().append("exercise-").append(exercise.getExerciseId()).append("-").append(exercise.getShortName()).append("-")
						.append(submission.getParticipantIdentifier()).append("-round-").append(submission.getCorrectionRound() + 1).append("-submission-")
						.append(submission.getSubmissionId()).toString());
	}

}
