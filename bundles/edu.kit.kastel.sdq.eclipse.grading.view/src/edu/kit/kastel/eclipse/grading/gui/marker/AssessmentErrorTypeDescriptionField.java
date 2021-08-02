package edu.kit.kastel.eclipse.grading.gui.marker;

import org.eclipse.ui.views.markers.MarkerField;
import org.eclipse.ui.views.markers.MarkerItem;

/**
 * Class of the mistake type description field in the marker view
 * 
 * @See {@link MarkerField}
 *
 */
public class AssessmentErrorTypeDescriptionField extends MarkerField {

	@Override
	public String getValue(MarkerItem item) {
		return item.getAttributeValue("errorTypeDescription", "");
	}

}