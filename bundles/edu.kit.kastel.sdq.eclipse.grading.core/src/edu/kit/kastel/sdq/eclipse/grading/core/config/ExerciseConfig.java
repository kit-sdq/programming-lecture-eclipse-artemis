package edu.kit.kastel.sdq.eclipse.grading.core.config;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IRatingGroup;
import edu.kit.kastel.sdq.eclipse.grading.core.model.MistakeType;
import edu.kit.kastel.sdq.eclipse.grading.core.model.RatingGroup;

/**
 * A mapped config file (from {@link JsonFileConfigDao})
 *
 */
@JsonDeserialize(converter = ExerciseConfigConverter.class) //used for adding associations between mistakeType and RatingGroup
public class ExerciseConfig {

	private String shortName;
	private List<RatingGroup> ratingGroups;
	private List<MistakeType> mistakeTypes;

	@JsonCreator
	public ExerciseConfig(
			@JsonProperty("shortName") String shortName,
			@JsonProperty("ratingGroups") List<RatingGroup> ratingGroups,
			@JsonProperty("mistakeTypes") List<MistakeType> mistakeTypes) {
		this.shortName = shortName;
		this.ratingGroups = ratingGroups;
		this.mistakeTypes = mistakeTypes;
	}

	public List<IMistakeType> getIMistakeTypes() {
		return this.mistakeTypes.stream().map(IMistakeType.class::cast).collect(Collectors.toList());
	}

	public List<IRatingGroup> getIRatingGroups() {
		return this.ratingGroups.stream().map(IRatingGroup.class::cast).collect(Collectors.toList());
	}

	public List<MistakeType> getMistakeTypes() {
		return this.mistakeTypes;
	}


	public List<RatingGroup> getRatingGroups() {
		return this.ratingGroups;
	}
	public String getShortName() {
		return this.shortName;
	}

	@Override
	public String toString() {
		return "ExerciseConfig [shortName=" + this.shortName + ", ratingGroupsSize=" + this.ratingGroups.size() + ", mistakeTypesSize="
				+ this.mistakeTypes.size() + "]";
	}
}