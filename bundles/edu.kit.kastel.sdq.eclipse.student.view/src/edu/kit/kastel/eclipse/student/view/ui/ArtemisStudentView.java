package edu.kit.kastel.eclipse.student.view.ui;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

import edu.kit.kastel.eclipse.student.view.controllers.StudentViewController;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Link;

/**
 * This class creates the view elements for the artemis grading process. It is
 * build as a tab folder with four tabs: grading, assessment, exam and backlog.
 *
 * @see {@link ViewPart}
 *
 */
public class ArtemisStudentView extends ViewPart {
	private static final String NO_SELECTED = "*NOTHING SELECTED*";
	private List<ArtemisStudentTab> tabs = new ArrayList<>();
	private StudentViewController viewController;

	private ScrolledComposite scrolledCompositeGrading;
	private Composite gradingComposite;

	private Button btnSubmitExcerise;
	private Button btnClean;
	private Button btnReset;
	private Combo examCombo;
	private Combo exerciseCombo;
	private Combo courseCombo;

	private ControlDecoration controlDecorationSubmitted;
	private ControlDecoration controlDecorationClean;

	private Composite examContainerComposite;
	private Composite examContentComposite;
	private Composite resultContentComposite;

	private Label resultScore;
	private Label lblExamDescription;
	private Label lblExamShortName;
	private Button btnStart;

	private IExam exam;
	private Composite composite_1;

	public ArtemisStudentView() {
		this.viewController = new StudentViewController();
		ResultTab resultTab = new ResultTab(viewController);
		tabs.add(resultTab);
		viewController.connectToWebsocket(resultTab);
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
		createMainTab(tabFolder);
		createAllTabs(tabFolder);
	}

	@Override
	public void setFocus() {
		// NOP
	}

