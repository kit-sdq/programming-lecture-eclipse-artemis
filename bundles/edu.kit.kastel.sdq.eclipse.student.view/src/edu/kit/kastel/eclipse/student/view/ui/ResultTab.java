package edu.kit.kastel.eclipse.student.view.ui;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
import org.eclipse.wb.swt.SWTResourceManager;

import edu.kit.kastel.eclipse.student.view.controllers.StudentViewController;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ResultsDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.client.websocket.WebsocketCallback;
import org.eclipse.swt.graphics.Point;

public class ResultTab implements ArtemisStudentTab, WebsocketCallback {
	private static final String RELOAD_BTN_TEXT = "Reload";
	private static final String LOAD_BTN_TEXT = "Loading...";
	private static final int ROUND_DECIMAL_PLACES = 2;
	private static final String CHECK_MARK_IN_UTF8 = new String(new byte[] { (byte) 0xE2, (byte) 0x9C, (byte) 0x93 }, StandardCharsets.UTF_8);

	private Display display;
	private StudentViewController viewController;

	private ScrolledComposite scrolledCompositeFeedback;
	private Composite feedbackContainerComposite;
	private Composite feedbackContentComposite;
	private ResultsDTO lastResult;
	private List<Feedback> feedbackOfLastResult = new ArrayList<>();
	private Table feedbackTabel;

	private Label resultScore;
	private Label btnResultSuccessfull;
	private Label lblResultExerciseDescription;
	private Label lblResultExerciseShortName;
	private Label lblPoints;
	private Button btnReload;
	private Label btnLoading;

	public ResultTab(final StudentViewController viewController) {
		this.viewController = viewController;
	}

	@Override
	public void create(TabFolder tabFolder) {
		display = tabFolder.getDisplay();
		TabItem tbtmResult = new TabItem(tabFolder, SWT.NONE);
		tbtmResult.setText("Test Results");

		this.scrolledCompositeFeedback = new ScrolledComposite(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmResult.setControl(this.scrolledCompositeFeedback);
		scrolledCompositeFeedback.setLayout(new FillLayout());
		this.scrolledCompositeFeedback.setExpandHorizontal(true);
		this.scrolledCompositeFeedback.setExpandVertical(true);

		this.feedbackContainerComposite = new Composite(this.scrolledCompositeFeedback, SWT.NONE);
		this.scrolledCompositeFeedback.setContent(this.feedbackContainerComposite);
		feedbackContainerComposite.setSize(scrolledCompositeFeedback.getSize());
		this.scrolledCompositeFeedback.setMinSize(this.feedbackContainerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		feedbackContainerComposite.setLayout(new GridLayout(1, true));
		feedbackContainerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite composite = new Composite(feedbackContainerComposite, SWT.NONE);
		GridLayout gl_composite = new GridLayout(2, true);
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		Label labelFeedback = new Label(composite, SWT.NONE);
		labelFeedback.setFont(SWTResourceManager.getFont("Segoe UI", 18, SWT.BOLD));
		labelFeedback.setText("Results");

		Composite composite_1 = new Composite(composite, SWT.NONE);
		GridLayout gl_composite_1 = new GridLayout(2, true);
		gl_composite_1.verticalSpacing = 0;
		gl_composite_1.marginWidth = 0;
		composite_1.setLayout(gl_composite_1);
		GridData gd_composite_1 = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_composite_1.widthHint = 238;
		composite_1.setLayoutData(gd_composite_1);

		btnLoading = new Label(composite_1, SWT.SHADOW_IN | SWT.CENTER);
		GridData gd_btnRLoading = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_btnRLoading.widthHint = 80;
		btnLoading.setLayoutData(gd_btnRLoading);
		btnLoading.setText(LOAD_BTN_TEXT);
		btnLoading.setVisible(false);

		btnReload = new Button(composite_1, SWT.CENTER);
		GridData gd_btnReload = new GridData(SWT.RIGHT, SWT.FILL, true, false, 1, 1);
		gd_btnReload.widthHint = 90;
		gd_btnReload.horizontalIndent = 5;
		btnReload.setLayoutData(gd_btnReload);
		btnReload.setText(RELOAD_BTN_TEXT);
		addSelectionListenerForReloadButton(btnReload);

		Label labelResult = new Label(feedbackContainerComposite, SWT.NONE);
		labelResult.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		labelResult.setText(Messages.ResultTab_INFO_FEEDBACK);

		this.feedbackContentComposite = new Composite(feedbackContainerComposite, SWT.NONE);
		feedbackContentComposite.setTouchEnabled(true);
		feedbackContentComposite.setLayout(new GridLayout(1, true));
		feedbackContentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		feedbackContentComposite.setVisible(false);
		Composite resultContentComposite = new Composite(feedbackContentComposite, SWT.BORDER);
		resultContentComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		resultContentComposite.setLayout(new GridLayout(1, false));

		Composite composite_2 = new Composite(resultContentComposite, SWT.NONE);
		composite_2.setLayout(new GridLayout(2, false));
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		lblResultExerciseShortName = new Label(composite_2, SWT.NONE);
		GridData gd_lblResultExerciseShortName = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
		gd_lblResultExerciseShortName.widthHint = 288;
		lblResultExerciseShortName.setLayoutData(gd_lblResultExerciseShortName);
		lblResultExerciseShortName.setText("Name");
		lblResultExerciseShortName.setTouchEnabled(true);
		lblResultExerciseShortName.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));

		btnResultSuccessfull = new Label(composite_2, SWT.RIGHT);
		btnResultSuccessfull.setForeground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
		btnResultSuccessfull.setBounds(360, 9, 123, 28);
		btnResultSuccessfull.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		btnResultSuccessfull.setText("Successful");

		lblResultExerciseDescription = new Label(resultContentComposite, SWT.NONE);
		lblResultExerciseDescription.setText("22.03.2022");
		GridData gd_lblResultExerciseDescription = new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 1);
		gd_lblResultExerciseDescription.widthHint = 398;
		gd_lblResultExerciseDescription.horizontalIndent = 5;
		lblResultExerciseDescription.setLayoutData(gd_lblResultExerciseDescription);

