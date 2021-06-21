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

import edu.kit.kastel.sdq.eclipse.grading.api.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.IRatingGroup;
import edu.kit.kastel.sdq.eclipse.grading.core.AssessmentController;
import gui.activator.Activator;
import gui.preferences.PreferenceConstants;
import gui.utilities.AssessmentUtilities;

public class AssessmentViewController {

	private static final String EXERCISE_NAME = "Final Task 2";
	private static String CONFIG_PATH;
	private static boolean IS_RELATIVE_PATH;
	private IAssessmentController assessmentController;

	public AssessmentViewController() {
	}

	public void addAssessmentAnnotaion(IMistakeType mistake, String customMessage, Double customPenalty, String ratingGroupName) {

		final ITextSelection textSelection = AssessmentUtilities.getTextSelection();
		if(textSelection == null) {
			throw new RuntimeErrorException(null, "No text is selected in the editor");
		}
		final Integer startLine = textSelection.getStartLine();
		final Integer endLine = textSelection.getEndLine();
		final Integer lenght = textSelection.getLength();

		//put correct parameter in it

		this.assessmentController.addAnnotation(
				mistake,
				startLine + 1,
				endLine + 1,
				AssessmentUtilities.getPathForAnnotation().toString(),
				customMessage,
				customPenalty);

		IMarker marker = null;
		try {
			marker = AssessmentUtilities.getCurrentFile().createMarker("gui.assessment.marker");
			marker.setAttribute(IMarker.CHAR_START, AssessmentUtilities.getLineOffSet(startLine));
			marker.setAttribute(IMarker.CHAR_END, AssessmentUtilities.getLineOffSet(startLine) + lenght + 1);
			if(mistake != null) {
				marker.setAttribute("errorTypeDescription",mistake.getMessage());
				marker.setAttribute("errorType", mistake.getButtonName());
			}
			marker.setAttribute("start", startLine + 1);
			marker.setAttribute("end", endLine + 1);
			marker.setAttribute("className", AssessmentUtilities.getClassNameForAnnotation());
			marker.setAttribute("ratingGroup", mistake == null ? ratingGroupName : mistake.getRatingGroup());
			if(customMessage != null) {
				marker.setAttribute("customMessage", customMessage);
			}
			if(customPenalty != null) {
				marker.setAttribute("customPenalty", customPenalty);
			}
			if(mistake != null) {
				marker.setAttribute(IMarker.MESSAGE, AssessmentUtilities.createMarkerTooltip(startLine+1, endLine+1, mistake.getButtonName(), mistake.getRatingGroupName(), mistake.getMessage()));
			} else {
				marker.setAttribute(IMarker.MESSAGE,AssessmentUtilities.createMarkerTooltipForCustomButton(startLine+1, endLine+1, customMessage, customPenalty));
			}

		} catch (final CoreException e) {
			e.printStackTrace();
		}

	}


	public void createAssessmentController() {
		this.setConfigPathFromPreferenceStore();
		this.assessmentController = new AssessmentController(this.createConfigFile(),
				AssessmentViewController.EXERCISE_NAME);
	}

	private File createConfigFile() {
		return AssessmentViewController.IS_RELATIVE_PATH ?
					new File(this.getEclipseWorkspaceRootFile(), AssessmentViewController.CONFIG_PATH) :
					new File(AssessmentViewController.CONFIG_PATH);
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

		AssessmentViewController.CONFIG_PATH = AssessmentViewController.IS_RELATIVE_PATH ?
					store.getString(PreferenceConstants.P_RELATIVE_CONFIG_PATH) :
					store.getString(PreferenceConstants.P_ABSOLUTE_CONFIG_PATH);
	}

}
