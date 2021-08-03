package edu.kit.kastel.eclipse.grading.gui.assessment.view;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

import edu.kit.kastel.eclipse.grading.gui.controllers.AssessmentViewController;
import edu.kit.kastel.eclipse.grading.gui.utilities.AssessmentUtilities;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IRatingGroup;

/**
 * This class creates the view elements for the artemis grading process. It is
 * build as a tab folder with four tabs grading, assessment, exam and backlog.
 * 
 * @see {@link ViewPart}
 *
 */
public class ArtemisGradingView extends ViewPart {

	private final AssessmentViewController viewController;
	private Map<String, Group> ratingGroupViewElements;
	private Map<String, Button> mistakeButtons;
	private ScrolledComposite scrolledCompositeGrading;
	private Composite gradingComposite;

	public ArtemisGradingView() {
		this.viewController = new AssessmentViewController();
		this.ratingGroupViewElements = new HashMap<>();
		this.mistakeButtons = new HashMap<>();
		this.addListenerForMarkerDeletion();
	}

	private void addListenerForMarkerDeletion() {
		AssessmentUtilities.getWorkspace().addResourceChangeListener(event -> Arrays
				.asList(event.findMarkerDeltas(AssessmentUtilities.MARKER_NAME, true)).forEach(marker -> {
					// check if marker is deleted
					if (marker.getKind() == 2) {
						this.viewController.deleteAnnotation(marker.getId());
						this.updatePenalties();
					}
				}));
	}

	private void createCustomButton(IRatingGroup ratingGroup, Group rgDisplay) {
		final Button customButton = new Button(rgDisplay, SWT.PUSH);
		customButton.setText("Custom");
		customButton.addListener(SWT.Selection, event -> {
			final CustomButtonDialog customDialog = new CustomButtonDialog(AssessmentUtilities.getWindowsShell(),
					this.viewController, ratingGroup.getDisplayName());
			customDialog.setBlockOnOpen(true);
			customDialog.open();
			// avoid SWT Exception
			Display.getDefault().asyncExec(() -> this.updatePenalty(ratingGroup.getDisplayName()));
		});
	}

	private void updatePenalties() {
		this.viewController.getRatingGroups().forEach(ratingGroup -> this.updatePenalty(ratingGroup.getDisplayName()));
	}

	private void createExerciseListInput(String courseShortName, Combo exerciseList) {
		exerciseList.removeAll();
		this.viewController.getExerciseShortNames(courseShortName)
				.forEach(exerciseShortName -> exerciseList.add(exerciseShortName));
		exerciseList.addListener(SWT.Selection,
				e -> this.viewController.setExerciseID(exerciseList.getItem(exerciseList.getSelectionIndex())));
	}

