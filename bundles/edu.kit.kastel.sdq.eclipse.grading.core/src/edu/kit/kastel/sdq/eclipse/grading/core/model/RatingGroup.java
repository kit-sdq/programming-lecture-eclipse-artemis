package edu.kit.kastel.sdq.eclipse.grading.core.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.kit.kastel.sdq.eclipse.grading.api.IRatingGroup;

public class RatingGroup implements IRatingGroup {

	private String shortName;
	private String displayName;
	
	private Collection<MistakeType> mistakeTypes;
	
	public RatingGroup(final String shortName, final String displayName) {
		this.shortName = shortName;
		this.displayName = displayName;
		this.mistakeTypes = new LinkedList<>();
	}

	@Override
	public String getShortName() {
		return shortName;
	}
	
	@Override
	public String getDisplayName() {
		return displayName;
	}
	
	public Collection<MistakeType> getMistakeTypes() {
		return this.mistakeTypes;
	}
	
	public void addMistakeType(MistakeType mistakeType) {
		this.mistakeTypes.add(mistakeType);
	}

	@Override
	public String toString() {
		return "RatingGroup [shortName=" + shortName + ", displayName=" + displayName
				+ "]";
	}
	
	
}
