package edu.kit.kastel.eclipse.student.view.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import edu.kit.kastel.eclipse.student.view.controllers.StudentViewController;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

public class ExamTab implements ArtemisStudentTab {
	private StudentViewController viewController; 
	
	public ExamTab(StudentViewController viewController) {
		this.viewController = viewController;
	}
	
	@Override
	public void create(TabFolder tabFolder) {
		TabItem gradingTabItem = new TabItem(tabFolder, SWT.NONE);
		gradingTabItem.setText("Exam");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		gradingTabItem.setControl(composite);
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		lblNewLabel.setBounds(0, 0, 432, 44);
		lblNewLabel.setText(" Exam");
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void callEvent() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	private void createView(Composite parent) {
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
		create(tabFolder);
	}
}
