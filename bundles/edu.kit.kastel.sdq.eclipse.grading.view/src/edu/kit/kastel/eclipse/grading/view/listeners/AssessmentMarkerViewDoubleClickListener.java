/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.grading.view.listeners;

import java.util.NoSuchElementException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.views.markers.MarkerItem;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.model.IAnnotation;
import edu.kit.kastel.eclipse.common.view.utilities.AssessmentUtilities;
import edu.kit.kastel.eclipse.grading.view.activator.Activator;
import edu.kit.kastel.eclipse.grading.view.assessment.ArtemisGradingView;
import edu.kit.kastel.eclipse.grading.view.assessment.CustomButtonDialog;

public class AssessmentMarkerViewDoubleClickListener implements IDoubleClickListener {

	private ArtemisGradingView gradingView;

	public AssessmentMarkerViewDoubleClickListener(ArtemisGradingView gradingView) {
		this.gradingView = gradingView;
	}

	@Override
	public void doubleClick(DoubleClickEvent event) {
		if (event.getSelection().isEmpty()) {
			// No element clicked
			return;
		}

		// instanceof is required... :(
		if (event.getSelection()instanceof TreeSelection selection) {
			if (selection.getFirstElement()instanceof MarkerItem item) {
				try {
					IAnnotation annotation = Activator.getDefault().getSystemwideController().getCurrentAssessmentController()
							.getAnnotationByID(item.getMarker().getAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_ANNOTATION_ID).toString())
							.orElseThrow(() -> new NoSuchElementException("Could not find annotation. Please create it again."));

					String customMessage = annotation.getCustomMessage().orElse("");

					CustomButtonDialog dialog = new CustomButtonDialog(AssessmentUtilities.getWindowsShell(), this.gradingView.isPositiveFeedbackAllowed(),
							null, null);

					if (annotation.getMistakeType().isCustomPenalty()) {
						dialog.setCustomPoints(annotation.getCustomPenalty().orElse(null));
						dialog.setForcePenaltyField(true);
					}

					dialog.setCustomMessage(customMessage);
					dialog.setBlockOnOpen(true);
					dialog.open();

					if (!dialog.isClosedByOk()) {
						return;
					}

					String newMessage = dialog.getCustomMessage();
					Double newPenalty = annotation.getMistakeType().isCustomPenalty() //
							? dialog.getCustomPoints()
							: annotation.getCustomPenalty().orElse(null);

					AssessmentUtilities.updateMarkerMessage(item.getMarker(), newMessage, newPenalty);
					Activator.getDefault().getSystemwideController().getCurrentAssessmentController().modifyAnnotation(annotation.getUUID(), newMessage,
							newPenalty);
					this.gradingView.updatePenalties();
				} catch (CoreException | ArtemisClientException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
