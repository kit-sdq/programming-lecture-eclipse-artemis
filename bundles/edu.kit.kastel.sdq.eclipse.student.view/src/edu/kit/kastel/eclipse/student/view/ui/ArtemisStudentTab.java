package edu.kit.kastel.eclipse.student.view.ui;

import org.eclipse.swt.widgets.TabFolder;

public interface ArtemisStudentTab {
	void create(TabFolder tabFolder);

	void reset();

	void callExercisesEvent();

	void callExamEvent();

}
