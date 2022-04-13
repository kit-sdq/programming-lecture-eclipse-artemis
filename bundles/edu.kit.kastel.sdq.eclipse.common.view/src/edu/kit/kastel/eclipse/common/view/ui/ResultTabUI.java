/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.view.ui;

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

import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.Feedback;

abstract class ResultTabUI {
	private static final String RELOAD_BTN_TEXT = "Reload";

	protected Composite feedbackContainerComposite;
	protected Composite feedbackContentComposite;

	protected Table feedbackTable;

	protected Label resultScore;
	protected Label btnResultSuccessful;
	protected Label lblResultExerciseShortName;
	protected Label lblResultExerciseDescription;
	protected Label lblPoints;

	private Composite resultContentComposite;
	private Composite compositeHeader;
	private Composite compositeFooter;
	private ProgressBar loadingIndicator;

	private final boolean hasReloadFunctionality;

	protected ResultTabUI(boolean hasReloadFunctionality) {
		this.hasReloadFunctionality = hasReloadFunctionality;
	}

	protected void createTabFolder(TabFolder tabFolder) {
		TabItem tbtmResult = new TabItem(tabFolder, SWT.NONE);
		tbtmResult.setText("Test Results");

		ScrolledComposite scrolledCompositeFeedback = new ScrolledComposite(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmResult.setControl(scrolledCompositeFeedback);
		scrolledCompositeFeedback.setLayout(new FillLayout());

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

		if (this.hasReloadFunctionality) {
			this.loadingIndicator = new ProgressBar(headerComposite, SWT.INDETERMINATE);
			GridData gdLoadingIndicator = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
			gdLoadingIndicator.widthHint = 80;
			this.loadingIndicator.setLayoutData(gdLoadingIndicator);
			this.loadingIndicator.setVisible(false);

			Button btnReload = new Button(headerComposite, SWT.CENTER);
			btnReload.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false, 1, 1));
			btnReload.setText(RELOAD_BTN_TEXT);
			btnReload.addListener(SWT.Selection, e -> this.reloadFeedbackForExcerise());
		}
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
		GridData gdLblResultExerciseDescription = new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 1);
		gdLblResultExerciseDescription.widthHint = 400;
		gdLblResultExerciseDescription.horizontalIndent = 5;
		this.lblResultExerciseDescription.setLayoutData(gdLblResultExerciseDescription);

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

	protected abstract void reloadFeedbackForExcerise();

	protected final void loadingStarted() {
		if (this.loadingIndicator != null) {
			this.loadingIndicator.setVisible(true);
		}
	}

	protected final void loadingFinished() {
		if (this.loadingIndicator != null) {
			this.loadingIndicator.setVisible(false);
		}
	}

	protected final void resetView() {
		this.feedbackContentComposite.setVisible(false);
		if (this.loadingIndicator != null) {
			this.loadingIndicator.setVisible(false);
		}
	}

	protected final void layout() {
		this.resultContentComposite.layout();
		this.compositeFooter.layout();
		this.compositeHeader.layout();
	}

	private void createTableForFeedback(Composite parent) {
		this.feedbackTable = new Table(parent, SWT.BORDER | SWT.V_SCROLL);
		this.feedbackTable.setToolTipText("Feedbacks for Excerise");
		this.feedbackTable.setLinesVisible(true);
		this.feedbackTable.setHeaderVisible(true);
		this.feedbackTable.setLayout(new GridLayout(1, true));
		this.feedbackTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		String[] colNames = { "Name", "Credits", "Success", "Detailed Text" };
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

}
