package edu.kit.kastel.eclipse.grading.view.commands;

import java.util.Arrays;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import edu.kit.kastel.eclipse.common.api.model.IMistakeType;

public class AnnotationFilter extends ViewerFilter {
	private String filter;
	
	public void setFilterString(String filter) {
		this.filter = filter.toLowerCase();
	}
	
	@Override
	public boolean select(Viewer viewer, Object parent, Object element) {
		if (this.filter != null) {
			IMistakeType mistake = (IMistakeType) element;
			return matchesPart(mistake.getButtonText().toLowerCase(), this.filter)
					|| matchesPart(mistake.getMessage().toLowerCase(), this.filter);
		} else {
			return true;
		}
	}
	
	private boolean matchesPart(String text, String filter) {
		return Arrays.stream(text.split(" ")).anyMatch(w -> w.startsWith(this.filter));
	}
}
