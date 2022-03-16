package edu.kit.kastel.eclipse.common.view.marker;

import org.eclipse.ui.views.markers.MarkerField;
import org.eclipse.ui.views.markers.MarkerItem;

/**
 * Class of the mistake type field in the marker view
 * 
 * @See {@link MarkerField}
 *
 */
public class AssessmentErrorTypeField extends MarkerField {

	@Override
	public String getValue(MarkerItem item) {
		return item.getAttributeValue("errorType", "");
	}

}
