package edu.kit.kastel.eclipse.student.view.ui;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;

import edu.kit.kastel.eclipse.student.view.controllers.StudentViewController;

import java.util.ArrayList;
import java.util.List;

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
	private Combo examCombo;
	private Combo exerciseCombo;
	private Combo courseCombo;
	
	private ControlDecoration controlDecorationSubmitted;
	private ControlDecoration controlDecorationClean;

	public ArtemisStudentView() {
		this.viewController = new StudentViewController();
		ResultTab resultTab = new ResultTab(viewController);
		tabs.add(resultTab);
		tabs.add(new ExamTab(viewController));
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
		this.scrolledCompositeGrading.setMinSize(this.gradingComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		gradingComposite.setLayout(new GridLayout(1, true));

		Composite assessmentComposite = new Composite(gradingComposite, SWT.BORDER);
		GridData gd_assessmentComposite = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_assessmentComposite.widthHint = 326;
		assessmentComposite.setLayoutData(gd_assessmentComposite);
		assessmentComposite.setLayout(new GridLayout(2, false));

		Label lblCourse = new Label(assessmentComposite, SWT.NONE);
		lblCourse.setAlignment(SWT.CENTER);
		lblCourse.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		GridData gd_lblCourse = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_lblCourse.widthHint = 164;
		lblCourse.setLayoutData(gd_lblCourse);
		lblCourse.setText("Course");

		this.courseCombo = new Combo(assessmentComposite, SWT.READ_ONLY);
		GridData gd_courseCombo = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_courseCombo.widthHint = 188;
		this.courseCombo.setLayoutData(gd_courseCombo);

		Label lblExam = new Label(assessmentComposite, SWT.NONE);
		lblExam.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblExam.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblExam.setText("Exam");

		this.examCombo = new Combo(assessmentComposite, SWT.READ_ONLY);
		this.examCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblExercise = new Label(assessmentComposite, SWT.NONE);
		lblExercise.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblExercise.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblExercise.setText("Exercise");

		this.exerciseCombo = new Combo(assessmentComposite, SWT.READ_ONLY);
		this.exerciseCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		this.loadExamComboEntries(this.courseCombo, this.examCombo, this.exerciseCombo);

		Composite buttons = new Composite(assessmentComposite, SWT.NONE);
		buttons.setLayout(new GridLayout(2, false));
		buttons.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true, 2, 1));

		Button btnRefreshArtemisState = new Button(buttons, SWT.NONE);
		btnRefreshArtemisState.setText("Refresh Artemis State");
		GridData gd_btnRefreshArtemisState = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_btnRefreshArtemisState.widthHint = 157;
		btnRefreshArtemisState.setLayoutData(gd_btnRefreshArtemisState);

		this.addSelectionListenerForRefreshArtemisStateButton(btnRefreshArtemisState);

		Button btnLoadExercise = new Button(buttons, SWT.NONE);
		btnLoadExercise.setText("Start Exercise");
		GridData gd_btnLoadExercise = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_btnLoadExercise.widthHint = 152;
		btnLoadExercise.setLayoutData(gd_btnLoadExercise);

		this.addLoadExerciseListenerForButton(btnLoadExercise);

		// Submit

		Composite submitArea = new Composite(gradingComposite, SWT.BORDER);
		GridData gd_submitArea = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_submitArea.widthHint = 403;
		submitArea.setLayoutData(gd_submitArea);
		submitArea.setLayout(new GridLayout(2, true));

		Label label1 = new Label(submitArea, SWT.NONE);
		label1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		label1.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		label1.setText("Submit your solution");

		Label labelClean = new Label(submitArea, SWT.NONE);
		labelClean.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		labelClean.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		labelClean.setText("Clean your last changes");

		Composite composite = new Composite(submitArea, SWT.NONE);
		GridData gd_composite = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		gd_composite.widthHint = 191;
		composite.setLayoutData(gd_composite);
		composite.setLayout(new GridLayout(1, false));

		btnSubmitExcerise = new Button(composite, SWT.NONE);
		GridData gd_btnSubmitExcerise = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnSubmitExcerise.widthHint = 168;
		btnSubmitExcerise.setLayoutData(gd_btnSubmitExcerise);
		btnSubmitExcerise.setText(NO_SELECTED);
		btnSubmitExcerise.setEnabled(false);

		this.addSelectionListenerForSubmitButton(btnSubmitExcerise);

		Image image = FieldDecorationRegistry.getDefault()
                .getFieldDecoration(FieldDecorationRegistry.DEC_WARNING).getImage();
		controlDecorationSubmitted = new ControlDecoration(btnSubmitExcerise, SWT.RIGHT | SWT.CENTER);
		controlDecorationSubmitted.setImage(image);
		controlDecorationSubmitted.setMarginWidth(5);
		controlDecorationSubmitted.setDescriptionText("The exercise is expired and can therefore not be submitted!");
		controlDecorationSubmitted.hide();
		
		Composite composite_1 = new Composite(submitArea, SWT.NONE);
		GridData gd_composite_1 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_composite_1.widthHint = 191;
		composite_1.setLayoutData(gd_composite_1);
		composite_1.setLayout(new GridLayout(1, false));

		btnClean = new Button(composite_1, SWT.NONE);
		GridData gd_btnClean = new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1);
		gd_btnClean.widthHint = 168;
		btnClean.setLayoutData(gd_btnClean);
		btnClean.setText(NO_SELECTED);
		btnClean.addListener(SWT.Selection, e -> {
			cleanWorkspaceForSelectedExercise();
		});
		btnClean.setEnabled(false);

		controlDecorationClean = new ControlDecoration(btnClean, SWT.RIGHT | SWT.CENTER);
		controlDecorationClean.setMarginWidth(5);
		controlDecorationSubmitted.setImage(image);
		controlDecorationClean.setDescriptionText("The exercise can not be cleaned!");
		controlDecorationClean.hide();

		scrolledCompositeGrading.setContent(gradingComposite);
		scrolledCompositeGrading.setMinSize(gradingComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	private void addSelectionListenerForSubmitButton(Button btnSubmit) {
		btnSubmit.addListener(SWT.Selection, e -> {
			this.viewController.onSubmitSolution();
		});
	}


	private void createExamComboList(Combo courseCombo, Combo examCombo, Combo exerciseCombo) {
		examCombo.removeAll();
		exerciseCombo.removeAll();
		this.viewController.getExerciseShortNames(courseCombo.getItem(courseCombo.getSelectionIndex()))
				.forEach(exerciseCombo::add);
		examCombo.add("None");
		this.viewController.getExamShortNames(courseCombo.getItem(courseCombo.getSelectionIndex()))
				.forEach(examCombo::add);
		examCombo.addListener(SWT.Selection, e -> {
			exerciseCombo.removeAll();
			String examName = examCombo.getItem(examCombo.getSelectionIndex());
			if ("None".equals(examName)) {
				this.viewController.setExamToNull();
				this.viewController.getExerciseShortNames(courseCombo.getItem(courseCombo.getSelectionIndex()))
						.forEach(exerciseCombo::add);
			} else {
				this.viewController.getExercisesShortNamesForExam(examCombo.getItem(examCombo.getSelectionIndex()))
						.forEach(exerciseCombo::add);
			}
			callAllTabsForExamEvent();
		});
		exerciseCombo.addListener(SWT.Selection, e -> {
			handleExerciseComboListEvent(exerciseCombo);
		});
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
	}

	private void enableButtons() {
		boolean canSubmit = this.viewController.canSubmit();
		boolean canClean = this.viewController.canClean();
		this.btnSubmitExcerise.setEnabled(canSubmit);
		btnClean.setEnabled(canClean);
		if(!canSubmit) {
			this.controlDecorationSubmitted.show();
		} else {
			this.controlDecorationSubmitted.hide();
		}
		if(!canClean) {
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
		btn.addListener(SWT.Selection, e -> this.viewController.startExercise());
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
		this.resetCombos();
		this.resetButtons();
		this.resetAllTabs();
	}

	private void resetCombos() {
		this.courseCombo.removeAll();
		this.examCombo.removeAll();
		this.exerciseCombo.removeAll();
		this.viewController.fetchCourses();
		this.viewController.getCourseShortNames().forEach(courseShortName -> this.courseCombo.add(courseShortName));
	}
	private void resetButtons() {
		resetButtonText();
		resetButtonEnable();
	}
	
	private void resetButtonText() {
		this.btnSubmitExcerise.setText(NO_SELECTED);
		this.btnClean.setText(NO_SELECTED);
	}
	private void resetButtonEnable() {
		this.btnSubmitExcerise.setEnabled(false);
		this.btnClean.setEnabled(false);
	}
	private void resetAllTabs() {
		this.tabs.forEach(ArtemisStudentTab::reset);
	}
	
	private void createAllTabs(TabFolder folder) {
		this.tabs.forEach(t ->t.create(folder));
	}
	
	private void callAllTabsForExerciseEvent() {
		this.tabs.forEach(ArtemisStudentTab::callExercisesEvent);
	}
	
	private void callAllTabsForExamEvent() {
		this.tabs.forEach(ArtemisStudentTab::callExamEvent);
	}
}
