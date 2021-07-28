package edu.kit.kastel.sdq.eclipse.grading.core.model;

import java.util.Collection;
import java.util.LinkedList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.grading.api.model.IRatingGroup;

public class RatingGroup implements IRatingGroup {

	private String shortName;
	private String displayName;
	private Double penaltyLimit;

	private Collection<MistakeType> mistakeTypes;

	@JsonCreator
	public RatingGroup(
			@JsonProperty("shortName") final String shortName,
			@JsonProperty("displayName") final String displayName,
			@JsonProperty("penaltyLimit") final Double penaltyLimit) {
		this.shortName = shortName;
		this.displayName = displayName;
		this.mistakeTypes = new LinkedList<>();
		this.penaltyLimit = penaltyLimit;
	}

	public void addMistakeType(MistakeType mistakeType) {
		this.mistakeTypes.add(mistakeType);
	}

	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	public Collection<MistakeType> getMistakeTypes() {
		return this.mistakeTypes;
	}

	@Override
	public Double getPenaltyLimit() {
		//in case the caller does not call this::hasPenaltyLimit.
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
		return "RatingGroup ["
				+ "shortName=" + this.shortName
				+ ", displayName=" + this.displayName
				+ ", penaltyLimit= " + (this.hasPenaltyLimit() ? this.penaltyLimit : "NO_LIMIT")
				+ "]";
	}


}
