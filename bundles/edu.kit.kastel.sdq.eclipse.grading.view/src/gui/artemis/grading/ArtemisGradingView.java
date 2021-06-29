package gui.artemis.grading;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

public class ArtemisGradingView extends ViewPart {

	private final AssessmentViewController viewController;
	private Collection<IMistakeType> mistakeTypes;
	private ArrayList<IRatingGroup> ratingGroups;
	private int showcaseId;
	private Map<String, Group> ratingGroupViewElements;

	public ArtemisGradingView() {
		this.viewController = new AssessmentViewController();
		this.ratingGroupViewElements = new HashMap<String, Group>();
	}

	private void createCustomButton(IRatingGroup ratingGroup, Group rgDisplay, Composite parent) {
		final Button customButton = new Button(rgDisplay, SWT.PUSH);
		customButton.setText("Custom");
		customButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				final CustomButtonDialog customDialog = new CustomButtonDialog(AssessmentUtilities.getWindowsShell(),
						ArtemisGradingView.this.viewController, ratingGroup.getDisplayName());
				customDialog.open();
			}
		});
	}

	private void createErrorTypesButtons(Composite parent) {
		final ScrolledComposite sc2 = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		sc2.setExpandHorizontal(true);
		sc2.setExpandVertical(true);
		final Composite child = new Composite(sc2, SWT.NONE);
		sc2.setContent(child);
		child.setLayout(new GridLayout(this.ratingGroups.size(), true));
		Group artemisActionsGroup = new Group(child, SWT.NULL);
		artemisActionsGroup.setText("Grading Actions");
		final GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 1;
		artemisActionsGroup.setLayout(gridLayout2);
		final GridData gridData2 = new GridData(GridData.VERTICAL_ALIGN_FILL);
		gridData2.horizontalSpan = 3;
		artemisActionsGroup.setLayoutData(gridData2);
		this.createStartAssessmentButton(artemisActionsGroup);
		this.createSaveAssessmentButton(artemisActionsGroup);
		this.ratingGroups.forEach(element -> {
			final Group rgDisplay = new Group(child, SWT.NULL);
			this.ratingGroupViewElements.put(element.getDisplayName(), rgDisplay);
			rgDisplay.setText(element.getDisplayName() + " ("
					+ this.viewController.getCurrentPenaltyForRatingGroup(element) + " penalty points)");
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
							ArtemisGradingView.this.viewController.addAssessmentAnnotaion(mistake, null, null,
									mistake.getRatingGroupName());
							ArtemisGradingView.this.updatePenalties(mistake.getRatingGroupName());
						}
					});
				}
			});
			this.createCustomButton(element, rgDisplay, parent);
		});
		sc2.setMinSize(child.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	protected void updatePenalties(String ratingGroupName) {
		Group viewElement = this.ratingGroupViewElements.get(ratingGroupName);
		viewElement.setText(ratingGroupName + " ("
				+ this.viewController.getCurrentPenaltyForRatingGroup(this.findRatingGroup(ratingGroupName))
				+ " penalty points)");
	}

	private IRatingGroup findRatingGroup(String ratingGroupName) {
		for (int i = 0; i < this.ratingGroups.size(); i++) {
			if (this.ratingGroups.get(i).getDisplayName().equals(ratingGroupName)) {
				return this.ratingGroups.get(i);
			}
		}
		return null;
	}

	@Override
	public void createPartControl(Composite parent) {
		try {
			this.mistakeTypes = this.viewController.getMistakeTypesForButtonView();
			this.ratingGroups = (ArrayList<IRatingGroup>) this.viewController.getRatingGroups();

		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			this.createErrorTypesButtons(parent);
		}
	}

	@Override
	public void setFocus() {
	}

	private void createSaveAssessmentButton(Group artemisActionsGroup) {
		final Button saveAssessmentButton = new Button(artemisActionsGroup, SWT.PUSH);
		saveAssessmentButton.setText("Save Assessment");
		saveAssessmentButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				try {
					ArtemisGradingView.this.viewController.saveAssessmentShowcase(ArtemisGradingView.this.showcaseId);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void createStartAssessmentButton(Group artemisActionsGroup) {
		final Button startAssessmentButton = new Button(artemisActionsGroup, SWT.PUSH);
		startAssessmentButton.setText("Start Assessment");
		startAssessmentButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				try {
					ArtemisGradingView.this.showcaseId = ArtemisGradingView.this.viewController
							.startAssessmentShowcase();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}
}