	public void createMainTab(TabFolder tabFolder) {
		TabItem tbtmAssessment = new TabItem(tabFolder, SWT.NONE);
		tbtmAssessment.setText("Exercise");

		this.scrolledCompositeGrading = new ScrolledComposite(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmAssessment.setControl(this.scrolledCompositeGrading);
		scrolledCompositeGrading.setLayout(new GridLayout(1, true));
		this.scrolledCompositeGrading.setExpandHorizontal(true);
		this.scrolledCompositeGrading.setExpandVertical(true);

		this.gradingComposite = new Composite(this.scrolledCompositeGrading, SWT.NONE);
		this.scrolledCompositeGrading.setContent(this.gradingComposite);
		this.scrolledCompositeGrading.setMinSize(new Point(100, 100));
		gradingComposite.setLayout(new GridLayout(1, true));
		gradingComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

		Composite composite_2 = new Composite(gradingComposite, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite_2.setLayout(new GridLayout(1, true));

		Label lblExercise_1 = new Label(composite_2, SWT.NONE);
		lblExercise_1.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		lblExercise_1.setText(Messages.ARTEMISSTUDENTVIEW_LABEL);
		FontDescriptor boldDescriptor = FontDescriptor.createFrom(lblExercise_1.getFont()).setStyle(SWT.BOLD).setHeight(18);
		Font boldFont = boldDescriptor.createFont(lblExercise_1.getDisplay());
		lblExercise_1.setFont(boldFont);

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
		btnRefreshArtemisState.setText("Refresh Artemis State");
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
		labelReset.setText("Reset to remote state");
		boldDescriptor = FontDescriptor.createFrom(labelReset.getFont()).setStyle(SWT.BOLD).setHeight(9);
		boldFont = boldDescriptor.createFont(labelReset.getDisplay());
		labelReset.setFont(boldFont);

		Composite composite_1 = new Composite(submitArea, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite_1.setLayout(new GridLayout(1, false));

		btnSubmitExcerise = new Button(composite_1, SWT.NONE);
		btnSubmitExcerise.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnSubmitExcerise.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnSubmitExcerise.setText(NO_SELECTED);
		btnSubmitExcerise.setEnabled(false);

		this.addSelectionListenerForSubmitButton(btnSubmitExcerise);
		controlDecorationSubmitted = new ControlDecoration(btnSubmitExcerise, SWT.RIGHT | SWT.CENTER);
		controlDecorationSubmitted.setImage(image);
		controlDecorationSubmitted.setDescriptionText("The exercise is expired and can therefore not be submitted!");
		controlDecorationSubmitted.hide();
		controlDecorationSubmitted.setImage(image);

		Composite composite_3 = new Composite(submitArea, SWT.NONE);
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		composite_3.setLayout(new GridLayout(1, false));

		btnClean = new Button(composite_3, SWT.NONE);
		btnClean.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnClean.setText(NO_SELECTED);
		btnClean.addListener(SWT.Selection, e -> {
			cleanWorkspaceForSelectedExercise();
		});
		btnClean.setEnabled(false);

		controlDecorationClean = new ControlDecoration(btnClean, SWT.RIGHT | SWT.CENTER);
		controlDecorationClean.setMarginWidth(5);
		controlDecorationClean.setDescriptionText("The exercise can not be cleaned!");
		controlDecorationClean.hide();

		Composite composite_1_1 = new Composite(submitArea, SWT.NONE);
		composite_1_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite_1_1.setLayout(new GridLayout(1, false));

		btnReset = new Button(composite_1_1, SWT.NONE);
		btnReset.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		btnReset.setText("*NOTHING SELECTED*");
		btnReset.setEnabled(false);
		btnReset.addListener(SWT.Selection, e -> {
			resetWorkspaceForSelectedExercise();
		});

		createExamPart(gradingComposite);

		scrolledCompositeGrading.setContent(gradingComposite);
		scrolledCompositeGrading.setMinSize(gradingComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private void createExamPart(Composite tabFolder) {
		this.examContainerComposite = new Composite(tabFolder, SWT.NONE);
		examContainerComposite.setSize(tabFolder.getSize());
		examContainerComposite.setLayout(new GridLayout(1, true));
		examContainerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		examContainerComposite.setVisible(false);

		Composite composite = new Composite(examContainerComposite, SWT.NONE);
		GridLayout gl_composite = new GridLayout(2, true);
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		Label labelFeedback = new Label(composite, SWT.NONE);
		labelFeedback.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));
		FontDescriptor boldDescriptor = FontDescriptor.createFrom(labelFeedback.getFont()).setStyle(SWT.BOLD).setHeight(18);
		Font boldFont = boldDescriptor.createFont(labelFeedback.getDisplay());
		labelFeedback.setFont(boldFont);
		labelFeedback.setText("Exam");

		btnStart = new Button(composite, SWT.CENTER);
		GridData gd_btnStart = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnStart.widthHint = 59;
		btnStart.setLayoutData(gd_btnStart);
		btnStart.setText("Start");
		addSelectionListenerForStartButton(btnStart);

		composite_1 = new Composite(examContainerComposite, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		composite_1.setLayout(new GridLayout(1, false));

		Label labelResult = new Label(composite_1, SWT.NONE);
		labelResult.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		labelResult.setText(Messages.EXAMTAB_REMEMBER);

		Link examLink = new Link(composite_1, SWT.NONE);
		examLink.setText(Messages.ARTEMISSTUDENTVIEW_LINK);
		examLink.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Program.launch(getLink());
			}
		});

		this.examContentComposite = new Composite(examContainerComposite, SWT.NONE);
		examContentComposite.setTouchEnabled(true);
		examContentComposite.setLayout(new GridLayout(1, true));
		examContentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		examContentComposite.setVisible(true);

		resultContentComposite = new Composite(examContentComposite, SWT.BORDER);
		resultContentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		resultContentComposite.setLayout(new GridLayout(1, false));

		lblExamShortName = new Label(resultContentComposite, SWT.NONE);
		lblExamShortName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		lblExamShortName.setTouchEnabled(true);
		boldDescriptor = FontDescriptor.createFrom(lblExamShortName.getFont()).setStyle(SWT.BOLD).setHeight(12);
		boldFont = boldDescriptor.createFont(lblExamShortName.getDisplay());
		lblExamShortName.setFont(boldFont);

		lblExamDescription = new Label(resultContentComposite, SWT.NONE);
		lblExamDescription.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 1));

		Label separator = new Label(resultContentComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

		resultScore = new Label(resultContentComposite, SWT.RIGHT);
		resultScore.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		boldDescriptor = FontDescriptor.createFrom(resultScore.getFont()).setStyle(SWT.BOLD | SWT.ITALIC).setHeight(12);
		boldFont = boldDescriptor.createFont(resultScore.getDisplay());
		resultScore.setFont(boldFont);
	}

