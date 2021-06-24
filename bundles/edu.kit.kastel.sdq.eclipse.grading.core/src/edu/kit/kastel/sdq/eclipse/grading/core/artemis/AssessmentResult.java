package edu.kit.kastel.sdq.eclipse.grading.core.artemis;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IAssessor;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IFeedback;

/**
 *
 * This is used to construct a JSON Object to be sent as the assessment
 */
@JsonInclude(Include.NON_NULL)
public class AssessmentResult {

	private int id;
	private String resultString;
	private String assessmentType;
	private double score;
	private boolean rated;
	private boolean hasFeedback;
	private String completionDate;
	private IAssessor assessor;			//TODO create Assessor.java
	private Collection<IFeedback> feedbacks;		//TODO create Feedback.java

	public AssessmentResult(int id, String resultString, String assessmentType, double score, boolean rated,
			boolean hasFeedback, String completionDate, IAssessor assessor, Collection<IFeedback> feedbacks) {
		this.id = id;
		this.resultString = resultString;
		this.assessmentType = assessmentType;
		this.score = score;
		this.rated = rated;
		this.hasFeedback = hasFeedback;
		this.completionDate = completionDate;
		this.assessor = assessor;
		this.feedbacks = feedbacks;
	}

	public String getAssessmentType() {
		return this.assessmentType;
	}

	public IAssessor getAssessor() {
		return this.assessor;
	}

	public String getCompletionDate() {
		return this.completionDate;
	}

	public Collection<IFeedback> getFeedbacks() {
		return this.feedbacks;
	}

	public int getId() {
		return this.id;
	}

	public String getResultString() {
		return this.resultString;
	}

	public double getScore() {
		return this.score;
	}

	public boolean isHasFeedback() {
		return this.hasFeedback;
	}

	public boolean isRated() {
		return this.rated;
	}


}
