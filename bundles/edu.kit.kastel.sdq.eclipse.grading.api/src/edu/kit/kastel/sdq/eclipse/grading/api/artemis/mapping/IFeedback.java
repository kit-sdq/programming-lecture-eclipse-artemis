package edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping;

/**
 * Artemis Object Mapping:
 * Represents kinds of "sub assessments", each contributing to the total credit sum. Types:
 * <ul>
 * 		<li>{@link FeedbackType#MANUAL} represents single line annotations</li>
 * 		<li>{@link FeedbackType#MANUAL_UNREFERENCED} represents remarks that are shown below the code</li>
 * 		<li>{@link FeedbackType#AUTOMATIC} represents e.g. unit test results</li>
 * </ul>
 *
 * Whether a method returns null or not depends on the FeedbackType {@link edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IFeedback#getFeedbackType() }!
 *
 */
public interface IFeedback {

	public enum FeedbackType {
		MANUAL, MANUAL_UNREFERENCED, AUTOMATIC;

		public static FeedbackType valueOfIgnoreCase(String str) {
			return FeedbackType.valueOf(str.toUpperCase());
		}
	}

	/**
	 *
	 * @return this Feedbacks contribution to the total credit sum. Can be positive or negative.
	 */
	double getCredits();

	/**
	 *
	 * @return detail text shown in the Artemis GUI on viewing the assessment: Comes after {@link #getText()}, if that is not <b>null</b>.<br/>
	 *  <b>null</b> for {@link FeedbackType#AUTOMATIC}
	 */
	String getDetailText();

	/**
	 *
	 * @return getType(), but typed ;)
	 */
	FeedbackType getFeedbackType();

	/**
	 *
	 * @return <b>null</b> for {@link FeedbackType#MANUAL} and {@link FeedbackType#MANUAL_UNREFERENCED}
	 */
	Integer getId();

	/**
	 *
	 * @return not sure what. Unimportant for now.<br/>
	 *  <b>null</b> for {@link FeedbackType#MANUAL} and {@link FeedbackType#MANUAL_UNREFERENCED}
	 */
	Boolean getPositive();

	/**
	 *
	 * @return
	 * <ul>
	 *  	<li> code reference string like so: "file:${CLASS_FILE_PATH}.java_line:${START_LINE}".</li>
	 * 		<li> Note that Artemis does only consider single lines.</li>
	 * 		<li> <b>null</b> for {@link FeedbackType#AUTOMATIC} and {@link FeedbackType#MANUAL_UNREFERENCED}</li>
	 * </ul>
	 */
	String getReference();

	/**
	 *
	 * @return text shown in the Artemis GUI on viewing the assessment.<br/>
	 *  <b>null</b> for {@link FeedbackType#MANUAL_UNREFERENCED}
	 */
	String getText();

	/**
	 *
	 * @return this Feedback's {@link FeedbackType}
	 */
	String getType();

	/**
	 *
	 * @return not sure what. Unimportant for now.<br/>
	 *  <b>null</b> for {@link FeedbackType#MANUAL} and {@link FeedbackType#MANUAL_UNREFERENCED}
	 */
	String getVisibility();

}
