/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.common.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.common.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.common.api.model.IRatingGroup;

public class RatingGroup implements IRatingGroup {

	private String shortName;
	private String displayName;
	private Double penaltyLimit;

	private List<MistakeType> mistakeTypes;

	@JsonCreator
	public RatingGroup(@JsonProperty("shortName") final String shortName, @JsonProperty("displayName") final String displayName,
			@JsonProperty("penaltyLimit") final Double penaltyLimit) {
		this.shortName = shortName;
		this.displayName = displayName;
		this.mistakeTypes = new ArrayList<>();
		this.penaltyLimit = penaltyLimit;
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
	public double getPenaltyLimit() {
		// in case the caller does not call this::hasPenaltyLimit.
		return this.penaltyLimit != null ? this.penaltyLimit : Double.MAX_VALUE;
	}

	@Override
	public String getShortName() {
		return this.shortName;
	}

	@Override
	public boolean hasPenaltyLimit() {
		return this.penaltyLimit != null;
	}

	@Override
	public String toString() {
		return "RatingGroup [" + "shortName=" + this.shortName + ", displayName=" + this.displayName + ", penaltyLimit= "
				+ (this.hasPenaltyLimit() ? this.penaltyLimit : "NO_LIMIT") + "]";
	}

}
