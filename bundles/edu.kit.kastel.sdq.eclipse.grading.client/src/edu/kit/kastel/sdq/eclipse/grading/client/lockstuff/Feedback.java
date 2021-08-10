package edu.kit.kastel.sdq.eclipse.grading.client.lockstuff;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IFeedback;

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

	@JsonCreator
	public Feedback(
			@JsonProperty("type") String type,
			@JsonProperty("credits") Double credits,
			@JsonProperty("id") Integer id,
			@JsonProperty("positive") Boolean positive,
			@JsonProperty("visibility") String visibility,
			@JsonProperty("text") String text,
			@JsonProperty("reference") String reference,
			@JsonProperty("detailText") String detailText) {
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
	public FeedbackType getFeedbackType() {
		return FeedbackType.valueOfIgnoreCase(this.type);
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
	public String getVisibility() {
		return this.visibility;
	}

	@Override
	public String toString() {
		return "Feedback [type=" + this.type + ", credits=" + this.credits + ", id=" + this.id + ", positive=" + this.positive
				+ ", visibility=" + this.visibility + ", text=" + this.text + ", reference=" + this.reference + ", detailText="
				+ this.detailText + "]";
	}

}
