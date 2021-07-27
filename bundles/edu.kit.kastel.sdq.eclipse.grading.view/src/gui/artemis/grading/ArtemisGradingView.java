package gui.artemis.grading;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.IRatingGroup;
import gui.controllers.AssessmentViewController;
import gui.utilities.AssessmentUtilities;

public class ArtemisGradingView extends ViewPart {

	private final AssessmentViewController viewController;
	private Collection<IMistakeType> mistakeTypes;
	private ArrayList<IRatingGroup> ratingGroups;
	private Map<String, Group> ratingGroupViewElements;
	private final String MARKER_NAME = "gui.assessment.marker";
	private HashMap<String, Button> mistakeButtons;
	private Combo backlogCombo;
	private ScrolledComposite scrolledComposite_grading;
	private Composite gradingComposite;

	public ArtemisGradingView() {
		this.viewController = new AssessmentViewController();
		this.ratingGroupViewElements = new HashMap<String, Group>();
		this.mistakeButtons = new HashMap<String, Button>();
		this.addListenerForMarkerDeletion();
	}

	private void addListenerForMarkerDeletion() {
		IWorkspace workspace = AssessmentUtilities.getWorkspace();
		IResourceChangeListener listener = new IResourceChangeListener() {

			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				Arrays.asList(event.findMarkerDeltas(ArtemisGradingView.this.MARKER_NAME, true)).forEach(marker -> {
					if (marker.getKind() == 2) {
						ArtemisGradingView.this.viewController.deleteAnnotation(marker.getId());
						ArtemisGradingView.this.updatePenalties();
					}
				});

			}

		};

