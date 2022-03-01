package edu.kit.kastel.eclipse.student.view.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import edu.kit.kastel.eclipse.student.view.controllers.StudentViewController;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

public class ExamTab implements ArtemisStudentTab {
	private StudentViewController viewController;

	private ScrolledComposite scrolledCompositeExam;
	private Composite examContainerComposite;
	private Composite examContentComposite;

	private Label resultScore;
	private Label lblExamDescription;
	private Label lblExamShortName;
	private Label lblLink;
	private Button btnStart;

	private IExam exam;
	private Composite composite_1;

	public ExamTab(StudentViewController viewController) {
		this.viewController = viewController;
	}

	@Override
	public void create(TabFolder tabFolder) {
		TabItem gradingTabItem = new TabItem(tabFolder, SWT.NONE);
		gradingTabItem.setText("Exam");

		this.scrolledCompositeExam = new ScrolledComposite(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		gradingTabItem.setControl(this.scrolledCompositeExam);
		scrolledCompositeExam.setLayout(new FillLayout());
		this.scrolledCompositeExam.setExpandHorizontal(true);
		this.scrolledCompositeExam.setExpandVertical(true);

		this.examContainerComposite = new Composite(this.scrolledCompositeExam, SWT.NONE);
		this.scrolledCompositeExam.setContent(this.examContainerComposite);
		examContainerComposite.setSize(scrolledCompositeExam.getSize());
		this.scrolledCompositeExam.setMinSize(this.examContainerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		examContainerComposite.setLayout(new GridLayout(1, true));
		examContainerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite composite = new Composite(examContainerComposite, SWT.NONE);
		GridLayout gl_composite = new GridLayout(2, true);
		gl_composite.marginHeight = 0;
		gl_composite.verticalSpacing = 0;
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		Label labelFeedback = new Label(composite, SWT.NONE);
		labelFeedback.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));
		labelFeedback.setFont(SWTResourceManager.getFont("Segoe UI", 18, SWT.BOLD));
		labelFeedback.setText("Exam");

		btnStart = new Button(composite, SWT.CENTER);
		GridData gd_btnReload = new GridData(SWT.RIGHT, SWT.FILL, true, false, 1, 1);
		gd_btnReload.heightHint = 20;
		gd_btnReload.widthHint = 80;
		btnStart.setLayoutData(gd_btnReload);
		btnStart.setText("Start");
		addSelectionListenerForStartButton(btnStart);

		composite_1 = new Composite(examContainerComposite, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		composite_1.setLayout(new GridLayout(1, false));

		Label labelResult = new Label(composite_1, SWT.NONE);
		labelResult.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		labelResult.setText(Messages.ExamTab_REMEMBER);

		lblLink = new Label(composite_1, SWT.NONE);
		lblLink.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblLink.setText(getLink());

		this.examContentComposite = new Composite(examContainerComposite, SWT.NONE);
		examContentComposite.setTouchEnabled(true);
		examContentComposite.setLayout(new GridLayout(1, true));
		examContentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		examContentComposite.setVisible(false);
		Composite resultContentComposite = new Composite(examContentComposite, SWT.BORDER);
		resultContentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		resultContentComposite.setLayout(new GridLayout(1, false));

		lblExamShortName = new Label(resultContentComposite, SWT.NONE);
		lblExamShortName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		lblExamShortName.setText("Name");
		lblExamShortName.setTouchEnabled(true);
		lblExamShortName.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));

		lblExamDescription = new Label(resultContentComposite, SWT.NONE);
		lblExamDescription.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 1));

		Label separator = new Label(resultContentComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

		resultScore = new Label(resultContentComposite, SWT.RIGHT);
		resultScore.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true, 1, 1));
		resultScore.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD | SWT.ITALIC));
		resultScore.setText("Due to: 0 / 20");

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
			resultScore.setText(Messages.ExamTab_END + exam.getEndDate());
			lblExamDescription.setText(Messages.ExamTab_START + exam.getStartDate());
			btnStart.setEnabled(!exam.isStarted());
			lblLink.setText(getLink());
			examContentComposite.setVisible(true);
			examContentComposite.pack();
		}
	}

	private void setExam() {
		exam = viewController.getCurrentlySelectedExam();
		setExamDataToUI();
	}

	@Override
	public void reset() {
		examContentComposite.setVisible(false);
	}

	@Override
	public void callExamEvent() {
		setExam();
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private void createView(Composite parent) {
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
		create(tabFolder);
	}

	@Override
	public void callExercisesEvent() {
		setExam();
	}

	private String getLink() {
		return this.viewController.getExamUrlForCurrentExam();
	}

	@Override
	public void setViewController(StudentViewController viewController) {
		this.viewController = viewController;
	}
}
