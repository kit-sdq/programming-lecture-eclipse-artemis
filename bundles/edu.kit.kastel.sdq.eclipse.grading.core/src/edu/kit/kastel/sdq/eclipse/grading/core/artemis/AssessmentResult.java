package edu.kit.kastel.sdq.eclipse.grading.core.artemis;

import java.util.List;

/**
 * 
 * This is used to construct a JSON Object to be sent as the assessment
 */
public class AssessmentResult {
	
	private int id;
	private String resultString;
	private String assessmentType;
	private double score;
	private boolean rated;
	private boolean hasFeedback;
	private String completionDate;
	private Assessor assessor;			//TODO create Assessor.java
	private List<Feedback> feedbacks;		//TODO create Feedback.java
	
	public AssessmentResult(int id, String resultString, String assessmentType, double score, boolean rated,
			boolean hasFeedback, String completionDate, Assessor assessor, List<Feedback> feedbacks) {
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
}
