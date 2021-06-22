package edu.kit.kastel.sdq.eclipse.grading.api.artemis;

public interface IFeedback {

	double getCredits();

	String getDetailText();

	Integer getId();

	Boolean getPositive();

	String getReference();

	String getText();

	String getType();

	String getVisibility();
}
