/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.grading.view.assessment;

import java.util.List;

import org.eclipse.swt.widgets.TabFolder;

import edu.kit.kastel.eclipse.common.view.ui.AbstractResultTab;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.FeedbackType;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ResultsDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.IGradingSystemwideController;
import edu.kit.kastel.sdq.eclipse.grading.api.util.Pair;

public class ResultTab extends AbstractResultTab {

	private IGradingSystemwideController controller;

	public ResultTab(IGradingSystemwideController controller) {
		this.controller = controller;
	}

	public void setController(IGradingSystemwideController controller) {
		this.controller = controller;
	}

	public void create(TabFolder tabFolder) {
		this.createTabFolder(tabFolder);
	}

	@Override
	protected IExercise getCurrentExercise() {
		return this.controller.getCurrentAssessmentController().getExercise();
	}

	@Override
	protected Pair<ResultsDTO, List<Feedback>> getCurrentResultAndFeedback() {
		var submission = this.controller.getCurrentAssessmentController().getSubmission();
		var feedbacks = this.controller.getArtemisController().getAllFeedbacksGottenFromLocking(submission);

		return new Pair<>(feedbacks.first() == null ? new ResultsDTO() : feedbacks.first(),
				feedbacks.second().stream().filter(f -> f.getFeedbackType() == FeedbackType.AUTOMATIC).toList());
	}

	@Override
	protected String getCurrentProjectNameForAnnotations() {
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
