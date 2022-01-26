package edu.kit.kastel.eclipse.student.view.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Rectangle;
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
import org.eclipse.swt.layout.RowLayout;

public class ResultTab implements ArtemisStudentTab, WebsocketCallback{
	private static final String RELOAD_BTN_TEXT= "Reload";
	private static final String LOAD_BTN_TEXT = "Loading...";
	
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
	private Button btnReload;
	
	
	public ResultTab(StudentViewController viewController) {
		this.viewController = viewController;
	}
	
	@Override
	public void create(TabFolder tabFolder) {
		display = tabFolder.getDisplay();
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
		GridLayout gl_composite = new GridLayout(2, true);
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		Label labelFeedback = new Label(composite, SWT.NONE);
		labelFeedback.setFont(SWTResourceManager.getFont("Segoe UI", 18, SWT.BOLD));
		labelFeedback.setText("Feedback      ");

		btnReload = new Button(composite, SWT.CENTER);
		btnReload.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnReload.setText("Reload");
		addSelectionListenerForReloadButton(btnReload);

		Label labelResult = new Label(feedbackContainerComposite, SWT.NONE);
		labelResult.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		labelResult.setText("  Summary of all executed tests for the selected exercise");

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
	
	@Override
	public void reset() {
		feedbackContentComposite.setVisible(false);	
	}

	@Override
	public void callEvent() {
		getFeedbackForExcerise();
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
		GridData gd_feedbackTabel = new GridData(SWT.LEFT, SWT.FILL, true, true);
		gd_feedbackTabel.widthHint = 500;
		feedbackTabel.setLayoutData(gd_feedbackTabel);
		final Rectangle clientArea = parent.getClientArea();
		feedbackTabel.setBounds(clientArea.x, clientArea.y, 500, 500);
		String[] colNames = { "Name", "Credits", "Success", "Detailed Text" };

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
		String checkSign = "";
		try {
			checkSign = new String(new byte[] { (byte) 0xE2, (byte) 0xE2}, "utf-8");
		} catch (UnsupportedEncodingException e) {
			checkSign = "Y";
		}
		
		if (entries != null) {
			sortEntries(entries);
			for (var feedback : entries) {
				final TableItem item = new TableItem(table, SWT.NULL);
				item.setText(0, feedback.getText());
				item.setText(1, "" + feedback.getCredits());
				item.setText(2, feedback.getPositive() ? "successful" : "failed");
				item.setForeground(2, feedback.getPositive() ? display.getSystemColor(SWT.COLOR_GREEN)
						: display.getSystemColor(SWT.COLOR_RED));
				item.setText(3, (feedback.getDetailText() != null) ? checkSign : "X");
				item.setForeground(3, (feedback.getDetailText() != null)? display.getSystemColor(SWT.COLOR_GREEN)
						: display.getSystemColor(SWT.COLOR_RED));
			}
		}

	}
	
	private void sortEntries(final List<Feedback> feedbacks) {
		//TODO: maybe add different sorting
		Collections.sort(feedbacks, new Comparator<Feedback>() {
			@Override
			public int compare(Feedback o1, Feedback o2) {
				return o1.getText().compareToIgnoreCase(o2.getText());
			}
		});
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
	
	/**
	 * @wbp.parser.entryPoint
	 */
	private void createView(Composite parent) {
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
		create(tabFolder);
	}

	@Override
	public void handleFrame(Object payload) {
		this.btnReload.setEnabled(false);
		this.btnReload.setText(LOAD_BTN_TEXT);
	}

	@Override
	public void handleFrame(ResultsDTO result) {
		this.btnReload.setEnabled(true);
		this.btnReload.setText(RELOAD_BTN_TEXT);
		handleNewResult(result, Arrays.asList(result.feedbacks));
	}

	@Override
	public void handleException(Throwable e) {
		handleWebsocketError();
		e.printStackTrace();
	}

	@Override
	public void handleTransportError(Throwable e) {
		handleWebsocketError();
		e.printStackTrace();
	}
	
	private void handleWebsocketError() {
		this.btnReload.setEnabled(false);
		this.btnReload.setText("ERROR");
	}
}
