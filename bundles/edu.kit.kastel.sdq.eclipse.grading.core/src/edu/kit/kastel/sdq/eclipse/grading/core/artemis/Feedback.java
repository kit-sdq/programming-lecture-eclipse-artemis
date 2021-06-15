package edu.kit.kastel.sdq.eclipse.grading.core.artemis;

public class Feedback {

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
	
	
}
