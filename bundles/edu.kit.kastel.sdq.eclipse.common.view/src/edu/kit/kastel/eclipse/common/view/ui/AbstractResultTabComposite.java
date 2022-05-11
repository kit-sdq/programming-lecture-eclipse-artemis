/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.view.ui;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

class AbstractResultTabComposite extends Composite {
	protected static final String POINTS = "?/? Points";
	protected static final String SCORE = "Score:";
	protected static final String TASK_DETAILS = "TaskDetails";
	protected static final String STATE_OF_TESTS = "StateOfTests";
	protected static final String TASK_NAME = "TaskName";

	protected Table testTable;
	protected ProgressBar loadingIndicator;
	protected Button btnReload;
	protected Label lblStateOfTests;
	protected Label lblTaskname;
	protected Label lblScore;
	protected Label lblPoints;
	protected Label lblTaskdetails;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public AbstractResultTabComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		Label lblLatestResults = new Label(this, SWT.NONE);
		lblLatestResults.setText("Latest Results from Artemis");
		FontDescriptor boldDescriptor = FontDescriptor.createFrom(lblLatestResults.getFont()).setStyle(SWT.BOLD).setHeight(12);
		Font boldFont = boldDescriptor.createFont(lblLatestResults.getDisplay());
		lblLatestResults.setFont(boldFont);

		btnReload = new Button(this, SWT.NONE);
		btnReload.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnReload.setText("Reload");

		Composite compositeOverview = new Composite(this, SWT.BORDER);
		compositeOverview.setLayout(new GridLayout(2, false));
		compositeOverview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));

		lblTaskname = new Label(compositeOverview, SWT.NONE);
		lblTaskname.setText(TASK_NAME);

		lblStateOfTests = new Label(compositeOverview, SWT.NONE);
		lblStateOfTests.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStateOfTests.setText(STATE_OF_TESTS);

		lblTaskdetails = new Label(compositeOverview, SWT.NONE);
		lblTaskdetails.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		lblTaskdetails.setText(TASK_DETAILS);

		Label separator = new Label(compositeOverview, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		lblScore = new Label(compositeOverview, SWT.NONE);
		lblScore.setText(SCORE);

		lblPoints = new Label(compositeOverview, SWT.NONE);
		lblPoints.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPoints.setText(POINTS);

		loadingIndicator = new ProgressBar(this, SWT.INDETERMINATE);
		loadingIndicator.setVisible(false);
		loadingIndicator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		Label lblSummaryOfAll = new Label(this, SWT.NONE);
		lblSummaryOfAll.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		lblSummaryOfAll.setText("Summary of all visible Tests");

		ScrolledComposite scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		testTable = new Table(scrolledComposite, SWT.BORDER | SWT.FULL_SELECTION);
		testTable.setHeaderVisible(true);
		testTable.setLinesVisible(true);

		TableColumn tblclmnName = new TableColumn(testTable, SWT.NONE);
		tblclmnName.setWidth(100);
		tblclmnName.setText("Name");

		TableColumn tblclmnCredits = new TableColumn(testTable, SWT.NONE);
		tblclmnCredits.setWidth(100);
		tblclmnCredits.setText("Credits");

		TableColumn tblclmnSuccessful = new TableColumn(testTable, SWT.NONE);
		tblclmnSuccessful.setWidth(100);
		tblclmnSuccessful.setText("Successful");

		TableColumn tblclmnDetailText = new TableColumn(testTable, SWT.NONE);
		tblclmnDetailText.setWidth(100);
		tblclmnDetailText.setText("Detail Text");
		scrolledComposite.setContent(testTable);
		scrolledComposite.setMinSize(testTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}

	@Override
	protected final void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
