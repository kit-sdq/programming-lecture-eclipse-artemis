/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.student.view.ui;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
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
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.FeedbackType;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ResultsDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.client.websocket.WebsocketCallback;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.core.model.annotation.Annotation;

public class ResultTab implements ArtemisStudentTab, WebsocketCallback {
	private static final ILog log = Platform.getLog(ResultTab.class);

	private static final String RELOAD_BTN_TEXT = "Reload";
	private static final String CHECK_MARK_IN_UTF8 = "\u2713";
	private static final String X_MARK_IN_UTF8 = "\u2717";

	private StudentViewController viewController;

	private Composite feedbackContainerComposite;
	private Composite feedbackContentComposite;
	private Composite resultContentComposite;
	private Composite compositeHeader;
	private Composite compositeFooter;
	private Table feedbackTable;

	private Label resultScore;
	private Label btnResultSuccessful;
	private Label lblResultExerciseDescription;
	private Label lblResultExerciseShortName;
	private Label lblPoints;
	private ProgressBar loadingIndicator;

	public ResultTab(final StudentViewController viewController) {
		this.viewController = viewController;
	}

	@Override
	public void create(TabFolder tabFolder) {
		TabItem tbtmResult = new TabItem(tabFolder, SWT.NONE);
		tbtmResult.setText("Test Results");

		ScrolledComposite scrolledCompositeFeedback = new ScrolledComposite(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmResult.setControl(scrolledCompositeFeedback);
		scrolledCompositeFeedback.setLayout(new FillLayout());
		// this.scrolledCompositeFeedback.setExpandHorizontal(true);
		// this.scrolledCompositeFeedback.setExpandVertical(true);

		this.feedbackContainerComposite = new Composite(scrolledCompositeFeedback, SWT.NONE);
		scrolledCompositeFeedback.setContent(this.feedbackContainerComposite);
		this.feedbackContainerComposite.setSize(scrolledCompositeFeedback.getSize());
		scrolledCompositeFeedback.setMinSize(this.feedbackContainerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.feedbackContainerComposite.setLayout(new GridLayout(1, true));
		this.feedbackContainerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite composite = new Composite(this.feedbackContainerComposite, SWT.NONE);
		composite.setLayout(new GridLayout(2, true));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		Label labelFeedback = new Label(composite, SWT.NONE);
		FontDescriptor boldDescriptor = FontDescriptor.createFrom(labelFeedback.getFont()).setStyle(SWT.BOLD).setHeight(18);
		Font boldFont = boldDescriptor.createFont(labelFeedback.getDisplay());
		labelFeedback.setFont(boldFont);
		labelFeedback.setText("Latest Results");

		Composite headerComposite = new Composite(composite, SWT.NONE);
		headerComposite.setLayout(new GridLayout(2, true));
		headerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		this.loadingIndicator = new ProgressBar(headerComposite, SWT.INDETERMINATE);
		GridData gd_loadingIndicator = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_loadingIndicator.widthHint = 80;
		this.loadingIndicator.setLayoutData(gd_loadingIndicator);
		this.loadingIndicator.setVisible(false);

		Button btnReload = new Button(headerComposite, SWT.CENTER);
		btnReload.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false, 1, 1));
		btnReload.setText(RELOAD_BTN_TEXT);
		this.addSelectionListenerForReloadButton(btnReload);

		Label labelResult = new Label(this.feedbackContainerComposite, SWT.NONE);
		labelResult.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		labelResult.setText("Summary of the Results for the currently selected Exercise");

		this.feedbackContentComposite = new Composite(this.feedbackContainerComposite, SWT.NONE);
		this.feedbackContentComposite.setTouchEnabled(true);
		this.feedbackContentComposite.setLayout(new GridLayout(1, true));
		this.feedbackContentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.feedbackContentComposite.setVisible(true);
		this.resultContentComposite = new Composite(this.feedbackContentComposite, SWT.BORDER);
		this.resultContentComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		this.resultContentComposite.setLayout(new GridLayout(1, false));

		this.compositeHeader = new Composite(this.resultContentComposite, SWT.NONE);
		this.compositeHeader.setLayout(new GridLayout(2, false));
		this.compositeHeader.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		this.lblResultExerciseShortName = new Label(this.compositeHeader, SWT.NONE);
		this.lblResultExerciseShortName.setText("Name");
		this.lblResultExerciseShortName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		this.lblResultExerciseShortName.setTouchEnabled(true);
		boldDescriptor = FontDescriptor.createFrom(this.lblResultExerciseShortName.getFont()).setStyle(SWT.BOLD).setHeight(12);
		boldFont = boldDescriptor.createFont(this.lblResultExerciseShortName.getDisplay());
		this.lblResultExerciseShortName.setFont(boldFont);

		this.btnResultSuccessful = new Label(this.compositeHeader, SWT.RIGHT);
		this.btnResultSuccessful.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_GREEN));
		this.btnResultSuccessful.setBounds(360, 9, 123, 28);
		boldDescriptor = FontDescriptor.createFrom(this.btnResultSuccessful.getFont()).setStyle(SWT.BOLD).setHeight(12);
		boldFont = boldDescriptor.createFont(this.btnResultSuccessful.getDisplay());
		this.btnResultSuccessful.setFont(boldFont);
		this.btnResultSuccessful.setText("Successful");

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
		this.resultScore.setText("?/?");
		this.resultScore.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 1, 1));
		boldDescriptor = FontDescriptor.createFrom(this.resultScore.getFont()).setStyle(SWT.BOLD | SWT.ITALIC).setHeight(12);
		boldFont = boldDescriptor.createFont(this.resultScore.getDisplay());
		this.resultScore.setFont(boldFont);

		Label labelFeedbackSummary = new Label(this.feedbackContentComposite, SWT.NONE);
		boldDescriptor = FontDescriptor.createFrom(labelFeedbackSummary.getFont()).setHeight(9);
		boldFont = boldDescriptor.createFont(labelFeedbackSummary.getDisplay());
		labelFeedbackSummary.setFont(boldFont);
		labelFeedbackSummary.setText("Summary of all visible Tests");
		this.createTableForFeedback(this.feedbackContentComposite);

		scrolledCompositeFeedback.setContent(this.feedbackContainerComposite);

		this.feedbackContainerComposite.pack();
		this.feedbackContentComposite.setVisible(true);
	}

	@Override
	public void reset() {
		this.feedbackContentComposite.setVisible(false);
		this.loadingIndicator.setVisible(false);
	}

	private void addSelectionListenerForReloadButton(Button btn) {
		btn.addListener(SWT.Selection, e -> this.getFeedbackForExcerise());
	}

	private void createTableForFeedback(Composite parent) {
		this.feedbackTable = new Table(parent, SWT.BORDER | SWT.V_SCROLL);
		this.feedbackTable.setToolTipText("Feedbacks for Excerise");
		this.feedbackTable.setLinesVisible(true);
		this.feedbackTable.setHeaderVisible(true);
		this.feedbackTable.setLayout(new GridLayout(1, true));
		this.feedbackTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		String[] colNames = { "Name", "Credits", "Success", "Detailed Text" }; //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		int[] width = { 200, 100, 100, 100 };

		for (int loopIndex = 0; loopIndex < colNames.length; loopIndex++) {
			final TableColumn column = new TableColumn(this.feedbackTable, SWT.NULL);
			column.setWidth(width[loopIndex]);
			column.setText(colNames[loopIndex]);
		}

		this.feedbackTable.addListener(SWT.Selection, this::handleResultTableEvent);
	}

	private void handleResultTableEvent(Event e) {
		TableItem item = (TableItem) e.item;
		Feedback selectedFeedback = (Feedback) item.getData();
		if (selectedFeedback == null) {
			return;
		}
		Shell s = new Shell(Display.getDefault());
		s.setMinimumSize(500, 500);
		if (selectedFeedback.getDetailText() != null) {
			var text = item.getText(0);
			new TestDetailsDialog(s, text, selectedFeedback.getDetailText()).open();
		}
	}

	private void addResultToTab(ResultsDTO result, List<Feedback> feedbacks, IExercise exercise) {
		Display display = Display.getDefault();
		if (result != null) {
			boolean success = Boolean.TRUE.equals(result.successful) || feedbacks != null && !feedbacks.isEmpty() //
					&& feedbacks.stream().filter(f -> f.getFeedbackType() == FeedbackType.AUTOMATIC).allMatch(f -> Boolean.TRUE.equals(f.getPositive()));

			this.btnResultSuccessful.setForeground(success ? display.getSystemColor(SWT.COLOR_GREEN) : display.getSystemColor(SWT.COLOR_RED));
			this.btnResultSuccessful.setText(success ? "Test(s) succeeded" : "Test(s) failed");

			if (exercise != null) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				LocalDateTime date = this.convertToLocalDateTimeViaInstant(result.completionDate);
				this.lblResultExerciseShortName.setText(exercise.getTitle());
				this.lblResultExerciseDescription.setText(date.format(formatter));
				this.resultScore.setText(result.resultString);
				this.lblPoints.setText(String.format(Locale.ENGLISH, "Points: %.2f%%", result.score));
				this.resultContentComposite.layout();
				this.compositeFooter.layout();
				this.compositeHeader.layout();
			} else {
				this.resultScore.setText(Double.toString(result.score));
			}
		}
	}

	private void addFeedbackToTable(Table table, List<Feedback> entries) {
		if (entries != null) {
			entries.stream().sorted().forEach(feedback -> {
				var name = feedback.getFeedbackType() != FeedbackType.AUTOMATIC && feedback.getText() == null ? "Tutor Comment" : feedback.getText();
				this.createTableItemsForFeedback(table, name, feedback);
			});
		}
	}

	private void createTableItemsForFeedback(Table table, String name, Feedback feedback) {
		String roundedCredits = feedback.getCredits() == null ? "0.00" : String.format(Locale.ENGLISH, "%.2f", feedback.getCredits());
		String success = feedback.getPositive() == null ? "" : feedback.getPositive() ? "successful" : "failed";
		int successColor = feedback.getPositive() == null ? SWT.COLOR_BLACK : feedback.getPositive() ? SWT.COLOR_GREEN : SWT.COLOR_RED;

		final TableItem item = new TableItem(table, SWT.NULL);
		item.setData(feedback);
		item.setText(0, name);
		item.setText(1, "" + roundedCredits);
		item.setText(2, success);
		item.setForeground(2, Display.getDefault().getSystemColor(successColor));
		item.setText(3, feedback.getDetailText() != null ? CHECK_MARK_IN_UTF8 : X_MARK_IN_UTF8);
	}

	private void getFeedbackForExcerise() {
		var resultFeedbackMap = this.viewController.getFeedbackExcerise();

		if (!resultFeedbackMap.isEmpty()) {
			Entry<ResultsDTO, List<Feedback>> entry = resultFeedbackMap.entrySet().iterator().next();
			this.handleNewResult(entry.getKey(), entry.getValue());
			this.createAnnotationsMarkers(entry.getValue());
		} else {
			this.feedbackTable.removeAll();
			this.reset();
		}
	}

	private void handleNewResult(ResultsDTO result, List<Feedback> feedbacks) {
		this.feedbackTable.removeAll();

		IExercise exercise = this.viewController.getCurrentSelectedExercise();
		this.addFeedbackToTable(this.feedbackTable, feedbacks);
		this.addResultToTab(result, feedbacks, exercise);
		this.feedbackContainerComposite.pack();
		this.feedbackContentComposite.setVisible(true);

	}

	@Override
	public void handleSubmission(Object payload) {
		Display.getDefault().syncExec(() -> this.loadingIndicator.setVisible(true));

	}

	@Override
	public void handleResult(Object payload) {
		Display.getDefault().syncExec(() -> {
			this.loadingIndicator.setVisible(false);
			this.getFeedbackForExcerise();
		});

	}

	@Override
	public void handleException(Throwable e) {
		Display.getDefault().syncExec(() -> {
			this.handleWebsocketError();
			e.printStackTrace();
		});

	}

	@Override
	public void handleTransportError(Throwable e) {
		Display.getDefault().syncExec(() -> {
			this.handleWebsocketError();
			e.printStackTrace();
		});
	}

	private void handleWebsocketError() {
		this.loadingIndicator.setVisible(false);
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
	 *
	 * @param feedbacks
	 */
	public void createAnnotationsMarkers(List<Feedback> feedbacks) {
		String currentProjectName = this.viewController.getCurrentProjectName();

		// Translate Feedback to annotations ..
		var feedbackForLines = feedbacks.stream().filter(f -> f.getFeedbackType() == FeedbackType.MANUAL).collect(Collectors.toList());
		var annotations = this.convertToAnnotation(feedbackForLines);
		for (var annotation : annotations) {
			IMarker present = AssessmentUtilities.findPresentAnnotation(annotation, currentProjectName, "src/");
			if (present != null) {
				try {
					present.delete();
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			try {
				AssessmentUtilities.createMarkerForAnnotation(annotation, currentProjectName, "src/");
			} catch (ArtemisClientException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	private List<Annotation> convertToAnnotation(List<Feedback> feedbackForLines) {
		List<Annotation> annotations = new ArrayList<>();
		for (Feedback f : feedbackForLines) {
			// e.g., file:src/edu/kit/informatik/Client.java.java_line:21
			String reference = f.getReference();
			if (reference == null || !reference.startsWith("file:")) {
				continue;
			}

			var fileXline = reference.split(".java_line:");

			String uuid = reference;
			IMistakeType type = null;
			int startLine = Integer.parseInt(fileXline[1]);
			int endLine = startLine;
			String fullyClassifiedClassName = fileXline[0].substring("file:src/".length());
			String customMessage = f.getDetailText();
			Double customPenalty = f.getCredits();
			int markerCharStart = -1;
			int markerCharEnd = -1;

			Annotation annotation = new Annotation(uuid, type, startLine, endLine, fullyClassifiedClassName, customMessage, customPenalty, markerCharStart,
					markerCharEnd);
			annotations.add(annotation);
		}
		return annotations;
	}
}
