/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.view.ui;

import static edu.kit.kastel.eclipse.common.view.languages.LanguageSettings.I18N;

import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import edu.kit.kastel.eclipse.common.api.artemis.mapping.Feedback;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.view.utilities.UIUtilities;

abstract class AbstractResultTabCompositeController extends AbstractResultTabComposite {
	private static final String CHECK_MARK_IN_UTF8 = "\u2713";
	private static final String X_MARK_IN_UTF8 = "\u2717";

	protected AbstractResultTabCompositeController(TabFolder tabFolder, boolean hasReloadFunctionality) {
		super(UIUtilities.createTabWithScrolledComposite(tabFolder, I18N().tabResults()), SWT.NONE);
		if (!hasReloadFunctionality) {
			this.btnReload.setVisible(false);
		}
		btnReload.addListener(SWT.Selection, e -> this.reloadFeedbackForExercise());
		testTable.addListener(SWT.Selection, this::handleResultTableEvent);
		UIUtilities.initializeTabAfterFilling(this.getParent(), this);
	}

	protected abstract void reloadFeedbackForExercise();

	protected void setSuccessAndScore(IExercise currentExercise, boolean successOfAutomaticTests, double points, double score, String completionTime) {
		var display = getDisplay();
		String title = currentExercise == null ? I18N().unknownTask() : currentExercise.getTitle();

		this.lblStateOfTests.setForeground(successOfAutomaticTests ? display.getSystemColor(SWT.COLOR_GREEN) : display.getSystemColor(SWT.COLOR_RED));
		this.lblStateOfTests.setText(I18N().tests() + " " + (successOfAutomaticTests ? I18N().successful() : I18N().unsuccessful()));

		this.lblTaskname.setText(title);
		this.lblTaskdetails.setText(completionTime == null ? "" : completionTime);
		this.lblScore.setText(String.format(Locale.ENGLISH, I18N().score() + ": %.2f%%", score));
		this.lblPoints.setText(String.format(Locale.ENGLISH, I18N().points() + ": %.2f", points));
	}

	protected void createTableItemsForFeedback(Table table, String name, Feedback feedback) {
		String roundedCredits = feedback.getCredits() == null ? "0.00" : String.format(Locale.ENGLISH, "%.2f", feedback.getCredits());
		String success = this.calculateSuccessMessage(feedback);
		int colorIndicator = this.calculateSuccessColorIndicator(feedback);

		final TableItem item = new TableItem(table, SWT.NULL);
		item.setData(feedback);
		item.setText(0, name);
		item.setText(1, roundedCredits);
		item.setText(2, success);
		item.setForeground(2, Display.getDefault().getSystemColor(colorIndicator));
		item.setText(3, feedback.getDetailText() != null ? CHECK_MARK_IN_UTF8 : X_MARK_IN_UTF8);
	}

	private String calculateSuccessMessage(Feedback feedback) {
		if (feedback.getPositive() == null) {
			return "";
		}
		return Boolean.TRUE.equals(feedback.getPositive()) ? I18N().successful() : I18N().unsuccessful();
	}

	private int calculateSuccessColorIndicator(Feedback feedback) {
		if (feedback.getPositive() == null) {
			return SWT.COLOR_BLACK;
		}
		return Boolean.TRUE.equals(feedback.getPositive()) ? SWT.COLOR_GREEN : SWT.COLOR_RED;
	}

	private void handleResultTableEvent(Event e) {
		TableItem item = (TableItem) e.item;
		Feedback selectedFeedback = (Feedback) item.getData();
		if (selectedFeedback == null) {
			return;
		}
		if (selectedFeedback.getDetailText() != null) {
			var text = item.getText(0);
			new TestDetailsDialog(text, selectedFeedback.getDetailText()).open();
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
