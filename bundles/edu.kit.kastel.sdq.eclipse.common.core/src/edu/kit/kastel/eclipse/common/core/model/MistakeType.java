/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.core.model;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.eclipse.common.api.model.IAnnotation;
import edu.kit.kastel.eclipse.common.api.model.IMistakeType;
import edu.kit.kastel.eclipse.common.api.model.IRatingGroup;
import edu.kit.kastel.eclipse.common.core.model.rule.PenaltyRule;

public class MistakeType implements IMistakeType {
	private static final Locale DEFAULT_LOCALE = Locale.GERMAN;

	private String shortName;

	private Map<Locale, String> names;
	private Map<Locale, String> messages;

	// used for deserialization
	private String appliesTo;

	private RatingGroup ratingGroup;
	private PenaltyRule penaltyRule;

	/**
	 * This Constructor is used by Deserialization! Using this Constructor means
	 * having to add
	 * <li>the rating group
	 * <li>this object to the rating Group
	 * 
	 * @since 2.7
	 */
	@JsonCreator
	public MistakeType(@JsonProperty("shortName") String shortName, @JsonProperty("button") String buttonName, @JsonProperty("message") String message,
			@JsonProperty("engButton") String englishButton, @JsonProperty("engMessage") String englishMessage,
			@JsonProperty("penaltyRule") PenaltyRule penaltyRule, @JsonProperty("appliesTo") String appliesTo) { // Map(string, string)
		this.shortName = shortName;

		// locale -> getCountry

		messages = new HashMap<>();
		messages.put(Locale.US, englishMessage);
		messages.put(Locale.GERMAN, message);

		names = new HashMap<>();
		names.put(Locale.US, englishButton);
		names.put(Locale.GERMAN, buttonName);

		this.penaltyRule = penaltyRule;
		this.appliesTo = appliesTo;

	}

	@Override
	public double calculate(List<IAnnotation> annotations) {
		assert annotations.stream().allMatch(a -> this.equals(a.getMistakeType()));
		return this.penaltyRule.calculate(annotations);
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
		return this.messages.get(DEFAULT_LOCALE);
	}

	public String getMessage(Locale locale) {
		return this.messages.get(locale);
	}

	@Override
	public String getButtonText() {
		return this.names.get(DEFAULT_LOCALE);
	}

	public String getButtonText(Locale locale) {
		return this.names.get(locale);
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
	public String getTooltip(Locale locale, List<IAnnotation> annotations) {
		String penaltyText = this.penaltyRule.getTooltip(annotations);
		return this.getMessage(locale) + "\n" + penaltyText;
	}
	
	public String getTooltip(List<IAnnotation> annotations) {
		String penaltyText = this.penaltyRule.getTooltip(annotations);
		return this.getMessage() + "\n" + penaltyText;
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
		return "MistakeType [shortName=" + this.shortName + ", name=" + this.getButtonText() + ", message=" + this.getMessage() + ", ratingGroup="
				+ this.ratingGroup + ", penaltyRule=" + this.penaltyRule + "]";
	}

	@Override
	public boolean isCustomPenalty() {
		return this.penaltyRule.isCustomPenalty();
	}

	@Override
	public int hashCode() {
		return Objects.hash(appliesTo, penaltyRule, shortName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		MistakeType other = (MistakeType) obj;
		return Objects.equals(appliesTo, other.appliesTo) && Objects.equals(penaltyRule, other.penaltyRule) && Objects.equals(shortName, other.shortName);
	}

}