		workspace.addResourceChangeListener(listener);
	}

	private void createCustomButton(IRatingGroup ratingGroup, Group rgDisplay, Composite parent) {
		final Button customButton = new Button(rgDisplay, SWT.PUSH);
		customButton.setText("Custom");
		customButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				final CustomButtonDialog customDialog = new CustomButtonDialog(AssessmentUtilities.getWindowsShell(),
						ArtemisGradingView.this.viewController, ratingGroup.getDisplayName(), rgDisplay);
				customDialog.setBlockOnOpen(true);
				customDialog.open();
				Display.getDefault()
						.asyncExec(() -> ArtemisGradingView.this.updatePenalties(ratingGroup.getDisplayName(), false));
			}
		});
	}

	private void updatePenalties() {
		this.ratingGroups.forEach(ratingGroup -> {
			this.updatePenalties(ratingGroup.getDisplayName(), false);
		});
	}

	private void createCourseAndExerciseListView(Composite composite) {
		Composite listHolder = new Composite(composite, SWT.NONE);
		listHolder.setLayout(new GridLayout(2, true));
		Label courseListLabel = new Label(listHolder, SWT.NONE);
		courseListLabel.setText("Courses:");
		final Combo courseList = new Combo(listHolder, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		Label exerciseListLabel = new Label(listHolder, SWT.NONE);
		exerciseListLabel.setText("Exercises: ");
		final Combo exerciseList = new Combo(listHolder, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		exerciseList.add("-placeholder-");
		this.viewController.getCourses().forEach(course -> {
			courseList.add(course.getTitle());
		});
		courseList.addListener(SWT.Selection, e -> {
			this.createExerciseListInput(courseList.getItem(courseList.getSelectionIndex()), exerciseList);
		});
	}

	private void createExerciseListInput(String courseShortName, Combo exerciseList) {
		exerciseList.removeAll();
		this.viewController.getExerciseShortNames(courseShortName).forEach(exerciseShortName -> {
			exerciseList.add(courseShortName);
		});
		exerciseList.addListener(SWT.Selection, e -> {
			this.viewController.setExerciseID(exerciseList.getItem(exerciseList.getSelectionIndex()));
		});
	}

	private void createAssessmentViewElements() {
		this.gradingComposite.setLayout(new GridLayout(3, true));
		this.ratingGroups.forEach(element -> {
			final Group rgDisplay = new Group(this.gradingComposite, SWT.NONE);
			this.ratingGroupViewElements.put(element.getDisplayName(), rgDisplay);
			this.updatePenalties(element.getDisplayName(), false);
			final GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 3;
			rgDisplay.setLayout(gridLayout);
			final GridData gridData = new GridData(GridData.VERTICAL_ALIGN_FILL);
			gridData.horizontalSpan = 3;
			rgDisplay.setLayoutData(gridData);
			this.mistakeTypes.forEach(mistake -> {
				if (mistake.getRatingGroup().getDisplayName().equals(element.getDisplayName())) {
					final Button mistakeButton = new Button(rgDisplay, SWT.PUSH);
					mistakeButton.setText(mistake.getButtonName());
					this.mistakeButtons.put(mistake.getButtonName(), mistakeButton);
					mistakeButton.setToolTipText(this.viewController.getToolTipForMistakeType(mistake));
					mistakeButton.addListener(SWT.Selection, new Listener() {

						@Override
						public void handleEvent(Event event) {
							ArtemisGradingView.this.viewController.addAssessmentAnnotaion(mistake, null, null,
									mistake.getRatingGroupName());
							ArtemisGradingView.this.updatePenalties(mistake.getRatingGroupName(), false);
							ArtemisGradingView.this.updateMistakeButtonToolTips(mistake);
						}
					});
				}
			});
			this.createCustomButton(element, rgDisplay, this.gradingComposite);
		});
		this.scrolledComposite_grading.setContent(this.gradingComposite);
		this.scrolledComposite_grading.setMinSize(this.gradingComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private void updateMistakeButtonToolTips(IMistakeType mistakeType) {
		Button button = this.mistakeButtons.get(mistakeType.getButtonName());
		if (button != null) {
			button.setToolTipText(this.viewController.getToolTipForMistakeType(mistakeType));
		}
	}

	protected void updatePenalties(String ratingGroupName, boolean reset) {
		Group viewElement = this.ratingGroupViewElements.get(ratingGroupName);
		IRatingGroup ratingGroup = this.findRatingGroup(ratingGroupName);
		StringBuilder builder = new StringBuilder(ratingGroupName);
		builder.append("(");
		builder.append(reset ? 0 : this.viewController.getCurrentPenaltyForRatingGroup(ratingGroup));
		if (ratingGroup.hasPenaltyLimit()) {
			builder.append("/");
			builder.append(ratingGroup.getPenaltyLimit());
		}
		builder.append(") penalty points");
		Display.getDefault().asyncExec(() -> {
			viewElement.setText(builder.toString());
		});
	}

	private IRatingGroup findRatingGroup(String ratingGroupName) {
		for (int i = 0; i < this.ratingGroups.size(); i++) {
			if (this.ratingGroups.get(i).getDisplayName().equals(ratingGroupName)) {
				return this.ratingGroups.get(i);
			}
		}
		return null;
	}

	@Override
	public void createPartControl(Composite parent) {
		this.generateLayout(parent);
	}

	private void generateLayout(Composite parent) {
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);

		TabItem gradingTabItem = new TabItem(tabFolder, SWT.NONE);
		gradingTabItem.setText("Grading");

		this.scrolledComposite_grading = new ScrolledComposite(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		gradingTabItem.setControl(this.scrolledComposite_grading);
		this.scrolledComposite_grading.setExpandHorizontal(true);
		this.scrolledComposite_grading.setExpandVertical(true);

		this.gradingComposite = new Composite(this.scrolledComposite_grading, SWT.NONE);
		this.scrolledComposite_grading.setContent(this.gradingComposite);
		this.scrolledComposite_grading.setMinSize(this.gradingComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		TabItem assessmentTabItem = new TabItem(tabFolder, SWT.NONE);
		assessmentTabItem.setText("Assessment");

		ScrolledComposite scrolledComposite_assessment = new ScrolledComposite(tabFolder,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		assessmentTabItem.setControl(scrolledComposite_assessment);
		scrolledComposite_assessment.setExpandHorizontal(true);
		scrolledComposite_assessment.setExpandVertical(true);

		Composite assessmentComposite = new Composite(scrolledComposite_assessment, SWT.NONE);
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

		scrolledComposite_assessment.setContent(assessmentComposite);
		scrolledComposite_assessment.setMinSize(assessmentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		TabItem tbtmExam = new TabItem(tabFolder, SWT.NONE);
		tbtmExam.setText("Exam");

		ScrolledComposite scrolledComposite_exam = new ScrolledComposite(tabFolder,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmExam.setControl(scrolledComposite_exam);
		scrolledComposite_exam.setExpandHorizontal(true);
		scrolledComposite_exam.setExpandVertical(true);

		Composite examComposite = new Composite(scrolledComposite_exam, SWT.NONE);
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

		scrolledComposite_exam.setContent(examComposite);
		scrolledComposite_exam.setMinSize(examComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		TabItem backlogTabItem = new TabItem(tabFolder, SWT.NONE);
		backlogTabItem.setText("Backlog");

		ScrolledComposite scrolledComposite_backlog = new ScrolledComposite(tabFolder,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		backlogTabItem.setControl(scrolledComposite_backlog);
		scrolledComposite_backlog.setExpandHorizontal(true);
		scrolledComposite_backlog.setExpandVertical(true);

		Composite backlogComposite = new Composite(scrolledComposite_backlog, SWT.NONE);
		backlogComposite.setLayout(new GridLayout(2, false));

		Label lblSubmitted = new Label(backlogComposite, SWT.NONE);
		lblSubmitted.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSubmitted.setText("Submitted");

		this.backlogCombo = new Combo(backlogComposite, SWT.READ_ONLY);
		GridData gd_combo_4 = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_combo_4.widthHint = 152;
		this.backlogCombo.setLayoutData(gd_combo_4);

		this.initializeBacklogCombo(this.backlogCombo);

		Button btnLoadAgain = new Button(backlogComposite, SWT.NONE);
		btnLoadAgain.setText("Load again");

		this.addSelectionListenerForLoadFromBacklogButton(btnLoadAgain);

		scrolledComposite_backlog.setContent(backlogComposite);
		scrolledComposite_backlog.setMinSize(backlogComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}

	private void initializeBacklogCombo(Combo backlogCombo2) {
		this.viewController.getSubmissionsForBacklog();
	}

	private void loadExamComboEntries(Combo examCourseCombo, Combo examCombo, Combo examExerciseCombo) {
		this.viewController.getCourseShortNames().forEach(courseShortName -> {
			examCourseCombo.add(courseShortName);
		});
		examCourseCombo.addListener(SWT.Selection, e -> {
			this.createExamComboList(examCourseCombo.getItem(examCourseCombo.getSelectionIndex()), examCombo,
					examExerciseCombo);
		});
	}

	private void createExamComboList(String courseTitle, Combo examCombo, Combo examExerciseCombo) {
		examCombo.removeAll();
		this.viewController.getExamShortNames(courseTitle).forEach(examShortName -> {
			examCombo.add(examShortName);
		});
		examCombo.addListener(SWT.Selection, e -> {
			this.viewController.getExercisesShortNamesForExam(examCombo.getItem(examCombo.getSelectionIndex()))
					.forEach(exerciseShortName -> {
						examExerciseCombo.add(exerciseShortName);
					});
		});
		examExerciseCombo.addListener(SWT.Selection, e -> {
			this.viewController.setExerciseID(examExerciseCombo.getItem(examExerciseCombo.getSelectionIndex()));
		});
	}

	private void addSelectionListenerForLoadFromBacklogButton(Button btnLoadAgain) {
		btnLoadAgain.addListener(SWT.Selection, e -> {
			this.viewController.onLoadAgain();
		});
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
		btnSubmit.addListener(SWT.Selection, e -> {
			this.viewController.onSubmitAssessment();
		});
	}

	private void addSelectionListenerForSaveButton(Button btnSave) {
		btnSave.addListener(SWT.Selection, e -> {
			this.viewController.onSaveAssessment();
		});
	}

	private void addSelectionListenerForReloadButton(Button btnReloadA) {
		btnReloadA.addListener(SWT.Selection, e -> {
			this.viewController.onReloadAssessment();
		});
	}

	private void addSelectionListenerForStartAssessmentButton(Button startAssessmentButton) {
		startAssessmentButton.addListener(SWT.Selection, e -> {
			this.viewController.onStartAssessment();
			// TODO: clean up and new generation
			this.createAssessmentViewElements();
			this.prepareNewAssessment();
		});
	}

	@Override
	public void setFocus() {
	}

	public void createCourseListForAssessmentTabItem(Combo courseCombo, Combo exerciseCombo) {
		this.viewController.getCourseShortNames().forEach(courseShortName -> {
			courseCombo.add(courseShortName);
		});
		courseCombo.addListener(SWT.Selection, e -> {
			this.createExerciseListInput(courseCombo.getItem(courseCombo.getSelectionIndex()), exerciseCombo);
		});
	}

	private void prepareNewAssessment() {
		this.loadAnnotationMarkers();
		this.ratingGroups.forEach(ratingGroup -> {
			this.updatePenalties(ratingGroup.getDisplayName(), false);
		});
	}

	private void loadAnnotationMarkers() {
		this.viewController.createAnnotationsMarkers();
	}
}
