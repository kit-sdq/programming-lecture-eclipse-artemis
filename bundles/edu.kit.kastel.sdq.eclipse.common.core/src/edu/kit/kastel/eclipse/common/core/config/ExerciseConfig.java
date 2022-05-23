/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.core.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import edu.kit.kastel.eclipse.common.api.model.IMistakeType;
import edu.kit.kastel.eclipse.common.api.model.IRatingGroup;
import edu.kit.kastel.eclipse.common.core.model.MistakeType;
import edu.kit.kastel.eclipse.common.core.model.RatingGroup;

/**
 * A mapped config file (from {@link JsonFileConfigDao})
 *
 */
@JsonDeserialize(converter = ExerciseConfigConverter.class) // used for adding associations between
															// mistakeType and RatingGroup
public class ExerciseConfig {

	@JsonProperty("shortName")
	private String shortName;
	@JsonProperty("ratingGroups")
	private List<RatingGroup> ratingGroups;
	@JsonProperty("mistakeTypes")
	private List<MistakeType> mistakeTypes;

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