/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.api.artemis.mapping;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
 * Whether a method returns null or not depends on the FeedbackType
 * {@link edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IFeedback#getFeedbackType() }!
 */
@JsonInclude(Include.NON_NULL)
public class Feedback implements Comparable<Feedback>, Serializable {
	private static final long serialVersionUID = 4531964872375020131L;

	@JsonProperty("type")
	private String type;
	@JsonProperty("credits")
	private Double credits;
	@JsonProperty("id")
	private Integer id; // null for all manual feedback
	@JsonProperty("positive")
	private Boolean positive; // null for all manual feedback
	@JsonProperty("visibility")
	private String visibility; // null for all manual feedback
	@JsonProperty("text")
	private String text; // null for UNREFERENCED manual feedback
	@JsonProperty("reference")
	private String reference; // null for UNREFERENCED manual feedback and auto feedback
	@JsonProperty("detailText")
	private String detailText; // null for auto feedback

	public Feedback() {
		// NOP
	}

	public Feedback(String type, Double credits, Integer id, Boolean positive, String visibility, String text, String reference, String detailText) {
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
	 * @return this Feedbacks contribution to the total credit sum. Can be positive
	 *         or negative.
	 */
	public Double getCredits() {
		if (Objects.equals("NEVER", this.visibility)) {
			// Bugfix for wrong Artemis points for NEVER visibility
			return 0.0;
		}
		return this.credits;
	}

	/**
	 * @return detail text shown in the Artemis GUI on viewing the assessment: Comes
	 *         after {@link #getText()}, if that is not <b>null</b>.<br/>
	 *         <b>null</b> for {@link FeedbackType#AUTOMATIC}
	 */
	public String getDetailText() {
		return this.detailText;
	}

	/**
	 * @return {@link #getType()} , but typed ;)
	 */
	public FeedbackType getFeedbackType() {
		return FeedbackType.valueOfIgnoreCase(this.type);
	}

	/**
	 * @return <b>null</b> for {@link FeedbackType#MANUAL} and
	 *         {@link FeedbackType#MANUAL_UNREFERENCED}
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * @return not sure what. Unimportant for now.<br/>
	 *         <b>null</b> for {@link FeedbackType#MANUAL} and
	 *         {@link FeedbackType#MANUAL_UNREFERENCED}
	 */
	public Boolean getPositive() {
		return this.positive;
	}

	/**
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

	@Override
	public int compareTo(Feedback o) {
		// Sort (1): Automatic before Manual
		// Sort (2): Tests with name containing "Mandatory" before any other test

		boolean similarType = this.getFeedbackType() == o.getFeedbackType()
				|| this.getFeedbackType() != FeedbackType.AUTOMATIC && o.getFeedbackType() != FeedbackType.AUTOMATIC;

		if (!similarType) {
			return this.getFeedbackType() == FeedbackType.AUTOMATIC ? -1 : 1;
		}

		if (this.mandatoryTest() != o.mandatoryTest()) {
			return this.mandatoryTest() ? -1 : 1;
		}

		String thisName = this.getText() == null ? "" : this.getText();
		String otherName = o.getText() == null ? "" : o.getText();
		return thisName.compareToIgnoreCase(otherName);
	}

	private boolean mandatoryTest() {
		// Only by naming convention so far, since Artemis has no "mandatory" tests.
		return this.text != null && this.text.toLowerCase().contains("mandatory");
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.credits, this.detailText, this.id, this.positive, this.reference, this.text, this.type, this.visibility);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		Feedback other = (Feedback) obj;
		return Objects.equals(this.credits, other.credits) && Objects.equals(this.detailText, other.detailText) && Objects.equals(this.id, other.id)
				&& Objects.equals(this.positive, other.positive) && Objects.equals(this.reference, other.reference) && Objects.equals(this.text, other.text)
				&& Objects.equals(this.type, other.type) && Objects.equals(this.visibility, other.visibility);
	}

	@JsonIgnore
	public boolean isSCA() {
		return this.text != null && this.text.startsWith("SCAFeedbackIdentifier");
	}
}
