package edu.kit.kastel.eclipse.student.view.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;

import edu.kit.kastel.eclipse.student.view.controllers.StudentViewController;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ResultsDTO;

import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

public class ExamTab implements ArtemisStudentTab {
	private StudentViewController viewController;

	private ScrolledComposite scrolledCompositeFeedback;
	private Composite feedbackContainerComposite;
	private Composite feedbackContentComposite;

	private Label resultScore;
	private Label btnResultSuccessfull;
	private Label lblResultExerciseDescription;
	private Label lblResultExerciseShortName;
	private Label lblPoints;
	private Button btnReload;
	private Button btnLoading;
	private Composite composite_1;

	private IExam exam;

	public ExamTab(StudentViewController viewController) {
		this.viewController = viewController;
	}

	@Override
	public void create(TabFolder tabFolder) {
		TabItem gradingTabItem = new TabItem(tabFolder, SWT.NONE);
		gradingTabItem.setText("Exam");

		this.scrolledCompositeFeedback = new ScrolledComposite(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		gradingTabItem.setControl(this.scrolledCompositeFeedback);
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
		GridLayout gl_composite = new GridLayout(2, true);
		composite.setLayout(gl_composite);
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_composite.widthHint = 527;
		composite.setLayoutData(gd_composite);

		Label labelFeedback = new Label(composite, SWT.NONE);
		labelFeedback.setFont(SWTResourceManager.getFont("Segoe UI", 18, SWT.BOLD));
		labelFeedback.setText("Exam");

		composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, true));
		GridData gd_composite_1 = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_composite_1.widthHint = 257;
		composite_1.setLayoutData(gd_composite_1);

		btnLoading = new Button(composite_1, SWT.CENTER);
		GridData gd_btnRLoading = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnRLoading.widthHint = 80;
		btnLoading.setLayoutData(gd_btnRLoading);
		btnLoading.setText("Loading...");
		btnLoading.setEnabled(false);
		btnLoading.setVisible(false);

		btnReload = new Button(composite_1, SWT.CENTER);
		GridData gd_btnReload = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnReload.horizontalIndent = 5;
		gd_btnReload.widthHint = 117;
		btnReload.setLayoutData(gd_btnReload);
		btnReload.setText("Start");
		addSelectionListenerForStartButton(btnReload);

		Label labelResult = new Label(feedbackContainerComposite, SWT.NONE);
		labelResult.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		labelResult.setText("  Summary of the currently selected exam.");

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

		Label separator = new Label(resultContentComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setBounds(20, 62, 476, 10);

		lblPoints = new Label(resultContentComposite, SWT.NONE);
		lblPoints.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD | SWT.ITALIC));
		lblPoints.setBounds(22, 78, 186, 30);
		lblPoints.setText("");

		resultScore = new Label(resultContentComposite, SWT.RIGHT);
		resultScore.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD | SWT.ITALIC));
		resultScore.setBounds(30, 78, 453, 30);
		resultScore.setText("0 / 20");

	}

	private void addSelectionListenerForStartButton(Button btn) {
		btn.addListener(SWT.Selection, e -> {
			exam = viewController.startExam();
			setExamDataToUI();
		});
	}

	private void setExamDataToUI() {
		if (exam != null) {
			lblResultExerciseShortName.setText(exam.getTitle());
			resultScore.setText("Due to: " + exam.getEndDate());
			lblResultExerciseDescription.setText("Starts at: " + exam.getStartDate());
			btnReload.setEnabled(!exam.isStarted());
			feedbackContentComposite.setVisible(true);
		}
	}

	private void setExam() {
		exam = viewController.getCurrentlySelectedExam();
		setExamDataToUI();
	}

	@Override
	public void reset() {
		feedbackContentComposite.setVisible(false);
	}

	@Override
	public void callEvent() {
		setExam();
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private void createView(Composite parent) {
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
		create(tabFolder);
	}
}
