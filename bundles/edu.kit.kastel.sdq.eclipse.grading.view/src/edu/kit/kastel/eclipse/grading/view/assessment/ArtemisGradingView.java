/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.grading.view.assessment;

import static edu.kit.kastel.eclipse.common.view.languages.LanguageSettings.I18N;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import edu.kit.kastel.eclipse.common.view.activator.CommonActivator;
import edu.kit.kastel.eclipse.common.view.marker.AssessmentMarkerView;
import edu.kit.kastel.eclipse.common.view.utilities.AssessmentUtilities;
import edu.kit.kastel.eclipse.common.view.utilities.UIUtilities;
import edu.kit.kastel.eclipse.grading.view.activator.Activator;
import edu.kit.kastel.eclipse.grading.view.controllers.AssessmentViewController;
import edu.kit.kastel.eclipse.grading.view.listeners.AssessmentMarkerViewDoubleClickListener;
import edu.kit.kastel.eclipse.grading.view.listeners.KeyboardAwareMouseListener;
import edu.kit.kastel.sdq.eclipse.common.api.PreferenceConstants;
import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.SubmissionFilter;
import edu.kit.kastel.sdq.eclipse.common.api.controller.IGradingSystemwideController;
import edu.kit.kastel.sdq.eclipse.common.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.common.api.model.IRatingGroup;

/**
 * This class creates the view elements for the artemis grading process. It is
 * build as a tab folder with with three tabs: assessment (incl. backlog),
 * grading, and tests.
 *
 * @see {@link ViewPart}
 *
 */
public class ArtemisGradingView extends ViewPart {

	private AssessmentViewController viewController;
	private Map<String, Group> ratingGroupViewElements;
	private Map<String, Button> mistakeButtons;

	private AssessmentTab assessmentTab;

	private GradingTabComposite gradingTabComposite;
	private Composite gradingButtonComposite;

	private ResultTab result;

	public ArtemisGradingView() {
		this.viewController = new AssessmentViewController();
		this.ratingGroupViewElements = new HashMap<>();
		this.mistakeButtons = new HashMap<>();
		this.initializeAnnotationEditing();
		this.addListenerForMarkerDeletion();
	}

