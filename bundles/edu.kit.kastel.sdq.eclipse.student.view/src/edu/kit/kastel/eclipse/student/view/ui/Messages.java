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
	public static String ARTEMISSTUDENTVIEW_LINK;
	public static String ARTEMISSTUDENTVIEW_EXAM_NOT_SUBMITTED;
	public static String ArtemisStudentView_lblExamShortName_text;
	public static String ArtemisStudentView_lblExamIsEnded_text;
	public static String ArtemisStudentView_lblExamDescription_text;
	public static String ArtemisStudentView_lblExamStart_text;
	public static String ArtemisStudentView_resultScore_text;
	public static String ResultTab_lblResultExerciseShortName_text;
	public static String ResultTab_resultScore_text;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
