package edu.kit.kastel.eclipse.student.view.assessment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import edu.kit.kastel.eclipse.student.view.controllers.StudentViewController;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ResultsDTO;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;

/**
 * This class creates the view elements for the artemis grading process. It is
 * build as a tab folder with four tabs: grading, assessment, exam and backlog.
 *
 * @see {@link ViewPart}
 *
 */
public class ArtemisStudentView extends ViewPart {
	private static final String NO_SELECTED = "*NOTHING SELECTED*";
	
	private Display display;

	private StudentViewController viewController;
	private ScrolledComposite scrolledCompositeGrading;
	private ScrolledComposite scrolledCompositeFeedback;
	private Composite gradingComposite;
	private Composite feedbackContainerComposite;
	private Composite feedbackContentComposite;
	private Combo examCombo;
	private Combo exerciseCombo;
	private Combo courseCombo;
	private Button btnSubmitExcerise;
	private Button btnClean;
	private ResultsDTO lastResult;
	private List<Feedback> feedbackOfLastResult = new ArrayList<>();
	private Table feedbackTabel;

	private Label resultScore;
	private Label btnResultSuccessfull;
	private Label lblResultExerciseDescription;
	private Label lblResultExerciseShortName;

	public ArtemisStudentView() {
		this.viewController = new StudentViewController();
	}

	private void addSelectionListenerForSubmitButton(Button btnSubmit) {
		btnSubmit.addListener(SWT.Selection, e -> {
			this.viewController.onSubmitSolution();
		});
	}
	
	private void addSelectionListenerForReloadButton(Button btn) {
		btn.addListener(SWT.Selection, e -> {
			getFeedbackForExcerise();
		});
	}

