/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.core.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.eclipse.common.api.model.IAnnotation;
import edu.kit.kastel.eclipse.common.api.model.IMistakeType;
import edu.kit.kastel.eclipse.common.api.model.IRatingGroup;

public class MistakeType implements IMistakeType {
	private String shortName;
	private String name;
	private String message;

	// used for deserialization
	private String appliesTo;

	private RatingGroup ratingGroup;
	private PenaltyRule penaltyRule;

	/**
	 * This Constructor is used by Deserialization! Using this Constructor means
	 * having to add
	 * <li>the rating group
	 * <li>this object to the rating Group
	 */
	@JsonCreator
	public MistakeType(@JsonProperty("shortName") String shortName, @JsonProperty("button") String buttonName, @JsonProperty("message") String message,
			@JsonProperty("penaltyRule") PenaltyRule penaltyRule, @JsonProperty("appliesTo") String appliesTo) {
		this.shortName = shortName;
		this.name = buttonName;
		this.message = message;
		this.penaltyRule = penaltyRule;

		this.appliesTo = appliesTo;
	}

	@Override
	public double calculatePenalty(List<IAnnotation> annotations) {
		return this.penaltyRule.calculatePenalty(annotations);
	}

	/**
	 *
	 * @return to which rating group this applies. Used for deserialization...
	 */
	public String getAppliesTo() {
		return this.appliesTo;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	public String getButtonText() {
		return this.name;
	}

	@Override
	public String getId() {
		return this.shortName;
	}

	@Override
	public String getPenaltyName() {
		return this.penaltyRule.getShortName();
	}

	public PenaltyRule getPenaltyRule() {
		return this.penaltyRule;
	}

	@Override
	public IRatingGroup getRatingGroup() {
		return this.ratingGroup;
	}

	public String getShortName() {
		return this.shortName;
	}

	@Override
	public String getTooltip(List<IAnnotation> annotations) {
		String penaltyText = this.penaltyRule.getTooltip(annotations);
		return this.message + "\n" + penaltyText;
	}

	/**
	 * Sets a new rating group if there ain't already one. (Used for
	 * deserialization).
	 *
	 * @param ratingGroup the new rating group
	 */
	public void setRatingGroup(RatingGroup ratingGroup) {
		if (this.ratingGroup == null) {
			this.ratingGroup = ratingGroup;
		}
	}

	@Override
	public String toString() {
		return "MistakeType [shortName=" + this.shortName + ", name=" + this.name + ", message=" + this.message + ", ratingGroup=" + this.ratingGroup
				+ ", penaltyRule=" + this.penaltyRule + "]";
	}

	@Override
	public boolean isCustomPenalty() {
		return this.penaltyRule.isCustomPenalty();
	}
}
