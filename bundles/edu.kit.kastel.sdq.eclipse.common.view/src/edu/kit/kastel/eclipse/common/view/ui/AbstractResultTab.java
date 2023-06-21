/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.view.ui;

import static edu.kit.kastel.eclipse.common.view.languages.LanguageSettings.I18N;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.Feedback;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.FeedbackType;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.model.IMistakeType;
import edu.kit.kastel.eclipse.common.api.util.Pair;
import edu.kit.kastel.eclipse.common.core.model.annotation.Annotation;
import edu.kit.kastel.eclipse.common.view.utilities.AssessmentUtilities;

public abstract class AbstractResultTab extends AbstractResultTabCompositeController {

	protected final ILog log = Platform.getLog(this.getClass());

	protected AbstractResultTab(TabFolder tabFolder, boolean hasReloadFunctionality) {
		super(tabFolder, hasReloadFunctionality);
		this.layout(true);
	}

	/**
	 * Get the exercise title that is currently selected.
	 *
	 * @return the current exercise title
	 */
	protected abstract IExercise getCurrentExercise();

	/**
	 * Get the latest result of the currently selected submission.
	 *
	 * @return the current feedback as
	 *         {@code [String completionTime, List<Feedback> feedbacks]}
	 */
	protected abstract Pair<String, List<Feedback>> getCurrentResultAndFeedback();

	/**
	 * Get the project name of the currently selected project in eclipse to add
	 * annotations based on the feedback in Artemis. If set to {@code null} no
	 * annotations will be created.
	 *
	 * @return the name of the project
	 */
	protected abstract String getCurrentProjectNameForAnnotations();

	/**
	 * Returns the current source directory relative to the project directory. For
	 * students this should be "src/".
	 */
	protected abstract String getCurrentSourceDirectoryRelative();

	@Override
	protected final void reloadFeedbackForExercise() {
		var currentExercise = this.getCurrentExercise();
		var resultFeedback = this.getCurrentResultAndFeedback();
		var currentProjectFileForAnnotation = this.getCurrentProjectNameForAnnotations();
		var currentSourceDirectory = this.getCurrentSourceDirectoryRelative();

		if (!resultFeedback.isEmpty()) {
			// IExercise currentExercise, String completionTime, List<Feedback> feedbacks
			this.handleNewResult(currentExercise, resultFeedback.first(), resultFeedback.second());
			if (currentProjectFileForAnnotation != null) {
				this.createAnnotationsMarkers(currentProjectFileForAnnotation, currentSourceDirectory, resultFeedback.second());
			}
		} else {
			this.testTable.removeAll();
			this.resetView();
		}
	}

	private void addResultToTab(IExercise currentExercise, String completionTime, List<Feedback> feedbacks) {
		boolean successOfAutomaticTests = this.calculateSuccessOfAutomaticTests(feedbacks);
		double points = this.calculatePoints(currentExercise, feedbacks);
		double score = this.calculateScore(currentExercise, points);

		this.setSuccessAndScore(currentExercise, successOfAutomaticTests, points, score, completionTime);
		this.layout();
	}

	private double calculatePoints(IExercise exercise, List<Feedback> feedbacks) {
		if (exercise == null || feedbacks == null) {
			return Double.NaN;
		}
		var sum = feedbacks.stream().mapToDouble(f -> f.getCredits() == null ? 0.0 : f.getCredits()).sum();
		return Math.max(0, sum);
	}

	private double calculateScore(IExercise exercise, double points) {
		if (exercise == null || Double.isNaN(points)) {
			return Double.NaN;
		}

		points = Math.max(0, Math.min(exercise.getMaxPoints(), points));
		return points / exercise.getMaxPoints() * 100;
	}

	private boolean calculateSuccessOfAutomaticTests(List<Feedback> feedbacks) {
		if (feedbacks == null || feedbacks.isEmpty()) {
			return false;
		}
		return feedbacks.stream().filter(f -> f.getFeedbackType() == FeedbackType.AUTOMATIC).allMatch(f -> Boolean.TRUE.equals(f.getPositive()));
	}

	private void addFeedbackToTable(Table table, List<Feedback> entries) {
		if (entries == null) {
			return;
		}

		entries.stream().sorted().forEach(feedback -> {
			var name = feedback.getFeedbackType() != FeedbackType.AUTOMATIC && feedback.getText() == null ? I18N().tabResultsTutorComment()
					: feedback.getText();
			this.createTableItemsForFeedback(table, name, feedback);
		});

	}

	private void handleNewResult(IExercise currentExercise, String completionTime, List<Feedback> feedbacks) {
		this.testTable.removeAll();
		this.addFeedbackToTable(this.testTable, feedbacks);
		this.addResultToTab(currentExercise, completionTime, feedbacks);
	}

	/**
	 * Create markers for current annotations in the backend
	 *
	 * @param currentProjectName the current project name in eclipse
	 * @param sourceDirectory    typically "src/" or "assignment/src/"
	 * @param feedbacks          all feedbacks (manual and automatic)
	 */
	protected final void createAnnotationsMarkers(String currentProjectName, String sourceDirectory, List<Feedback> feedbacks) {
		// Translate Feedback to annotations ..
		var feedbackForLines = feedbacks.stream().filter(f -> f.getFeedbackType() == FeedbackType.MANUAL).toList();
		var annotations = this.convertToAnnotation(feedbackForLines);
		for (var annotation : annotations) {
			IMarker present = AssessmentUtilities.findPresentAnnotation(annotation, currentProjectName, "src/");
			if (present != null) {
				try {
					present.delete();
				} catch (CoreException e) {
					this.log.error(e.getMessage(), e);
				}
			}
			try {
				AssessmentUtilities.createMarkerByAnnotation(annotation, currentProjectName, sourceDirectory);
			} catch (ArtemisClientException e) {
				this.log.error(e.getMessage(), e);
			}
		}
	}

	private List<Annotation> convertToAnnotation(List<Feedback> feedbackForLines) {
		List<Annotation> annotations = new ArrayList<>();
		for (Feedback f : feedbackForLines) {
			// e.g., file:src/edu/kit/informatik/Client.java.java_line:21
			String reference = f.getReference();
			if (reference == null || !reference.startsWith("file:")) {
				continue;
			}

			var fileXline = reference.split(".java_line:");

			String id = reference;
			IMistakeType type = null;
			int startLine = Integer.parseInt(fileXline[1]);
			int endLine = startLine;
			String fullyClassifiedClassName = fileXline[0].substring("file:src/".length());
			String customMessage = f.getDetailText();
			Double customPenalty = f.getCredits();

			Annotation annotation = new Annotation(id, type, startLine, endLine, fullyClassifiedClassName, customMessage, customPenalty);
			annotations.add(annotation);
		}
		return annotations;
	}
}
