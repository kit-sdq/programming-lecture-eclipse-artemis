package edu.kit.kastel.eclipse.student.view.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages";
	public static String EXAMTAB_END;
	public static String EXAMTAB_REMEMBER;
	public static String EXAMTAB_START;
	public static String RESULTTAB_INFO_RESULT;
	public static String RESULTTAB_INFO_FEEDBACK;
	public static String ARTEMISSTUDENTVIEW_LABEL;
	public static String ArtemisStudentView_link_text;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
