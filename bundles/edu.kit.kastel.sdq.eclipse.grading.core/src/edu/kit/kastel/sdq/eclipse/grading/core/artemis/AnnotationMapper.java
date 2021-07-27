package edu.kit.kastel.sdq.eclipse.grading.core.artemis;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
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
		final boolean submissionIsInvalid = this.penaltyCalculationStrategy.submissionIsInvalid();

		final List<IFeedback> result = new LinkedList();
		result.addAll(this.getFilteredPreexistentFeedbacks(FeedbackType.AUTOMATIC));
		result.addAll( submissionIsInvalid ? this.calculateInvalidManualFeedback() : this.calculateManualFeedbacks());

		try {
			result.add(this.calculateAnnotationSerialitationAsFeedback());
		} catch (JsonProcessingException e) {
			System.out.println("TODO handle this exception in calculateAllFeedbacks: " + e.getMessage());
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

	private Collection<Feedback> calculateInvalidManualFeedback() {
		final Collection<Feedback> manualFeedbacks = new LinkedList<Feedback>();
		manualFeedbacks.add(
				new Feedback(IFeedback.FeedbackType.MANUAL_UNREFERENCED.name(),
						0.D,
						// TODO check visibility
						null, null, "", null, null, "Invalid Submission.")
		);
		return manualFeedbacks;
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
				.filter(feedback -> !Util.isZero(feedback.getCredits()))
				.collect(Collectors.toList())
		);

		return manualFeedbacks;
	}

	/**
	 * Negate what the strategy gives.
	 */
	private double calculatePenaltyForMistakeType(IMistakeType mistakeType) {
		return -1D * this.penaltyCalculationStrategy.calculatePenaltyForMistakeType(mistakeType);
	}

	/**
	 * Negate what the strategy gives.
	 */
	private double calculatePenaltyForRatingGroup(IRatingGroup ratingGroup) {
		return -1D * this.penaltyCalculationStrategy.calcultatePenaltyForRatingGroup(ratingGroup);
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
				.append(Util.formatDouble(absoluteScore))
				.append(" of ")
				.append(Util.formatDouble(this.lockResult.getMaxPoints()))
				.append(" points")
				.toString();
	}

	private AssessmentResult createAssessmentResult() {
		final boolean submissionIsInvalid = this.penaltyCalculationStrategy.submissionIsInvalid();
		// only add preexistent automatic feedback (unit tests etc) and manual feedback.										arTem155
		//this should work indepently of invalid or not. if invalid, there should just be no feedbacks.
		final Collection<IFeedback> allFeedbacks = this.calculateAllFeedbacks();
		final double absoluteScore = submissionIsInvalid ? 0.D : this.calculateAbsoluteScore(allFeedbacks);
		final double relativeScore = submissionIsInvalid ? 0.D : this.calculateRelativeScore(absoluteScore);

		return new AssessmentResult(
				this.lockResult.getSubmissionID(),
				this.calculateResultString(allFeedbacks, absoluteScore), "SEMI_AUTOMATIC",
				relativeScore, true, true, null, this.assessor, allFeedbacks);
	}

	private Feedback createNewManualFeedback(IAnnotation annotation) {
		// manual feedbacks do not have no credits!
		final Optional<String> customMessageOptional = annotation.getCustomMessage();
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
				.append(annotation.getCustomMessage().orElse(""))	//assuming mistake type has no message in custom case!
				.toString();

		return new Feedback(FeedbackType.MANUAL.toString(), 0D, null, null, null, text, reference, detailText);
	}

	private Feedback createNewManualUnreferencedFeedback(IRatingGroup ratingGroup) {
		final double calculatedPenalty = this.calculatePenaltyForRatingGroup(ratingGroup);


		final StringBuilder detailTextStringBuilder = new StringBuilder()
				.append(ratingGroup.getDisplayName())
				.append(" [")
				.append(Util.formatDouble(calculatedPenalty));
		if (ratingGroup.hasPenaltyLimit()) {
			detailTextStringBuilder
				.append(" of at most ")
				.append(Util.formatDouble(-1D * ratingGroup.getPenaltyLimit()));
		}
		detailTextStringBuilder
				.append(" points]");

		// add mistake-specific penalties
		this.mistakeTypes.stream()
			.filter(mistakeType -> mistakeType.getRatingGroup().equals(ratingGroup))
			.forEach(mistakeType -> {
				final double currentPenalty = this.calculatePenaltyForMistakeType(mistakeType);
				final Collection<IAnnotation> currentAnnotations = this.annotations.stream()
						.filter(annotation -> annotation.getMistakeType().equals(mistakeType))
						.collect(Collectors.toList());

				if ( !Util.isZero(currentPenalty)) {
					detailTextStringBuilder
						.append("\n    * \"")
						.append(mistakeType.getButtonName())
						.append("\" [")
						.append(Util.formatDouble(currentPenalty))
						.append("]:");

					currentAnnotations.forEach(annotation ->  {
						detailTextStringBuilder
						.append("\n        * ")
						.append(annotation.getClassFilePath())
						.append(" at line ")
						.append(annotation.getStartLine());
					});
				}
			});

		//TODO add Anmerkung "penalty limit reached"
		if (this.penaltyCalculationStrategy.penaltyLimitIsHitForRatingGroup(ratingGroup)) {
			detailTextStringBuilder
				.append("\n    * ")
				.append("Note: The sum of penalties hit the penalty limit for this rating group.");
		}

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

	public String mapToJsonFormattedString() throws JsonProcessingException {
		return new ObjectMapper()
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
