package edu.kit.kastel.eclipse.student.view.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

import edu.kit.kastel.eclipse.student.view.Messages;
import edu.kit.kastel.eclipse.student.view.controllers.StudentViewController;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IStudentExam;

/**
 * This class creates the view elements for the artemis grading process. It is
 * build as a tab folder with four tabs: grading, assessment, exam and backlog.
 * 
 * @see {@link ViewPart}
 */
public class ArtemisStudentView extends ViewPart {
	private static final String NO_SELECTED = "*NOTHING SELECTED*";
	private List<ArtemisStudentTab> tabs = new ArrayList<>();
	private StudentViewController viewController;

	private Button btnSubmitExcerise;
	private Button btnClean;
	private Button btnReset;
	private Combo examCombo;
	private Combo exerciseCombo;
	private Combo courseCombo;

	private ControlDecoration controlDecorationSubmitted;
	private ControlDecoration controlDecorationClean;

	private Composite examContainerComposite;
	private Composite resultContentComposite;
	private Composite compositeFooter;
	private Composite examHeaderComposite;

	private Label resultScore;
	private Label lblExamDescription;
	private Label lblExamShortName;
	private Label lblExamIsEnded;
	private Label lblExamStart;
	private Button btnStart;

	public ArtemisStudentView() {
		this.viewController = new StudentViewController();
		ResultTab resultTab = new ResultTab(this.viewController);
		this.tabs.add(resultTab);
		this.viewController.connectToWebsocket(resultTab);
	}

	/**
	 * This methods creates the whole view components.
	 */
	@Override
	public void createPartControl(Composite parent) {
		this.createView(parent);
	}

	private void createView(Composite parent) {
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
		this.createMainTab(tabFolder);
		this.createAllTabs(tabFolder);
	}

	@Override
	public void setFocus() {
		// NOP
	}

