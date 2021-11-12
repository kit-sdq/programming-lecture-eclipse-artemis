package edu.kit.kastel.eclipse.student.view.marker;

import org.eclipse.ui.views.markers.MarkerField;
import org.eclipse.ui.views.markers.MarkerItem;

/**
 * Class of the custom message field in the marker view
 * 
 * @See {@link MarkerField}
 *
 */
public class AssessmentMarkerCustomMessageField extends MarkerField {

	@Override
	public String getValue(MarkerItem item) {
		return item.getAttributeValue("customMessage", "");
	}

}