	private void addListenerForMarkerDeletion() {
		AssessmentUtilities.getWorkspace()
				.addResourceChangeListener(event -> Arrays.asList(event.findMarkerDeltas(AssessmentUtilities.MARKER_CLASS_NAME, true)).forEach(marker -> {
					// check if marker is deleted
					if (marker.getKind() == 2) {
						this.viewController.deleteAnnotation((String) marker.getAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_ANNOTATION_ID));
						this.updatePenalties();
					}
				}));
	}

	private void initializeAnnotationEditing() {
		IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(AssessmentMarkerView.class.getName());
		if (view instanceof AssessmentMarkerView markerView) {
			markerView.addDoubleClickListener(new AssessmentMarkerViewDoubleClickListener(this));
		}
	}

	private void addSelectionListenerForLoadFromBacklogButton(Combo backlogCombo, Button btnLoadAgain) {
		btnLoadAgain.addListener(SWT.Selection, e -> {
			if (backlogCombo.getSelectionIndex() < 0) {
				return;
			}
			this.viewController.setAssessedSubmission(backlogCombo.getItem(backlogCombo.getSelectionIndex()));
			this.viewController.onLoadAgain();
			this.prepareNewAssessment();
			this.updateState();
		});
	}

	private void addSelectionListenerForRefreshButton(Button refreshButton, Combo backlogCombo, Combo filterCombo) {
		refreshButton.addListener(SWT.Selection, e -> this.fillBacklogComboWithData(backlogCombo, filterCombo));
	}

	private void addSelectionListenerForReloadButton(Button btnReloadA) {
		btnReloadA.addListener(SWT.Selection, e -> {
			this.viewController.onReloadAssessment();
			this.prepareNewAssessment();
			this.updateState();
		});
	}

	private void addSelectionListenerForSaveButton(Button btnSave) {
		btnSave.addListener(SWT.Selection, e -> {
			this.viewController.onSaveAssessment();
			this.updateState();
		});
	}

	private void addSelectionListenerForStartFirstRound(Button btnStartRound1) {
		btnStartRound1.addListener(SWT.Selection, e -> {
			boolean started = this.viewController.onStartCorrectionRound1();
			if (started) {
				this.prepareNewAssessment();
			}
			this.updateState();
		});
	}

	private void addSelectionListenerForStartSecondRound(Button btnStartRound2) {
		btnStartRound2.addListener(SWT.Selection, e -> {
			boolean started = this.viewController.onStartCorrectionRound2();
			if (started) {
				this.prepareNewAssessment();
			}
			this.updateState();
		});
	}

	private void addSelectionListenerForSubmitButton(Button btnSubmit) {
		btnSubmit.addListener(SWT.Selection, e -> {
			this.viewController.onSubmitAssessment();
			this.updateState();
			this.result.reset();
		});
	}

	private void createBacklog() {
		var backlogCombo = assessmentTab.comboBacklogSubmission;
		var filterCombo = assessmentTab.comboBacklogFilter;
		var refreshButton = assessmentTab.btnBacklogRefreshSubmissions;
		var btnLoadAgain = assessmentTab.btnBacklogLoadSubmission;

		for (SubmissionFilter filter : SubmissionFilter.values()) {
			assessmentTab.comboBacklogFilter.add(filter.name());
		}

		this.addSelectionListenerForFilterCombo(backlogCombo, filterCombo);
		this.addSelectionListenerForRefreshButton(refreshButton, backlogCombo, filterCombo);
		this.addSelectionListenerForLoadFromBacklogButton(backlogCombo, btnLoadAgain);
	}

	private void createResultTab(TabFolder tabFolder) {
		this.result = new ResultTab(Activator.getDefault().getSystemwideController(), tabFolder);
	}

	private void addSelectionListenerForFilterCombo(Combo backlogCombo, Combo filterCombo) {
		filterCombo.addListener(SWT.Selection, e -> {
			this.fillBacklogComboWithData(backlogCombo, filterCombo);
		});
	}

	private void createCustomButton(IRatingGroup ratingGroup, Group rgDisplay, IMistakeType mistake) {
		final Button customButton = new Button(rgDisplay, SWT.PUSH);
		customButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		customButton.setText(mistake.getButtonText());
		customButton.addListener(SWT.Selection, event -> {
			final CustomButtonDialog customDialog = new CustomButtonDialog(AssessmentUtilities.getWindowsShell(), this.viewController, mistake);
			customDialog.setBlockOnOpen(true);
			customDialog.open();
			// avoid SWT Exception
			Display.getDefault().asyncExec(() -> this.updatePenalty(ratingGroup.getDisplayName()));
		});
	}

	private void fillExamComboList(Combo courseCombo, Combo examCombo, Combo examExerciseCombo) {
		examCombo.removeAll();
		examExerciseCombo.removeAll();
		this.viewController.getExerciseShortNames(courseCombo.getItem(courseCombo.getSelectionIndex())).forEach(examExerciseCombo::add);
		examCombo.add("None");
		this.viewController.getExamShortNames(courseCombo.getItem(courseCombo.getSelectionIndex())).forEach(examCombo::add);
		examCombo.addListener(SWT.Selection, e -> {
			examExerciseCombo.removeAll();
			if ("None".equals(examCombo.getItem(examCombo.getSelectionIndex()))) {
				this.viewController.getExerciseShortNames(courseCombo.getItem(courseCombo.getSelectionIndex())).forEach(examExerciseCombo::add);
			} else {
				this.viewController.getExercisesShortNamesForExam(examCombo.getItem(examCombo.getSelectionIndex())).forEach(examExerciseCombo::add);
			}
			this.updateState();
		});
		examExerciseCombo.addListener(SWT.Selection, e -> {
			this.viewController.setExerciseID(examExerciseCombo.getItem(examExerciseCombo.getSelectionIndex()));
			this.updateState();
		});
	}

	private void createAssessmentTab(TabFolder tabFolder) {
		this.assessmentTab = new AssessmentTab(tabFolder);
		/*
		 * The exam combo does not really have an influence on the backend state, but
		 * should be disabled after a new assessment is started
		 */
		this.loadExamComboEntries(this.assessmentTab.comboCourse, this.assessmentTab.comboExam, this.assessmentTab.comboExercise);

		this.addSelectionListenerForReloadButton(this.assessmentTab.btnReload);
		this.addSelectionListenerForSaveButton(this.assessmentTab.btnSave);
		this.addSelectionListenerForStartFirstRound(this.assessmentTab.btnStartRoundOne);
		this.addSelectionListenerForStartSecondRound(this.assessmentTab.btnStartRoundTwo);
		this.addSelectionListenerForSubmitButton(this.assessmentTab.btnSubmit);
		this.addSelectionListenerForRefreshArtemisStateButton(this.assessmentTab.btnResetPluginState);

		setVersionText(this.assessmentTab.lblPluginVersion);
	}

	private void setVersionText(Label label) {
		var pluginVersion = Activator.getDefault().getBundle().getVersion();
		String versionText = String.format("Artemis Grading %d.%d.%d", pluginVersion.getMajor(), pluginVersion.getMinor(), pluginVersion.getMicro());
		label.setText(versionText);
	}

	private void addSelectionListenerForRefreshArtemisStateButton(Button btnRefreshArtemisState) {
		btnRefreshArtemisState.addListener(SWT.Selection, e -> this.refreshArtemisState());
	}

	private void fillGradingTab() {
		if (this.gradingButtonComposite != null && !this.gradingButtonComposite.isDisposed()) {
			gradingButtonComposite.dispose();
		}

		var container = this.gradingTabComposite.gradingCompositeContainerScrollable;
		this.gradingButtonComposite = new Composite(container, SWT.NONE);
		this.viewController.setCurrentAssessmentController();
		this.gradingButtonComposite.setLayout(new GridLayout(1, true));
		this.viewController.getRatingGroups().forEach(ratingGroup -> {
			final Group rgDisplay = new Group(this.gradingButtonComposite, SWT.NONE);
			this.ratingGroupViewElements.put(ratingGroup.getDisplayName(), rgDisplay);
			this.updatePenalty(ratingGroup.getDisplayName());
			var columns = CommonActivator.getDefault().getPreferenceStore().getInt(PreferenceConstants.GRADING_VIEW_BUTTONS_IN_COLUMN);
			final GridLayout gridLayout = new GridLayout(columns, true);
			rgDisplay.setLayout(gridLayout);
			final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
			rgDisplay.setLayoutData(gridData);
			this.viewController.getMistakeTypes().forEach(mistake -> {
				if (mistake.getRatingGroup().getDisplayName().equals(ratingGroup.getDisplayName())) {
					if (mistake.isCustomPenalty()) {
						this.createCustomButton(ratingGroup, rgDisplay, mistake);
						return;
					}
					final Button mistakeButton = new Button(rgDisplay, SWT.PUSH);
					mistakeButton.setText(mistake.getButtonText());
					mistakeButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

					this.mistakeButtons.put(mistake.getId(), mistakeButton);
					mistakeButton.setToolTipText(this.viewController.getToolTipForMistakeType(mistake));

					KeyboardAwareMouseListener listener = new KeyboardAwareMouseListener();
					// Normal click
					listener.setClickHandler(
							() -> AssessmentUtilities.createAssessmentAnnotation(this.viewController.getAssessmentController(), mistake, null, null),
							SWT.BUTTON1);
					// shift-click and middle-click
					listener.setClickHandler(() -> this.createMistakePenaltyWithCustomMessageDialog(mistake), SWT.SHIFT, SWT.BUTTON2);
					// every click
					listener.setClickHandlerForEveryClick(() -> {
						this.updatePenalty(mistake.getRatingGroup().getDisplayName());
						this.updateMistakeButtonToolTips(mistake);
					});
					mistakeButton.addMouseListener(listener);
				}
			});
		});

		UIUtilities.initializeTabAfterFilling(container, gradingButtonComposite);
	}

	/**
	 * This methods creates the whole view components.
	 */
	@Override
	public void createPartControl(Composite parent) {
		this.createView(parent);
	}

	private void createMistakePenaltyWithCustomMessageDialog(IMistakeType mistake) {
		CustomButtonDialog buttonDialog = new CustomButtonDialog(AssessmentUtilities.getWindowsShell(), this.viewController, null);
		buttonDialog.setBlockOnOpen(true);
		buttonDialog.open();
		if (buttonDialog.isClosedByOk()) {
			AssessmentUtilities.createAssessmentAnnotation(this.viewController.getAssessmentController(), mistake, buttonDialog.getCustomMessage(), null);
		}
	}

	private void createView(Composite parent) {
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
		this.createAssessmentTab(tabFolder);
		this.createGradingTab(tabFolder);
		this.createResultTab(tabFolder);
		this.createBacklog();
		this.updateState();
	}

	private void createGradingTab(TabFolder tabFolder) {
		this.gradingTabComposite = new GradingTabComposite(tabFolder);
		setVersionText(this.gradingTabComposite.lblPluginVersion);
	}

	private void fillBacklogComboWithData(Combo backlogCombo, Combo filterCombo) {
		backlogCombo.removeAll();
		SubmissionFilter filter = SubmissionFilter.ALL;
		int idx = filterCombo.getSelectionIndex();
		if (idx >= 0) {
			String value = filterCombo.getItem(idx);
			filter = Arrays.stream(SubmissionFilter.values()).filter(f -> f.name().equals(value)).findFirst().orElse(SubmissionFilter.ALL);
		}
		this.viewController.getSubmissionsForBacklog(filter).forEach(backlogCombo::add);
	}

	private void loadExamComboEntries(Combo examCourseCombo, Combo examCombo, Combo examExerciseCombo) {
		this.viewController.getCourseShortNames().forEach(examCourseCombo::add);
		examCourseCombo.addListener(SWT.Selection, e -> {
			this.fillExamComboList(this.assessmentTab.comboCourse, examCombo, examExerciseCombo);
			this.updateState();
		});

	}

	private void prepareNewAssessment() {
		this.fillGradingTab();
		this.viewController.createAnnotationsMarkers();
		this.viewController.getRatingGroups().forEach(ratingGroup -> this.updatePenalty(ratingGroup.getDisplayName()));
		this.result.loadFeedbackForExcerise();
	}

	@Override
	public void setFocus() {
		// NOP
	}

	private void updateMistakeButtonToolTips(IMistakeType mistakeType) {
		Button button = this.mistakeButtons.get(mistakeType.getId());
		if (button != null) {
			Display.getDefault().asyncExec( //
					() -> button.setToolTipText(this.viewController.getToolTipForMistakeType(mistakeType)) //
			);
		}
	}

	public void updatePenalties() {
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
		boolean courseSelected = this.assessmentTab.comboCourse.getSelectionIndex() >= 0;
		boolean examSelected = this.assessmentTab.comboExam.getSelectionIndex() >= 0;
		boolean exerciseSelected = this.assessmentTab.comboExercise.getSelectionIndex() >= 0;

		boolean secondCorrectionRound = courseSelected && examSelected && exerciseSelected;
		if (secondCorrectionRound) {
			var currentExercise = this.viewController.getSelectedExercise();
			secondCorrectionRound &= currentExercise.isPresent();
			secondCorrectionRound &= currentExercise.get().hasSecondCorrectionRound();
			secondCorrectionRound &= currentExercise.get().isSecondCorrectionEnabled();
		}

		boolean assessmentStarted = this.viewController.isAssessmentStarted();

		this.assessmentTab.setAssessmentInProgress(courseSelected, examSelected, exerciseSelected, assessmentStarted, secondCorrectionRound);

		this.updateCorrectedSubmissionCount();
	}

	/**
	 * Updates the text above exam & exercise-selection according to the amount of
	 * assessed submissions (by the current tutor) for the currently selected
	 * exercise (if selected; otherwise just an empty string) Method is triggered by
	 * all invocations of updateState, hence a variety of {@link Transition}s could
	 * trigger a change. (e.g. selecting another exercise, starting an assessment,
	 * submitting an assessment, ...)
	 */
	private void updateCorrectedSubmissionCount() {
		if (this.assessmentTab.comboExercise.getSelectionIndex() != -1) {
			IGradingSystemwideController sc = Activator.getDefault().getSystemwideController();
			this.assessmentTab.lblStatisticsInformation
					.setText(I18N().tabAssessmentStartedSubmitted(sc.getBegunSubmissionsProjectNames(SubmissionFilter.ALL).size(),
							sc.getBegunSubmissionsProjectNames(SubmissionFilter.SAVED_AND_SUBMITTED).size()));
		} else {
			this.assessmentTab.lblStatisticsInformation.setText("");
		}
	}

	private void refreshArtemisState() {
		this.viewController = new AssessmentViewController();
		this.result.setController(Activator.getDefault().getSystemwideController());
		this.result.reset();
		this.resetCombos();
		this.updateState();
	}

	private void resetCombos() {
		this.assessmentTab.resetCombos();
		this.viewController.getCourseShortNames().forEach(courseShortName -> this.assessmentTab.comboCourse.add(courseShortName));
	}
}
