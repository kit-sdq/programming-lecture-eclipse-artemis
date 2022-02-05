package edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Artemis Object Mapping: Represents kinds of "sub assessments", each
 * contributing to the total credit sum. Types:
 * <ul>
 * <li>{@link FeedbackType#MANUAL} represents single line annotations</li>
 * <li>{@link FeedbackType#MANUAL_UNREFERENCED} represents remarks that are
 * shown below the code</li>
 * <li>{@link FeedbackType#AUTOMATIC} represents e.g. unit test results</li>
 * </ul>
 *
 * Whether a method returns null or not depends on the FeedbackType
 * {@link edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IFeedback#getFeedbackType() }!
 *
 */
@JsonInclude(Include.NON_NULL)
public class Feedback implements Serializable {
	private static final long serialVersionUID = 4531964872375020131L;

	private String type;
	private Double credits;
	private Integer id; // null for all manual feedback
	private Boolean positive; // null for all manual feedback
	private String visibility; // null for all manual feedback
	private String text; // null for UNREFERENCED manual feedback
	private String reference; // null for UNREFERENCED manual feedback and auto feedback
	private String detailText; // null for auto feedback

	@JsonCreator
	public Feedback(@JsonProperty("type") String type, @JsonProperty("credits") Double credits, @JsonProperty("id") Integer id,
			@JsonProperty("positive") Boolean positive, @JsonProperty("visibility") String visibility, @JsonProperty("text") String text,
			@JsonProperty("reference") String reference, @JsonProperty("detailText") String detailText) {
		this.type = type;
		this.credits = credits;
		this.id = id;
		this.positive = positive;
		this.visibility = visibility;
		this.text = text;
		this.reference = reference;
		this.detailText = detailText;
	}

	/**
	 *
	 * @return this Feedbacks contribution to the total credit sum. Can be positive
	 *         or negative.
	 */
	public double getCredits() {
		if (Objects.equals("NEVER", visibility)) {
			// Bugfix for wrong Artemis points for NEVER visibility
			return 0;
		}
		return this.credits;
	}

	/**
	 *
	 * @return detail text shown in the Artemis GUI on viewing the assessment: Comes
	 *         after {@link #getText()}, if that is not <b>null</b>.<br/>
	 *         <b>null</b> for {@link FeedbackType#AUTOMATIC}
	 */
	public String getDetailText() {
		return this.detailText;
	}

	/**
	 *
	 * @return {@link #getType()} , but typed ;)
	 */
	public FeedbackType getFeedbackType() {
		return FeedbackType.valueOfIgnoreCase(this.type);
	}

	/**
	 *
	 * @return <b>null</b> for {@link FeedbackType#MANUAL} and
	 *         {@link FeedbackType#MANUAL_UNREFERENCED}
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 *
	 * @return not sure what. Unimportant for now.<br/>
	 *         <b>null</b> for {@link FeedbackType#MANUAL} and
	 *         {@link FeedbackType#MANUAL_UNREFERENCED}
	 */
	public Boolean getPositive() {
		return this.positive;
	}

	/**
	 *
	 * @return
	 *         <ul>
	 *         <li>code reference string like so:
	 *         "file:${CLASS_FILE_PATH}.java_line:${START_LINE}".</li>
	 *         <li>Note that Artemis does only consider single lines.</li>
	 *         <li><b>null</b> for {@link FeedbackType#AUTOMATIC} and
	 *         {@link FeedbackType#MANUAL_UNREFERENCED}</li>
	 *         </ul>
	 */
	public String getReference() {
		return this.reference;
	}

	/**
	 *
	 * @return text shown in the Artemis GUI on viewing the assessment.<br/>
	 *         <b>null</b> for {@link FeedbackType#MANUAL_UNREFERENCED}
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * This is NECESSARY! for serialization to artemis.
	 *
	 * @return
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * <b>null</b> for {@link FeedbackType#MANUAL} and
	 * {@link FeedbackType#MANUAL_UNREFERENCED}
	 */
	public String getVisibility() {
		return this.visibility;
	}
}
