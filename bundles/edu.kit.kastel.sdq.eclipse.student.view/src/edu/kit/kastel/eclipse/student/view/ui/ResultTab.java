package edu.kit.kastel.eclipse.student.view.ui;

import java.nio.charset.StandardCharsets;
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
	private static final String RELOAD_BTN_TEXT = "Reload";
	private static final String LOAD_BTN_TEXT = "Loading...";
	private static final String CHECK_MARK_IN_UTF8 = new String(new byte[] { (byte) 0xE2, (byte) 0x9C, (byte) 0x93 }, StandardCharsets.UTF_8);

	private Display display;
	private StudentViewController viewController;

	private Composite feedbackContainerComposite;
	private Composite feedbackContentComposite;
	private Composite resultContentComposite;
	private Composite compositeHeader;
	private Composite compositeFooter;
	private List<Feedback> feedbackOfLastResult = new ArrayList<>();
	private Table feedbackTabel;

	private Label resultScore;
	private Label btnResultSuccessfull;
	private Label lblResultExerciseDescription;
	private Label lblResultExerciseShortName;
	private Label lblPoints;
	private Label btnLoading;

	public ResultTab(final StudentViewController viewController) {
		this.viewController = viewController;
	}

	@Override
	public void create(TabFolder tabFolder) {
		this.display = tabFolder.getDisplay();
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

		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, true));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		this.btnLoading = new Label(composite_1, SWT.SHADOW_IN | SWT.CENTER | SWT.BORDER);
		GridData gd_btnRLoading = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_btnRLoading.widthHint = 80;
		this.btnLoading.setLayoutData(gd_btnRLoading);
		this.btnLoading.setText(LOAD_BTN_TEXT);
		this.btnLoading.setVisible(false);

		Button btnReload = new Button(composite_1, SWT.CENTER);
		btnReload.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false, 1, 1));
		btnReload.setText(RELOAD_BTN_TEXT);
		this.addSelectionListenerForReloadButton(btnReload);

		Label labelResult = new Label(this.feedbackContainerComposite, SWT.NONE);
		labelResult.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
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
		this.resultScore.setText("?/?");
		this.resultScore.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 1, 1));
		boldDescriptor = FontDescriptor.createFrom(this.resultScore.getFont()).setStyle(SWT.BOLD | SWT.ITALIC).setHeight(12);
		boldFont = boldDescriptor.createFont(this.resultScore.getDisplay());
		this.resultScore.setFont(boldFont);

		Label labelFeedback2 = new Label(this.feedbackContentComposite, SWT.NONE);
		boldDescriptor = FontDescriptor.createFrom(labelFeedback2.getFont()).setHeight(9);
		boldFont = boldDescriptor.createFont(labelFeedback2.getDisplay());
		labelFeedback2.setFont(boldFont);
		labelFeedback2.setText("Summary of all visible Tests");
		this.createTableForFeedback(this.feedbackContentComposite);

		scrolledCompositeFeedback.setContent(this.feedbackContainerComposite);

		this.feedbackContainerComposite.pack();
		this.feedbackContentComposite.setVisible(true);
	}

	@Override
	public void reset() {
		this.feedbackContentComposite.setVisible(false);
		this.btnLoading.setText(LOAD_BTN_TEXT);
		this.btnLoading.setVisible(false);
	}

	private void addSelectionListenerForReloadButton(Button btn) {
		btn.addListener(SWT.Selection, e -> this.getFeedbackForExcerise());
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

		this.feedbackTabel.addListener(SWT.Selection, this::handleResultTableEvent);
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
			var text = selectedFeedback.getFeedbackType() != FeedbackType.AUTOMATIC ? "Tutor Comment" : selectedFeedback.getText();
			new TestDetailsDialog(s, text, selectedFeedback.getDetailText()).open();
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
		if (entries != null) {
			// TODO Sort it ..
			List<Pair<String, Feedback>> feedbacks = entries.stream().map(feedback -> {
				var name = feedback.getFeedbackType() != FeedbackType.AUTOMATIC && feedback.getText() == null ? "Tutor Comment" : feedback.getText();
				return new Pair<>(name, feedback);
			}).collect(Collectors.toList());

			for (var nameXfeedback : feedbacks) {
				var name = nameXfeedback.a;
				var feedback = nameXfeedback.b;
				String roundedCredits = feedback.getCredits() == null ? "0.00" : String.format(Locale.ENGLISH, "%.2f", feedback.getCredits());
				String success = feedback.getPositive() == null ? "" : feedback.getPositive() ? "successful" : "failed";
				int successColor = feedback.getPositive() == null ? SWT.COLOR_BLACK : feedback.getPositive() ? SWT.COLOR_GREEN : SWT.COLOR_RED;

				final TableItem item = new TableItem(table, SWT.NULL);
				item.setText(0, name);
				item.setText(1, "" + roundedCredits);
				item.setText(2, success);
				item.setForeground(2, this.getDisplay().getSystemColor(successColor));
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

	private void getFeedbackForExcerise() {
		var resultFeedbackMap = this.viewController.getFeedbackExcerise();

		if (!resultFeedbackMap.isEmpty()) {
			Entry<ResultsDTO, List<Feedback>> entry = resultFeedbackMap.entrySet().iterator().next();
			this.handleNewResult(entry.getKey(), entry.getValue());
			this.createAnnotationsMarkers(entry.getValue());
		} else {
			this.feedbackTabel.removeAll();
			this.reset();
		}
	}

	private void handleNewResult(ResultsDTO result, List<Feedback> feedbacks) {
		IExercise exercise = this.viewController.getCurrentSelectedExercise();

		this.feedbackOfLastResult = feedbacks;

		this.feedbackTabel.removeAll();
		this.addFeedbackToTable(this.feedbackTabel, this.feedbackOfLastResult);
		this.addResultToTab(result, exercise);
		this.feedbackContainerComposite.pack();
		this.feedbackContentComposite.setVisible(true);

	}

	@Override
	public void handleSubmission(Object payload) {
		if (this.display != null) {
			this.display.syncExec(() -> this.btnLoading.setVisible(true));
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
				this.handleAnnotationError(e);
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

	private void handleAnnotationError(ArtemisClientException e) {
		// TODO Handle error
		e.printStackTrace();

	}

	private static final class Pair<A extends Comparable<A>, B> implements Comparable<Pair<A, B>> {
		public final A a;
		public final B b;

		private Pair(A a, B b) {
			this.a = a;
			this.b = b;
		}

		@Override
		public int compareTo(Pair<A, B> o) {
			return this.a.compareTo(o.a);
		}

	}

}
