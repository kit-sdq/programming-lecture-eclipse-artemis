/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.grading.view.assessment;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class AssessmentTabComposite extends Composite {
	protected Combo comboCourse;
	protected Combo comboExam;
	protected Combo comboExercise;
	protected Button btnReload;
	protected Button btnSave;
	protected Button btnSubmit;
	protected Button btnStartRoundOne;
	protected Button btnStartRoundTwo;
	protected Button btnResetPluginState;
	protected Label lblStatisticsInformation;
	protected Label lblPluginVersion;

	/**
	 * Create the composite.
	 */
	public AssessmentTabComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		Label lblCourse = new Label(this, SWT.NONE);
		lblCourse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCourse.setText("Course");

		comboCourse = new Combo(this, SWT.READ_ONLY);
		comboCourse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblExam = new Label(this, SWT.NONE);
		lblExam.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblExam.setText("Exam");

		comboExam = new Combo(this, SWT.READ_ONLY);
		comboExam.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblExercise = new Label(this, SWT.NONE);
		lblExercise.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblExercise.setText("Exercise");

		comboExercise = new Combo(this, SWT.READ_ONLY);
		comboExercise.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Composite compositeButtons = new Composite(this, SWT.NONE);
		compositeButtons.setLayout(new GridLayout(3, true));
		compositeButtons.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));

		btnReload = new Button(compositeButtons, SWT.NONE);
		btnReload.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnReload.setText("Reload");

		btnSave = new Button(compositeButtons, SWT.NONE);
		btnSave.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnSave.setText("Save");

		btnSubmit = new Button(compositeButtons, SWT.NONE);
		btnSubmit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnSubmit.setText("Submit");

		btnStartRoundOne = new Button(compositeButtons, SWT.NONE);
		btnStartRoundOne.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnStartRoundOne.setText("Start Round 1");

		btnStartRoundTwo = new Button(compositeButtons, SWT.NONE);
		btnStartRoundTwo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnStartRoundTwo.setText("Start Round 2");

		btnResetPluginState = new Button(compositeButtons, SWT.NONE);
		btnResetPluginState.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnResetPluginState.setText("Reset Plugin State");

		Group grpMetaInformation = new Group(this, SWT.NONE);
		grpMetaInformation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		grpMetaInformation.setText("Meta Information");
		grpMetaInformation.setLayout(new GridLayout(2, false));

		Label lblStatistics = new Label(grpMetaInformation, SWT.NONE);
		lblStatistics.setText("Statistics: ");

		lblStatisticsInformation = new Label(grpMetaInformation, SWT.NONE);
		lblStatisticsInformation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		lblPluginVersion = new Label(this, SWT.NONE);
		lblPluginVersion.setAlignment(SWT.RIGHT);
		lblPluginVersion.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, true, 2, 1));
		lblPluginVersion.setText("PluginVersion");

	}

	@Override
	protected final void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
