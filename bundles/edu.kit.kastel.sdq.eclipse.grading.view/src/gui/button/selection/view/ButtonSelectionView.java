package gui.button.selection.view;

import java.io.IOException;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import gui.controllers.AssessmentViewController;

public class ButtonSelectionView extends ViewPart {

	private AssessmentViewController viewController;

	public ButtonSelectionView() {
		this.viewController = new AssessmentViewController();
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		try {
			Collection<IMistakeType> mistakeTypes = this.viewController.getMistakeTypesForButtonView();
			mistakeTypes.forEach(type -> {
				Button viewButton = new Button(parent, SWT.PUSH);
				viewButton.setText(type.getButtonName());
			});
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}
