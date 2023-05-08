/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.eclipse.common.api.model.IMistakeType;
import edu.kit.kastel.eclipse.common.api.model.IRatingGroup;
import edu.kit.kastel.eclipse.common.api.util.Pair;

public class RatingGroup implements IRatingGroup {

	@JsonProperty("shortName")
	private String identifier;
	@JsonProperty("displayName")
	private String displayName;
	// {"en" -> "Name in English"}
	@JsonProperty("additionalDisplayNames")
	private Map<String, String> additionalDisplayNames;

	@JsonProperty
	private Double positiveLimit;
	@JsonProperty
	private Double negativeLimit;

	private transient List<MistakeType> mistakeTypes = new ArrayList<>();

	public RatingGroup() {
		// NOP
	}

	public void addMistakeType(MistakeType mistakeType) {
		this.mistakeTypes.add(mistakeType);
	}

	@Override
	public String getDisplayName(String languageKey) {
		if (languageKey == null || additionalDisplayNames == null || !additionalDisplayNames.containsKey(languageKey))
			return this.displayName;
		return additionalDisplayNames.get(languageKey);
	}

	@Override
	public List<IMistakeType> getMistakeTypes() {
		return this.mistakeTypes.stream().map(IMistakeType.class::cast).collect(Collectors.toList());
	}

	@Override
	public String getIdentifier() {
		return this.identifier;
	}

	@Override
	public Pair<Double, Double> getRange() {
		return new Pair<>(this.negativeLimit, this.positiveLimit);
	}

	@Override
	public double setToRange(double points) {
		if (negativeLimit != null && points < negativeLimit) {
			return negativeLimit;
		}
		if (positiveLimit != null && points > positiveLimit) {
			return positiveLimit;
		}
		return points;
	}

	@Override
	public int hashCode() {
		return Objects.hash(identifier);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		RatingGroup other = (RatingGroup) obj;
		return Objects.equals(identifier, other.identifier);
	}

}
