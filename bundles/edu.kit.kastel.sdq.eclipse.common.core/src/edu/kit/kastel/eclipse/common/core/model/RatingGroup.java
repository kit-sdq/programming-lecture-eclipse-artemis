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
