package edu.kit.kastel.eclipse.grading.view.listeners;

import java.util.NoSuchElementException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.views.markers.MarkerItem;

import edu.kit.kastel.eclipse.common.view.utilities.AssessmentUtilities;
import edu.kit.kastel.eclipse.grading.view.activator.Activator;
import edu.kit.kastel.eclipse.grading.view.assessment.ArtemisGradingView;
import edu.kit.kastel.eclipse.grading.view.assessment.CustomButtonDialog;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IAnnotation;

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
		if (event.getSelection() instanceof TreeSelection) {
			TreeSelection selection = (TreeSelection) event.getSelection();
			if (selection.getFirstElement() instanceof MarkerItem) {
				MarkerItem item = (MarkerItem) selection.getFirstElement();
				
				try {					
					IAnnotation annotation = Activator.getDefault().getSystemwideController().getCurrentAssessmentController().getAnnotationByUUID(item.getMarker().getAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_ANNOTATION_ID).toString()).orElseThrow(() -> new NoSuchElementException("Could not find annotation. Please create it again.")); 
					
					String customMessage = annotation.getCustomMessage().orElse("");
					
					CustomButtonDialog dialog = new CustomButtonDialog(AssessmentUtilities.getWindowsShell(), null, null, null);
					
					if (annotation.getMistakeType().isCustomPenalty()) {
						dialog.setCustomPenalty(annotation.getCustomPenalty().orElse(0d));
						dialog.setForcePenaltyField(true);
					}
					
					dialog.setCustomMessage(customMessage);
					dialog.setBlockOnOpen(true);
					dialog.open();
					
					String newMessage = dialog.getCustomMessage();
					Double newPenalty = annotation.getMistakeType().isCustomPenalty() ? dialog.getCustomPenalty() : annotation.getCustomPenalty().orElse(0d);
					
					item.getMarker().setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_CUSTOM_MESSAGE, newMessage);
					item.getMarker().setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_CUSTOM_PENALTY, newPenalty.toString());
					

					
					Activator.getDefault().getSystemwideController().getCurrentAssessmentController().modifyAnnotation(annotation.getUUID(), newMessage, newPenalty);
					this.gradingView.updatePenalties();
				} catch (CoreException e) {
					e.printStackTrace();
				}				
			}
		}
	}

}
