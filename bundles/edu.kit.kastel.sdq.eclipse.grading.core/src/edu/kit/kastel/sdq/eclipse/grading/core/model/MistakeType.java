package edu.kit.kastel.sdq.eclipse.grading.core.model;

import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.IRatingGroup;

import java.util.List;

import edu.kit.kastel.sdq.eclipse.grading.api.IAnnotation;

public class MistakeType implements IMistakeType {
	private String shortName;
	private String buttonName;
	private String message;
	
	private RatingGroup ratingGroup;
	private PenaltyRule penaltyRule;
	private List<IAnnotation> annotations;
	
	/**
	 * 
	 * @param shortName
	 * @param buttonName
	 * @param message
	 * @param ratingGroup
	 * @param penaltyType
	 */
	public MistakeType(String shortName, String buttonName, String message, RatingGroup ratingGroup,
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
	
	
}
