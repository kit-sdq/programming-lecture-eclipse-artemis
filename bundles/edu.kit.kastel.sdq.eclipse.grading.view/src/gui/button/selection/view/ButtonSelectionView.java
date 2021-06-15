package gui.button.selection.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.part.ViewPart;


import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.IRatingGroup;
import gui.controllers.AssessmentViewController;

public class ButtonSelectionView extends ViewPart {

	private AssessmentViewController viewController;
	private Collection<IMistakeType> mistakeTypes;
	private Collection<IRatingGroup> ratingGroups;
	
	public ButtonSelectionView() {
		this.viewController = new AssessmentViewController();
		this.viewController.createAssessmentController();
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void createPartControl(Composite parent) {
		try {
			this.mistakeTypes = this.viewController.getMistakeTypesForButtonView();
			this.ratingGroups = this.getRatingGroups(this.mistakeTypes);
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
			 parent.setLayout(new GridLayout(this.ratingGroups.size(), true));
			 this.createErrorTypesButtons(parent);
		}
	}
	
	public Collection<IRatingGroup> getRatingGroups(Collection<IMistakeType> mistakeTypes){
		ArrayList<IRatingGroup> out = new ArrayList<IRatingGroup>();
		mistakeTypes.forEach(type -> {
			if(!out.contains(type.getRatingGroup())) {
				out.add(type.getRatingGroup());
			}
		});
		return out;
	}
	
	public void createErrorTypesButtons(Composite parent) {
//		final ScrolledComposite sc1 = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
	//	 final ScrolledComposite sc2 = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
	 //     sc2.setExpandHorizontal(true);
	  //    sc2.setExpandVertical(true);
		Composite child = new Composite(parent, SWT.NONE);
	//	sc2.setContent(child);
		child.setLayout(new GridLayout(this.ratingGroups.size(), true));
		this.ratingGroups.forEach(element -> {
			Group rgDisplay = new Group(child, SWT.NULL);
			rgDisplay.setText(element.getDisplayName());
			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 3;
			rgDisplay.setLayout(gridLayout);
			GridData gridData = new GridData(GridData.VERTICAL_ALIGN_FILL);
			gridData.horizontalSpan = 3;
			rgDisplay.setLayoutData(gridData);
			this.mistakeTypes.forEach(mistake -> {
				if(mistake.getRatingGroup().getDisplayName().equals(element.getDisplayName())) {
					Button mistakeButton = new Button(rgDisplay, SWT.PUSH);
					mistakeButton.setText(mistake.getButtonName());	
				}
			});
		});
	//	sc2.setMinSize(child.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	//	sc1.setContent(child);
	//	sc1.setExpandHorizontal(true);
	 //     sc1.setExpandVertical(true);
	//	child.setSize(child.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
	}

}
