package edu.kit.kastel.eclipse.student.view.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages";  
	public static String ExamTab_END;
	public static String ExamTab_REMEMBER;
	public static String ExamTab_START;
	public static String ResultTab_INFO_RESULT;
	public static String ResultTab_INFO_FEEDBACK;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
