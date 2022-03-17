package edu.kit.kastel.eclipse.student.view.ui;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
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

import edu.kit.kastel.eclipse.common.view.utilities.AssessmentUtilities;
import edu.kit.kastel.eclipse.student.view.controllers.StudentViewController;
import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ResultsDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.client.websocket.WebsocketCallback;

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
	private Composite resultContentComposite;
	private Composite compositeHeader;
	private Composite compositeFooter;
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
		this.display = tabFolder.getDisplay();
		TabItem tbtmResult = new TabItem(tabFolder, SWT.NONE);
		tbtmResult.setText("Test Results");

		this.scrolledCompositeFeedback = new ScrolledComposite(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmResult.setControl(this.scrolledCompositeFeedback);
		this.scrolledCompositeFeedback.setLayout(new FillLayout());
		this.scrolledCompositeFeedback.setExpandHorizontal(true);
		this.scrolledCompositeFeedback.setExpandVertical(true);

		this.feedbackContainerComposite = new Composite(this.scrolledCompositeFeedback, SWT.NONE);
		this.scrolledCompositeFeedback.setContent(this.feedbackContainerComposite);
		this.feedbackContainerComposite.setSize(this.scrolledCompositeFeedback.getSize());
		this.scrolledCompositeFeedback.setMinSize(this.feedbackContainerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.feedbackContainerComposite.setLayout(new GridLayout(1, true));
		this.feedbackContainerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite composite = new Composite(this.feedbackContainerComposite, SWT.NONE);
		GridLayout gl_composite = new GridLayout(2, true);
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		Label labelFeedback = new Label(composite, SWT.NONE);
		FontDescriptor boldDescriptor = FontDescriptor.createFrom(labelFeedback.getFont()).setStyle(SWT.BOLD).setHeight(18);
		Font boldFont = boldDescriptor.createFont(labelFeedback.getDisplay());
		labelFeedback.setFont(boldFont);
		labelFeedback.setText("Latest Results");

		Composite composite_1 = new Composite(composite, SWT.NONE);
		GridLayout gl_composite_1 = new GridLayout(2, true);
		composite_1.setLayout(gl_composite_1);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		this.btnLoading = new Label(composite_1, SWT.SHADOW_IN | SWT.CENTER | SWT.BORDER);
		GridData gd_btnRLoading = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_btnRLoading.widthHint = 80;
		this.btnLoading.setLayoutData(gd_btnRLoading);
		this.btnLoading.setText(LOAD_BTN_TEXT);
		this.btnLoading.setVisible(false);

		this.btnReload = new Button(composite_1, SWT.CENTER);
		this.btnReload.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false, 1, 1));
		this.btnReload.setText(RELOAD_BTN_TEXT);
		this.addSelectionListenerForReloadButton(this.btnReload);

		Label labelResult = new Label(this.feedbackContainerComposite, SWT.NONE);
		labelResult.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		labelResult.setText(Messages.RESULTTAB_INFO_FEEDBACK);

		this.feedbackContentComposite = new Composite(this.feedbackContainerComposite, SWT.NONE);
		this.feedbackContentComposite.setTouchEnabled(true);
		this.feedbackContentComposite.setLayout(new GridLayout(1, true));
		this.feedbackContentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		this.feedbackContentComposite.setVisible(true);
		this.resultContentComposite = new Composite(this.feedbackContentComposite, SWT.BORDER);
		this.resultContentComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		this.resultContentComposite.setLayout(new GridLayout(1, false));

		this.compositeHeader = new Composite(this.resultContentComposite, SWT.NONE);
		this.compositeHeader.setLayout(new GridLayout(2, false));
		this.compositeHeader.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		this.lblResultExerciseShortName = new Label(this.compositeHeader, SWT.NONE);
		this.lblResultExerciseShortName.setText(Messages.ResultTab_lblResultExerciseShortName_text);
		this.lblResultExerciseShortName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		this.lblResultExerciseShortName.setTouchEnabled(true);
		boldDescriptor = FontDescriptor.createFrom(this.lblResultExerciseShortName.getFont()).setStyle(SWT.BOLD).setHeight(12);
		boldFont = boldDescriptor.createFont(this.lblResultExerciseShortName.getDisplay());
		this.lblResultExerciseShortName.setFont(boldFont);

		this.btnResultSuccessfull = new Label(this.compositeHeader, SWT.RIGHT);
		this.btnResultSuccessfull.setForeground(this.display.getSystemColor(SWT.COLOR_GREEN));
		this.btnResultSuccessfull.setBounds(360, 9, 123, 28);
		boldDescriptor = FontDescriptor.createFrom(this.btnResultSuccessfull.getFont()).setStyle(SWT.BOLD).setHeight(12);
		boldFont = boldDescriptor.createFont(this.btnResultSuccessfull.getDisplay());
		this.btnResultSuccessfull.setFont(boldFont);
		this.btnResultSuccessfull.setText("Successful");

		this.lblResultExerciseDescription = new Label(this.resultContentComposite, SWT.NONE);
		GridData gd_lblResultExerciseDescription = new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 1);
		gd_lblResultExerciseDescription.widthHint = 398;
		gd_lblResultExerciseDescription.horizontalIndent = 5;
		this.lblResultExerciseDescription.setLayoutData(gd_lblResultExerciseDescription);

		Label separator = new Label(this.resultContentComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

		this.compositeFooter = new Composite(this.resultContentComposite, SWT.NONE);
		this.compositeFooter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		this.compositeFooter.setLayout(new GridLayout(2, true));

		this.lblPoints = new Label(this.compositeFooter, SWT.NONE);
		this.lblPoints.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		boldDescriptor = FontDescriptor.createFrom(this.lblPoints.getFont()).setStyle(SWT.BOLD | SWT.ITALIC).setHeight(12);
		boldFont = boldDescriptor.createFont(this.lblPoints.getDisplay());
		this.lblPoints.setFont(boldFont);
		this.lblPoints.setText("Points: ");

		this.resultScore = new Label(this.compositeFooter, SWT.RIGHT);
		this.resultScore.setText(Messages.ResultTab_resultScore_text);
		this.resultScore.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 1, 1));
		boldDescriptor = FontDescriptor.createFrom(this.resultScore.getFont()).setStyle(SWT.BOLD | SWT.ITALIC).setHeight(12);
		boldFont = boldDescriptor.createFont(this.resultScore.getDisplay());
		this.resultScore.setFont(boldFont);

		Label labelFeedback2 = new Label(this.feedbackContentComposite, SWT.NONE);
		boldDescriptor = FontDescriptor.createFrom(labelFeedback2.getFont()).setHeight(9);
		boldFont = boldDescriptor.createFont(labelFeedback2.getDisplay());
		labelFeedback2.setFont(boldFont);
		labelFeedback2.setText(Messages.RESULTTAB_INFO_RESULT);
		this.createTableForFeedback(this.feedbackContentComposite);

		this.scrolledCompositeFeedback.setContent(this.feedbackContainerComposite);
		this.scrolledCompositeFeedback.setMinSize(new Point(64, 64));
	}

	@Override
	public void reset() {
		this.feedbackContentComposite.setVisible(false);
		this.btnLoading.setText(LOAD_BTN_TEXT);
		this.btnLoading.setVisible(false);
	}

	private void addSelectionListenerForReloadButton(Button btn) {
		btn.addListener(SWT.Selection, e -> {
			this.getFeedbackForExcerise();
		});
	}

	private void createTableForFeedback(Composite parent) {
		this.feedbackTabel = new Table(parent, SWT.BORDER | SWT.V_SCROLL);
		this.feedbackTabel.setToolTipText("Feedbacks for Excerise");
		this.feedbackTabel.setLinesVisible(true);
		this.feedbackTabel.setHeaderVisible(true);
		this.feedbackTabel.setLayout(new GridLayout(1, true));
		this.feedbackTabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		String[] colNames = { "Name", "Credits", "Success", "Detailed Text" }; //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		int[] width = { 200, 100, 100, 100 };

		for (int loopIndex = 0; loopIndex < colNames.length; loopIndex++) {
			final TableColumn column = new TableColumn(this.feedbackTabel, SWT.NULL);
			column.setWidth(width[loopIndex]);
			column.setText(colNames[loopIndex]);
		}

		this.feedbackTabel.addListener(SWT.Selection, e -> {
			this.handleResultTableEvent(e);
		});
	}

	private void handleResultTableEvent(Event e) {
		TableItem item = (TableItem) e.item;
		int index = this.feedbackTabel.indexOf(item);
		Feedback selectedFeedback = this.feedbackOfLastResult.get(index);
		if (selectedFeedback == null) {
			return;
		}
		Shell s = new Shell(this.display);
		s.setMinimumSize(500, 500);
		if (selectedFeedback.getDetailText() != null) {
			new TestDetailsDialog(s, selectedFeedback.getText(), selectedFeedback.getDetailText()).open();
			// MessageDialog.openInformation(null, selectedFeedback.getText(),
			// selectedFeedback.getDetailText());
		}
	}

	private void addResultToTab(ResultsDTO result, IExercise exercise) {
		Display display = Display.getDefault();
		if (result != null) {
			this.btnResultSuccessfull
					.setForeground(Boolean.TRUE.equals(result.successful) ? display.getSystemColor(SWT.COLOR_GREEN) : display.getSystemColor(SWT.COLOR_RED));
			this.btnResultSuccessfull.setText(Boolean.TRUE.equals(result.successful) ? "success" : "failed"); //$NON-NLS-2$

			if (exercise != null) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				LocalDateTime date = this.convertToLocalDateTimeViaInstant(result.completionDate);
				this.lblResultExerciseShortName.setText(exercise.getTitle());
				this.lblResultExerciseDescription.setText(date.format(formatter));
				this.resultScore.setText(result.resultString);
				this.lblPoints.setText("Points: " + result.score + "%");
				this.resultContentComposite.layout();
				this.compositeFooter.layout();
				this.compositeHeader.layout();
			} else {
				this.resultScore.setText(Integer.toString(result.score));
			}
		}
	}

	private void addFeedbackToTable(Table table, List<Feedback> entries) {
		Display display = this.getDisplay();

		if (entries != null) {
			Collections.sort(entries);
			for (var feedback : entries) {
				double roundedCredits = this.roundToDeciamlPlaces(feedback.getCredits());
				final TableItem item = new TableItem(table, SWT.NULL);
				item.setText(0, feedback.getText());
				item.setText(1, "" + roundedCredits);
				item.setText(2, feedback.getPositive() ? "successful" : "failed"); //$NON-NLS-2$
				item.setForeground(2, feedback.getPositive() ? display.getSystemColor(SWT.COLOR_GREEN) : display.getSystemColor(SWT.COLOR_RED));
				item.setText(3, feedback.getDetailText() != null ? CHECK_MARK_IN_UTF8 : "X");
			}
		}
	}

	private Display getDisplay() {
		if (this.display == null) {
			return Display.getDefault();
		}
		return this.display;
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
			this.handleNewResult(entry.getKey(), entry.getValue());
			this.createAnnotationsMarkers();
		} else {
			this.feedbackTabel.removeAll();
			this.reset();
		}
	}

	private void handleNewResult(ResultsDTO result, List<Feedback> feedbacks) {
		IExercise exercise = this.viewController.getCurrentSelectedExercise();

		this.lastResult = result;
		this.feedbackOfLastResult = feedbacks;

		this.feedbackTabel.removeAll();
		this.addFeedbackToTable(this.feedbackTabel, this.feedbackOfLastResult);
		this.addResultToTab(this.lastResult, exercise);
		this.feedbackContainerComposite.pack();
		this.feedbackContentComposite.setVisible(true);

	}

	@Override
	public void handleSubmission(Object payload) {
		if (this.display != null) {
			this.display.syncExec(() -> {
				this.btnLoading.setVisible(true);
			});
		}
	}

	@Override
	public void handleResult(Object payload) {
		if (this.display != null) {
			this.display.syncExec(() -> {
				this.btnLoading.setVisible(false);
				this.getFeedbackForExcerise();
			});
		}
	}

	@Override
	public void handleException(Throwable e) {
		if (this.display != null) {
			this.display.syncExec(() -> {
				this.handleWebsocketError();
				e.printStackTrace();
			});
		}

	}

	@Override
	public void handleTransportError(Throwable e) {
		if (this.display != null) {
			this.display.syncExec(() -> {
				this.handleWebsocketError();
				e.printStackTrace();
			});
		}
	}

	private void handleWebsocketError() {
		this.btnLoading.setVisible(false);
	}

	@Override
	public void callExercisesEvent() {
		this.getFeedbackForExcerise();

	}

	@Override
	public void callExamEvent() {
		this.reset();
	}

	@Override
	public void setViewController(StudentViewController viewController) {
		this.viewController = viewController;
	}

	private LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
		return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	/**
	 * creates markers for current annotations in the backend
	 */
	public void createAnnotationsMarkers() {
		String currentProjectName = this.viewController.getCurrentProjectName();
		this.viewController.getAnnotations().stream().filter(annotation -> !AssessmentUtilities.isAnnotationPresent(annotation, currentProjectName))
				.forEach(annatoation -> {
					try {
						AssessmentUtilities.createMarkerForAnnotation(annatoation, currentProjectName);
					} catch (ArtemisClientException e) {
						this.handleAnnotationError(e);
					}
				});
	}

	private void handleAnnotationError(ArtemisClientException e) {

	}
}