	private void createResultTab(TabFolder tabFolder) {
		TabItem tbtmAssessment = new TabItem(tabFolder, SWT.NONE);
		tbtmAssessment.setText("Test Results");

		this.scrolledCompositeFeedback = new ScrolledComposite(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmAssessment.setControl(this.scrolledCompositeFeedback);
		scrolledCompositeFeedback.setLayout(new FillLayout());
		this.scrolledCompositeFeedback.setExpandHorizontal(true);
		this.scrolledCompositeFeedback.setExpandVertical(true);

		this.feedbackContainerComposite = new Composite(this.scrolledCompositeFeedback, SWT.NONE);
		this.scrolledCompositeFeedback.setContent(this.feedbackContainerComposite);
		feedbackContainerComposite.setSize(scrolledCompositeFeedback.getSize());
		this.scrolledCompositeFeedback
				.setMinSize(this.feedbackContainerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		feedbackContainerComposite.setLayout(new GridLayout(1, true));
		feedbackContainerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Composite composite = new Composite(feedbackContainerComposite, SWT.NONE);
		composite.setLayout(new GridLayout(13, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		
				Label labelFeedback = new Label(composite, SWT.NONE);
				labelFeedback.setFont(SWTResourceManager.getFont("Segoe UI", 18, SWT.BOLD));
				labelFeedback.setText("Feedback");
				new Label(composite, SWT.NONE);
				new Label(composite, SWT.NONE);
				new Label(composite, SWT.NONE);
				new Label(composite, SWT.NONE);
				new Label(composite, SWT.NONE);
				new Label(composite, SWT.NONE);
				new Label(composite, SWT.NONE);
				new Label(composite, SWT.NONE);
				new Label(composite, SWT.NONE);
				new Label(composite, SWT.NONE);
				new Label(composite, SWT.NONE);
				
				Button btnNewButton = new Button(composite, SWT.NONE);
				btnNewButton.setText("Reload");
				addSelectionListenerForReloadButton(btnNewButton);

		Label labelResult = new Label(feedbackContainerComposite, SWT.NONE);
		labelResult.setText("Summary of all executed tests for the selected exercise");

		this.feedbackContentComposite = new Composite(feedbackContainerComposite, SWT.NONE);
		feedbackContentComposite.setTouchEnabled(true);
		feedbackContentComposite.setLayout(new GridLayout(1, true));
		GridData gd_feedbackContentComposite = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_feedbackContentComposite.widthHint = 515;
		feedbackContentComposite.setLayoutData(gd_feedbackContentComposite);
		feedbackContentComposite.setVisible(false);
		Composite resultContentComposite = new Composite(feedbackContentComposite, SWT.BORDER);
		GridData gd_resultContentComposite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_resultContentComposite.heightHint = 108;
		gd_resultContentComposite.widthHint = 518;
		resultContentComposite.setLayoutData(gd_resultContentComposite);

		lblResultExerciseShortName = new Label(resultContentComposite, SWT.NONE);
		lblResultExerciseShortName.setText("Name");
		lblResultExerciseShortName.setTouchEnabled(true);
		lblResultExerciseShortName.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		lblResultExerciseShortName.setBounds(22, 9, 105, 28);

		lblResultExerciseDescription = new Label(resultContentComposite, SWT.NONE);
		lblResultExerciseDescription.setBounds(22, 35, 461, 21);

		btnResultSuccessfull = new Label(resultContentComposite, SWT.RIGHT);
		btnResultSuccessfull.setForeground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
		btnResultSuccessfull.setBounds(360, 9, 123, 28);
		btnResultSuccessfull.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		btnResultSuccessfull.setText("Successfull");

		resultScore = new Label(resultContentComposite, SWT.RIGHT);
		resultScore.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD | SWT.ITALIC));
		resultScore.setBounds(36, 78, 447, 30);
		resultScore.setText("0 / 20");

		Label separator = new Label(resultContentComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setBounds(20, 62, 476, 10);

		Label labelFeedback2 = new Label(feedbackContentComposite, SWT.NONE);
		labelFeedback2.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		labelFeedback2.setText("Summary of all executed tests");
		createTableForFeedback(feedbackContentComposite);

		scrolledCompositeFeedback.setContent(feedbackContainerComposite);
		scrolledCompositeFeedback.setMinSize(feedbackContainerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
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
			if ("None".equals(examCombo.getItem(examCombo.getSelectionIndex()))) {
				this.viewController.getExerciseShortNames(courseCombo.getItem(courseCombo.getSelectionIndex()))
						.forEach(exerciseCombo::add);
			} else {
				this.viewController.getExercisesShortNamesForExam(examCombo.getItem(examCombo.getSelectionIndex()))
						.forEach(exerciseCombo::add);
			}
		});
		exerciseCombo.addListener(SWT.Selection, e -> {
			handleExerciseComboListEvent(exerciseCombo);
		});
	}

	private void createExerciseTab(TabFolder tabFolder) {
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

		/*
		 * o9 The exam combo does not really have an influence on the backend state, but
		 * should be disabled after a new assessment is started
		 */

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

		ControlDecoration controlDecoration = new ControlDecoration(btnSubmitExcerise, SWT.RIGHT | SWT.CENTER);
		controlDecoration.setMarginWidth(5);
		controlDecoration.setDescriptionText("Some description");

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

		ControlDecoration controlDecoration_1 = new ControlDecoration(btnClean, SWT.RIGHT | SWT.CENTER);
		controlDecoration_1.setMarginWidth(5);
		controlDecoration_1.setDescriptionText("Some description");

		scrolledCompositeGrading.setContent(gradingComposite);
		scrolledCompositeGrading.setMinSize(gradingComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}

	private void handleExerciseComboListEvent(Combo exerciseCombo) {
		String exerciseShortName = exerciseCombo.getItem(exerciseCombo.getSelectionIndex());
		this.viewController.setExerciseID(exerciseShortName);
		getFeedbackForExcerise();
		updateButtons(exerciseShortName);
	}

	private void createStudentTab(TabFolder tabFolder) {
		TabItem gradingTabItem = new TabItem(tabFolder, SWT.NONE);
		gradingTabItem.setText("Exam");

	}

	private void updateButtons(String exerciseName) {
		setButtonText(exerciseName);
		enableButtons();
	}

	private void setButtonText(String exerciseName) {
		this.btnSubmitExcerise.setText("Submit: " + exerciseName);
		this.btnClean.setText("Clean: " + exerciseName);
	}

	private void enableButtons() {
		this.btnSubmitExcerise.setEnabled(this.viewController.canSubmit());
		btnClean.setEnabled(this.viewController.canClean());
	}

	/**
	 * This methods creates the whole view components.
	 */
	@Override
	public void createPartControl(Composite parent) {
		this.createView(parent);
		display = parent.getDisplay();
	}

	private void createView(Composite parent) {
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
		this.createExerciseTab(tabFolder);
		this.createResultTab(tabFolder);
		this.createStudentTab(tabFolder);
	}

	private void loadExamComboEntries(Combo examCourseCombo, Combo examCombo, Combo examExerciseCombo) {
		this.viewController.getCourseShortNames().forEach(examCourseCombo::add);
		examCourseCombo.addListener(SWT.Selection, e -> {
			this.createExamComboList(this.courseCombo, examCombo, examExerciseCombo);
		});

	}

	@Override
	public void setFocus() {
		// NOP
	}

	private void getFeedbackForExcerise() {
		var resultFeedbackMap = this.viewController.getFeedbackExcerise();

		IExercise exercise = this.viewController.getCurrentSelectedExercise();
		if (!resultFeedbackMap.isEmpty()) {
			Entry<ResultsDTO, List<Feedback>> entry = resultFeedbackMap.entrySet().iterator().next();
			this.lastResult = entry.getKey();
			this.feedbackOfLastResult = entry.getValue();
			feedbackTabel.removeAll();
			addFeedbackToTable(feedbackTabel, feedbackOfLastResult);
			addResultToTab(lastResult, exercise);
			feedbackContainerComposite.pack();
			feedbackContentComposite.setVisible(true);
		} else {
			feedbackTabel.removeAll();
			resetFeedback();
		}
	}

	private void createTableForFeedback(Composite parent) {
		feedbackTabel = new Table(parent, SWT.BORDER | SWT.V_SCROLL);
		feedbackTabel.setToolTipText("Feedbacks for Excerise");
		feedbackTabel.setLinesVisible(true);
		feedbackTabel.setHeaderVisible(true);
		feedbackTabel.setLayout(new GridLayout(1, true));
		GridData gd_feedbackTabel = new GridData(SWT.LEFT, SWT.FILL, true, true);
		gd_feedbackTabel.widthHint = 500;
		feedbackTabel.setLayoutData(gd_feedbackTabel);
		final Rectangle clientArea = parent.getClientArea();
		feedbackTabel.setBounds(clientArea.x, clientArea.y, 500, 500);
		String[] colNames = { "Name", "Credits", "Success", "Type" };

		for (int loopIndex = 0; loopIndex < colNames.length; loopIndex++) {
			final TableColumn column = new TableColumn(feedbackTabel, SWT.NULL);
			column.setText(colNames[loopIndex]);
			column.setWidth(110);
		}

		feedbackTabel.addListener(SWT.Selection, e -> {
			handleResultTableEvent(e);
		});
	}

	private void handleResultTableEvent(Event e) {
		TableItem item = (TableItem) e.item;
		int index = feedbackTabel.indexOf(item);
		Feedback selectedFeedback = feedbackOfLastResult.get(index);
		if(selectedFeedback == null) {
			return;
		}
		Shell s = new Shell(display);
		s.setMinimumSize(500, 500);
		String detailedText = (selectedFeedback.getDetailText() != null) ? selectedFeedback.getDetailText() : "No detailed text found!";
		MessageDialog.openInformation(s, selectedFeedback.getText(), detailedText);
	}

	private void addResultToTab(ResultsDTO result, IExercise exercise) {
		Display display = Display.getDefault();
		if (result != null) {
			this.btnResultSuccessfull.setForeground((result.successful ? display.getSystemColor(SWT.COLOR_GREEN)
					: display.getSystemColor(SWT.COLOR_RED)));
			btnResultSuccessfull.setText(result.successful ? "success" : "failed");

			if (exercise != null) {
				lblResultExerciseShortName.setText(exercise.getTitle());
				lblResultExerciseDescription.setText(result.completionDate);
				resultScore.setText(result.resultString);
			} else {
				resultScore.setText(Integer.toString(result.score));
			}
		}
	}

	private void addFeedbackToTable(Table table, List<Feedback> entries) {
		Display display = Display.getDefault();
		if (entries != null) {
			for (var feedback : entries) {
				final TableItem item = new TableItem(table, SWT.NULL);
				item.setText(0, feedback.getText());
				item.setText(1, "" + feedback.getCredits());
				item.setText(2, feedback.getPositive() ? "successful" : "failed");
				item.setForeground(2, feedback.getPositive() ? display.getSystemColor(SWT.COLOR_GREEN)
						: display.getSystemColor(SWT.COLOR_RED));
				item.setText(3, feedback.getType().toString());
			}
		}

	}

	private void refreshArtemisState() {
		this.viewController = new StudentViewController();
		this.resetCombos();
	}

	private void resetCombos() {
		this.courseCombo.removeAll();
		this.examCombo.removeAll();
		this.exerciseCombo.removeAll();
		resetButtonText();
		resetButtonEnable();
		resetFeedback();
		this.viewController.fetchCourses();
		this.viewController.getCourseShortNames().forEach(courseShortName -> this.courseCombo.add(courseShortName));
	}

	private void resetFeedback() {
		feedbackContentComposite.setVisible(false);
	}

	private void resetButtonText() {
		this.btnSubmitExcerise.setText(NO_SELECTED);
		this.btnClean.setText(NO_SELECTED);
	}

	private void resetButtonEnable() {
		this.btnSubmitExcerise.setEnabled(false);
		this.btnClean.setEnabled(false);
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
}