	private void createGradingViewElements() {
		this.gradingComposite.dispose();
		this.scrolledCompositeGrading.setContent(null);
		this.gradingComposite = new Composite(this.scrolledCompositeGrading, SWT.NONE);
		this.viewController.setCurrentAssessmentController();
		this.gradingComposite.setLayout(new GridLayout(3, true));
		this.viewController.getRatingGroups().forEach(ratingGroup -> {
			final Group rgDisplay = new Group(this.gradingComposite, SWT.NONE);
			this.ratingGroupViewElements.put(ratingGroup.getDisplayName(), rgDisplay);
			this.updatePenalty(ratingGroup.getDisplayName());
			final GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 3;
			rgDisplay.setLayout(gridLayout);
			final GridData gridData = new GridData(GridData.VERTICAL_ALIGN_FILL);
			gridData.horizontalSpan = 3;
			rgDisplay.setLayoutData(gridData);
			this.viewController.getMistakeTypes().forEach(mistake -> {
				if (mistake.getRatingGroup().getDisplayName().equals(ratingGroup.getDisplayName())) {
					final Button mistakeButton = new Button(rgDisplay, SWT.PUSH);
					mistakeButton.setText(mistake.getButtonName());
					this.mistakeButtons.put(mistake.getButtonName(), mistakeButton);
					mistakeButton.setToolTipText(this.viewController.getToolTipForMistakeType(mistake));
					mistakeButton.addListener(SWT.Selection, event -> {
						this.viewController.addAssessmentAnnotaion(mistake, null, null, mistake.getRatingGroupName());
						this.updatePenalty(mistake.getRatingGroupName());
						this.updateMistakeButtonToolTips(mistake);
					});
				}
			});
			this.createCustomButton(ratingGroup, rgDisplay);
		});
		this.scrolledCompositeGrading.setContent(this.gradingComposite);
		this.scrolledCompositeGrading.setMinSize(this.gradingComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private void updateMistakeButtonToolTips(IMistakeType mistakeType) {
		Button button = this.mistakeButtons.get(mistakeType.getButtonName());
		if (button != null) {
			button.setToolTipText(this.viewController.getToolTipForMistakeType(mistakeType));
		}
	}

	private void updatePenalty(String ratingGroupName) {
		Group viewElement = this.ratingGroupViewElements.get(ratingGroupName);
		IRatingGroup ratingGroup = this.viewController.getRatingGroupByShortName(ratingGroupName);
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

	@Override
	public void createPartControl(Composite parent) {
		this.createView(parent);
	}

	private void createView(Composite parent) {
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
		this.createGradingTab(tabFolder);
		this.createAssessmentTab(tabFolder);
		this.createExamTab(tabFolder);
		this.createBacklogTab(tabFolder);

	}

	private void createBacklogTab(TabFolder tabFolder) {
		TabItem backlogTabItem = new TabItem(tabFolder, SWT.NONE);
		backlogTabItem.setText("Backlog");

		ScrolledComposite scrolledCompositeBacklog = new ScrolledComposite(tabFolder,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		backlogTabItem.setControl(scrolledCompositeBacklog);
		scrolledCompositeBacklog.setExpandHorizontal(true);
		scrolledCompositeBacklog.setExpandVertical(true);

		Composite backlogComposite = new Composite(scrolledCompositeBacklog, SWT.NONE);
		backlogComposite.setLayout(new GridLayout(2, false));

		Label lblSubmitted = new Label(backlogComposite, SWT.NONE);
		lblSubmitted.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSubmitted.setText("Submitted");

		Combo backlogCombo = new Combo(backlogComposite, SWT.READ_ONLY);
		GridData gdBacklogCombo = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gdBacklogCombo.widthHint = 152;
		backlogCombo.setLayoutData(gdBacklogCombo);

		this.initializeBacklogCombo(backlogCombo);

		Button refreshButton = new Button(backlogComposite, SWT.NONE);
		refreshButton.setText("Refresh Submitted");

		this.addSelectionListenerForRefreshButton(refreshButton, backlogCombo);

		Button btnLoadAgain = new Button(backlogComposite, SWT.NONE);
		btnLoadAgain.setText("Load again");

		this.addSelectionListenerForLoadFromBacklogButton(btnLoadAgain);

		scrolledCompositeBacklog.setContent(backlogComposite);
		scrolledCompositeBacklog.setMinSize(backlogComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private void createExamTab(TabFolder tabFolder) {
		TabItem tbtmExam = new TabItem(tabFolder, SWT.NONE);
		tbtmExam.setText("Exam");

		ScrolledComposite scrolledCompositeExam = new ScrolledComposite(tabFolder,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmExam.setControl(scrolledCompositeExam);
		scrolledCompositeExam.setExpandHorizontal(true);
		scrolledCompositeExam.setExpandVertical(true);

		Composite examComposite = new Composite(scrolledCompositeExam, SWT.NONE);
		examComposite.setLayout(new GridLayout(2, false));

		Label lblCourse = new Label(examComposite, SWT.NONE);
		lblCourse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCourse.setText("Course");

		Combo comboCourseForExam = new Combo(examComposite, SWT.READ_ONLY);
		comboCourseForExam.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

		Label lblExam = new Label(examComposite, SWT.NONE);
		lblExam.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblExam.setText("Exam");

		Combo examCombo = new Combo(examComposite, SWT.READ_ONLY);
		examCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

		Label lblExercise = new Label(examComposite, SWT.NONE);
		lblExercise.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblExercise.setText("Exercise");

		Combo examExerciseCombo = new Combo(examComposite, SWT.READ_ONLY);
		examExerciseCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

		this.loadExamComboEntries(comboCourseForExam, examCombo, examExerciseCombo);

		Button btnReloadExam = new Button(examComposite, SWT.NONE);
		btnReloadExam.setText("Reload");

		this.addSelectionListenerForReloadButton(btnReloadExam);

		Button btnSaveExam = new Button(examComposite, SWT.NONE);
		btnSaveExam.setText("Save");

		this.addSelectionListenerForSaveButton(btnSaveExam);

		Button btnStartFirstRound = new Button(examComposite, SWT.NONE);
		btnStartFirstRound.setText("Start Correction Round 1");

		this.addSelectionListenerForStartFirstRound(btnStartFirstRound);

		Button btnStartSecondRound = new Button(examComposite, SWT.NONE);
		btnStartSecondRound.setText("Start Correction Round 2");

		this.addSelectionListenerForStartSecondRound(btnStartSecondRound);

		Button btnSubmitExam = new Button(examComposite, SWT.NONE);
		btnSubmitExam.setText("Submit");

		this.addSelectionListenerForSubmitButton(btnSubmitExam);

		scrolledCompositeExam.setContent(examComposite);
		scrolledCompositeExam.setMinSize(examComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private void createAssessmentTab(TabFolder tabFolder) {
		TabItem assessmentTabItem = new TabItem(tabFolder, SWT.NONE);
		assessmentTabItem.setText("Assessment");

		ScrolledComposite scrolledCompositeAssessment = new ScrolledComposite(tabFolder,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		assessmentTabItem.setControl(scrolledCompositeAssessment);
		scrolledCompositeAssessment.setExpandHorizontal(true);
		scrolledCompositeAssessment.setExpandVertical(true);

		Composite assessmentComposite = new Composite(scrolledCompositeAssessment, SWT.NONE);
		assessmentComposite.setLayout(new GridLayout(2, false));

		Label courseLabel = new Label(assessmentComposite, SWT.NONE);
		courseLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		courseLabel.setText("Course");

		Combo courseCombo = new Combo(assessmentComposite, SWT.READ_ONLY);
		courseCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

		Label exerciseLabel = new Label(assessmentComposite, SWT.NONE);
		exerciseLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		exerciseLabel.setText("Exercise");

		Combo exerciseCombo = new Combo(assessmentComposite, SWT.READ_ONLY);
		exerciseCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

		this.createCourseListForAssessmentTabItem(courseCombo, exerciseCombo);

		Button btnStartAssessment = new Button(assessmentComposite, SWT.NONE);
		btnStartAssessment.setText("Start");

		this.addSelectionListenerForStartAssessmentButton(btnStartAssessment);

		Button btnReloadA = new Button(assessmentComposite, SWT.NONE);
		btnReloadA.setText("Reload");

		this.addSelectionListenerForReloadButton(btnReloadA);

		Button btnSaveAssessment = new Button(assessmentComposite, SWT.NONE);
		btnSaveAssessment.setText("Save ");

		this.addSelectionListenerForSaveButton(btnSaveAssessment);

		Button btnSubmitAssessment = new Button(assessmentComposite, SWT.NONE);
		btnSubmitAssessment.setText("Submit");

		this.addSelectionListenerForSubmitButton(btnSubmitAssessment);

		scrolledCompositeAssessment.setContent(assessmentComposite);
		scrolledCompositeAssessment.setMinSize(assessmentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private void createGradingTab(TabFolder tabFolder) {
		TabItem gradingTabItem = new TabItem(tabFolder, SWT.NONE);
		gradingTabItem.setText("Grading");

		this.scrolledCompositeGrading = new ScrolledComposite(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		gradingTabItem.setControl(this.scrolledCompositeGrading);
		this.scrolledCompositeGrading.setExpandHorizontal(true);
		this.scrolledCompositeGrading.setExpandVertical(true);

		this.gradingComposite = new Composite(this.scrolledCompositeGrading, SWT.NONE);
		this.scrolledCompositeGrading.setContent(this.gradingComposite);
		this.scrolledCompositeGrading.setMinSize(this.gradingComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private void addSelectionListenerForRefreshButton(Button refreshButton, Combo backlogCombo) {
		refreshButton.addListener(SWT.Selection, e -> this.fillBacklogComboWithData(backlogCombo));

	}

	private void initializeBacklogCombo(Combo backlogCombo) {
		this.fillBacklogComboWithData(backlogCombo);

		backlogCombo.addListener(SWT.Selection,
				e -> this.viewController.setAssessedSubmission(backlogCombo.getItem(backlogCombo.getSelectionIndex())));
	}

	private void fillBacklogComboWithData(Combo backlogCombo) {
		backlogCombo.removeAll();
		this.viewController.getSubmissionsForBacklog().forEach(project -> backlogCombo.add(project));
	}

	private void loadExamComboEntries(Combo examCourseCombo, Combo examCombo, Combo examExerciseCombo) {
		this.viewController.getCourseShortNames().forEach(courseShortName -> examCourseCombo.add(courseShortName));
		examCourseCombo.addListener(SWT.Selection,
				e -> this.createExamComboList(examCourseCombo.getItem(examCourseCombo.getSelectionIndex()), examCombo,
						examExerciseCombo));
	}

	private void createExamComboList(String courseTitle, Combo examCombo, Combo examExerciseCombo) {
		examCombo.removeAll();
		this.viewController.getExamShortNames(courseTitle).forEach(examShortName -> examCombo.add(examShortName));
		examCombo.addListener(SWT.Selection, e -> {
			this.viewController.getExercisesShortNamesForExam(examCombo.getItem(examCombo.getSelectionIndex()))
					.forEach(exerciseShortName -> examExerciseCombo.add(exerciseShortName));
		});
		examExerciseCombo.addListener(SWT.Selection, e -> this.viewController
				.setExerciseID(examExerciseCombo.getItem(examExerciseCombo.getSelectionIndex())));
	}

	private void addSelectionListenerForLoadFromBacklogButton(Button btnLoadAgain) {
		btnLoadAgain.addListener(SWT.Selection, e -> this.viewController.onLoadAgain());
	}

	private void addSelectionListenerForStartSecondRound(Button btnStartRound2) {
		btnStartRound2.addListener(SWT.Selection, e -> {
			this.viewController.onStartCorrectionRound2();
			this.prepareNewAssessment();
		});
	}

	private void addSelectionListenerForStartFirstRound(Button btnStartRound1) {
		btnStartRound1.addListener(SWT.Selection, e -> {
			this.viewController.onStartCorrectionRound1();
			this.prepareNewAssessment();
		});
	}

	private void addSelectionListenerForSubmitButton(Button btnSubmit) {
		btnSubmit.addListener(SWT.Selection, e -> this.viewController.onSubmitAssessment());
	}

	private void addSelectionListenerForSaveButton(Button btnSave) {
		btnSave.addListener(SWT.Selection, e -> this.viewController.onSaveAssessment());
	}

	private void addSelectionListenerForReloadButton(Button btnReloadA) {
		btnReloadA.addListener(SWT.Selection, e -> this.viewController.onReloadAssessment());
	}

	private void addSelectionListenerForStartAssessmentButton(Button startAssessmentButton) {
		startAssessmentButton.addListener(SWT.Selection, e -> {
			boolean started = this.viewController.onStartAssessment();
			if (started) {
				this.createGradingViewElements();
				this.prepareNewAssessment();
			}
		});
	}

	public void createCourseListForAssessmentTabItem(Combo courseCombo, Combo exerciseCombo) {
		this.viewController.getCourseShortNames().forEach(courseShortName -> courseCombo.add(courseShortName));
		courseCombo.addListener(SWT.Selection,
				e -> this.createExerciseListInput(courseCombo.getItem(courseCombo.getSelectionIndex()), exerciseCombo));
	}

	private void prepareNewAssessment() {
		this.viewController.createAnnotationsMarkers();
		this.viewController.getRatingGroups().forEach(ratingGroup -> this.updatePenalty(ratingGroup.getDisplayName()));
	}

	@Override
	public void setFocus() {

	}
}
