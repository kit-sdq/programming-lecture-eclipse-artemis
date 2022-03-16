package edu.kit.kastel.sdq.eclipse.grading.core.config;

import java.util.ArrayList;
import java.util.List;

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
@JsonDeserialize(converter = ExerciseConfigConverter.class) // used for adding associations between mistakeType and RatingGroup
public class ExerciseConfig {

	private String shortName;
	private List<RatingGroup> ratingGroups;
	private List<MistakeType> mistakeTypes;

	@JsonCreator
	public ExerciseConfig(//
			@JsonProperty("shortName") String shortName, //
			@JsonProperty("ratingGroups") List<RatingGroup> ratingGroups, //
			@JsonProperty("mistakeTypes") List<MistakeType> mistakeTypes //
	) {
		this.shortName = shortName;
		this.ratingGroups = ratingGroups;
		this.mistakeTypes = mistakeTypes;
	}

	public List<IMistakeType> getIMistakeTypes() {
		return new ArrayList<>(this.mistakeTypes);
	}

	public List<IRatingGroup> getIRatingGroups() {
		return new ArrayList<>(this.ratingGroups);
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

}