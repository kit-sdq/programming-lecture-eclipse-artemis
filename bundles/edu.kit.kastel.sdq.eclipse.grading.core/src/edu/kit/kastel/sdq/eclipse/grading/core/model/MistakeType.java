package edu.kit.kastel.sdq.eclipse.grading.core.model;

import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.IRatingGroup;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.grading.api.IAnnotation;

public class MistakeType implements IMistakeType {
	private String shortName;
	private String buttonName;
	private String message;
	
	//used for deserialization
	private String appliesTo;
	
	private RatingGroup ratingGroup;
	private PenaltyRule penaltyRule;
	private List<IAnnotation> annotations;
	
	/**
	 * This Constructor is used by Deserialization!
	 */
	@JsonCreator
	public MistakeType(@JsonProperty("shortName") String shortName, 
			@JsonProperty("button") String buttonName, 
			@JsonProperty("message") String message, 
			@JsonProperty("penaltyRule") PenaltyRule penaltyRule,
			@JsonProperty("appliesTo") String appliesTo) {
		super();
		this.shortName = shortName;
		this.buttonName = buttonName;
		this.message = message;
		this.penaltyRule = penaltyRule;
		
		this.appliesTo = appliesTo;
		
		//TODO using this constructor means having to add
		// the rating group,
		// this object in the rating group!
	}
	
	public MistakeType(String shortName, 
			String buttonName, 
			String message, 
			RatingGroup ratingGroup,
			PenaltyRule penaltyType) {
		super();
		this.shortName = shortName;
		this.buttonName = buttonName;
		this.message = message;
		this.ratingGroup = ratingGroup;
		this.penaltyRule = penaltyType;
		
		//add the inverse reference
		this.ratingGroup.addMistakeType(this);
	}

	/**
	 * 
	 * @return to which rating group this applies. Used for deserialization...
	 */
	public String getAppliesTo() {
		return this.appliesTo;
	}
	
	/**
	 * Sets a new rating group if there ain't already one. (Used for deserialization).
	 * @param ratingGroup the new rating group
	 */
	public void setRatingGroup(RatingGroup ratingGroup) {
		if (this.ratingGroup == null) {
			this.ratingGroup = ratingGroup;
		}
	}
	
	
	@Override
	public IRatingGroup getRatingGroup() {
		return this.ratingGroup;
	}

	@Override
	public String toString() {
		return "MistakeType [shortName=" + shortName + ", buttonName=" + buttonName + ", message=" + message
				+ ", ratingGroup=" + ratingGroup.getShortName() + ", penaltyRule=" + penaltyRule + ", annotations=" + annotations
				+ "]";
	}
	
	public String getShortName() {
		return this.shortName;
	}
	
	public PenaltyRule getPenaltyRule() {
		return this.penaltyRule;
	}

	@Override
	public String getButtonName() {
		return this.buttonName;
	}
	
	public String getRatingGroupName() {
		return this.ratingGroup.getDisplayName();
	}
}
