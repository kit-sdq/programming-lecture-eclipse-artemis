package edu.kit.kastel.sdq.eclipse.grading.core.artemis.naming;

import java.io.File;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;

/**
 * A naming strategy that creates projects like this:
 *
 * <pre>
 * exercise-${EXERCISE_ID}-${EXERCISE_SHORTNAME}-${STUDENT_ID}-round-${round}-submission-${SUBMISSION_ID}}
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
	public File getGitFileInProjectDirectory(File projectDirectory) {
		return new File(projectDirectory, ".git");
	}

	@Override
	public File getProjectFileInWorkspace(File workspaceDirectory, IExercise exercise, ISubmission submission) {
		String projectName = "";
		projectName += "exercise-" + exercise.getExerciseId() + "-" + exercise.getShortName();
		if (submission != null) {
			projectName += "-" + submission.getParticipantIdentifier();
			projectName += "-round-" + (submission.getCorrectionRound() + 1);
			projectName += "-submission-" + submission.getSubmissionId();
		}
		return new File(workspaceDirectory, projectName);
	}

}
