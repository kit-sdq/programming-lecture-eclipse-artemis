package edu.kit.kastel.sdq.eclipse.grading.core.messages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages";
	public static String StudentArtemisController_EXAM_INFO;
	public static String StudentArtemisController_Confirm_Start_Exam;
	public static String StudentSystemwideController_CLEAN_SUCCESSFUL;
	public static String StudentSystemwideController_SUBMITTING_SOLUTION;
	public static String StudentSystemwideController_CLEAN;
	public static String ASSESSMENT_COULD_NOT_BE_STARTED_MESSAGE;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
