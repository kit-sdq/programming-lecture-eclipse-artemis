package gui.controllers;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.management.RuntimeErrorException;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextSelection;

import edu.kit.kastel.sdq.eclipse.grading.api.IArtemisGUIController;
import edu.kit.kastel.sdq.eclipse.grading.api.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.IRatingGroup;
import edu.kit.kastel.sdq.eclipse.grading.core.SystemwideController;
import gui.activator.Activator;
import gui.preferences.PreferenceConstants;
import gui.utilities.AssessmentUtilities;

public class AssessmentViewController {

	private static final String EXERCISE_NAME = "Final Task 1";
	private static String CONFIG_PATH;
	private static boolean IS_RELATIVE_PATH;
	private IAssessmentController assessmentController;
	private IArtemisGUIController artemisGUIController;
	private SystemwideController systemwideController;

	public AssessmentViewController() {
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		this.systemwideController = new SystemwideController(
				new File(store.getString(PreferenceConstants.P_ABSOLUTE_CONFIG_PATH)),
				store.getDefaultString(PreferenceConstants.P_ARTEMIS_URL),
				store.getDefaultString(PreferenceConstants.P_ARTEMIS_USER),
				store.getDefaultString(PreferenceConstants.P_ARTEMIS_PASSWORD));
		this.artemisGUIController = this.systemwideController.getArtemisGUIController();
		this.assessmentController = this.systemwideController.getAssessmentController(EXERCISE_NAME);
	}

	public void addAssessmentAnnotaion(IMistakeType mistake, String customMessage, Double customPenalty,
			String ratingGroupName) {

		final ITextSelection textSelection = AssessmentUtilities.getTextSelection();
		if (textSelection == null) {
			throw new RuntimeErrorException(null, "No text is selected in the editor");
		}
		final Integer startLine = textSelection.getStartLine();
		final Integer endLine = textSelection.getEndLine();
		final Integer lenght = textSelection.getLength();

		this.assessmentController.addAnnotation(mistake, startLine + 1, endLine + 1,
				AssessmentUtilities.getPathForAnnotation(), customMessage, customPenalty);

		IMarker marker = null;
		try {
			marker = AssessmentUtilities.getCurrentFile().createMarker("gui.assessment.marker");
			marker.setAttribute(IMarker.CHAR_START, AssessmentUtilities.getLineOffSet(startLine));
			marker.setAttribute(IMarker.CHAR_END, AssessmentUtilities.getLineOffSet(startLine) + lenght + 10);
			if (mistake != null) {
				marker.setAttribute("errorTypeDescription", mistake.getMessage());
				marker.setAttribute("errorType", mistake.getButtonName());
			}
			marker.setAttribute("start", startLine + 1);
			marker.setAttribute("end", endLine + 1);
			marker.setAttribute("className", AssessmentUtilities.getClassNameForAnnotation());
			marker.setAttribute("ratingGroup", mistake == null ? ratingGroupName : mistake.getRatingGroupName());
			if (customMessage != null) {
				marker.setAttribute("customMessage", customMessage);
			}
			if (customPenalty != null) {
				marker.setAttribute("customPenalty", customPenalty);
			}
			if (mistake != null) {
				marker.setAttribute(IMarker.MESSAGE, AssessmentUtilities.createMarkerTooltip(startLine + 1, endLine + 1,
						mistake.getButtonName(), mistake.getRatingGroupName(), mistake.getMessage()));
			} else {
				marker.setAttribute(IMarker.MESSAGE, AssessmentUtilities
						.createMarkerTooltipForCustomButton(startLine + 1, endLine + 1, customMessage, customPenalty));
			}

		} catch (final CoreException e) {
			e.printStackTrace();
		}

	}

	public void createAssessmentController() {
		this.setConfigPathFromPreferenceStore();
	}

	private File createConfigFile() {
		return AssessmentViewController.IS_RELATIVE_PATH
				? new File(this.getEclipseWorkspaceRootFile(), AssessmentViewController.CONFIG_PATH)
				: new File(AssessmentViewController.CONFIG_PATH);
	}

	public double getCurrentPenaltyForRatingGroup(IRatingGroup ratingGroup) {
		try {
			return this.assessmentController.calculateCurrentPenaltyForRatingGroup(ratingGroup);
		} catch (final IOException e) {
			return -1;
		}
	}

	private File getEclipseWorkspaceRootFile() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
	}

	public Collection<IMistakeType> getMistakeTypesForButtonView() throws IOException {
		return this.assessmentController.getMistakes();
	}

	public Collection<IRatingGroup> getRatingGroups() throws IOException {
		return this.assessmentController.getRatingGroups();
	}

	private void setConfigPathFromPreferenceStore() {
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		AssessmentViewController.IS_RELATIVE_PATH = store.getBoolean(PreferenceConstants.P_IS_RELATIVE_CONFIG_PATH);

		AssessmentViewController.CONFIG_PATH = AssessmentViewController.IS_RELATIVE_PATH
				? store.getString(PreferenceConstants.P_RELATIVE_CONFIG_PATH)
				: store.getString(PreferenceConstants.P_ABSOLUTE_CONFIG_PATH);
	}

	public int startAssessmentShowcase() throws Exception {
		int submissonId = this.artemisGUIController.downloadHardcodedExerciseAndSubmissionExample();
		final String exerciseName = "Final Task 1";
		this.artemisGUIController.startAssessment(submissonId, exerciseName);
		return submissonId;
	}

	public void saveAssessmentShowcase(int submissonId) throws Exception {
		final String exerciseName = "Final Task 1";
		System.out.println("ANNOTATIONS:" + this.assessmentController.getAnnotations().toString());
		this.artemisGUIController.submitAssessment(submissonId, exerciseName);
	}

}
