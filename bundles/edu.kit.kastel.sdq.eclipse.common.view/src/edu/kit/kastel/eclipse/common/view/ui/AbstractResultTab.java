/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.view.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import edu.kit.kastel.eclipse.common.view.utilities.AssessmentUtilities;
import edu.kit.kastel.eclipse.common.view.utilities.ResourceBundleProvider;
import edu.kit.kastel.sdq.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.FeedbackType;
import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.common.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.common.api.util.Triple;
import edu.kit.kastel.sdq.eclipse.common.core.model.annotation.Annotation;

public abstract class AbstractResultTab extends ResultTabUI {
	private static final String CHECK_MARK_IN_UTF8 = "\u2713";
	private static final String X_MARK_IN_UTF8 = "\u2717";

	protected final ILog log = Platform.getLog(this.getClass());

	protected AbstractResultTab(boolean hasReloadFunctionality) {
		super(hasReloadFunctionality);
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
	 *         {@code [String completionTime, String resultString, List<Feedback> feedbacks]}
	 */
	protected abstract Triple<String, String, List<Feedback>> getCurrentResultAndFeedback();

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
	protected final void reloadFeedbackForExcerise() {
		var currentExercise = this.getCurrentExercise();
		var resultFeedback = this.getCurrentResultAndFeedback();
		var currentProjectFileForAnnotation = this.getCurrentProjectNameForAnnotations();
		var currentSourceDirectory = this.getCurrentSourceDirectoryRelative();

		if (!resultFeedback.isEmpty()) {
			// IExercise currentExercise, String completionTime, String resultString,
			// List<Feedback> feedbacks
			this.handleNewResult(currentExercise, resultFeedback.first(), resultFeedback.second(), resultFeedback.third());
			if (currentProjectFileForAnnotation != null) {
				this.createAnnotationsMarkers(currentProjectFileForAnnotation, currentSourceDirectory, resultFeedback.third());
			}
		} else {
			this.feedbackTable.removeAll();
			this.resetView();
		}
	}

	private void addResultToTab(IExercise currentExercise, String completionTime, String resultString, List<Feedback> feedbacks) {
		Display display = Display.getDefault();

		boolean successOfAutomaticTests = this.calculateSuccessOfAutomaticTests(feedbacks);
		double points = this.calculatePoints(currentExercise, feedbacks);

		double score = this.calculateScore(currentExercise, points);

		String title = currentExercise == null ? "Unknown Task" : currentExercise.getTitle();

		this.btnResultSuccessful.setForeground(successOfAutomaticTests ? display.getSystemColor(SWT.COLOR_GREEN) : display.getSystemColor(SWT.COLOR_RED));
		this.btnResultSuccessful.setText("Test(s) " + (successOfAutomaticTests ? ResourceBundleProvider.getResourceBundle().getString("tabs.results.successful")
				: ResourceBundleProvider.getResourceBundle().getString("tabs.results.unsuccessful")));

		this.lblResultExerciseShortName.setText(title);
		this.lblResultExerciseDescription.setText(completionTime == null ? "" : completionTime);
		this.resultScore.setText(resultString == null ? String.format(Locale.ENGLISH, "Score: %.2f%%", score) : resultString);
		this.lblPoints.setText(String.format(Locale.ENGLISH, "Points: %.2f", points));
		this.layout();

	}

	private double calculatePoints(IExercise exercise, List<Feedback> feedbacks) {
		if (exercise == null || feedbacks == null) {
			return Double.NaN;
		}
		return feedbacks.stream().mapToDouble(f -> f.getCredits() == null ? 0.0 : f.getCredits()).sum();
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
			var name = feedback.getFeedbackType() != FeedbackType.AUTOMATIC && feedback.getText() == null
					? ResourceBundleProvider.getResourceBundle().getString("tabs.results.tutorComment")
					: feedback.getText();
			this.createTableItemsForFeedback(table, name, feedback);
		});

	}

	private void createTableItemsForFeedback(Table table, String name, Feedback feedback) {
		String roundedCredits = feedback.getCredits() == null ? "0.00" : String.format(Locale.ENGLISH, "%.2f", feedback.getCredits());
		String success = this.calculateSuccessMessage(feedback);
		int colorIndicator = this.calculateSuccessColorIndicator(feedback);

		final TableItem item = new TableItem(table, SWT.NULL);
		item.setData(feedback);
		item.setText(0, name);
		item.setText(1, "" + roundedCredits);
		item.setText(2, success);
		item.setForeground(2, Display.getDefault().getSystemColor(colorIndicator));
		item.setText(3, feedback.getDetailText() != null ? CHECK_MARK_IN_UTF8 : X_MARK_IN_UTF8);
	}

	private String calculateSuccessMessage(Feedback feedback) {
		if (feedback.getPositive() == null) {
			return "";
		}
		return Boolean.TRUE.equals(feedback.getPositive()) ? ResourceBundleProvider.getResourceBundle().getString("tabs.results.successful")
				: ResourceBundleProvider.getResourceBundle().getString("tabs.results.unsuccessful");
	}

	private int calculateSuccessColorIndicator(Feedback feedback) {
		if (feedback.getPositive() == null) {
			return SWT.COLOR_BLACK;
		}
		return Boolean.TRUE.equals(feedback.getPositive()) ? SWT.COLOR_GREEN : SWT.COLOR_RED;
	}

	private void handleNewResult(IExercise currentExercise, String completionTime, String resultString, List<Feedback> feedbacks) {
		this.feedbackTable.removeAll();
		this.addFeedbackToTable(this.feedbackTable, feedbacks);
		this.addResultToTab(currentExercise, completionTime, resultString, feedbacks);
		this.feedbackContainerComposite.pack();
		this.feedbackContentComposite.setVisible(true);
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

			String uuid = reference;
			IMistakeType type = null;
			int startLine = Integer.parseInt(fileXline[1]);
			int endLine = startLine;
			String fullyClassifiedClassName = fileXline[0].substring("file:src/".length());
			String customMessage = f.getDetailText();
			Double customPenalty = f.getCredits();

			Annotation annotation = new Annotation(uuid, type, startLine, endLine, fullyClassifiedClassName, customMessage, customPenalty);
			annotations.add(annotation);
		}
		return annotations;
	}
}
