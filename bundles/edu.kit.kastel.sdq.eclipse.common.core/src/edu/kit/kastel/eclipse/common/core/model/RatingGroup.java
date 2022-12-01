/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.eclipse.common.api.model.IMistakeType;
import edu.kit.kastel.eclipse.common.api.model.IRatingGroup;
import edu.kit.kastel.eclipse.common.api.util.Pair;

public class RatingGroup implements IRatingGroup {

	@JsonProperty
	private String shortName;
	@JsonProperty
	private String displayName;
	@JsonProperty
	private String engDisplayName;
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
	public String getDisplayName() {
		return this.displayName;
	}
	
	@Override
	/**
	 * @since 2.7
	 */
	public String getLanguageSensitiveDisplayName(String language) {
		// TODO Auto-generated method stub
		switch (language) {
			case "English":
				return engDisplayName;
			case "Deutsch":
				return displayName;
			default:
				return getDisplayName();
		}
	}

	@Override
	public List<IMistakeType> getMistakeTypes() {
		return this.mistakeTypes.stream().map(IMistakeType.class::cast).collect(Collectors.toList());
	}

	@Override
	public String getShortName() {
		return this.shortName;
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
}
