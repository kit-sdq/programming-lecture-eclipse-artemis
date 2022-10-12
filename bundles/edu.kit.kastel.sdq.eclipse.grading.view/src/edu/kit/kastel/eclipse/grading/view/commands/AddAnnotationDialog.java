package edu.kit.kastel.eclipse.grading.view.commands;

import java.util.Optional;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.kit.kastel.eclipse.common.api.model.IMistakeType;
import edu.kit.kastel.eclipse.grading.view.controllers.AssessmentViewController;

// One could also use the FilteredResourcesSelectionDialog (used e.g. for the buit-in Open Type command)
// but I don't like the UX of this dialog, it takes a comparatively long time to open
// and adding the shift-click listener is hard (I think)
public class AddAnnotationDialog extends Dialog {
	private final AssessmentViewController controller;
	private TableViewer displayList;
	private AnnotationFilter filter;
	
	private IMistakeType selectedMistake;
	private boolean customMessageWanted;

	public AddAnnotationDialog(Shell parentShell, AssessmentViewController controller) {
		super(parentShell);
		this.controller = controller;
	}
	
	public Optional<IMistakeType> getSelectedMistake() {
		return Optional.ofNullable(this.selectedMistake);
	}
	
	public boolean isCustomMessageWanted() {
		return this.customMessageWanted;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setSize(SWT.DEFAULT, 200);
		
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		
		this.createSearchField(container);
		this.createAnnotationList(container);
		
		return container;
	}
	
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Add Annotation");
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// Removes the ok and close buttons
	}
	
	private void updateDisplayList(String filter) {
		this.filter.setFilterString(filter);
		this.displayList.refresh();
		this.displayList.getTable().setSelection(0);
	}
	
	private void createSearchField(Composite container) {
		Text inputField = new Text(container, SWT.SINGLE | SWT.BORDER);
		inputField.addTraverseListener(e -> {
			if (e.detail == SWT.TRAVERSE_RETURN) {
				processMistakeSelection(isShiftPressed(e.stateMask));
				this.okPressed();
			} else if (e.detail == SWT.TRAVERSE_ESCAPE) {
				this.cancelPressed();
			}
		});
		inputField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int index = displayList.getTable().getSelectionIndex();
				if (e.keyCode == SWT.ARROW_DOWN) {
					displayList.getTable().select(index + 1);
				} else if (e.keyCode == SWT.ARROW_UP) {
					displayList.getTable().select(index - 1);
				}
			}
		});
		inputField.addModifyListener(e -> updateDisplayList(inputField.getText()));
		inputField.setFocus();
		
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		inputField.setLayoutData(gridData);
	}
	
	private void createAnnotationList(Composite container) {
		// List (-> table) of mistake types
		this.displayList = new TableViewer(container, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		this.displayList.getTable().setHeaderVisible(false);
		this.displayList.getTable().setLinesVisible(false);
		this.displayList.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object item) {
				return ((IMistakeType) item).getButtonText();
			}
		});
		
		this.displayList.setContentProvider(ArrayContentProvider.getInstance());
		this.displayList.setInput(this.controller.getAssessmentController().getMistakes());
		
		// Using the low-level table to detect shift-clicks
		this.displayList.getTable().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				processMistakeSelection(isShiftPressed(e.stateMask));
				close();
			}
		});
		
		this.filter = new AnnotationFilter();
		this.displayList.addFilter(this.filter);
		
		this.updateDisplayList("");
		
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = 200;
		gridData.horizontalAlignment = GridData.FILL;
		this.displayList.getControl().setLayoutData(gridData);
	}
	
	private void processMistakeSelection(boolean customText) {
		var mistake = (IMistakeType) displayList.getStructuredSelection().getFirstElement();
		if (mistake != null) {
			this.selectedMistake = mistake;
			this.customMessageWanted = customText;
		}
	}
	
	private boolean isShiftPressed(int stateMask) {
		return (stateMask & SWT.MOD2) != 0;
	}
}
