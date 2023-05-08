/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.grading.view.assessment;

import static edu.kit.kastel.eclipse.common.view.languages.LanguageSettings.I18N;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.ResourceManager;

import edu.kit.kastel.eclipse.common.api.EclipseArtemisConstants;

public class AssessmentTabComposite extends Composite {
	protected Combo comboCourse;
	protected Combo comboExam;
	protected Combo comboExercise;
	protected Button btnReload;
	protected Button btnSave;
	protected Button btnSubmit;
	protected Button btnStartRoundOne;
	protected Button btnStartRoundTwo;
	protected Button btnCloseAssessment;
	protected Button btnResetPluginState;
	protected Label lblStatisticsInformation;
	protected Label lblPluginVersion;
	protected Button btnHelp;
	protected Button btnRerunAutograder;

	// Backlog
	protected Combo comboBacklogSubmission;
	protected Button btnBacklogRefreshSubmissions;
	protected Button btnBacklogLoadSubmission;

	/**
	 * Create the composite.
	 */
	public AssessmentTabComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		Label lblCourse = new Label(this, SWT.NONE);
		lblCourse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCourse.setText(I18N().course());

		comboCourse = new Combo(this, SWT.READ_ONLY);
		comboCourse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblExam = new Label(this, SWT.NONE);
		lblExam.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblExam.setText(I18N().exam());

		comboExam = new Combo(this, SWT.READ_ONLY);
		comboExam.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblExercise = new Label(this, SWT.NONE);
		lblExercise.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblExercise.setText(I18N().exercise());

		comboExercise = new Combo(this, SWT.READ_ONLY);
		comboExercise.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Composite compositeButtons = new Composite(this, SWT.BORDER);
		compositeButtons.setLayout(new GridLayout(1, true));
		compositeButtons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		Group grpGeneral = new Group(compositeButtons, SWT.NONE);
		grpGeneral.setText(I18N().general());
		grpGeneral.setLayout(new GridLayout(3, true));
		grpGeneral.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		btnStartRoundOne = new Button(grpGeneral, SWT.NONE);
		btnStartRoundOne.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnStartRoundOne.setText(I18N().tabAssessmentStartGradingRound(1));

		btnStartRoundTwo = new Button(grpGeneral, SWT.NONE);
		btnStartRoundTwo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnStartRoundTwo.setText(I18N().tabAssessmentStartGradingRound(2));

		btnResetPluginState = new Button(grpGeneral, SWT.NONE);
		btnResetPluginState.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnResetPluginState.setText(I18N().resetPluginState());

		Group grpAssessment = new Group(compositeButtons, SWT.NONE);
		grpAssessment.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpAssessment.setText(I18N().assessment());
		grpAssessment.setLayout(new GridLayout(2, true));

		btnSave = new Button(grpAssessment, SWT.NONE);
		btnSave.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnSave.setText(I18N().saveAssessment());

		btnSubmit = new Button(grpAssessment, SWT.NONE);
		btnSubmit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnSubmit.setText(I18N().submitAssessment());

		btnReload = new Button(grpAssessment, SWT.NONE);
		btnReload.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnReload.setText(I18N().reloadAssessment());

		btnCloseAssessment = new Button(grpAssessment, SWT.NONE);
		btnCloseAssessment.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnCloseAssessment.setText(I18N().closeAssessment());

		Group grpAutograder = new Group(compositeButtons, SWT.NONE);
		grpAutograder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpAutograder.setText(I18N().autograder());
		grpAutograder.setLayout(new GridLayout(2, true));

		btnRerunAutograder = new Button(grpAutograder, SWT.NONE);
		btnRerunAutograder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnRerunAutograder.setText(I18N().rerunAutograder());

		Group grpMetaInformation = new Group(this, SWT.NONE);
		grpMetaInformation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		grpMetaInformation.setText(I18N().metaInformation());
		grpMetaInformation.setLayout(new GridLayout(2, false));

		Label lblStatistics = new Label(grpMetaInformation, SWT.NONE);
		lblStatistics.setText(I18N().statistics());

		lblStatisticsInformation = new Label(grpMetaInformation, SWT.NONE);
		lblStatisticsInformation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Group grpBacklog = new Group(this, SWT.NONE);
		grpBacklog.setLayout(new GridLayout(2, false));
		grpBacklog.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		grpBacklog.setText(I18N().backlog());

		Label lblSubmission = new Label(grpBacklog, SWT.NONE);
		lblSubmission.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSubmission.setText(I18N().submission());

		comboBacklogSubmission = new Combo(grpBacklog, SWT.READ_ONLY);
		comboBacklogSubmission.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Composite compositeBacklogButtons = new Composite(grpBacklog, SWT.NONE);
		compositeBacklogButtons.setLayout(new GridLayout(2, true));
		compositeBacklogButtons.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));

		btnBacklogRefreshSubmissions = new Button(compositeBacklogButtons, SWT.NONE);
		btnBacklogRefreshSubmissions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnBacklogRefreshSubmissions.setText(I18N().backlogRefresh());

		btnBacklogLoadSubmission = new Button(compositeBacklogButtons, SWT.NONE);
		btnBacklogLoadSubmission.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnBacklogLoadSubmission.setText(I18N().reloadAssessment());

		Composite bottomComposite = new Composite(this, SWT.NONE);
		bottomComposite.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true, 2, 1));
		bottomComposite.setLayout(new GridLayout(2, false));

		btnHelp = new Button(bottomComposite, SWT.NONE);
		btnHelp.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/etool16/help_contents@2x.png"));
		btnHelp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Program.launch(EclipseArtemisConstants.GRADING_WIKI_URL);
			}
		});

		lblPluginVersion = new Label(bottomComposite, SWT.NONE);
		lblPluginVersion.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 1));
		lblPluginVersion.setSize(359, 20);
		lblPluginVersion.setAlignment(SWT.RIGHT);
		lblPluginVersion.setText("PluginVersion");

	}

	@Override
	protected final void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
