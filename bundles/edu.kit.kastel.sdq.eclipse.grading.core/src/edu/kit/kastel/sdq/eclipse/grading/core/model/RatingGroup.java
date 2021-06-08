package edu.kit.kastel.sdq.eclipse.grading.core.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.grading.api.IRatingGroup;

public class RatingGroup implements IRatingGroup {

	private String shortName;
	private String displayName;
	
	private Collection<MistakeType> mistakeTypes;
	
	@JsonCreator
	public RatingGroup(@JsonProperty("shortName") final String shortName, @JsonProperty("displayName") final String displayName) {
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
