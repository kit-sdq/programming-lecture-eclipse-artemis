package edu.kit.kastel.sdq.eclipse.grading.core.config;

import java.util.Collection;

import edu.kit.kastel.sdq.eclipse.grading.core.model.MistakeType;
import edu.kit.kastel.sdq.eclipse.grading.core.model.RatingGroup;

public class ExerciseConfig {
	
	private String shortName;
	private Collection<RatingGroup> ratingGroups;
	private Collection<MistakeType> mistakeTypes;
	
	public ExerciseConfig(String shortName, Collection<RatingGroup> ratingGroups,
			Collection<MistakeType> mistakeTypes) {
		this.shortName = shortName;
		this.ratingGroups = ratingGroups;
		this.mistakeTypes = mistakeTypes;
	}

	public String getShortName() {
		return shortName;
	}

	public Collection<RatingGroup> getRatingGroups() {
		return ratingGroups;
	}

	public Collection<MistakeType> getMistakeTypes() {
		return mistakeTypes;
	}

	@Override
	public String toString() {
		return "ExerciseConfig [shortName=" + shortName + ", ratingGroupsSize=" + ratingGroups.size() + ", mistakeTypesSize="
				+ mistakeTypes.size() + "]";
	}
}