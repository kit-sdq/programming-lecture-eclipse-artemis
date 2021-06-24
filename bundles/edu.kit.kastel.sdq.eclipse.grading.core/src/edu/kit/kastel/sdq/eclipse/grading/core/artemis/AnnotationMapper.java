package edu.kit.kastel.sdq.eclipse.grading.core.artemis;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kit.kastel.sdq.eclipse.grading.api.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.IRatingGroup;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IAssessor;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IFeedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IFeedback.FeedbackType;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;

/**
 * Maps Annotations to Artemis-accepted json-formatted strings.
 */
public class AnnotationMapper {

	private static final String DATE_FORMAT_STRING = "yyyy-MM-dd'T'hh:mm:ss.nnnnnn";

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

	private Collection<IFeedback> calculateAllFeedbacks() {
		//TODO auslagern in eigene Klasse evlt
		final List<IFeedback> result = new LinkedList();
		result.addAll(this.getFilteredPreexistentFeedbacks(FeedbackType.AUTOMATIC));
		result.addAll(this.calculateManualFeedbacks());

		return result;
	}

	private Collection<Feedback> calculateManualFeedbacks() {
		//TODO dis is just for test
		final String text = "File src/edu/kit/informatik/BubbleSort at line 11";
		final String reference = "file:src/edu/kit/informatik/BubbleSort.java_line:10";
		final String detailText = " SENT FROM ZE ECLIPSE CLIENT (BubbleSort CodeRef)";
		return List.of(
				new Feedback(FeedbackType.MANUAL.toString(), -1D, null, null, null, text, reference, detailText),
				new Feedback(FeedbackType.MANUAL_UNREFERENCED.toString(), -1D, null, null, null, null, null, " SENT FROM ZE ECLIPSE CLIENT (Feedback unrefD)")

				);


		//TODO implement calculation
		//TODO implement calculating the UNREFERENCED Feedbacks (Zusammenfassung RatingGroups!)
	}

	private double calculateScore() {
		//TODO implement
		return 51;
	}

	private AssessmentResult createAssessmentResult() {
		// only add preexistent automatic feedback (unit tests etc) and manual feedback.													arTem155
		final Collection<IFeedback> allFeedbacks = new LinkedList();
		allFeedbacks.addAll(this.calculateAllFeedbacks());

		return new AssessmentResult(this.lockResult.getId(), "TODO resultString", "SEMI_AUTOMATIC", this.calculateScore(),
				true, true, null, this.assessor, allFeedbacks);
	}

	private String getCurrentTimestamp() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern(this.DATE_FORMAT_STRING));
	}

	private Collection<IFeedback> getFilteredPreexistentFeedbacks(FeedbackType feedbackType) {
		return this.lockResult.getPreexistentFeedbacks().stream()
				.filter(feedback -> feedback.getFeedbackType().equals(feedbackType)).collect(Collectors.toList());
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