	public void createMainTab(TabFolder tabFolder) {
		TabItem tbtmAssessment = new TabItem(tabFolder, SWT.NONE);
		tbtmAssessment.setText("Exercise");

		ScrolledComposite scrolledCompositeGrading = new ScrolledComposite(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmAssessment.setControl(scrolledCompositeGrading);
		scrolledCompositeGrading.setLayout(new GridLayout(1, true));
		scrolledCompositeGrading.setExpandHorizontal(true);
		scrolledCompositeGrading.setExpandVertical(true);

		Composite gradingComposite = new Composite(scrolledCompositeGrading, SWT.NONE);
		scrolledCompositeGrading.setContent(gradingComposite);
		scrolledCompositeGrading.setMinSize(new Point(100, 100));
		gradingComposite.setLayout(new GridLayout(1, true));
		gradingComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

		Composite composite_2 = new Composite(gradingComposite, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite_2.setLayout(new GridLayout(1, true));

		Label labelExerciseGrading = new Label(composite_2, SWT.NONE);
		labelExerciseGrading.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		labelExerciseGrading.setText("Exercise");
		FontDescriptor boldDescriptor = FontDescriptor.createFrom(labelExerciseGrading.getFont()).setStyle(SWT.BOLD).setHeight(18);
		Font boldFont = boldDescriptor.createFont(labelExerciseGrading.getDisplay());
		labelExerciseGrading.setFont(boldFont);

		Composite assessmentComposite = new Composite(gradingComposite, SWT.BORDER);
		assessmentComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		assessmentComposite.setLayout(new GridLayout(2, true));

		Label lblCourse = new Label(assessmentComposite, SWT.NONE);
		lblCourse.setAlignment(SWT.CENTER);
		boldDescriptor = FontDescriptor.createFrom(lblCourse.getFont()).setStyle(SWT.BOLD).setHeight(9);
		boldFont = boldDescriptor.createFont(lblCourse.getDisplay());
		lblCourse.setFont(boldFont);
		lblCourse.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		lblCourse.setText("Course");

		this.courseCombo = new Combo(assessmentComposite, SWT.READ_ONLY);
		this.courseCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblExam = new Label(assessmentComposite, SWT.NONE);
		lblExam.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		boldDescriptor = FontDescriptor.createFrom(lblExam.getFont()).setStyle(SWT.BOLD).setHeight(9);
		boldFont = boldDescriptor.createFont(lblExam.getDisplay());
		lblExam.setFont(boldFont);
		lblExam.setText("Exam");

		this.examCombo = new Combo(assessmentComposite, SWT.READ_ONLY);
		this.examCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblExercise = new Label(assessmentComposite, SWT.NONE);
		boldDescriptor = FontDescriptor.createFrom(lblExercise.getFont()).setStyle(SWT.BOLD).setHeight(9);
		boldFont = boldDescriptor.createFont(lblExercise.getDisplay());
		lblExercise.setFont(boldFont);
		lblExercise.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblExercise.setText("Exercise");

		this.exerciseCombo = new Combo(assessmentComposite, SWT.READ_ONLY);
		this.exerciseCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		this.loadExamComboEntries(this.courseCombo, this.examCombo, this.exerciseCombo);

		Composite buttons = new Composite(gradingComposite, SWT.BORDER);
		buttons.setLayout(new GridLayout(2, true));
		buttons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		Button btnRefreshArtemisState = new Button(buttons, SWT.NONE);
		btnRefreshArtemisState.setText("Refresh Preferences");
		GridData gd_btnRefreshArtemisState = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_btnRefreshArtemisState.widthHint = 155;
		btnRefreshArtemisState.setLayoutData(gd_btnRefreshArtemisState);

		this.addSelectionListenerForRefreshArtemisStateButton(btnRefreshArtemisState);

		Button btnLoadExercise = new Button(buttons, SWT.NONE);
		btnLoadExercise.setText("Start Exercise");
		btnLoadExercise.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		this.addLoadExerciseListenerForButton(btnLoadExercise);

		// Submit

		Composite submitArea = new Composite(gradingComposite, SWT.BORDER);
		submitArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		submitArea.setLayout(new GridLayout(3, true));

		Label labelSubmit = new Label(submitArea, SWT.NONE);
		labelSubmit.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		boldDescriptor = FontDescriptor.createFrom(labelSubmit.getFont()).setStyle(SWT.BOLD).setHeight(9);
		boldFont = boldDescriptor.createFont(labelSubmit.getDisplay());
		labelSubmit.setFont(boldFont);
		labelSubmit.setText("Submit your solution");

		Label labelClean = new Label(submitArea, SWT.NONE);
		boldDescriptor = FontDescriptor.createFrom(labelClean.getFont()).setStyle(SWT.BOLD).setHeight(9);
		boldFont = boldDescriptor.createFont(labelClean.getDisplay());
		labelClean.setFont(boldFont);
		labelClean.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		labelClean.setText("Clean your last changes");

		Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_WARNING).getImage();

		Label labelReset = new Label(submitArea, SWT.NONE);
		labelReset.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		labelReset.setText("Reset exercise to remote state");
		boldDescriptor = FontDescriptor.createFrom(labelReset.getFont()).setStyle(SWT.BOLD).setHeight(9);
		boldFont = boldDescriptor.createFont(labelReset.getDisplay());
		labelReset.setFont(boldFont);

		Composite composite_1 = new Composite(submitArea, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite_1.setLayout(new GridLayout(1, false));

		this.btnSubmitExcerise = new Button(composite_1, SWT.NONE);
		this.btnSubmitExcerise.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.btnSubmitExcerise.setText(NO_SELECTED);
		this.btnSubmitExcerise.setEnabled(false);

		this.addSelectionListenerForSubmitButton(this.btnSubmitExcerise);
		this.controlDecorationSubmitted = new ControlDecoration(this.btnSubmitExcerise, SWT.RIGHT | SWT.CENTER);
		this.controlDecorationSubmitted.setImage(image);
		this.controlDecorationSubmitted.setDescriptionText("The exercise is expired and can therefore not be submitted!");
		this.controlDecorationSubmitted.hide();
		this.controlDecorationSubmitted.setImage(image);

		Composite composite_3 = new Composite(submitArea, SWT.NONE);
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		composite_3.setLayout(new GridLayout(1, false));

		this.btnClean = new Button(composite_3, SWT.NONE);
		this.btnClean.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.btnClean.setText(NO_SELECTED);
		this.btnClean.addListener(SWT.Selection, e -> this.cleanWorkspaceForSelectedExercise());
		this.btnClean.setEnabled(false);

		this.controlDecorationClean = new ControlDecoration(this.btnClean, SWT.RIGHT | SWT.CENTER);
		this.controlDecorationClean.setMarginWidth(5);
		this.controlDecorationClean.setDescriptionText("The exercise can not be cleaned!");
		this.controlDecorationClean.hide();

		Composite composite_1_1 = new Composite(submitArea, SWT.NONE);
		composite_1_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite_1_1.setLayout(new GridLayout(1, false));

		this.btnReset = new Button(composite_1_1, SWT.NONE);
		this.btnReset.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		this.btnReset.setText("*NOTHING SELECTED*");
		this.btnReset.setEnabled(false);
		this.btnReset.addListener(SWT.Selection, e -> this.resetWorkspaceForSelectedExercise());

		this.createExamPart(gradingComposite);

		scrolledCompositeGrading.setContent(gradingComposite);
		scrolledCompositeGrading.setMinSize(gradingComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private void createExamPart(Composite tabFolder) {
		this.examContainerComposite = new Composite(tabFolder, SWT.NONE);
		this.examContainerComposite.setSize(tabFolder.getSize());
		this.examContainerComposite.setLayout(new GridLayout(1, true));
		this.examContainerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.examContainerComposite.setVisible(false);

		Composite composite = new Composite(this.examContainerComposite, SWT.NONE);
		GridLayout gl_composite = new GridLayout(2, true);
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		Label labelFeedback = new Label(composite, SWT.NONE);
		labelFeedback.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));
		FontDescriptor boldDescriptor = FontDescriptor.createFrom(labelFeedback.getFont()).setStyle(SWT.BOLD).setHeight(18);
		Font boldFont = boldDescriptor.createFont(labelFeedback.getDisplay());
		labelFeedback.setFont(boldFont);
		labelFeedback.setText("Exam");

		this.btnStart = new Button(composite, SWT.CENTER);
		GridData gd_btnStart = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnStart.widthHint = 59;
		this.btnStart.setLayoutData(gd_btnStart);
		this.btnStart.setText("Start");
		this.addSelectionListenerForExamStartButton(this.btnStart);

		Composite composite_1 = new Composite(this.examContainerComposite, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		composite_1.setLayout(new GridLayout(1, false));

		Label labelResult = new Label(composite_1, SWT.NONE);
		labelResult.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		labelResult.setText(Messages.EXAMTAB_REMEMBER);

		Link examLink = new Link(composite_1, SWT.NONE);
		examLink.setText("<a>Click Here to access Artemis to end your Exam</a>");
		examLink.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Program.launch(ArtemisStudentView.this.getLink());
			}
		});

		this.resultContentComposite = new Composite(this.examContainerComposite, SWT.BORDER);
		this.resultContentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		this.resultContentComposite.setLayout(new GridLayout(1, false));

		this.examHeaderComposite = new Composite(this.resultContentComposite, SWT.NONE);
		this.examHeaderComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.examHeaderComposite.setLayout(new GridLayout(2, true));

		this.lblExamShortName = new Label(this.examHeaderComposite, SWT.NONE);
		this.lblExamShortName.setText("Name");
		this.lblExamShortName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		boldDescriptor = FontDescriptor.createFrom(this.lblExamShortName.getFont()).setStyle(SWT.BOLD).setHeight(12);
		boldFont = boldDescriptor.createFont(this.lblExamShortName.getDisplay());
		this.lblExamShortName.setFont(boldFont);

		this.lblExamIsEnded = new Label(this.examHeaderComposite, SWT.NONE);
		this.lblExamIsEnded.setText("finished");
		this.lblExamIsEnded.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		boldDescriptor = FontDescriptor.createFrom(this.lblExamIsEnded.getFont()).setStyle(SWT.BOLD).setHeight(12);
		boldFont = boldDescriptor.createFont(this.lblExamIsEnded.getDisplay());
		this.lblExamIsEnded.setFont(boldFont);

		this.lblExamDescription = new Label(this.resultContentComposite, SWT.NONE);
		this.lblExamDescription.setText("Start Exercises");
		GridData gd_lblExamDescription = new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 1);
		gd_lblExamDescription.horizontalIndent = 5;
		this.lblExamDescription.setLayoutData(gd_lblExamDescription);
		boldDescriptor = FontDescriptor.createFrom(this.lblExamDescription.getFont()).setStyle(SWT.ITALIC).setHeight(9);
		boldFont = boldDescriptor.createFont(this.lblExamDescription.getDisplay());
		this.lblExamDescription.setFont(boldFont);