		Label separator = new Label(resultContentComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

		Composite composite_3 = new Composite(resultContentComposite, SWT.NONE);
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		composite_3.setLayout(new GridLayout(2, false));

		lblPoints = new Label(composite_3, SWT.NONE);
		GridData gd_lblPoints = new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1);
		gd_lblPoints.widthHint = 200;
		lblPoints.setLayoutData(gd_lblPoints);
		lblPoints.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD | SWT.ITALIC));
		lblPoints.setBounds(22, 78, 186, 30);
		lblPoints.setText("70%");

		resultScore = new Label(composite_3, SWT.RIGHT);
		GridData gd_resultScore = new GridData(SWT.RIGHT, SWT.CENTER, true, true, 1, 1);
		gd_resultScore.widthHint = 195;
		resultScore.setLayoutData(gd_resultScore);
		resultScore.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD | SWT.ITALIC));
		resultScore.setText("0 / 20");

		Label labelFeedback2 = new Label(feedbackContentComposite, SWT.NONE);
		labelFeedback2.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		labelFeedback2.setText(Messages.ResultTab_INFO_RESULT);
		createTableForFeedback(feedbackContentComposite);

		scrolledCompositeFeedback.setContent(feedbackContainerComposite);
		scrolledCompositeFeedback.setMinSize(new Point(64, 64));
	}

	@Override
	public void reset() {
		feedbackContentComposite.setVisible(false);
		btnLoading.setText(LOAD_BTN_TEXT);
		btnLoading.setVisible(false);
	}

	private void addSelectionListenerForReloadButton(Button btn) {
		btn.addListener(SWT.Selection, e -> {
			getFeedbackForExcerise();
		});
	}

	private void createTableForFeedback(Composite parent) {
		feedbackTabel = new Table(parent, SWT.BORDER | SWT.V_SCROLL);
		feedbackTabel.setToolTipText("Feedbacks for Excerise");
		feedbackTabel.setLinesVisible(true);
		feedbackTabel.setHeaderVisible(true);
		feedbackTabel.setLayout(new GridLayout(1, true));
		feedbackTabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		String[] colNames = { "Name", "Credits", "Success", "Detailed Text" }; //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		int[] width = { 200, 100, 100, 100 };

		for (int loopIndex = 0; loopIndex < colNames.length; loopIndex++) {
			final TableColumn column = new TableColumn(feedbackTabel, SWT.NULL);
			column.setWidth(width[loopIndex]);
			column.setText(colNames[loopIndex]);
		}

		feedbackTabel.addListener(SWT.Selection, e -> {
			handleResultTableEvent(e);
		});
	}

	private void handleResultTableEvent(Event e) {
		TableItem item = (TableItem) e.item;
		int index = feedbackTabel.indexOf(item);
		Feedback selectedFeedback = feedbackOfLastResult.get(index);
		if (selectedFeedback == null) {
			return;
		}
		Shell s = new Shell(display);
		s.setMinimumSize(500, 500);
		if (selectedFeedback.getDetailText() != null)
			MessageDialog.openInformation(s, selectedFeedback.getText(), selectedFeedback.getDetailText());
	}

	private void addResultToTab(ResultsDTO result, IExercise exercise) {
		Display display = Display.getDefault();
		if (result != null) {
			this.btnResultSuccessfull.setForeground((result.successful ? display.getSystemColor(SWT.COLOR_GREEN) : display.getSystemColor(SWT.COLOR_RED)));
			btnResultSuccessfull.setText(result.successful ? "success" : "failed"); //$NON-NLS-2$

			if (exercise != null) {
				lblResultExerciseShortName.setText(exercise.getTitle());
				lblResultExerciseDescription.setText(result.completionDate);
				resultScore.setText(result.resultString);
				lblPoints.setText("Points: " + result.score + "%"); //$NON-NLS-2$
			} else {
				resultScore.setText(Integer.toString(result.score));
			}
		}
	}

	private void addFeedbackToTable(Table table, List<Feedback> entries) {
		Display display = getDisplay();

		if (entries != null) {
			Collections.sort(entries);
			for (var feedback : entries) {
				double roundedCredits = roundToDeciamlPlaces(feedback.getCredits());
				final TableItem item = new TableItem(table, SWT.NULL);
				item.setText(0, feedback.getText());
				item.setText(1, "" + roundedCredits);
				item.setText(2, feedback.getPositive() ? "successful" : "failed"); //$NON-NLS-2$
				item.setForeground(2, feedback.getPositive() ? display.getSystemColor(SWT.COLOR_GREEN) : display.getSystemColor(SWT.COLOR_RED));
				item.setText(3, (feedback.getDetailText() != null) ? CHECK_MARK_IN_UTF8 : "X");
			}
		}
	}

	private Display getDisplay() {
		if (display == null) {
			return Display.getDefault();
		}
		return display;
	}

	private double roundToDeciamlPlaces(Double credits) {
		if (credits == null) {
			return 0.0;
		}

		BigDecimal bd = new BigDecimal(credits).setScale(ROUND_DECIMAL_PLACES, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	private void getFeedbackForExcerise() {
		var resultFeedbackMap = this.viewController.getFeedbackExcerise();

		if (!resultFeedbackMap.isEmpty()) {
			Entry<ResultsDTO, List<Feedback>> entry = resultFeedbackMap.entrySet().iterator().next();
			handleNewResult(entry.getKey(), entry.getValue());
		} else {
			feedbackTabel.removeAll();
			reset();
		}
	}

	private void handleNewResult(ResultsDTO result, List<Feedback> feedbacks) {
		IExercise exercise = this.viewController.getCurrentSelectedExercise();

		this.lastResult = result;
		this.feedbackOfLastResult = feedbacks;

		feedbackTabel.removeAll();
		addFeedbackToTable(feedbackTabel, feedbackOfLastResult);
		addResultToTab(lastResult, exercise);
		feedbackContainerComposite.pack();
		feedbackContentComposite.setVisible(true);

	}

	@Override
	public void handleSubmission(Object payload) {
		if (display != null) {
			this.display.syncExec(() -> {
				this.btnLoading.setVisible(true);
			});
		}
	}

	@Override
	public void handleResult(Object payload) {
		if (display != null) {
			this.display.syncExec(() -> {
				this.btnLoading.setVisible(false);
				this.getFeedbackForExcerise();
			});
		}
	}

	@Override
	public void handleException(Throwable e) {
		if (display != null) {
			this.display.syncExec(() -> {
				handleWebsocketError();
				e.printStackTrace();
			});
		}

	}

	@Override
	public void handleTransportError(Throwable e) {
		if (display != null) {
			this.display.syncExec(() -> {
				handleWebsocketError();
				e.printStackTrace();
			});
		}
	}

	private void handleWebsocketError() {
		this.btnLoading.setVisible(true);
		this.btnLoading.setText("ERROR");
	}

	@Override
	public void callExercisesEvent() {
		getFeedbackForExcerise();

	}

	@Override
	public void callExamEvent() {
		reset();
	}

	@Override
	public void setViewController(StudentViewController viewController) {
		this.viewController = viewController;
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private void createView(Composite parent) {
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
		create(tabFolder);
	}
}
