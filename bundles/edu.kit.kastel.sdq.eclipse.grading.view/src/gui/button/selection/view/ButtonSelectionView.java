package gui.button.selection.view;

import java.io.IOException;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.part.ViewPart;

import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.IRatingGroup;
import gui.controllers.AssessmentViewController;
import gui.utilities.AssessmentUtilities;

public class ButtonSelectionView extends ViewPart {

	private final AssessmentViewController viewController;
	private Collection<IMistakeType> mistakeTypes;
	private Collection<IRatingGroup> ratingGroups;

	public ButtonSelectionView() {
		this.viewController = new AssessmentViewController();
		this.viewController.createAssessmentController();
	}


	private void createCustomButton(IRatingGroup ratingGroup, Group rgDisplay, Composite parent) {
		final Button customButton = new Button(rgDisplay, SWT.PUSH);
		customButton.setText("Custom");
		customButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				final CustomButtonDialog customDialog = new CustomButtonDialog(AssessmentUtilities.getWindowsShell(), ButtonSelectionView.this.viewController, ratingGroup.getDisplayName());
				customDialog.open();
			}
		});
	}

	public void createErrorTypesButtons(Composite parent) {
		final ScrolledComposite sc2 = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		sc2.setExpandHorizontal(true);
		sc2.setExpandVertical(true);
		final Composite child = new Composite(sc2, SWT.NONE);
		sc2.setContent(child);
		child.setLayout(new GridLayout(this.ratingGroups.size(), true));
		this.ratingGroups.forEach(element -> {
			final Group rgDisplay = new Group(child, SWT.NULL);
			rgDisplay.setText(element.getDisplayName() + " (" +
			this.viewController.getCurrentPenaltyForRatingGroup(element) + " penalty points)");
			final GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 3;
			rgDisplay.setLayout(gridLayout);
			final GridData gridData = new GridData(GridData.VERTICAL_ALIGN_FILL);
			gridData.horizontalSpan = 3;
			rgDisplay.setLayoutData(gridData);
			this.mistakeTypes.forEach(mistake -> {
				if (mistake.getRatingGroup().getDisplayName().equals(element.getDisplayName())) {
					final Button mistakeButton = new Button(rgDisplay, SWT.PUSH);
					mistakeButton.setText(mistake.getButtonName());
					mistakeButton.addListener(SWT.Selection, new Listener() {

						@Override
						public void handleEvent(Event event) {
							ButtonSelectionView.this.viewController.addAssessmentAnnotaion(mistake, null, null, null);
						}
					});
				}
			});
			this.createCustomButton(element, rgDisplay, parent);
		});
		sc2.setMinSize(child.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	@Override
	public void createPartControl(Composite parent) {
		try {
			this.mistakeTypes = this.viewController.getMistakeTypesForButtonView();
			this.ratingGroups = this.viewController.getRatingGroups();

		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			this.createErrorTypesButtons(parent);
		}
	}

	@Override
	public void setFocus() {
	}

}
