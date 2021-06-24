package edu.kit.kastel.sdq.eclipse.grading.core.artemis;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IFeedback;

@JsonInclude(Include.NON_NULL)
public class Feedback implements IFeedback {

	private String type;
	private double credits;
	private Integer id;								// null for all manual feedback
	private Boolean positive;						// null for all manual feedback
	private String visibility;						// null for all manual feedback
	private String text;							// null for UNREFERENCED manual feedback
	private String reference;						// null for UNREFERENCED manual feedback and auto feedback
	private String detailText;						// null for auto feedback

	//TODO make subclasses or just null the stuff?
	public Feedback(String type, double credits, Integer id, Boolean positive, String visibility, String text,
			String reference, String detailText) {
		super();
		this.type = type;
		this.credits = credits;
		this.id = id;
		this.positive = positive;
		this.visibility = visibility;
		this.text = text;
		this.reference = reference;
		this.detailText = detailText;
	}

	@Override
	public double getCredits() {
		return this.credits;
	}

	@Override
	public String getDetailText() {
		return this.detailText;
	}

	@Override
	@JsonIgnore
	public FeedbackType getFeedbackType() {
		return FeedbackType.valueOfIgnoreCase(this.getType());
	}

	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public Boolean getPositive() {
		return this.positive;
	}

	@Override
	public String getReference() {
		return this.reference;
	}

	@Override
	public String getText() {
		return this.text;
	}

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public String getVisibility() {
		return this.visibility;
	}


}
