/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.grading.view.commands;

import static edu.kit.kastel.eclipse.common.view.languages.LanguageSettings.I18N;

import java.util.List;
import java.util.Optional;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.kit.kastel.eclipse.common.api.model.IAnnotation;

public class DeleteAnnotationDialog extends Dialog {
	private static final int LIST_HEIGHT = 200;
	private static final int LIST_WIDTH = 400;

	private final List<IAnnotation> annotations;
	private TableViewer displayList;

	private IAnnotation selectedAnnotation;

	public DeleteAnnotationDialog(Shell parentShell, List<IAnnotation> annotations) {
		super(parentShell);
		this.annotations = annotations;
	}

	public Optional<IAnnotation> getSelectedAnnotation() {
		return Optional.ofNullable(this.selectedAnnotation);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		GridLayout layout = new GridLayout();
		container.setLayout(layout);

		Label delInfo = new Label(container, SWT.NONE);
		delInfo.setText("Press Del to delete");
		delInfo.setForeground(new Color(100, 100, 100));
		this.createAnnotationList(container);

		return container;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Annotations");
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// Removes the ok and close buttons
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	private void createAnnotationList(Composite container) {
		this.displayList = new TableViewer(container, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		this.displayList.getTable().setHeaderVisible(false);
		this.displayList.getTable().setLinesVisible(false);
		this.displayList.setLabelProvider(new StyledCellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				IAnnotation annotation = (IAnnotation) cell.getElement();

				String rangeInfo;
				if (annotation.getStartLine() == annotation.getEndLine()) {
					rangeInfo = annotation.getStartLine() + "";
				} else {
					rangeInfo = annotation.getStartLine() + " - " + annotation.getEndLine();
				}
				String prefix = rangeInfo + " " + annotation.getMistakeType().getButtonText(I18N().key());

				String text = prefix;
				if (annotation.getCustomMessage().isPresent()) {
					text += " " + annotation.getCustomMessage().get();
				}
				cell.setText(text);
				StyleRange style = new StyleRange(0, prefix.length(), null, null);
				style.fontStyle = SWT.BOLD;
				cell.setStyleRanges(new StyleRange[] { style });
			}
		});

		this.displayList.setContentProvider(ArrayContentProvider.getInstance());
		this.displayList.setInput(this.annotations);

		this.displayList.getTable().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.DEL) {
					selectedAnnotation = (IAnnotation) displayList.getStructuredSelection().getFirstElement();
					close();
				}
			}
		});

		this.displayList.setComparator(new ViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				IAnnotation a1 = (IAnnotation) e1;
				IAnnotation a2 = (IAnnotation) e2;

				int a1Lines = a1.getEndLine() - a1.getStartLine();
				int a2Lines = a2.getEndLine() - a2.getStartLine();

				if (a1Lines != a2Lines) {
					return a1Lines - a2Lines;
				} else {
					return a1.getMistakeType().getButtonText(I18N().key()).compareTo(a1.getMistakeType().getButtonText(I18N().key()));
				}
			}
		});

		this.displayList.refresh();
		this.displayList.getTable().setSelection(0);

		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = LIST_HEIGHT;
		gridData.widthHint = LIST_WIDTH;
		gridData.horizontalAlignment = GridData.FILL;
		this.displayList.getControl().setLayoutData(gridData);
	}
}
