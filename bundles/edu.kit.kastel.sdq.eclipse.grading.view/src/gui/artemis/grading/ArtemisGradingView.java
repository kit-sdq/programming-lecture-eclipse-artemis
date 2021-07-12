package gui.artemis.grading;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.part.ViewPart;

import edu.kit.kastel.sdq.eclipse.grading.api.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.IRatingGroup;
import gui.controllers.AssessmentViewController;
import gui.utilities.AssessmentUtilities;

public class ArtemisGradingView extends ViewPart {

	private final AssessmentViewController viewController;
	private Collection<IMistakeType> mistakeTypes;
	private ArrayList<IRatingGroup> ratingGroups;
	private Map<String, Group> ratingGroupViewElements;
	private int courseID;
	private int exerciseID;
	private int submissionID;
	private boolean errorTypesCreated;
	private ScrolledComposite scrolledComposite;
	private final String MARKER_NAME = "gui.assessment.marker";

	public ArtemisGradingView() {
		this.viewController = new AssessmentViewController();
		this.ratingGroupViewElements = new HashMap<String, Group>();
		this.errorTypesCreated = false;
		this.addListenerForDeleteMarker();
	}

	private void addListenerForDeleteMarker() {
		IWorkspace workspace = AssessmentUtilities.getWorkspace();
		IResourceChangeListener listener = new IResourceChangeListener() {

			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				Arrays.asList(event.findMarkerDeltas(ArtemisGradingView.this.MARKER_NAME, true)).forEach(marker -> {
					if (marker.getKind() == 2) {
						ArtemisGradingView.this.viewController.deleteAnnotation(marker.getId());
					}
				});

			}
		};

		workspace.addResourceChangeListener(listener);
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

	private void createCourseAndExerciseListView(Composite composite) {
		Composite listHolder = new Composite(composite, SWT.NONE);
		listHolder.setLayout(new GridLayout(2, true));
		Label courseListLabel = new Label(listHolder, SWT.NONE);
		courseListLabel.setText("Courses:");
		final List courseList = new List(listHolder, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		Label exerciseListLabel = new Label(listHolder, SWT.NONE);
		exerciseListLabel.setText("Exercises: ");
		final List exerciseList = new List(listHolder, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		this.viewController.getCourses().forEach(course -> {
			courseList.add(course.getTitle());
		});
		courseList.addListener(SWT.Selection, e -> {
			this.createExerciseListInput(courseList.getSelection()[0], exerciseList);
		});
	}

	private void createExerciseListInput(String courseTitle, List exerciseList) {
		exerciseList.removeAll();
		Optional<ICourse> optionalCourse = this.viewController.getCourses().stream()
				.filter(course -> course.getTitle().equals(courseTitle)).findFirst();
		if (optionalCourse.isPresent()) {
			this.courseID = optionalCourse.get().getCourseId();
			optionalCourse.get().getExercises().forEach(exercise -> {
				exerciseList.add(exercise.getShortName());
			});
		}
		exerciseList.addListener(SWT.Selection, e -> {
			optionalCourse.get().getExercises().forEach(exercise -> {
				if (exercise.getShortName().equals(exerciseList.getSelection()[0])) {
					this.exerciseID = exercise.getExerciseId();
				}
			});
		});
	}

	private void createArtemisGradingViewElements(Composite parent) {
		Composite child = this.createScrolledComposite(parent);
		this.createCourseAndExerciseListView(child);
		this.createGradingActionViewElements(child, parent);
		this.computeSize(this.scrolledComposite, child);
	}

	private void createAssessmentViewElements(Composite child, Composite parent) {
		this.ratingGroups.forEach(element -> {
			final Group rgDisplay = new Group(child, SWT.NULL);
			this.ratingGroupViewElements.put(element.getDisplayName(), rgDisplay);
			this.updatePenalties(element.getDisplayName());
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
		this.computeSize(this.scrolledComposite, child);
	}

	private Composite createScrolledComposite(Composite parent) {
		this.scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		this.scrolledComposite.setExpandHorizontal(true);
		this.scrolledComposite.setExpandVertical(true);
		final Composite child = new Composite(this.scrolledComposite, SWT.NONE);
		this.scrolledComposite.setContent(child);
		child.setLayout(new GridLayout(3, true));
		this.computeSize(this.scrolledComposite, child);
		return child;
	}

	private void computeSize(ScrolledComposite sc2, Composite child) {
		this.scrolledComposite.setContent(child);
		sc2.setMinSize(child.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private void createGradingActionViewElements(Composite child, Composite parent) {
		Group artemisActionsGroup = new Group(child, SWT.NULL);
		artemisActionsGroup.setText("Grading Actions");
		final GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 1;
		artemisActionsGroup.setLayout(gridLayout2);
		final GridData gridData2 = new GridData(GridData.VERTICAL_ALIGN_FILL);
		gridData2.horizontalSpan = 3;
		artemisActionsGroup.setLayoutData(gridData2);
		this.createStartAssessmentButton(artemisActionsGroup, child, parent);
		this.createSaveAssessmentButton(artemisActionsGroup);
	}

	protected void updatePenalties(String ratingGroupName) {
		Group viewElement = this.ratingGroupViewElements.get(ratingGroupName);
		viewElement.setText(ratingGroupName + " ("
				+ this.viewController.getCurrentPenaltyForRatingGroup(this.findRatingGroup(ratingGroupName)) + "/"
				+ this.findRatingGroup(ratingGroupName).getPenaltyLimit() + " penalty points)");
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
		this.createArtemisGradingViewElements(parent);
	}

	@Override
	public void setFocus() {
	}

	private void createSaveAssessmentButton(Group artemisActionsGroup) {
		final Button saveAssessmentButton = new Button(artemisActionsGroup, SWT.PUSH);
		saveAssessmentButton.setText("Save current Assessment");
		saveAssessmentButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				try {
					ArtemisGradingView.this.viewController.submitAssessment(ArtemisGradingView.this.submissionID);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void createStartAssessmentButton(Group artemisActionsGroup, Composite child, Composite parent) {
		final Button startAssessmentButton = new Button(artemisActionsGroup, SWT.PUSH);
		startAssessmentButton.setText("Start next Assessment");
		startAssessmentButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				try {
					ArtemisGradingView.this.submissionID = ArtemisGradingView.this.viewController
							.startAssessment(ArtemisGradingView.this.exerciseID, ArtemisGradingView.this.courseID);
					if (!ArtemisGradingView.this.errorTypesCreated) {
						ArtemisGradingView.this.viewController
								.createAssessmentController(ArtemisGradingView.this.submissionID);
						ArtemisGradingView.this.mistakeTypes = ArtemisGradingView.this.viewController
								.getMistakeTypesForButtonView();
						ArtemisGradingView.this.ratingGroups = (ArrayList<IRatingGroup>) ArtemisGradingView.this.viewController
								.getRatingGroups();
						ArtemisGradingView.this.createAssessmentViewElements(child, parent);
						ArtemisGradingView.this.errorTypesCreated = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}
}
