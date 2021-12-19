package edu.kit.kastel.eclipse.student.view.assessment;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import edu.kit.kastel.eclipse.student.view.controllers.AssessmentViewController;
import edu.kit.kastel.eclipse.student.view.controllers.StudentViewController;
import edu.kit.kastel.eclipse.student.view.utilities.AssessmentUtilities;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ResultsDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.SubmissionFilter;
import edu.kit.kastel.sdq.eclipse.grading.api.backendstate.Transition;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IRatingGroup;

/**
 * This class creates the view elements for the artemis grading process. It is
 * build as a tab folder with four tabs: grading, assessment, exam and backlog.
 *
 * @see {@link ViewPart}
 *
 */
public class ArtemisStudentView extends ViewPart {
	private final String NO_SELECTED = "*NOTHING SELECTED*";

	private StudentViewController viewController;
	private Map<String, Group> ratingGroupViewElements;
	private Map<String, Button> mistakeButtons;
	private ScrolledComposite scrolledCompositeGrading;
	private ScrolledComposite scrolledCompositeFeedback;
	private Composite gradingComposite;
	private Composite feedbackComposite;
	private Map<Transition, Set<Control>> possibleActions;
	private Combo examCombo;
	private Combo exerciseCombo;
	private Combo courseCombo;
	private Button btnSubmitExcerise;
	private Button btnClean;
	private Button btnFeedback;
	private Map<ResultsDTO, List<Feedback>> resultFeedbackMap = new HashMap<>();
	private Table feedbackTabel;
	private Table resultTabel;

	public ArtemisStudentView() {
		this.viewController = new StudentViewController();
		this.ratingGroupViewElements = new HashMap<>();
		this.mistakeButtons = new HashMap<>();
		this.possibleActions = new EnumMap<>(Transition.class);
		this.initializePossibleActions();
		this.addListenerForMarkerDeletion();
	}

	private void initializePossibleActions() {
		for (int i = 0; i < Transition.values().length; i++) {
			Transition current = Transition.values()[i];
			this.possibleActions.put(current, new HashSet<>());
		}
	}

	private void addListenerForMarkerDeletion() {
		AssessmentUtilities.getWorkspace().addResourceChangeListener(event -> Arrays
				.asList(event.findMarkerDeltas(AssessmentUtilities.MARKER_NAME, true)).forEach(marker -> {
					// check if marker is deleted
					if (marker.getKind() == 2) {
						this.viewController.deleteAnnotation((String) marker.getAttribute("annotationID"));
						this.updatePenalties();
					}
				}));
	}

	private void addSelectionListenerForSubmitButton(Button btnSubmit) {
		btnSubmit.addListener(SWT.Selection, e -> {
			this.viewController.onSubmitSolution();
			this.updateState();
		});
	}

