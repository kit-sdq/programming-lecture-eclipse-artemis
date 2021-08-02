package edu.kit.kastel.eclipse.grading.gui.marker;

import org.eclipse.ui.views.markers.MarkerField;
import org.eclipse.ui.views.markers.MarkerItem;

/**
 * Class of the rating group field in the marker view
 * 
 * @See {@link MarkerField}
 *
 */
public class AssessmentRatingGroupField extends MarkerField {

	@Override
	public String getValue(MarkerItem item) {
		return item.getAttributeValue("ratingGroup", "");
	}

}
