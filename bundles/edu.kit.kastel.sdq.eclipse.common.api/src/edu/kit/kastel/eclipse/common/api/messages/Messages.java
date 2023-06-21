/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.api.messages;

public class Messages {
	private Messages() {
		throw new IllegalAccessError();
	}

	public static final String CLIENT_COMMUNICATION_ERROR_FORMAT = "Communication with \" %s \" failed with status \"%s: %s\".";
	public static final String CLIENT_NO_SUBMISSION_FOUND_FORMAT = "Submission %d not found!";
	public static final String ASSESSMENT_COULD_NOT_BE_STARTED_MESSAGE = "Assessment could not be started: ";
	public static final String GIT_OPEN_FAILED = "Git open failed for path: ";
	public static final String GIT_CLONE_FAILED = "Git clone failed with exception: ";
	public static final String GIT_COMMIT_FAILED = "Git commit failed for path: ";
	public static final String GIT_PUSH_FAILED = "Git push failed for path: ";
	public static final String GIT_PULL_FAILED = "Git push failed for path: ";
	public static final String GIT_RESET_FAILED = "Git reset failed for path: ";
}
