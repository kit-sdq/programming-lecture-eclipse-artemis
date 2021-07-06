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
import edu.kit.kastel.sdq.eclipse.grading.core.IPenaltyCalculationStrategy;

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
	private final IPenaltyCalculationStrategy penaltyCalculationStrategy;

	public AnnotationMapper(Collection<IAnnotation> annotations, Collection<IMistakeType> mistakeTypes, Collection<IRatingGroup> ratingGroups,
			IAssessor assessor, ILockResult lockResult, IPenaltyCalculationStrategy penaltyCalculationStrategy) {
		//TODO needs results from LOCK call and from USERS call!
		this.annotations = annotations;
		this.mistakeTypes = mistakeTypes;
		this.ratingGroups = ratingGroups;

		this.assessor = assessor;
		this.lockResult = lockResult;
		this.penaltyCalculationStrategy = penaltyCalculationStrategy;
	}

	private double calculateAbsoluteScore(Collection<IFeedback> allFeedbacks) {
		return allFeedbacks.stream().map(IFeedback::getCredits).reduce(Double::sum).get();
	}

	private Collection<IFeedback> calculateAllFeedbacks() {
		//TODO auslagern in eigene Klasse evlt
		final List<IFeedback> result = new LinkedList();
		result.addAll(this.getFilteredPreexistentFeedbacks(FeedbackType.AUTOMATIC));
		result.addAll(this.calculateManualFeedbacks());

		//TODO highly experimental!
		try {
			result.add(this.calculateAnnotationSerialitationAsFeedback());
		} catch (JsonProcessingException e) {
			System.out.println("TODO handle this exception in calculateAllFeedbacks: " + e.getMessage());
//			e.printStackTrace();
		}

		return result;
	}

	private Feedback calculateAnnotationSerialitationAsFeedback() throws JsonProcessingException {
		System.out.println("DEBUG in calculateAnnotationSerialitationAsFeedback: BEFORE");
		final String annotationsJSONString = new ObjectMapper()
				.writeValueAsString(this.annotations);
		System.out.println("DEBUG in calculateAnnotationSerialitationAsFeedback:\n" + annotationsJSONString);
		return new Feedback(FeedbackType.MANUAL_UNREFERENCED.name(), 0D, null, null, "NEVER", "CLIENT_DATA", null, annotationsJSONString);
	}

	private Collection<Feedback> calculateManualFeedbacks() {
		Collection<Feedback> manualFeedbacks = new LinkedList<Feedback>();
		//add the code annotations
		manualFeedbacks.addAll(
			this.annotations.stream()
				.map(this::createNewManualFeedback)
				.collect(Collectors.toList())
		);

		//add the (rated!) rating group annotations
		manualFeedbacks.addAll(
			this.ratingGroups.stream()
				.map(this::createNewManualUnreferencedFeedback)
				.filter(feedback -> !this.isZero(feedback.getCredits()))
				.collect(Collectors.toList())
		);

		return manualFeedbacks;
	}

	public double calculatePenaltyForMistakeType(IMistakeType mistakeType) {
		return this.penaltyCalculationStrategy.calculatePenaltyForMistakeType(mistakeType);
	}

	public double calculatePenaltyForRatingGroup(IRatingGroup ratingGroup) {
		return this.penaltyCalculationStrategy.calcultatePenaltyForRatingGroup(ratingGroup);
	}


	private double calculateRelativeScore(double absoluteScore) {
		return (absoluteScore / this.lockResult.getMaxPoints()) * 100D;
	}

	private String calculateResultString(final Collection<IFeedback> allFeedbacks, final double absoluteScore) {
		final Collection<IFeedback> autoFeedbacks = allFeedbacks.stream()
				.filter(feedback -> feedback.getFeedbackType().equals(FeedbackType.AUTOMATIC)).collect(Collectors.toList());
		long positiveTests = autoFeedbacks.stream()
			.filter(IFeedback::getPositive).count();
		long numberOfTests = autoFeedbacks.stream().count();
		return new StringBuilder()
				.append(positiveTests)
				.append(" of ")
				.append(numberOfTests)
				.append(" passed, ")
				.append(absoluteScore)
				.append(" of ")
				.append(this.lockResult.getMaxPoints())
				.append(" points")
				.toString();
	}

	private AssessmentResult createAssessmentResult() {
		// only add preexistent automatic feedback (unit tests etc) and manual feedback.										arTem155
		final Collection<IFeedback> allFeedbacks = this.calculateAllFeedbacks();
		final double absoluteScore = this.calculateAbsoluteScore(allFeedbacks);
		final double relativeScore = this.calculateRelativeScore(absoluteScore);

		return new AssessmentResult(this.lockResult.getSubmissionID(), this.calculateResultString(allFeedbacks, absoluteScore), "SEMI_AUTOMATIC",
				relativeScore, true, true, null, this.assessor, allFeedbacks);
	}

	private Feedback createNewManualFeedback(IAnnotation annotation) {
		// manual feedbacks do not have no credits!
//		final String text = "File src/edu/kit/informatik/BubbleSort at line 11";
//		final String reference = "file:src/edu/kit/informatik/BubbleSort.java_line:10";
//		final String detailText = " SENT FROM ZE ECLIPSE CLIENT (BubbleSort CodeRef)";
		final String text = new StringBuilder()
				.append("File ")
				.append(annotation.getClassFilePath())
				.append(" at line ")
				.append(annotation.getStartLine())
				.toString();
		final String reference = new StringBuilder()
				.append("file:")
				.append(annotation.getClassFilePath())
				.append(".java_line:")
				.append(annotation.getStartLine())
				.toString();
		final String detailText = new StringBuilder()
				.append("[")
				.append(annotation.getMistakeType().getRatingGroupName())
				.append(":")
				.append(annotation.getMistakeType().getButtonName())
				.append("] ")
				.append(annotation.getMistakeType().getMessage())
				.append("\n")
				.append(annotation.getCustomMessage().orElse(""))
				.toString();

		return new Feedback(FeedbackType.MANUAL.toString(), 0D, null, null, null, text, reference, detailText);
	}

	private Feedback createNewManualUnreferencedFeedback(IRatingGroup ratingGroup) {
		final double calculatedPenalty = this.calculatePenaltyForRatingGroup(ratingGroup);

		final StringBuilder detailTextStringBuilder = new StringBuilder()
				.append(ratingGroup.getDisplayName())
				.append(" [")
				.append(calculatedPenalty)
				.append(" /")
				.append("X")
				.append(" P]");

		// add mistake-specific penalties TODO this is not wanted! just for debug!!!!1
		this.mistakeTypes.stream()
			.filter(mistakeType -> mistakeType.getRatingGroup().equals(ratingGroup))
			.forEach(mistakeType -> {
				double currentPenalty = this.calculatePenaltyForMistakeType(mistakeType);
				//TODO make schön!
				if ( !this.isZero(currentPenalty)) {
					detailTextStringBuilder
						.append("\n  * ")
						.append(mistakeType.getButtonName())
						.append(" [")
						.append(currentPenalty)
						.append("]");
				}
			});


		return new Feedback(FeedbackType.MANUAL_UNREFERENCED.toString(), calculatedPenalty, null, null, null, null, null,
				detailTextStringBuilder.toString());
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

	private boolean isZero(double d) {
		return  (0D + Math.abs(d)) < 0.001D;
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
