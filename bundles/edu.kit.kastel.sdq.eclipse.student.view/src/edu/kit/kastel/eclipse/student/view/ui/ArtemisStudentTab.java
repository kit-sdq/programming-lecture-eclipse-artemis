package edu.kit.kastel.eclipse.student.view.ui;

import org.eclipse.swt.widgets.TabFolder;

import edu.kit.kastel.eclipse.student.view.controllers.StudentViewController;

public interface ArtemisStudentTab {
	void create(TabFolder tabFolder);

	void reset();

	void callExercisesEvent();

	void callExamEvent();

	void setViewController(StudentViewController viewController);

}
