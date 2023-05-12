/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.grading.view.assessment;

import static edu.kit.kastel.eclipse.common.view.languages.LanguageSettings.I18N;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;

import edu.kit.kastel.eclipse.common.api.PreferenceConstants;
import edu.kit.kastel.eclipse.common.api.controller.IGradingSystemwideController;
import edu.kit.kastel.eclipse.common.api.model.IMistakeType;
import edu.kit.kastel.eclipse.common.api.model.IRatingGroup;
import edu.kit.kastel.eclipse.common.view.activator.CommonActivator;
import edu.kit.kastel.eclipse.common.view.marker.AssessmentMarkerView;
import edu.kit.kastel.eclipse.common.view.utilities.AssessmentUtilities;
import edu.kit.kastel.eclipse.common.view.utilities.JDTUtilities;
import edu.kit.kastel.eclipse.common.view.utilities.UIUtilities;
import edu.kit.kastel.eclipse.grading.view.activator.Activator;
import edu.kit.kastel.eclipse.grading.view.commands.AddAnnotationCommandHandler;
import edu.kit.kastel.eclipse.grading.view.commands.DeleteAnnotationCommandHandler;
import edu.kit.kastel.eclipse.grading.view.controllers.AssessmentViewController;
import edu.kit.kastel.eclipse.grading.view.listeners.AssessmentMarkerViewDoubleClickListener;
import edu.kit.kastel.eclipse.grading.view.listeners.KeyboardAwareMouseListener;

/**
 * This class creates the view elements for the artemis grading process. It is
 * build as a tab folder with with three tabs: assessment (incl. backlog),
 * grading, and tests.
 *
 * @see {@link ViewPart}
 *
 */
public class ArtemisGradingView extends ViewPart {
	private static final String ADD_ANNOTATION_COMMAND = "edu.kit.kastel.eclipse.grading.assessment.keybindings.addAnnotation";
	private static final String DELETE_ANNOTATION_COMMAND = "edu.kit.kastel.eclipse.grading.assessment.keybindings.deleteAnnotation";

