package edu.kit.kastel.sdq.eclipse.grading.core.messages;

public class Messages {
	private Messages() {
		throw new IllegalAccessError();
	}

	public static final String STUDENT_ARTMIS_CONTROLLER_EXAM_INFO = "To load exercises for the exam in to your local workspace you have to start the exam first!\nAfter starting the exam you can load exercises in the workspace und submit solutions \\n After submitting solutions you can view results in the Result-Tab. The exam has not been submitted yet.\nYou can only view results after the exam was submitted.\nTo submit the exam you have to submit the exam in the Artemis webclient! It is not possible in Eclipse!";
	public static final String STUDENT_ARTMIS_CONTROLLER_CONFIRM_START_EXAM = "Do you want to start the exam now?";
	public static final String STUDENT_ARTMIS_CONTROLLER_CLEAN = "Your changes will be deleted. Are you sure?";
	public static final String STUDENT_ARTMIS_CONTROLLER_CLEAN_SUCCESSFUL = "Your workspace was successfully cleaned.\nFollowing files have been reset:\n";
	public static final String STUDENT_ARTMIS_CONTROLLER_SUBMITTING_SOLUTION = "Your solutions will be submitted for the selected exercise. Make sure all files are saved.";
	public static final String STUDENT_ARTMIS_CONTROLLER_RESET = "Your local changes will be deleted. Are you sure?";
	public static final String STUDENT_ARTMIS_CONTROLLER_RESET_SUCCESSFUL = "Your workspace was successfully reset to remote state.";
	public static final String STUDENT_ARTMIS_CONTROLLER_EXAM_NOT_SUBMITTED = "You have not handed in the exam in time. It will be graded with 0 Points";
	public static final String STUDENT_ARTMIS_CONTROLLER_EXAM_NO_SIGN_IN = ".\nYou need to be signed up for the exam.";
	public static final String STUDENT_ARTMIS_CONTROLLER_EXAM_OVER = "The exam is already over.\n";
	public static final String ASSESSMENT_COULD_NOT_BE_STARTED_MESSAGE = "Assessment could not be started: ";
}
