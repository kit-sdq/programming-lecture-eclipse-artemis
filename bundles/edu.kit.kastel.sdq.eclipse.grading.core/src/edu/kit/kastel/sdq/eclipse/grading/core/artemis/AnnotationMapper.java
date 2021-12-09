package edu.kit.kastel.sdq.eclipse.grading.core.artemis;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.AssessmentResult;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Assessor;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.FeedbackType;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IRatingGroup;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.calculation.IPenaltyCalculationStrategy;

/**
 * Maps Annotations to Artemis-accepted json-formatted strings.
 */
public class AnnotationMapper {
	// keep this up to date with
	// https://github.com/ls1intum/Artemis/blob/develop/src/main/java/de/tum/in/www1/artemis/config/Constants.java#L121
	private static final int FEEDBACK_DETAIL_TEXT_MAX_CHARACTERS = 5000;

	private static final NumberFormat nf = new DecimalFormat("##.###", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

	private final IExercise exercise;
	private final ISubmission submission;

	private final List<IAnnotation> annotations;
	private final List<IMistakeType> mistakeTypes;

	private final List<IRatingGroup> ratingGroups;
	private final Assessor assessor;

	private final IPenaltyCalculationStrategy penaltyCalculationStrategy;

	private final ILockResult lock;

	public AnnotationMapper(IExercise exercise, ISubmission submission, List<IAnnotation> annotations, List<IMistakeType> mistakeTypes,
			List<IRatingGroup> ratingGroups, Assessor assessor, IPenaltyCalculationStrategy penaltyCalculationStrategy, ILockResult lock) {
		this.exercise = exercise;
		this.submission = submission;

		this.annotations = annotations;
		this.mistakeTypes = mistakeTypes;
		this.ratingGroups = ratingGroups;

		this.assessor = assessor;
		this.penaltyCalculationStrategy = penaltyCalculationStrategy;

		this.lock = lock;
	}

	private double calculateAbsoluteScore(List<Feedback> allFeedbacks) {
		return allFeedbacks.stream().mapToDouble(Feedback::getCredits).sum();
	}

	private List<Feedback> calculateAllFeedbacks() throws IOException {
		final boolean submissionIsInvalid = this.penaltyCalculationStrategy.submissionIsInvalid();

		final List<Feedback> result = new ArrayList<>(this.getFilteredPreexistentFeedbacks(FeedbackType.AUTOMATIC));
		result.addAll(submissionIsInvalid ? this.calculateInvalidManualFeedback() : this.calculateManualFeedbacks());
		result.addAll(this.calculateAnnotationSerialitationAsFeedbacks());
		return result;
	}

	private List<Feedback> calculateAnnotationSerialisationAsFeedbacks(List<IAnnotation> givenAnnotations, int detailTextMaxCharacters) throws IOException {
		final String givenAnnotationsJSONString = this.convertAnnotationsToJSONString(givenAnnotations);
		// put as many feedbacks in one pack.
		if (givenAnnotationsJSONString.length() < detailTextMaxCharacters) {
			// we don't want the serialization to be visible (for non-privileged users)
			return List.of(new Feedback(FeedbackType.MANUAL_UNREFERENCED.name(), 0D, null, null, "NEVER", "CLIENT_DATA", null, givenAnnotationsJSONString));
		}
		// if one single annotation is too large, serialization is impossible!
		if (givenAnnotations.size() == 1) {
			throw new IOException("This annotation is too large to serialize! " + givenAnnotationsJSONString);
		}

		// recursion
		final int givenAnnotationsSize = givenAnnotations.size();
		final List<Feedback> resultFeedbacks = new ArrayList<>(
				this.calculateAnnotationSerialisationAsFeedbacks(givenAnnotations.subList(0, givenAnnotationsSize / 2), detailTextMaxCharacters));
		resultFeedbacks.addAll(this.calculateAnnotationSerialisationAsFeedbacks(givenAnnotations.subList(givenAnnotationsSize / 2, givenAnnotations.size()),
				detailTextMaxCharacters));
		return resultFeedbacks;
	}

	private List<Feedback> calculateAnnotationSerialitationAsFeedbacks() throws IOException {
		// because Artemis has a Limit on "detailText" of 5000, we gotta do this little
		// trick
		return this.calculateAnnotationSerialisationAsFeedbacks(new ArrayList<>(this.annotations), FEEDBACK_DETAIL_TEXT_MAX_CHARACTERS);
	}

	private List<Feedback> calculateInvalidManualFeedback() {
		final List<Feedback> manualFeedbacks = new ArrayList<>();
		manualFeedbacks.add(new Feedback(FeedbackType.MANUAL_UNREFERENCED.name(), 0.D, null, null, null, null, null, "Invalid Submission."));
		return manualFeedbacks;
	}

	private List<Feedback> calculateManualFeedbacks() {
		List<Feedback> manualFeedbacks = new ArrayList<>(this.annotations.stream().map(this::createNewManualFeedback).collect(Collectors.toList()));
		// add the (rated!) rating group annotations
		manualFeedbacks.addAll( //
				this.ratingGroups.stream() //
						.map(this::createNewManualUnreferencedFeedback) //
						.filter(feedback -> Math.abs(feedback.getCredits()) >= 1E-8) //
						.collect(Collectors.toList())//
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
		return absoluteScore / this.exercise.getMaxPoints() * 100D;
	}

	private String calculateResultString(final List<Feedback> allFeedbacks, final double absoluteScore) {
		final List<Feedback> autoFeedbacks = //
				allFeedbacks.stream().filter(feedback -> feedback.getFeedbackType() == FeedbackType.AUTOMATIC).collect(Collectors.toList());

		final List<Feedback> tests = autoFeedbacks.stream().filter(f -> f.getReference() == null).collect(Collectors.toList());
		long positiveTests = tests.stream().filter(Feedback::getPositive).count();
		long numberOfTests = tests.size();

		// ENHANCE We may add "Issues" as text here iff activated ?
		String result = "";
		result += String.format("%d of %d passed, ", positiveTests, numberOfTests);
		result += String.format("%s of %s points", nf.format(absoluteScore), nf.format(this.exercise.getMaxPoints()));
		return result;
	}

	private String convertAnnotationsToJSONString(final List<IAnnotation> givenAnnotations) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(givenAnnotations);
	}

	/**
	 * This transforms Annotations (in the context of the whole model, consisting of
	 * RatingGroupse, MistakteTypes etc) into a payload. In the process, calculation
	 * is done, including
	 * <ul>
	 * <li>calculating the rating score based on our annotations and the previously
	 * existent (automatic) feedbacks (e.g. Unit test results)
	 * <li>creating per-annotation artemis-annotations ("Feedbacks")
	 * {@link FeedbackType#MANUAL}
	 * <li>creating general artemis-annotations ("Feedbacks")
	 * {@link FeedbackType#MANUAL_UNREFERENCED}
	 * <li>creating our own database by serializing our Java Annotations into HIDDEN
	 * {@link FeedbackType#MANUAL_UNREFERENCED} Feedbacks with
	 * <ul>
	 * <li>"CLIENT_DATA" in the <I>text</I> field, as an identifier
	 * <li>the Java Annotations as json blob in the <I>detailText</I> field.
	 * </ul>
	 * </ul>
	 *
	 * @return a json-formattable object ready to be send as payload to the Client
	 */
	public AssessmentResult createAssessmentResult() throws IOException {
		final boolean submissionIsInvalid = this.penaltyCalculationStrategy.submissionIsInvalid();
		// only add preexistent automatic feedback (unit tests etc) and manual feedback.
		// arTem155
		// this should work indepently of invalid or not. if invalid, there should just
		// be no feedbacks.
		final List<Feedback> allFeedbacks = this.calculateAllFeedbacks();
		final double absoluteScore = submissionIsInvalid ? 0.D : Math.max(0.D, this.calculateAbsoluteScore(allFeedbacks));
		final double relativeScore = submissionIsInvalid ? 0.D : this.calculateRelativeScore(absoluteScore);

		return new AssessmentResult(this.submission.getSubmissionId(), this.calculateResultString(allFeedbacks, absoluteScore), "SEMI_AUTOMATIC", relativeScore,
				true, true, null, this.assessor, allFeedbacks);
	}

	private Feedback createNewManualFeedback(IAnnotation annotation) {
		// manual feedbacks do not have no credits!
		final String text = "File " + annotation.getClassFilePath() + " at line " + annotation.getStartLine();
		final String reference = "file:" + annotation.getClassFilePath() + ".java_line:" + (annotation.getStartLine() - 1);

		var mistakeType = annotation.getMistakeType();
		String detailText = "[" + mistakeType.getRatingGroup().getDisplayName() + ":" + mistakeType.getButtonText() + "] ";
		if (mistakeType.isCustomPenalty()) {
			detailText += annotation.getCustomMessage().get() + " (" + nf.format(-annotation.getCustomPenalty().get()) + "P)";
		} else {
			detailText += mistakeType.getMessage();
		}

		return new Feedback(FeedbackType.MANUAL.name(), 0D, null, null, null, text, reference, detailText);
	}

	private Feedback createNewManualUnreferencedFeedback(IRatingGroup ratingGroup) {
		final double calculatedPenalty = this.calculatePenaltyForRatingGroup(ratingGroup);

		String detailText = "";
		detailText += ratingGroup.getDisplayName() + " [" + nf.format(calculatedPenalty);

		if (ratingGroup.hasPenaltyLimit()) {
			detailText += " of at most " + nf.format(-ratingGroup.getPenaltyLimit());
		}

		detailText += " points]";

		for (var mistakeType : this.mistakeTypes) {
			if (!mistakeType.getRatingGroup().equals(ratingGroup)) {
				continue;
			}

			final double currentPenalty = this.calculatePenaltyForMistakeType(mistakeType);
			if (Math.abs(currentPenalty) < 1E-8) {
				continue;
			}

			final List<IAnnotation> currentAnnotations = this.annotations.stream() //
					.filter(annotation -> annotation.getMistakeType().equals(mistakeType)) //
					.collect(Collectors.toList());
			detailText += "\n    * \"" + mistakeType.getButtonText() + "\" [" + nf.format(currentPenalty) + "]:";
			if (mistakeType.isCustomPenalty()) {
				for (var annotation : currentAnnotations) {
					String penalty = nf.format(-annotation.getCustomPenalty().get());
					detailText += "\n        * " + annotation.getClassFilePath() + " at line " + annotation.getStartLine() + " (" + penalty + "P)";
				}
			} else {
				for (var annotation : currentAnnotations) {
					detailText += "\n        * " + annotation.getClassFilePath() + " at line " + annotation.getStartLine();
				}
			}
		}

		if (this.penaltyCalculationStrategy.penaltyLimitIsHitForRatingGroup(ratingGroup)) {
			detailText += "\n    * Note: The sum of penalties hit the penalty limit for this rating group.";
		}

		return new Feedback(FeedbackType.MANUAL_UNREFERENCED.name(), calculatedPenalty, null, null, null, null, null, detailText);
	}

	private List<Feedback> getFilteredPreexistentFeedbacks(FeedbackType feedbackType) {
		List<Feedback> feedbacks = new ArrayList<>();
		for (Feedback feedback : this.lock.getLatestFeedback()) {
			if (feedback.getFeedbackType() == null || feedback.getFeedbackType() != feedbackType) {
				continue;
			}
			feedbacks.add(feedback);
		}
		return feedbacks;
	}

}