	private static final ILog LOG = Platform.getLog(ArtemisGradingView.class);

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
		Activator.getDefault().getSystemwideController().addSubmissionBuildListener(this::openPackagesAndFiles);
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);

		// Set the command handlers manually to be able to inject the view controller
		ICommandService commandService = getSite().getService(ICommandService.class);

		var addAnnotationCommand = commandService.getCommand(ADD_ANNOTATION_COMMAND);
		addAnnotationCommand.setHandler(new AddAnnotationCommandHandler(this, this.viewController));

		var deleteAnnotationCommand = commandService.getCommand(DELETE_ANNOTATION_COMMAND);
		deleteAnnotationCommand.setHandler(new DeleteAnnotationCommandHandler(this, this.viewController));
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

	private void addSelectionListenerForRefreshButton(Button refreshButton, Combo backlogCombo) {
		refreshButton.addListener(SWT.Selection, e -> this.fillBacklogComboWithData(backlogCombo));
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

	private void addSelectionListenerForRerunAutograder(Button btnRerunAutograder) {
		btnRerunAutograder.addListener(SWT.Selection, e -> {
			boolean userWantsRerun = MessageDialog.openConfirm(AssessmentUtilities.getWindowsShell(), "Rerun Autograder?",
					"This action may create duplicate annotations! Are you sure that this is what you want?");
			if (userWantsRerun) {
				AutograderUtil.runAutograder(this.viewController.getAssessmentController(),
						Activator.getDefault().getSystemwideController().getCurrentProjectPath().resolve("assignment").resolve("src"),
						success -> this.updatePenalties(), true);
			}
		});
	}

	private void createBacklog() {
		var backlogCombo = assessmentTab.comboBacklogSubmission;
		var refreshButton = assessmentTab.btnBacklogRefreshSubmissions;
		var btnLoadAgain = assessmentTab.btnBacklogLoadSubmission;

		this.addSelectionListenerForRefreshButton(refreshButton, backlogCombo);
		this.addSelectionListenerForLoadFromBacklogButton(backlogCombo, btnLoadAgain);
	}

	private void createResultTab(TabFolder tabFolder) {
		this.result = new ResultTab(Activator.getDefault().getSystemwideController(), tabFolder);
	}

	private void createCustomButton(IRatingGroup ratingGroup, Group rgDisplay, IMistakeType mistake) {
		final Button customButton = new Button(rgDisplay, SWT.PUSH);
		customButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		customButton.setText(mistake.getButtonText(I18N().key()));
		customButton.addListener(SWT.Selection, event -> {
			final CustomButtonDialog customDialog = new CustomButtonDialog(AssessmentUtilities.getWindowsShell(), isPositiveFeedbackAllowed(),
					this.viewController, mistake);
			customDialog.setBlockOnOpen(true);
			customDialog.open();
			// avoid SWT Exception
			Display.getDefault().asyncExec(() -> this.updatePenalty(ratingGroup.getIdentifier()));
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
			this.viewController.setExerciseId(examExerciseCombo.getItem(examExerciseCombo.getSelectionIndex()));
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
		this.addSelectionListenerForCloseAssessmentButton(this.assessmentTab.btnCloseAssessment);
		this.addSelectionListenerForRerunAutograder(this.assessmentTab.btnRerunAutograder);
		this.addSelectionListenerForRefreshArtemisStateButton(this.assessmentTab.btnResetPluginState);

		setVersionText(this.assessmentTab.lblPluginVersion);
		this.assessmentTab.btnHelp.addListener(SWT.Selection, e -> {
		});
	}

	private void setVersionText(Label label) {
		var pluginVersion = Activator.getDefault().getBundle().getVersion();
		String versionText = String.format("Artemis Grading %d.%d.%d", pluginVersion.getMajor(), pluginVersion.getMinor(), pluginVersion.getMicro());
		label.setText(versionText);
	}

	private void addSelectionListenerForRefreshArtemisStateButton(Button btnRefreshArtemisState) {
		btnRefreshArtemisState.addListener(SWT.Selection, e -> this.refreshArtemisState());
	}

	private void addSelectionListenerForCloseAssessmentButton(Button btnCloseAssessment) {
		btnCloseAssessment.addListener(SWT.Selection, e -> {
			this.viewController.onCloseAssessment();
			this.updateState();
			this.result.reset();
		});
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
			this.ratingGroupViewElements.put(ratingGroup.getIdentifier(), rgDisplay);
			this.updatePenalty(ratingGroup.getIdentifier());
			var columns = CommonActivator.getDefault().getPreferenceStore().getInt(PreferenceConstants.GRADING_VIEW_BUTTONS_IN_COLUMN);
			final GridLayout gridLayout = new GridLayout(columns, true);
			rgDisplay.setLayout(gridLayout);
			final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
			rgDisplay.setLayoutData(gridData);
			this.viewController.getMistakeTypes().forEach(mistake -> {
				// TODO Check
				if (mistake.getRatingGroup().equals(ratingGroup)) {
					if (mistake.isCustomPenalty()) {
						this.createCustomButton(ratingGroup, rgDisplay, mistake);
						return;
					}
					final Button mistakeButton = new Button(rgDisplay, SWT.PUSH);
					mistakeButton.setText(mistake.getButtonText(I18N().key()));
					mistakeButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
					mistakeButton.setEnabled(mistake.isEnabledMistakeType());
					if (!mistake.isEnabledPenalty() && mistake.isEnabledMistakeType()) {
						mistakeButton.addPaintListener(e -> mistakeButton.setForeground(SWTResourceManager.getColor(133, 153, 0))); // solarized green
					}

					this.mistakeButtons.put(mistake.getIdentifier(), mistakeButton);
					mistakeButton.setToolTipText(this.viewController.getToolTipForMistakeType(I18N().key(), mistake));

					KeyboardAwareMouseListener listener = new KeyboardAwareMouseListener();
					// Normal click
					listener.setClickHandler(
							() -> AssessmentUtilities.createAssessmentAnnotation(this.viewController.getAssessmentController(), mistake, null, null),
							SWT.BUTTON1);
					// shift-click and middle-click
					listener.setClickHandler(() -> this.createMistakePenaltyWithCustomMessageDialog(mistake), SWT.SHIFT, SWT.BUTTON2);
					// every click
					listener.setClickHandlerForEveryClick(() -> {
						this.updatePenalty(mistake.getRatingGroup().getIdentifier());
						this.updateMistakeButtonToolTips(mistake);
					});
					mistakeButton.addMouseListener(listener);
				}
			});
			rgDisplay.setSize(rgDisplay.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			rgDisplay.layout();
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
		CustomButtonDialog buttonDialog = new CustomButtonDialog(AssessmentUtilities.getWindowsShell(), isPositiveFeedbackAllowed(), this.viewController, null);
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

	private void fillBacklogComboWithData(Combo backlogCombo) {
		backlogCombo.removeAll();
		this.viewController.getSubmissionsForBacklog().forEach(backlogCombo::add);
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
		this.viewController.getRatingGroups().forEach(ratingGroup -> this.updatePenalty(ratingGroup.getIdentifier()));
		this.result.loadFeedbackForExcerise();
		AutograderUtil.runAutograder(this.viewController.getAssessmentController(),
				Activator.getDefault().getSystemwideController().getCurrentProjectPath().resolve("assignment").resolve("src"),
				success -> this.updatePenalties(), false);
	}

	@Override
	public void setFocus() {
		// NOP
	}

	private void updateMistakeButtonToolTips(IMistakeType mistakeType) {
		Button button = this.mistakeButtons.get(mistakeType.getIdentifier());
		if (button != null) {
			Display.getDefault().asyncExec( //
					() -> button.setToolTipText(this.viewController.getToolTipForMistakeType(I18N().key(), mistakeType)) //
			);
		}
	}

	public void updatePenalties() {
		this.viewController.getRatingGroups().forEach(ratingGroup -> this.updatePenalty(ratingGroup.getIdentifier()));
		this.updateAllToolTips();
	}

	private void updateAllToolTips() {
		List<IMistakeType> mistakes = this.viewController.getMistakeTypes();
		for (IMistakeType mistake : mistakes) {
			this.updateMistakeButtonToolTips(mistake);
		}
	}

	private void updatePenalty(String ratingGroupId) {
		Group viewElement = this.ratingGroupViewElements.get(ratingGroupId);
		IRatingGroup ratingGroup = this.viewController.getRatingGroupById(ratingGroupId);
		if (ratingGroup == null) {
			return;
		}
		StringBuilder builder = new StringBuilder(ratingGroup.getDisplayName(I18N().key()));
		builder.append("(");
		builder.append(this.viewController.getAssessmentController().getCurrentPenaltyForRatingGroup(ratingGroup));
		var range = ratingGroup.getRange();
		if (!range.isEmpty()) {
			double lower = range.first() == null ? Double.NEGATIVE_INFINITY : range.first();
			double upper = range.second() == null ? Double.POSITIVE_INFINITY : range.second();
			builder.append(" in [").append(lower).append(",").append(upper).append("]");
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
			var stats = sc.getStats();
			this.assessmentTab.lblStatisticsInformation.setText(
					I18N().tabAssessmentStartedSubmitted(stats.totalAssessments(), stats.totalSubmissions(), stats.locked(), stats.submittedByTutor()));
		} else {
			this.assessmentTab.lblStatisticsInformation.setText("");
		}
	}

	private void refreshArtemisState() {
		this.viewController = new AssessmentViewController();
		Activator.getDefault().getSystemwideController().addSubmissionBuildListener(this::openPackagesAndFiles);
		this.result.setController(Activator.getDefault().getSystemwideController());
		this.result.reset();
		this.resetCombos();
		this.updateState();
	}

	private void resetCombos() {
		this.assessmentTab.resetCombos();
		this.viewController.getCourseShortNames().forEach(courseShortName -> this.assessmentTab.comboCourse.add(courseShortName));
	}

	private void openPackagesAndFiles(IProject project) {
		var page = ArtemisGradingView.this.getSite().getPage();
		try {
			var explorer = AssessmentUtilities.getProjectExplorer(page);

			// Expand all packages
			var packagePaths = JDTUtilities.getAllCompilationUnits(project).stream().map(ICompilationUnit::getResource).toList();
			Display.getDefault().asyncExec(() -> {
				// Select all packages to reveal them...
				explorer.ifPresent(e -> e.selectReveal(new StructuredSelection(packagePaths)));
				// ... and deselect them once they are expanded
				explorer.ifPresent(e -> e.selectReveal(new StructuredSelection()));
			});

			String openPreference = CommonActivator.getDefault().getPreferenceStore().getString(PreferenceConstants.OPEN_FILES_ON_ASSESSMENT_START);

			// Open all types if desired
			if (PreferenceConstants.OPEN_FILES_ON_ASSESSMENT_START_ALL.equals(openPreference)) {
				JDTUtilities.getAllCompilationUnits(project).forEach(c -> AssessmentUtilities.openJavaElement(c, page));
			}

			// Open/focus the main class
			if (!PreferenceConstants.OPEN_FILES_ON_ASSESSMENT_START_NONE.equals(openPreference)) {
				var mainType = JDTUtilities.findMainClass(project);
				if (mainType.isPresent()) {
					// Open/focus the main class in the editor...
					AssessmentUtilities.openJavaElement(mainType.get(), page);

					// ... and focus it in the package explorer
					Display.getDefault().asyncExec(() -> {
						explorer.ifPresent(e -> e.selectReveal(new StructuredSelection(mainType.get().getResource())));
					});
				} else {
					LOG.warn("No main class found");
				}
			}
		} catch (JavaModelException e) {
			LOG.error("JDT failure", e);
		}
	}

	public boolean isPositiveFeedbackAllowed() {
		return this.viewController.getAssessmentController().isPositiveFeedbackAllowed();
	}
}
