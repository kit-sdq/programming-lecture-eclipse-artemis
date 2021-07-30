package edu.kit.kastel.sdq.eclipse.grading.core.artemis;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IAssessor;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IFeedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IFeedback.FeedbackType;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IRatingGroup;

/**
 * Maps Annotations to Artemis-accepted json-formatted strings.
 */
public class AnnotationMapper {

	// TODO maybe put into a constants class.
	// keep this up to date with  https://github.com/ls1intum/Artemis/blob/develop/src/main/java/de/tum/in/www1/artemis/config/Constants.java#L121
	private static final int FEEDBACK_DETAIL_TEXT_MAX_CHARACTERS = 5000;
	private final Collection<IAnnotation> annotations;
	private final Collection<IMistakeType> mistakeTypes;

	private final Collection<IRatingGroup> ratingGroups;
	private final IAssessor assessor;
	private final ILockResult lockResult;

	private final IPenaltyCalculationStrategy penaltyCalculationStrategy;

	public AnnotationMapper(Collection<IAnnotation> annotations, Collection<IMistakeType> mistakeTypes, Collection<IRatingGroup> ratingGroups,
			IAssessor assessor, ILockResult lockResult, IPenaltyCalculationStrategy penaltyCalculationStrategy) {
		this.annotations = annotations;
		this.mistakeTypes = mistakeTypes;
		this.ratingGroups = ratingGroups;

		this.assessor = assessor;
		this.lockResult = lockResult;
		this.penaltyCalculationStrategy = penaltyCalculationStrategy;
	}

	private double calculateAbsoluteScore(Collection<IFeedback> allFeedbacks) {
		return allFeedbacks.stream()
				.map(IFeedback::getCredits)
				.reduce(Double::sum)
				.orElse(0D);
	}

	private Collection<IFeedback> calculateAllFeedbacks() throws IOException {
		final boolean submissionIsInvalid = this.penaltyCalculationStrategy.submissionIsInvalid();

		final List<IFeedback> result = new LinkedList<>();
		result.addAll(this.getFilteredPreexistentFeedbacks(FeedbackType.AUTOMATIC));
		result.addAll( submissionIsInvalid ? this.calculateInvalidManualFeedback() : this.calculateManualFeedbacks());

		result.addAll(this.calculateAnnotationSerialitationAsFeedbacks());

		return result;
	}

	private Collection<Feedback> calculateAnnotationSerialisationAsFeedbacks(List<IAnnotation> givenAnnotations, int detailTextMaxCharacters) throws IOException {
		final String givenAnnotationsJSONString = this.convertAnnotationsToJSONString(givenAnnotations);
		//put as many feedbacks in one pack.
		if (givenAnnotationsJSONString.length() < detailTextMaxCharacters) {
			// we don't want the serialization to be visible (for non-privileged users)
			return List.of(new Feedback(FeedbackType.MANUAL_UNREFERENCED.name(), 0D, null, null, "NEVER", "CLIENT_DATA", null, givenAnnotationsJSONString));
		}
		//if one single annotation is too large, serialization is impossible!
		if (givenAnnotations.size() == 1) {
			throw new IOException("This annotation is too large to serialize! " + givenAnnotationsJSONString);
		}

		//recursion
		final int givenAnnotationsSize = givenAnnotations.size();
		final Collection<Feedback> resultFeedbacks = new LinkedList<>();
		resultFeedbacks.addAll(
				this.calculateAnnotationSerialisationAsFeedbacks(givenAnnotations.subList(0, givenAnnotationsSize/2), detailTextMaxCharacters)
		);

		resultFeedbacks.addAll(
				this.calculateAnnotationSerialisationAsFeedbacks(givenAnnotations.subList((givenAnnotationsSize/2), givenAnnotations.size()), detailTextMaxCharacters)
		);
		return resultFeedbacks;
	}

	private Collection<Feedback> calculateAnnotationSerialitationAsFeedbacks() throws IOException {
		// because Artemis has a Limit on "detailText" of 5000, we gotta do this little trick
		return this.calculateAnnotationSerialisationAsFeedbacks(this.annotations.stream().collect(Collectors.toList()), FEEDBACK_DETAIL_TEXT_MAX_CHARACTERS);
	}

	private Collection<Feedback> calculateInvalidManualFeedback() {
		final Collection<Feedback> manualFeedbacks = new LinkedList<>();
		manualFeedbacks.add(
				new Feedback(IFeedback.FeedbackType.MANUAL_UNREFERENCED.name(),
						0.D,
						null, null, null, null, null, "Invalid Submission.")
		);
		return manualFeedbacks;
	}

	private Collection<Feedback> calculateManualFeedbacks() {
		Collection<Feedback> manualFeedbacks = new LinkedList<>();
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

	private String convertAnnotationsToJSONString(final Collection<IAnnotation> givenAnnotations) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(givenAnnotations);
	}

	private AssessmentResult createAssessmentResult() throws IOException {
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

					currentAnnotations.forEach(annotation ->
						detailTextStringBuilder
						.append("\n        * ")
						.append(annotation.getClassFilePath())
						.append(" at line ")
						.append(annotation.getStartLine())
					);
				}
			});

		if (this.penaltyCalculationStrategy.penaltyLimitIsHitForRatingGroup(ratingGroup)) {
			detailTextStringBuilder
				.append("\n    * ")
				.append("Note: The sum of penalties hit the penalty limit for this rating group.");
		}

		return new Feedback(FeedbackType.MANUAL_UNREFERENCED.toString(), calculatedPenalty, null, null, null, null, null,
				detailTextStringBuilder.toString());
	}

	private Collection<IFeedback> getFilteredPreexistentFeedbacks(FeedbackType feedbackType) {
		return this.lockResult.getPreexistentFeedbacks().stream()
				.filter(feedback -> feedback.getFeedbackType().equals(feedbackType)).collect(Collectors.toList());
	}

	public String mapToJsonFormattedString() throws IOException {
		return new ObjectMapper()
				.writeValueAsString(this.createAssessmentResult());
	}
}
