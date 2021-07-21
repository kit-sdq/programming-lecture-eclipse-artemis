package gui.artemis.grading;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.jface.dialogs.MessageDialog;
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

import edu.kit.kastel.sdq.eclipse.grading.api.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.IRatingGroup;
import gui.controllers.AssessmentViewController;
import gui.utilities.AssessmentUtilities;

public class ArtemisGradingView extends ViewPart {

	private final AssessmentViewController viewController;
	private Collection<IMistakeType> mistakeTypes;
	private ArrayList<IRatingGroup> ratingGroups;
	private Map<String, Group> ratingGroupViewElements;
	private int courseID;
	private int exerciseID;
	private int submissionID;
	private boolean errorTypesCreated;
	private final String MARKER_NAME = "gui.assessment.marker";
	private HashMap<String, Button> mistakeButtons;
	private String currentExerciseName;
	private Combo examExerciseCombo;
	private Combo backlogCombo;
	private ScrolledComposite scrolledComposite_grading;
	private Composite gradingComposite;
	private int examCourseID;
	private int examExerciseID;
	private int examID;
	private Optional<Integer> examSubmissionID;

	public ArtemisGradingView() {
		this.viewController = new AssessmentViewController();
		this.ratingGroupViewElements = new HashMap<String, Group>();
		this.mistakeButtons = new HashMap<String, Button>();
		this.errorTypesCreated = false;
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

	private void createExerciseListInput(String courseTitle, Combo exerciseList) {
		exerciseList.removeAll();
		Optional<ICourse> optionalCourse = this.viewController.getCourses().stream()
				.filter(course -> course.getTitle().equals(courseTitle)).findFirst();
		if (optionalCourse.isPresent()) {
			this.courseID = optionalCourse.get().getCourseId();
			optionalCourse.get().getExercises().forEach(exercise -> {
				exerciseList.add(exercise.getShortName());
			});
		}
		exerciseList.addListener(SWT.Selection, e -> {
			optionalCourse.get().getExercises().forEach(exercise -> {
				if (exercise.getShortName().equals(exerciseList.getItem(exerciseList.getSelectionIndex()))) {
					this.exerciseID = exercise.getExerciseId();
					this.currentExerciseName = exerciseList.getItem(exerciseList.getSelectionIndex());
				}
			});
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

		this.addSelectionListenerForReloadAssessmentButton(btnReloadA);

		Button btnSaveAssessment = new Button(assessmentComposite, SWT.NONE);
		btnSaveAssessment.setText("Save ");

		this.addSelectionListenerForSaveAssessmentButton(btnSaveAssessment);

		Button btnSubmitAssessment = new Button(assessmentComposite, SWT.NONE);
		btnSubmitAssessment.setText("Submit");

		this.addSelectionListenerForSubmitAssessment(btnSubmitAssessment);

		scrolledComposite_assessment.setContent(assessmentComposite);
		scrolledComposite_assessment.setMinSize(assessmentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		// TODO: Refactor names and implement selection listeners
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

		this.addSelectionListenerForReloadExamButton(btnReloadExam);

		Button btnSaveExam = new Button(examComposite, SWT.NONE);
		btnSaveExam.setText("Save");

		this.addSelectionListenerForSaveExamButton(btnSaveExam);

		Button btnStartFirstRound = new Button(examComposite, SWT.NONE);
		btnStartFirstRound.setText("Start Correction Round 1");

		this.addSelectionListenerForStartFirstRound(btnStartFirstRound);

		Button btnStartSecondRound = new Button(examComposite, SWT.NONE);
		btnStartSecondRound.setText("Start Correction Round 2");

		this.addSelectionListenerForStartSecondRound(btnStartSecondRound);

		Button btnSubmitExam = new Button(examComposite, SWT.NONE);
		btnSubmitExam.setText("Submit");

		this.addSelectionListenerForSubmitExamButton(btnSubmitExam);

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

		Button btnLoadAgain = new Button(backlogComposite, SWT.NONE);
		btnLoadAgain.setText("Load again");

		this.addSelectionListenerForLoadFromBacklogButton(btnLoadAgain);

		scrolledComposite_backlog.setContent(backlogComposite);
		scrolledComposite_backlog.setMinSize(backlogComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}

	private void loadExamComboEntries(Combo examCourseCombo, Combo examCombo, Combo examExerciseCombo) {
		this.viewController.getCourses().forEach(course -> {
			examCourseCombo.add(course.getTitle());
		});
		examCourseCombo.addListener(SWT.Selection, e -> {
			this.createExamComboList(examCourseCombo.getItem(examCourseCombo.getSelectionIndex()), examCombo,
					examExerciseCombo);
		});
	}

	private void createExamComboList(String courseTitle, Combo examCombo, Combo examExerciseCombo) {
		examCombo.removeAll();
		Optional<ICourse> optionalCourse = this.viewController.getCourses().stream()
				.filter(course -> course.getTitle().equals(courseTitle)).findFirst();
		if (optionalCourse.isPresent()) {
			this.examCourseID = optionalCourse.get().getCourseId();
			optionalCourse.get().getExams().forEach(exam -> {
				examCombo.add(exam.getTitle());
			});
		}
		examCombo.addListener(SWT.Selection, e -> {
			Optional<IExam> optionalExam = optionalCourse.get().getExams().stream()
					.filter(exam -> exam.getTitle().equals(examCombo.getItem(examCombo.getSelectionIndex())))
					.findFirst();
			if (optionalExam.isPresent()) {
				optionalExam.get().getExerciseGroups().forEach(exerciseGroup -> {
					exerciseGroup.getExercises().forEach(exercise -> {
						examExerciseCombo.add(exercise.getTitle());
					});
				});
			}
		});
		examExerciseCombo.addListener(SWT.Selection, e -> {
			Optional<IExam> optionalExam = optionalCourse.get().getExams().stream()
					.filter(exam -> exam.getTitle().equals(examCombo.getItem(examCombo.getSelectionIndex())))
					.findFirst();
			if (optionalExam.isPresent()) {
				this.examID = optionalExam.get().getExamId();
				optionalExam.get().getExerciseGroups().forEach(exerciseGroup -> {
					Optional<IExercise> optionalExercise = exerciseGroup.getExercises().stream()
							.filter(exercise -> exercise.getTitle()
									.equals(examExerciseCombo.getItem(examExerciseCombo.getSelectionIndex())))
							.findFirst();
					if (optionalExercise.isPresent()) {
						this.examExerciseID = optionalExercise.get().getExerciseId();
					}
				});
			}
		});
	}

	private void addSelectionListenerForLoadFromBacklogButton(Button btnLoadAgain) {
		// TODO Auto-generated method stub

	}

	private void addSelectionListenerForSaveExamButton(Button btnSave) {
		btnSave.addListener(SWT.Selection, e -> {
		});
	}

	private void addSelectionListenerForStartSecondRound(Button btnStartRound2) {
		btnStartRound2.addListener(SWT.Selection, e -> {
			this.examSubmissionID = this.viewController.startNextAssessment(this.examExerciseID, 1, this.examCourseID);
			if (this.examSubmissionID.isPresent()) {
				this.prepareNewAssessment();
			}
		});
	}

	private void addSelectionListenerForReloadExamButton(Button btnReload) {
		// TODO Auto-generated method stub

	}

	private void addSelectionListenerForStartFirstRound(Button btnStartRound1) {
		btnStartRound1.addListener(SWT.Selection, e -> {
			this.examSubmissionID = this.viewController.startNextAssessment(this.examExerciseID, 0, this.examCourseID);
			if (this.examSubmissionID.isPresent()) {
				this.prepareNewAssessment();
			}
		});
	}

	private void addSelectionListenerForSubmitExamButton(Button btnSubmitExam) {

	}

	private void addSelectionListenerForSubmitAssessment(Button btnSubmitAssessment) {
		btnSubmitAssessment.addListener(SWT.Selection, e -> {
			this.viewController.submitAssessment(this.submissionID);
			this.backlogCombo.add(this.currentExerciseName + " (submissionID= " + this.submissionID + ")");
		});
	}

	private void addSelectionListenerForSaveAssessmentButton(Button btnSaveAssessment) {
		btnSaveAssessment.addListener(SWT.Selection, e -> {
			ArtemisGradingView.this.viewController.saveAssessment(ArtemisGradingView.this.submissionID);
		});
	}

	private void addSelectionListenerForReloadAssessmentButton(Button btnReloadA) {
		btnReloadA.addListener(SWT.Selection, e -> {
			ArtemisGradingView.this.viewController.reloadAssessment(ArtemisGradingView.this.courseID,
					ArtemisGradingView.this.exerciseID, ArtemisGradingView.this.submissionID);
			this.viewController.startAssessment(this.submissionID);
			this.viewController.createAnnotationsMarkers();
		});
	}

	private void addSelectionListenerForStartAssessmentButton(Button startAssessmentButton) {
		startAssessmentButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				try {
					ArtemisGradingView.this.submissionID = ArtemisGradingView.this.viewController
							.startAssessment(ArtemisGradingView.this.exerciseID, ArtemisGradingView.this.courseID);
					if (!ArtemisGradingView.this.errorTypesCreated) {
						ArtemisGradingView.this.viewController
								.createAssessmentController(ArtemisGradingView.this.submissionID);
						ArtemisGradingView.this.mistakeTypes = ArtemisGradingView.this.viewController
								.getMistakeTypesForButtonView();
						ArtemisGradingView.this.ratingGroups = (ArrayList<IRatingGroup>) ArtemisGradingView.this.viewController
								.getRatingGroups();
						ArtemisGradingView.this.createAssessmentViewElements();
						ArtemisGradingView.this.errorTypesCreated = true;
						ArtemisGradingView.this.scrolledComposite_grading
								.setContent(ArtemisGradingView.this.gradingComposite);
						ArtemisGradingView.this.scrolledComposite_grading.setMinSize(
								ArtemisGradingView.this.gradingComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
					}
					if (ArtemisGradingView.this.submissionID == -1) {
						this.openExerciseCompletedDialog();
					} else {
						ArtemisGradingView.this.prepareNewAssessment();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			private void openExerciseCompletedDialog() {
				MessageDialog.openInformation(AssessmentUtilities.getWindowsShell(), "Exercise Completed!",
						"No further assessment for current selected exercise.");
			}
		});
	}

	@Override
	public void setFocus() {
	}

	public void createCourseListForAssessmentTabItem(Combo courseCombo, Combo exerciseCombo) {
		this.viewController.getCourses().forEach(course -> {
			courseCombo.add(course.getTitle());
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