		Label separator = new Label(this.resultContentComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

		this.compositeFooter = new Composite(this.resultContentComposite, SWT.NONE);
		this.compositeFooter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		this.compositeFooter.setLayout(new GridLayout(1, true));

		this.lblExamStart = new Label(this.compositeFooter, SWT.NONE);
		this.lblExamStart.setText("Starts at: ");
		this.lblExamStart.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 1));
		boldDescriptor = FontDescriptor.createFrom(this.lblExamStart.getFont()).setStyle(SWT.ITALIC).setHeight(9);
		boldFont = boldDescriptor.createFont(this.lblExamStart.getDisplay());
		this.lblExamStart.setFont(boldFont);

		this.resultScore = new Label(this.compositeFooter, SWT.RIGHT);
		this.resultScore.setAlignment(SWT.LEFT);
		this.resultScore.setText("Due to: ");
		this.resultScore.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		boldDescriptor = FontDescriptor.createFrom(this.resultScore.getFont()).setStyle(SWT.ITALIC).setHeight(9);
		boldFont = boldDescriptor.createFont(this.resultScore.getDisplay());
		this.resultScore.setFont(boldFont);
	}

	private void addSelectionListenerForExamStartButton(Button startButton) {
		startButton.addListener(SWT.Selection, e -> {
			IStudentExam exam = this.viewController.startExam();
			this.setExamDataToUI(exam);
			this.loadTasksFromExam();
		});
	}

	private void setExamDataToUI(IStudentExam exam) {
		if (exam != null && exam.getExam() != null) {
			this.lblExamShortName.setText(exam.getExam().getTitle());
			this.resultScore.setText("Due to: " + exam.getExam().getEndDate());
			this.lblExamStart.setText("Starts at: " + exam.getExam().getStartDate());
			this.btnStart.setEnabled(!exam.isStarted());
			this.lblExamIsEnded.setText(exam.isEnded() ? "ended" : "not ended");
			this.lblExamDescription.setText(!exam.isSubmitted() && exam.isEnded() ? Messages.ARTEMISSTUDENTVIEW_EXAM_NOT_SUBMITTED : "");

			this.resultContentComposite.layout();
			this.compositeFooter.layout();
			this.examHeaderComposite.layout();
			this.examContainerComposite.setVisible(true);
		}
	}

	private void setExam() {
		this.setExamDataToUI(this.viewController.getCurrentlySelectedExam());
	}

	private String getLink() {
		return this.viewController.getExamUrlForCurrentExam();
	}

	private void addSelectionListenerForSubmitButton(Button btnSubmit) {
		btnSubmit.addListener(SWT.Selection, e -> this.viewController.onSubmitSolution());
	}

	private void resetWorkspaceForSelectedExercise() {
		this.viewController.resetSelectedExercise();
	}

	private void createExamComboList(Combo courseCombo, Combo examCombo, Combo exerciseCombo) {
		examCombo.removeAll();
		exerciseCombo.removeAll();
		this.viewController.getExerciseShortNames(courseCombo.getItem(courseCombo.getSelectionIndex())).forEach(exerciseCombo::add);
		examCombo.add("None");
		this.viewController.getExamShortNames(courseCombo.getItem(courseCombo.getSelectionIndex())).forEach(examCombo::add);
		examCombo.addListener(SWT.Selection, e -> {
			this.resetBackendStateForNewExam();
			String examName = examCombo.getItem(examCombo.getSelectionIndex());
			if ("None".equals(examName)) {
				this.viewController.setExamToNull();
				this.viewController.getExerciseShortNames(courseCombo.getItem(courseCombo.getSelectionIndex())).forEach(exerciseCombo::add);
				this.resetExamPart();
			} else {
				this.loadTasksFromExam();

			}
		});
		exerciseCombo.addListener(SWT.Selection, e -> this.handleExerciseComboListEvent(exerciseCombo));
	}

	private void loadTasksFromExam() {
		var examName = this.examCombo.getItem(this.examCombo.getSelectionIndex());
		var exercises = this.viewController.getExercisesShortNamesForExam(examName);
		IStudentExam exam = this.viewController.getCurrentlySelectedExam();
		if ((exercises == null || exercises.isEmpty()) && exam != null && exam.isStarted()) {
			exam = this.viewController.startExam();
			this.setExamDataToUI(exam);
			// After start exams contains exercises; therefore no endless loop.
			this.loadTasksFromExam();
		} else {
			exercises.forEach(this.exerciseCombo::add);
			this.handleExamComboEvent();
		}
	}

	private void resetBackendStateForNewExam() {
		this.exerciseCombo.removeAll();
		this.viewController.resetBackendState();
		this.resetButtons();
		this.resetAllTabs();
	}

	private void resetExamPart() {
		this.examContainerComposite.setVisible(false);
	}

	private void updateButtons(String exerciseName) {
		this.setButtonText(exerciseName);
		this.enableButtons();
	}

	private void handleExerciseComboListEvent(Combo exerciseCombo) {
		String exerciseShortName = exerciseCombo.getItem(exerciseCombo.getSelectionIndex());
		this.viewController.setExerciseID(exerciseShortName);
		this.callAllTabsForExerciseEvent();
		this.updateButtons(exerciseShortName);
	}

	private void setButtonText(String exerciseName) {
		this.btnSubmitExcerise.setText("Submit: " + exerciseName);
		this.btnClean.setText("Clean: " + exerciseName);
		this.btnReset.setText("Reset: " + exerciseName);
	}

	private void enableButtons() {
		boolean canSubmit = this.viewController.canSubmit();
		boolean canClean = this.viewController.canClean();
		this.btnSubmitExcerise.setEnabled(canSubmit);
		this.btnClean.setEnabled(canClean);
		this.btnReset.setEnabled(this.viewController.canResetExercise());
		if (!canSubmit) {
			this.controlDecorationSubmitted.show();
		} else {
			this.controlDecorationSubmitted.hide();
		}
		if (!canClean) {
			this.controlDecorationClean.show();
		} else {
			this.controlDecorationClean.hide();
		}
	}

	private void cleanWorkspaceForSelectedExercise() {
		this.viewController.cleanWorkspace();

	}

	private void addSelectionListenerForRefreshArtemisStateButton(Button btnRefreshArtemisState) {
		btnRefreshArtemisState.addListener(SWT.Selection, e -> this.refreshArtemisState());
	}

	private void addLoadExerciseListenerForButton(Button btn) {
		btn.addListener(SWT.Selection, e -> this.handleStartButtonEvent());
	}

	private void handleStartButtonEvent() {
		this.viewController.startExercise();
		this.enableButtons();
	}

	private void loadExamComboEntries(Combo examCourseCombo, Combo examCombo, Combo examExerciseCombo) {
		this.viewController.getCourseShortNames().forEach(examCourseCombo::add);
		examCourseCombo.addListener(SWT.Selection, e -> this.createExamComboList(this.courseCombo, examCombo, examExerciseCombo));
	}

	private void refreshArtemisState() {
		this.viewController = new StudentViewController();
		this.viewController.connectToWebsocket((ResultTab) this.tabs.get(0));
		this.setViewControllerForAllTabs();
		this.resetCombos();
		this.resetButtons();
		this.resetAllTabs();
	}

	private void handleExamComboEvent() {
		this.setExam();
		this.callAllTabsForExamEvent();
	}

	private void resetCombos() {
		this.courseCombo.removeAll();
		this.examCombo.removeAll();
		this.exerciseCombo.removeAll();
		this.viewController.getCourseShortNames().forEach(courseShortName -> this.courseCombo.add(courseShortName));
	}

	private void resetButtons() {
		this.resetButtonText();
		this.resetButtonEnable();
		this.resetExamPart();
	}

	private void resetButtonText() {
		this.btnSubmitExcerise.setText(NO_SELECTED);
		this.btnClean.setText(NO_SELECTED);
		this.btnReset.setText(NO_SELECTED);
	}

	private void resetButtonEnable() {
		this.btnSubmitExcerise.setEnabled(false);
		this.btnClean.setEnabled(false);
		this.btnReset.setEnabled(false);
	}

	private void resetAllTabs() {
		this.tabs.forEach(ArtemisStudentTab::reset);
	}

	private void createAllTabs(TabFolder folder) {
		this.tabs.forEach(t -> t.create(folder));
	}

	private void callAllTabsForExerciseEvent() {
		this.tabs.forEach(ArtemisStudentTab::callExercisesEvent);
	}

	private void callAllTabsForExamEvent() {
		this.tabs.forEach(ArtemisStudentTab::callExamEvent);
	}

	private void setViewControllerForAllTabs() {
		this.tabs.forEach(tab -> tab.setViewController(this.viewController));
	}
}
