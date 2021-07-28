package edu.kit.kastel.sdq.eclipse.grading.core.config;

import java.util.Collection;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IRatingGroup;
import edu.kit.kastel.sdq.eclipse.grading.core.model.MistakeType;
import edu.kit.kastel.sdq.eclipse.grading.core.model.RatingGroup;

@JsonDeserialize(converter = ExerciseConfigConverter.class) //used for adding associations between mistakeType and RatingGroup
public class ExerciseConfig {
	
	private String shortName;
	private Collection<RatingGroup> ratingGroups;
	private Collection<MistakeType> mistakeTypes;
	
	@JsonCreator	
	public ExerciseConfig(
			@JsonProperty("shortName") String shortName, 
			@JsonProperty("ratingGroups") Collection<RatingGroup> ratingGroups,
			@JsonProperty("mistakeTypes") Collection<MistakeType> mistakeTypes) {
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

	public Collection<IRatingGroup> getIRatingGroups() {
		return ratingGroups.stream().map(ratingGroup -> (IRatingGroup) ratingGroup).collect(Collectors.toList());
	}


	public Collection<MistakeType> getMistakeTypes() {
		return mistakeTypes;
	}
	public Collection<IMistakeType> getIMistakeTypes() {
		return mistakeTypes.stream().map(mistakeType -> (IMistakeType) mistakeType).collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return "ExerciseConfig [shortName=" + shortName + ", ratingGroupsSize=" + ratingGroups.size() + ", mistakeTypesSize="
				+ mistakeTypes.size() + "]";
	}
}