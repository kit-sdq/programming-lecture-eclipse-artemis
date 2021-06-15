package gui.marker.view;

import org.eclipse.ui.views.markers.MarkerField;
import org.eclipse.ui.views.markers.MarkerItem;

public class AssessmentErrorTypeDescriptionField extends MarkerField {

	@Override
	public String getValue(MarkerItem item) {
		return item.getAttributeValue("errorTypeDescription", "No error type description defined");
	}

}