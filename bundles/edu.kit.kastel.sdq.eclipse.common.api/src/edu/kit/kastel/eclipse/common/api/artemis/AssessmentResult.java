/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.api.artemis;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.eclipse.common.api.artemis.mapping.Feedback;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.User;

public class AssessmentResult implements Serializable {
	private static final long serialVersionUID = -1703764424474018461L;

	@JsonProperty
	public final int id;
	@JsonProperty
	private String assessmentType;
	@JsonProperty
	private double score;
	@JsonProperty
	private boolean rated;
	@JsonProperty
	private boolean hasFeedback;
	@JsonProperty
	private User assessor;
	@JsonProperty
	private List<Feedback> feedbacks;
	@JsonProperty
	private int codeIssueCount;
	@JsonProperty
	private int passedTestCaseCount;
	@JsonProperty
	private int testCaseCount;

	public AssessmentResult(int id, String assessmentType, double score, boolean rated, boolean hasFeedback, User assessor, List<Feedback> feedbacks,
			int codeIssueCount, int passedTestCaseCount, int testCaseCount) {
		this.id = id;
		this.assessmentType = assessmentType;
		this.score = score;
		this.rated = rated;
		this.hasFeedback = hasFeedback;
		this.assessor = assessor;
		this.feedbacks = feedbacks;
		this.codeIssueCount = codeIssueCount;
		this.passedTestCaseCount = passedTestCaseCount;
		this.testCaseCount = testCaseCount;
	}
}
