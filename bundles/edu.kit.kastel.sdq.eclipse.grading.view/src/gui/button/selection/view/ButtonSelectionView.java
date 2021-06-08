package gui.button.selection.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.ViewPart;

public class ButtonSelectionView extends ViewPart {
	
	  public ButtonSelectionView() {
		// TODO Auto-generated constructor stub
         super();
	 }
	 
	 public void setFocus() {
	 }
	 
	 public void createPartControl(Composite parent) {
		 parent.setLayout(new GridLayout());
         Button exampleButton1 = new Button(parent, SWT.PUSH);
         exampleButton1.setText("Button1");
         Button exampleButton2 = new Button(parent, SWT.PUSH);
         exampleButton2.setText("Button2");
         Button exampleButton3 = new Button(parent, SWT.PUSH);
         exampleButton3.setText("Button3");
         Button exampleButton4 = new Button(parent, SWT.PUSH);
         exampleButton1.setText("Button4");
	 }

}
