package edu.kit.kastel.sdq.eclipse.grading.core.artemis;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IFeedback;

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
		// TODO Auto-generated method stub
		return this.credits;
	}

	@Override
	public String getDetailText() {
		// TODO Auto-generated method stub
		return this.detailText;
	}

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	public Boolean getPositive() {
		// TODO Auto-generated method stub
		return this.positive;
	}

	@Override
	public String getReference() {
		// TODO Auto-generated method stub
		return this.reference;
	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return this.text;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return this.type;
	}

	@Override
	public String getVisibility() {
		// TODO Auto-generated method stub
		return this.visibility;
	}


}
