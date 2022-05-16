/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.grading.view.assessment;

import java.util.List;

import org.eclipse.swt.widgets.TabFolder;

import edu.kit.kastel.eclipse.common.api.artemis.mapping.Feedback;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.FeedbackType;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.controller.IGradingSystemwideController;
import edu.kit.kastel.eclipse.common.api.util.Triple;
import edu.kit.kastel.eclipse.common.view.ui.AbstractResultTab;

public class ResultTab extends AbstractResultTab {

	private IGradingSystemwideController controller;

	public ResultTab(IGradingSystemwideController controller, TabFolder tabFolder) {
		super(tabFolder, false);
		this.controller = controller;
	}

	public void setController(IGradingSystemwideController controller) {
		this.controller = controller;
	}

	@Override
	protected IExercise getCurrentExercise() {
		return this.controller.getCurrentAssessmentController().getExercise();
	}

	@Override
	protected Triple<String, String, List<Feedback>> getCurrentResultAndFeedback() {
		var submission = this.controller.getCurrentAssessmentController().getSubmission();
		var feedbacks = this.controller.getArtemisController().getAllFeedbacksGottenFromLocking(submission);
		// TODO Extract these strings from Artemis request
		String completionTime = null;
		String resultText = null;

		return new Triple<>(completionTime, resultText, feedbacks.stream().filter(f -> f.getFeedbackType() == FeedbackType.AUTOMATIC).toList());
	}

	@Override
	protected String getCurrentProjectNameForAnnotations() {
		// null because annotations shall not be generated for the grading view.
		return null;
	}

	@Override
	protected String getCurrentSourceDirectoryRelative() {
		// null because annotations shall not be generated for the grading view.
		return null;
	}

	public void loadFeedbackForExcerise() {
		this.reloadFeedbackForExcerise();
	}

	public void reset() {
		this.resetView();
	}

}
