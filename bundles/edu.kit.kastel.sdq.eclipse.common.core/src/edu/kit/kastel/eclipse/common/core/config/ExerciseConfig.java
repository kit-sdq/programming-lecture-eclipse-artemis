/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.core.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.model.IMistakeType;
import edu.kit.kastel.eclipse.common.api.model.IRatingGroup;
import edu.kit.kastel.eclipse.common.core.model.MistakeType;
import edu.kit.kastel.eclipse.common.core.model.RatingGroup;

/**
 * A mapped config file (from {@link JsonFileConfigDAO})
 */
@JsonDeserialize(converter = ExerciseConfigConverter.class)
public class ExerciseConfig {

	@JsonProperty("shortName")
	private String shortName;
	@JsonProperty("allowedExercises")
	private List<Integer> allowedExercises;
	@JsonProperty("ratingGroups")
	private List<RatingGroup> ratingGroups;
	@JsonProperty("mistakeTypes")
	private List<MistakeType> mistakeTypes;
	@JsonProperty("positiveFeedbackAllowed")
	private Boolean isPositiveFeedbackAllowed;

	public List<Integer> getAllowedExercises() {
		return Collections.unmodifiableList(this.allowedExercises == null ? List.of() : this.allowedExercises);
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

	/**
	 * Modify mistakeTypes of config for the current exercise
	 *
	 * @param exercise the exercise
	 */
	public void initialize(IExercise exercise) {
		this.mistakeTypes.forEach(e -> e.initialize(exercise));
	}

	public boolean isPositiveFeedbackAllowed() {
		return this.isPositiveFeedbackAllowed == null ? true : this.isPositiveFeedbackAllowed;
	}

}