package edu.kit.kastel.eclipse.grading.view.marker;

import org.eclipse.ui.views.markers.MarkerSupportView;

import edu.kit.kastel.eclipse.grading.view.utilities.AssessmentUtilities;

/**
 * Class of the marker view
 * 
 * @See {@link MarkerSupportView}
 *
 */
public class AssessmentMarkerView extends MarkerSupportView {

	public AssessmentMarkerView() {
		super(AssessmentUtilities.MARKER_VIEW_ID);
	}

}