	private void addSelectionListenerForStartButton(Button btn) {
		btn.addListener(SWT.Selection, e -> {
			exam = viewController.startExam();
			setExamDataToUI();
		});
	}

	private void setExamDataToUI() {
		if (exam != null) {
			lblExamShortName.setText(exam.getTitle());
			resultScore.setText(Messages.EXAMTAB_END + exam.getEndDate());
			lblExamDescription.setText(Messages.EXAMTAB_START + exam.getStartDate());
			btnStart.setEnabled(!exam.isStarted());
			examContainerComposite.setVisible(true);
		}
	}

	private void setExam() {
		exam = viewController.getCurrentlySelectedExam();
		setExamDataToUI();
	}

	private String getLink() {
		return this.viewController.getExamUrlForCurrentExam();
	}

	private void addSelectionListenerForSubmitButton(Button btnSubmit) {
		btnSubmit.addListener(SWT.Selection, e -> {
			this.viewController.onSubmitSolution();
		});
	}

	private void resetWorkspaceForSelectedExercise() {
		viewController.resetSelectedExercise();
	}

	private void createExamComboList(Combo courseCombo, Combo examCombo, Combo exerciseCombo) {
		examCombo.removeAll();
		exerciseCombo.removeAll();
		this.viewController.getExerciseShortNames(courseCombo.getItem(courseCombo.getSelectionIndex())).forEach(exerciseCombo::add);
		examCombo.add("None");
		this.viewController.getExamShortNames(courseCombo.getItem(courseCombo.getSelectionIndex())).forEach(examCombo::add);
		examCombo.addListener(SWT.Selection, e -> {
			resetBackendStateForNewExam();
			String examName = examCombo.getItem(examCombo.getSelectionIndex());
			if ("None".equals(examName)) {
				this.viewController.setExamToNull();
				this.viewController.getExerciseShortNames(courseCombo.getItem(courseCombo.getSelectionIndex())).forEach(exerciseCombo::add);
				resetExamPart();
			} else {
				this.viewController.getExercisesShortNamesForExam(examCombo.getItem(examCombo.getSelectionIndex())).forEach(exerciseCombo::add);
				handleExamComboEvent();
			}
		});
		exerciseCombo.addListener(SWT.Selection, e -> {
			handleExerciseComboListEvent(exerciseCombo);
		});
	}
	
	private void resetBackendStateForNewExam() {
		exerciseCombo.removeAll();
		this.viewController.resetBackendState();
		this.resetButtons();
		this.resetAllTabs();
	}

	private void resetExamPart() {
		examContainerComposite.setVisible(false);
	}

	private void updateButtons(String exerciseName) {
		setButtonText(exerciseName);
		enableButtons();
	}

	private void handleExerciseComboListEvent(Combo exerciseCombo) {
		String exerciseShortName = exerciseCombo.getItem(exerciseCombo.getSelectionIndex());
		this.viewController.setExerciseID(exerciseShortName);
		callAllTabsForExerciseEvent();
		updateButtons(exerciseShortName);
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
		btn.addListener(SWT.Selection, e -> handleStartButtonEvent());
	}
	
	private void handleStartButtonEvent() {
		this.viewController.startExercise();
		enableButtons();
	}

	private void loadExamComboEntries(Combo examCourseCombo, Combo examCombo, Combo examExerciseCombo) {
		this.viewController.getCourseShortNames().forEach(examCourseCombo::add);
		examCourseCombo.addListener(SWT.Selection, e -> {
			this.createExamComboList(this.courseCombo, examCombo, examExerciseCombo);
		});
	}

	private void refreshArtemisState() {
		this.viewController = new StudentViewController();
		viewController.connectToWebsocket((ResultTab) tabs.get(0));
		setViewControllerForAllTabs();
		this.resetCombos();
		this.resetButtons();
		this.resetAllTabs();
	}

	private void handleExamComboEvent() {
		setExam();
		callAllTabsForExamEvent();
	}

	private void resetCombos() {
		this.courseCombo.removeAll();
		this.examCombo.removeAll();
		this.exerciseCombo.removeAll();
		this.viewController.getCourseShortNames().forEach(courseShortName -> this.courseCombo.add(courseShortName));
	}

	private void resetButtons() {
		resetButtonText();
		resetButtonEnable();
		resetExamPart();
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
		this.tabs.forEach(tab -> tab.setViewController(viewController));
	}
}