	private void createBacklogTab(TabFolder tabFolder) {
		TabItem tbtmAssessment = new TabItem(tabFolder, SWT.NONE);
		tbtmAssessment.setText("Test Results");

		this.scrolledCompositeFeedback = new ScrolledComposite(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmAssessment.setControl(this.scrolledCompositeFeedback);
		scrolledCompositeFeedback.setLayout(new FillLayout());
		this.scrolledCompositeFeedback.setExpandHorizontal(true);
		this.scrolledCompositeFeedback.setExpandVertical(true);

		this.feedbackComposite = new Composite(this.scrolledCompositeFeedback, SWT.NONE);
		this.scrolledCompositeFeedback.setContent(this.feedbackComposite);
		feedbackComposite.setSize(scrolledCompositeFeedback.getSize());
		this.scrolledCompositeFeedback.setMinSize(this.feedbackComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		feedbackComposite.setLayout(new GridLayout(1, true));
		feedbackComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label labelFeedback = new Label(feedbackComposite, SWT.NONE);
		labelFeedback.setText("Get feedback for excercise");

		btnFeedback = new Button(feedbackComposite, SWT.NONE);
		btnFeedback.setText(NO_SELECTED);
		btnFeedback.setSize(80, 20);
		btnFeedback.setLayoutData(new GridData(SWT.NONE, SWT.CENTER, false, false, 1, 1));
		btnFeedback.addListener(SWT.Selection, e -> {
			getFeedbackForExcerise();
		});

		Label labelResult = new Label(feedbackComposite, SWT.NONE);
		labelResult.setText("Summary of all executed tests");
		
		createTabelForResult(feedbackComposite);
		
		Label labelFeedback2 = new Label(feedbackComposite, SWT.NONE);
		labelFeedback2.setText("Results of all tests");
		createTableForFeedback(feedbackComposite);
		
		scrolledCompositeFeedback.setContent(feedbackComposite);
		scrolledCompositeFeedback.setMinSize(feedbackComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}

	private void createCustomButton(IRatingGroup ratingGroup, Group rgDisplay, IMistakeType mistake) {
		final Button customButton = new Button(rgDisplay, SWT.PUSH);
		customButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		customButton.setText(mistake.getPenaltyName());
		customButton.addListener(SWT.Selection, event -> {
			final CustomButtonDialog customDialog = new CustomButtonDialog(AssessmentUtilities.getWindowsShell(),
					this.viewController, ratingGroup.getDisplayName(), mistake);
			customDialog.setBlockOnOpen(true);
			customDialog.open();
			// avoid SWT Exception
			Display.getDefault().asyncExec(() -> this.updatePenalty(ratingGroup.getDisplayName()));
		});
	}

	private void createExamComboList(Combo courseCombo, Combo examCombo, Combo examExerciseCombo) {
		examCombo.removeAll();
		examExerciseCombo.removeAll();
		this.viewController.getExerciseShortNames(courseCombo.getItem(courseCombo.getSelectionIndex()))
				.forEach(examExerciseCombo::add);
		examCombo.add("None");
		this.viewController.getExamShortNames(courseCombo.getItem(courseCombo.getSelectionIndex()))
				.forEach(examCombo::add);
		examCombo.addListener(SWT.Selection, e -> {
			examExerciseCombo.removeAll();
			if ("None".equals(examCombo.getItem(examCombo.getSelectionIndex()))) {
				this.viewController.getExerciseShortNames(courseCombo.getItem(courseCombo.getSelectionIndex()))
						.forEach(examExerciseCombo::add);
			} else {
				this.viewController.getExercisesShortNamesForExam(examCombo.getItem(examCombo.getSelectionIndex()))
						.forEach(examExerciseCombo::add);
			}
			this.updateState();
		});
		examExerciseCombo.addListener(SWT.Selection, e -> {
			this.viewController.setExerciseID(examExerciseCombo.getItem(examExerciseCombo.getSelectionIndex()));
			this.updateState();
		});
	}

	private void createAssessmentTab(TabFolder tabFolder) {
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

		Composite assessmentComposite = new Composite(gradingComposite, SWT.NONE);
		assessmentComposite.setLayout(new GridLayout(2, false));

		Label lblCourse = new Label(assessmentComposite, SWT.NONE);
		lblCourse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCourse.setText("Course");

		this.courseCombo = new Combo(assessmentComposite, SWT.READ_ONLY);
		this.courseCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		this.addControlToPossibleActions(this.courseCombo, Transition.SET_COURSE_ID_AND_GET_EXERCISE_SHORT_NAMES);

		Label lblExam = new Label(assessmentComposite, SWT.NONE);
		lblExam.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblExam.setText("Exam");

		this.examCombo = new Combo(assessmentComposite, SWT.READ_ONLY);
		this.examCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		/*
		 * o9 The exam combo does not really have an influence on the backend state, but
		 * should be disabled after a new assessment is started
		 */
		this.addControlToPossibleActions(this.examCombo, Transition.SET_COURSE_ID_AND_GET_EXERCISE_SHORT_NAMES);

		Label lblExercise = new Label(assessmentComposite, SWT.NONE);
		lblExercise.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblExercise.setText("Exercise");

		this.exerciseCombo = new Combo(assessmentComposite, SWT.READ_ONLY);
		this.exerciseCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.exerciseCombo.addListener(SWT.Selection, e -> {
			String exerciseName = ((Combo) e.widget).getText();
			this.btnSubmitExcerise.setText("Submit: " + exerciseName);
			this.btnClean.setText("Clean: " + exerciseName);
			this.btnFeedback.setText("Feedback: " + exerciseName);
		});

		this.loadExamComboEntries(this.courseCombo, this.examCombo, this.exerciseCombo);
		this.addControlToPossibleActions(this.exerciseCombo, Transition.SET_EXERCISE_ID);

		Composite buttons = new Composite(assessmentComposite, SWT.NONE);
		buttons.setLayout(new GridLayout(2, false));
		buttons.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true, 2, 1));

		Button btnRefreshArtemisState = new Button(buttons, SWT.NONE);
		btnRefreshArtemisState.setText("Refresh Artemis State");
		btnRefreshArtemisState.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		this.addSelectionListenerForRefreshArtemisStateButton(btnRefreshArtemisState);
		this.addControlToPossibleActions(btnRefreshArtemisState, Transition.ON_RESET);

		Button btnLoadExercise = new Button(buttons, SWT.NONE);
		btnLoadExercise.setText("Start Exercise");
		btnLoadExercise.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		this.addLoadExerciseListenerForButton(btnLoadExercise);
		this.addControlToPossibleActions(btnLoadExercise, Transition.ON_RESET);

		// Submit

		Composite submitArea = new Composite(gradingComposite, SWT.None);
		submitArea.setLayout(new GridLayout(1, true));

		Label label1 = new Label(submitArea, SWT.NONE);
		label1.setText("Submit your solution");

		btnSubmitExcerise = new Button(submitArea, SWT.NONE);
		btnSubmitExcerise.setText(NO_SELECTED);
		btnSubmitExcerise.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		this.addSelectionListenerForSubmitButton(btnSubmitExcerise);

		// MORE BUTTONS
		Composite cleanArea = new Composite(gradingComposite, SWT.None);
		cleanArea.setLayout(new GridLayout(1, true));

		Label labelClean = new Label(submitArea, SWT.NONE);
		labelClean.setText("Clean your last changes");

		btnClean = new Button(submitArea, SWT.NONE);
		btnClean.setText(NO_SELECTED);
		btnClean.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnClean.addListener(SWT.Selection, e -> {
			cleanWorkspaceForSelectedExercise();
		});

		scrolledCompositeGrading.setContent(gradingComposite);
		scrolledCompositeGrading.setMinSize(gradingComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private void getFeedbackForExcerise() {
		this.resultFeedbackMap = this.viewController.getFeedbackExcerise();
		if (!resultFeedbackMap.isEmpty()) {
			Entry<ResultsDTO, List<Feedback>> entry = resultFeedbackMap.entrySet().iterator().next();
			feedbackTabel.removeAll();
			resultTabel.removeAll();
			addResultToTable(resultTabel, entry);
			addFeedbackToTable(feedbackTabel, entry);
			feedbackComposite.pack();
		} else {
			feedbackTabel.removeAll();
			resultTabel.removeAll();
		}
	}

	private void createTabelForResult(Composite parent) {
		resultTabel = new Table(parent, SWT.BORDER | SWT.V_SCROLL);
		resultTabel.setToolTipText("Feedbacks for Excerise");
		resultTabel.setLinesVisible(true);
		resultTabel.setHeaderVisible(true);
		resultTabel.setLayout(new GridLayout(1, true));
		resultTabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		resultTabel.setVisible(false);
		final Rectangle clientArea = parent.getClientArea();
		resultTabel.setBounds(clientArea.x, clientArea.y, 400, 10);
		String[] colNames = { "Description", "Score", "Positiv", "Rated", "Date" };

		for (int loopIndex = 0; loopIndex < colNames.length; loopIndex++) {
			final TableColumn column = new TableColumn(resultTabel, SWT.NULL);
			column.setText(colNames[loopIndex]);
			column.setWidth(80);
		}
	}

	private void createTableForFeedback(Composite parent) {
		feedbackTabel = new Table(parent, SWT.BORDER | SWT.V_SCROLL);
		feedbackTabel.setToolTipText("Feedbacks for Excerise");
		feedbackTabel.setLinesVisible(true);
		feedbackTabel.setHeaderVisible(true);
		feedbackTabel.setLayout(new GridLayout(1, true));
		feedbackTabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		feedbackTabel.setVisible(false);
		final Rectangle clientArea = parent.getClientArea();
		feedbackTabel.setBounds(clientArea.x, clientArea.y, 400, 400);
		String[] colNames = { "Name", "Credits", "Positiv", "Type" };

		for (int loopIndex = 0; loopIndex < colNames.length; loopIndex++) {
			final TableColumn column = new TableColumn(feedbackTabel, SWT.NULL);
			column.setText(colNames[loopIndex]);
			column.setWidth(110);
		}
	}

	private void addResultToTable(Table table, Entry<ResultsDTO, List<Feedback>> entry) {
		Display display = Display.getDefault();
		if (entry != null) {
			ResultsDTO result = entry.getKey();
			final TableItem item = new TableItem(table, SWT.NULL);
			item.setText(0, result.resultString);
			item.setText(1, "" + result.score);
			item.setText(2, result.successful ? "successful" : "failed");
			item.setForeground(2, result.successful ? display.getSystemColor(SWT.COLOR_GREEN)
					: display.getSystemColor(SWT.COLOR_RED));
			item.setText(3, result.rated.toString());
			item.setText(4, result.completionDate.toString());
			table.pack();
			table.setVisible(true);
		}
		table.pack();
		table.setVisible(true);

	}

	private void addFeedbackToTable(Table table, Entry<ResultsDTO, List<Feedback>> entry) {
		Display display = Display.getDefault();
		if (entry != null) {
			for (var feedback : entry.getValue()) {
				final TableItem item = new TableItem(table, SWT.NULL);
				item.setText(0, feedback.getText());
				item.setText(1, "" + feedback.getCredits());
				item.setText(2, feedback.getPositive() ? "successful" : "failed");
				item.setForeground(2, feedback.getPositive() ? display.getSystemColor(SWT.COLOR_GREEN)
						: display.getSystemColor(SWT.COLOR_RED));
				item.setText(3, feedback.getType().toString());
			}
			table.pack();
			table.setVisible(true);
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

	private void createGradingTab(TabFolder tabFolder) {
		TabItem gradingTabItem = new TabItem(tabFolder, SWT.NONE);
		gradingTabItem.setText("Student");

		this.scrolledCompositeGrading = new ScrolledComposite(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		gradingTabItem.setControl(this.scrolledCompositeGrading);
		scrolledCompositeGrading.setLayout(new GridLayout(1, true));
		this.scrolledCompositeGrading.setExpandHorizontal(true);
		this.scrolledCompositeGrading.setExpandVertical(true);

		this.gradingComposite = new Composite(this.scrolledCompositeGrading, SWT.NONE);
		this.scrolledCompositeGrading.setContent(this.gradingComposite);
		this.scrolledCompositeGrading.setMinSize(this.gradingComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		gradingComposite.setLayout(new GridLayout(1, true));

	}

	private void createGradingViewElements() {
		this.gradingComposite.dispose();
		this.scrolledCompositeGrading.setContent(null);
		this.gradingComposite = new Composite(this.scrolledCompositeGrading, SWT.NONE);
		this.viewController.setCurrentAssessmentController();
		this.gradingComposite.setLayout(new GridLayout(1, true));
		this.viewController.getRatingGroups().forEach(ratingGroup -> {
			final Group rgDisplay = new Group(this.gradingComposite, SWT.NONE);
			this.ratingGroupViewElements.put(ratingGroup.getDisplayName(), rgDisplay);
			this.updatePenalty(ratingGroup.getDisplayName());
			final GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 3;
			rgDisplay.setLayout(gridLayout);
			final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
			rgDisplay.setLayoutData(gridData);
			this.viewController.getMistakeTypes().forEach(mistake -> {
				if (mistake.getRatingGroup().getDisplayName().equals(ratingGroup.getDisplayName())) {
					if ("Custom Penalty".equals(mistake.getPenaltyName())) {
						this.createCustomButton(ratingGroup, rgDisplay, mistake);
						return;
					}
					final Button mistakeButton = new Button(rgDisplay, SWT.PUSH);
					mistakeButton.setText(mistake.getPenaltyName());
					mistakeButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

					this.mistakeButtons.put(mistake.getPenaltyName(), mistakeButton);
					mistakeButton.setToolTipText(this.viewController.getToolTipForMistakeType(mistake));
					mistakeButton.addListener(SWT.Selection, event -> {
						this.viewController.addAssessmentAnnotation(mistake, null, null,
								mistake.getRatingGroup().getDisplayName());
						this.updatePenalty(mistake.getRatingGroup().getDisplayName());
						this.updateMistakeButtonToolTips(mistake);
					});
				}
			});
		});
		this.scrolledCompositeGrading.setContent(this.gradingComposite);
		this.scrolledCompositeGrading.setMinSize(this.gradingComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
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
		this.createGradingTab(tabFolder);
		this.createAssessmentTab(tabFolder);
		this.createBacklogTab(tabFolder);
		this.updateState();
	}

	private void fillBacklogComboWithData(Combo backlogCombo, Combo filterCombo) {
		backlogCombo.removeAll();
		SubmissionFilter filter = SubmissionFilter.ALL;
		int idx = filterCombo.getSelectionIndex();
		if (idx >= 0) {
			String value = filterCombo.getItem(idx);
			filter = Arrays.stream(SubmissionFilter.values()).filter(f -> f.name().equals(value)).findFirst().get();
		}
		this.viewController.getSubmissionsForBacklog(filter).forEach(backlogCombo::add);
	}

	private void initializeBacklogCombo(Combo backlogCombo) {
		backlogCombo.addListener(SWT.Selection, e -> {
			this.viewController.setAssessedSubmission(backlogCombo.getItem(backlogCombo.getSelectionIndex()));
			this.updateState();
		});
	}

	private void loadExamComboEntries(Combo examCourseCombo, Combo examCombo, Combo examExerciseCombo) {
		this.viewController.getCourseShortNames().forEach(examCourseCombo::add);
		examCourseCombo.addListener(SWT.Selection, e -> {
			this.createExamComboList(this.courseCombo, examCombo, examExerciseCombo);
			this.updateState();
		});

	}

	private void prepareNewAssessment() {
		this.createGradingViewElements();
		this.viewController.createAnnotationsMarkers();
		this.viewController.getRatingGroups().forEach(ratingGroup -> this.updatePenalty(ratingGroup.getDisplayName()));
	}

	@Override
	public void setFocus() {
		// NOP
	}

	private void updateMistakeButtonToolTips(IMistakeType mistakeType) {
		Button button = this.mistakeButtons.get(mistakeType.getPenaltyName());
		if (button != null) {
			Display.getDefault().asyncExec( //
					() -> button.setToolTipText(this.viewController.getToolTipForMistakeType(mistakeType)) //
			);
		}
	}

	private void updatePenalties() {
		this.viewController.getRatingGroups().forEach(ratingGroup -> this.updatePenalty(ratingGroup.getDisplayName()));
		this.updateAllToolTips();
	}

	private void updateAllToolTips() {
		List<IMistakeType> mistakes = this.viewController.getMistakeTypes();
		for (IMistakeType mistake : mistakes) {
			this.updateMistakeButtonToolTips(mistake);
		}
	}

	private void updatePenalty(String ratingGroupName) {
		Group viewElement = this.ratingGroupViewElements.get(ratingGroupName);
		IRatingGroup ratingGroup = this.viewController.getRatingGroupByDisplayName(ratingGroupName);
		if (ratingGroup == null) {
			return;
		}
		StringBuilder builder = new StringBuilder(ratingGroupName);
		builder.append("(");
		builder.append(this.viewController.getCurrentPenaltyForRatingGroup(ratingGroup));
		if (ratingGroup.hasPenaltyLimit()) {
			builder.append("/");
			builder.append(ratingGroup.getPenaltyLimit());
		}
		builder.append(") penalty points");
		Display.getDefault().asyncExec(() -> viewElement.setText(builder.toString()));
	}

	private void updateState() {
		this.possibleActions.values().forEach(set -> set.forEach(control -> control.setEnabled(false)));
		this.viewController.getPossiblyTransitions().forEach(
				transition -> this.possibleActions.get(transition).forEach(control -> control.setEnabled(true)));
	}

	private void addControlToPossibleActions(Control control, Transition transition) {
		Set<Control> temp = this.possibleActions.get(transition);
		temp.add(control);
		this.possibleActions.put(transition, temp);
	}

	private void refreshArtemisState() {
		this.viewController = new StudentViewController();
		this.resetCombos();
		this.updateState();
	}

	private void resetCombos() {
		this.courseCombo.removeAll();
		this.examCombo.removeAll();
		this.exerciseCombo.removeAll();
		this.btnSubmitExcerise.setText(NO_SELECTED);
		this.viewController.getCourseShortNames().forEach(courseShortName -> this.courseCombo.add(courseShortName));
	}
}
