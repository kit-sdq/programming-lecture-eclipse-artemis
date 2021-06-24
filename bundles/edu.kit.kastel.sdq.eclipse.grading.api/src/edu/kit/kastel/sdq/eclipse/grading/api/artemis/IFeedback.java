package edu.kit.kastel.sdq.eclipse.grading.api.artemis;

public interface IFeedback {

	public enum FeedbackType {
		MANUAL, MANUAL_UNREFERENCED, AUTOMATIC;

		public static FeedbackType valueOfIgnoreCase(String str) {
			return FeedbackType.valueOf(str.toUpperCase());
		}
	}

	double getCredits();

	String getDetailText();

	/**
	 *
	 * @return getType(), but typed ;)
	 */
	FeedbackType getFeedbackType();

	Integer getId();

	Boolean getPositive();

	String getReference();

	String getText();

	String getType();

	String getVisibility();

}
