/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.view.ui;

import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.IExercise;

abstract class AbstractResultTabCompositeController extends AbstractResultTabComposite {
	private static final String CHECK_MARK_IN_UTF8 = "\u2713";
	private static final String X_MARK_IN_UTF8 = "\u2717";

	protected AbstractResultTabCompositeController(TabFolder tabFolder, boolean hasReloadFunctionality) {
		super(createTabItem(tabFolder), SWT.NONE);
		if (!hasReloadFunctionality) {
			this.btnReload.setVisible(false);
		}
		btnReload.addListener(SWT.Selection, e -> this.reloadFeedbackForExcerise());
		testTable.addListener(SWT.Selection, this::handleResultTableEvent);
	}

	private static Composite createTabItem(TabFolder tabFolder) {
		TabItem tbtmResult = new TabItem(tabFolder, SWT.NONE);
		tbtmResult.setText("Test Results");
		Composite container = new Composite(tabFolder, SWT.NONE);
		tbtmResult.setControl(container);
		container.setLayout(new FillLayout());
		return container;
	}

	protected abstract void reloadFeedbackForExcerise();

	protected void setSuccessAndScore(IExercise currentExercise, boolean successOfAutomaticTests, double points, double score, String completionTime,
			String resultString) {
		var display = getDisplay();
		String title = currentExercise == null ? "Unknown Task" : currentExercise.getTitle();

		this.lblStateOfTests.setForeground(successOfAutomaticTests ? display.getSystemColor(SWT.COLOR_GREEN) : display.getSystemColor(SWT.COLOR_RED));
		this.lblStateOfTests.setText(successOfAutomaticTests ? "Test(s) succeeded" : "Test(s) failed");

		this.lblTaskname.setText(title);
		this.lblTaskdetails.setText(completionTime == null ? "" : completionTime);
		this.lblScore.setText(resultString == null ? String.format(Locale.ENGLISH, "Score: %.2f%%", score) : resultString);
		this.lblPoints.setText(String.format(Locale.ENGLISH, "Points: %.2f", points));
	}

	protected void createTableItemsForFeedback(Table table, String name, Feedback feedback) {
		String roundedCredits = feedback.getCredits() == null ? "0.00" : String.format(Locale.ENGLISH, "%.2f", feedback.getCredits());
		String success = this.calculateSuccessMessage(feedback);
		int colorIndicator = this.calculateSuccessColorIndicator(feedback);

		final TableItem item = new TableItem(table, SWT.NULL);
		item.setData(feedback);
		item.setText(0, name);
		item.setText(1, "" + roundedCredits);
		item.setText(2, success);
		item.setForeground(2, Display.getDefault().getSystemColor(colorIndicator));
		item.setText(3, feedback.getDetailText() != null ? CHECK_MARK_IN_UTF8 : X_MARK_IN_UTF8);
	}

	private String calculateSuccessMessage(Feedback feedback) {
		if (feedback.getPositive() == null) {
			return "";
		}
		return Boolean.TRUE.equals(feedback.getPositive()) ? "successful" : "failed";
	}

	private int calculateSuccessColorIndicator(Feedback feedback) {
		if (feedback.getPositive() == null) {
			return SWT.COLOR_BLACK;
		}
		return Boolean.TRUE.equals(feedback.getPositive()) ? SWT.COLOR_GREEN : SWT.COLOR_RED;
	}

	protected final void loadingStarted() {
		this.loadingIndicator.setVisible(true);
	}

	protected final void loadingFinished() {
		this.loadingIndicator.setVisible(false);
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

	protected final void resetView() {
		this.testTable.removeAll();
		this.loadingIndicator.setVisible(false);
		this.lblPoints.setText(POINTS);
		this.lblScore.setText(SCORE);
		this.lblStateOfTests.setText(STATE_OF_TESTS);
		this.lblStateOfTests.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		this.lblTaskdetails.setText(TASK_DETAILS);
		this.lblTaskname.setText(TASK_NAME);
	}
}
