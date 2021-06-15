package gui.button.selection.view;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.IRatingGroup;
import gui.controllers.AssessmentViewController;

public class ButtonSelectionView extends ViewPart {

	private AssessmentViewController viewController;
	private Collection<IMistakeType> mistakeTypes;
//	private Collection<IRatingGroup> ratingGroups;
	
	public ButtonSelectionView() {
		this.viewController = new AssessmentViewController();
		this.viewController.createAssessmentController();
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(5, true));
		try {
			mistakeTypes = this.viewController.getMistakeTypesForButtonView();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			 mistakeTypes.forEach(type -> {
					Button viewButton = new Button(parent, SWT.PUSH);
					viewButton.setText(type.getButtonName());
			 });
		}
		 
		  
		    
//			ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL);
			//	    Composite composite = new Composite(sc, SWT.NONE);

			//	    composite.setLayout(new GridLayout(2, false));
//	    	sc.setContent(composite);
			//	    sc.setExpandHorizontal(true);
			//	    sc.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		   
	
	}

}
