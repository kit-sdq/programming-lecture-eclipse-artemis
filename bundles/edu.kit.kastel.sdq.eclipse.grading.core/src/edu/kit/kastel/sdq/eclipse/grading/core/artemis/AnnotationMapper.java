package edu.kit.kastel.sdq.eclipse.grading.core.artemis;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kit.kastel.sdq.eclipse.grading.api.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.IRatingGroup;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IAssessor;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IFeedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;

/**
 * Maps Annotations to Artemis-accepted json-formatted strings.
 */
public class AnnotationMapper {

	private final Collection<IAnnotation> annotations;
	private final Collection<IMistakeType> mistakeTypes;
	private final Collection<IRatingGroup> ratingGroups;

	private final IAssessor assessor;
	private final ILockResult lockResult;

	public AnnotationMapper(Collection<IAnnotation> annotations, Collection<IMistakeType> mistakeTypes, Collection<IRatingGroup> ratingGroups,
			IAssessor assessor, ILockResult lockResult) {
		//TODO needs results from LOCK call and from USERS call!
		this.annotations = annotations;
		this.mistakeTypes = mistakeTypes;
		this.ratingGroups = ratingGroups;

		this.assessor = assessor;
		this.lockResult = lockResult;
	}

	private Collection<IFeedback> convertAnnotationsToFeedbacks() {
		//TODO auslagern in eigene Klasse evlt

		//TODO implement dat

		// this is a dummy for testing
		// todo need to retrieve latest feedback id
		final String text = "File src/edu/kit/informatik/BubbleSort at line 11";
		final String reference = "file:src/edu/kit/informatik/BubbleSort.java_line:10";
		final String detailText = " SENT FROM ZE CLIENNENENENENENTTTT!";

		List<IFeedback> result = new LinkedList();
		result.addAll(this.lockResult.getPreexistentFeedbacks());
		result.add(new Feedback("MANUAL", -1D, this.getLatestFeedbackID()+1, null, null, text, reference, detailText));

		return result;
	}

	private AssessmentResult createAssessmentResult() {
		//TODO resultString, points calculation, annotations --> Feedbacks
		final Collection<IFeedback> allFeedbacks = new LinkedList(this.lockResult.getPreexistentFeedbacks());
		allFeedbacks.addAll(this.convertAnnotationsToFeedbacks());

		return new AssessmentResult(this.lockResult.getId(), "TODO resultString", "SEMI_AUTOMATIC", 51,
				true, true, null, this.assessor, allFeedbacks);
	}

	private int getLatestFeedbackID() {
		int greatestId = -1;
		for (IFeedback feedback : this.lockResult.getPreexistentFeedbacks()) {
			final int currentId = feedback.getId();
			if (currentId > greatestId) greatestId = currentId;
		}
		return greatestId;
	}

	public String mapToJsonFormattedString() throws JsonProcessingException {
		return new ObjectMapper()
//				.configure(SerializationFeature.FAIL, false)
//				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.writeValueAsString(this.createAssessmentResult());
	}

	//TODO need to map Collection<IAnnotation> to a "Result" json structure:
	/*
	 * id: 14															muss aus (*) geholt werden
	 * resultString: "13 of 13 passed, 56.5 of 66 points"				muss berechnet werden
	 * assessmentType: "SEMI_AUTOMATIC"
	 * score: 85.60606060606061 										muss berechnet werden: (= 56.5 /66)
	 * rated: true
	 * hasFeedback: true
	 * completionDate: 													???
	 * assessor															muss aus (**) geholt werden
	 * feedbacks: Array, bestehend aus präexistenten automatischen 		teils aus (*), teils aus this.annotations
	 * feedbacks und aus manuellen feedbacks
	 *
	 *
	 * (*)  Um das füllen zu können, muss folgendes aufgerufen werden: /api/programming-submissions/{submissionId}/lock
	 * (**) Um das füllen zu können, muss folgendes aufgerufen werden: /api/users/{login}. "login" ist der username.
	 *
	 */


//	public String map

}
