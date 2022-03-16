package edu.kit.kastel.sdq.eclipse.grading.core.messages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages";
	public static String STUDENT_ARTMIS_CONTROLLER_EXAM_INFO;
	public static String STUDENT_ARTMIS_CONTROLLER_CONFIRM_START_EXAM;
	public static String STUDENT_ARTMIS_CONTROLLER_CLEAN;
	public static String STUDENT_ARTMIS_CONTROLLER_CLEAN_SUCCESSFUL;
	public static String STUDENT_ARTMIS_CONTROLLER_SUBMITTING_SOLUTION;
	public static String STUDENT_ARTMIS_CONTROLLER_RESET;
	public static String STUDENT_ARTMIS_CONTROLLER_RESET_SUCCESSFUL;
	public static String STUDENT_ARTMIS_CONTROLLER_EXAM_NOT_SUBMITTED;
	public static String STUDENT_ARTMIS_CONTROLLER_EXAM_NO_SIGN_IN;
	public static String STUDENT_ARTMIS_CONTROLLER_EXAM_OVER;
	public static String ASSESSMENT_COULD_NOT_BE_STARTED_MESSAGE;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
