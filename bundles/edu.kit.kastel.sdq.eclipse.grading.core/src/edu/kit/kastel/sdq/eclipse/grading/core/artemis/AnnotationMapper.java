package edu.kit.kastel.sdq.eclipse.grading.core.artemis;

import java.util.Collection;

import edu.kit.kastel.sdq.eclipse.grading.api.IAnnotation;

/**
 * Maps Annotations to Artemis-accepted json-formatted strings.
 */
public class AnnotationMapper {

	private final Collection<IAnnotation> annotations;

	public AnnotationMapper(Collection<IAnnotation> annotations, Assessor assessor) {
		//TODO needs results from LOCK call and from USERS call!
		this.annotations = annotations;
	}

	private AssessmentResult createAssessmentResult() {
		//TODO implement
		return new AssessmentResult(-1, null, null, -1, false, false, null, null, null);
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
