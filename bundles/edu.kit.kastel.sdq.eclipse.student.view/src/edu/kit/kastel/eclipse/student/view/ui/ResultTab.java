/* Licensed under EPL-2.0 2021-2022. */
package edu.kit.kastel.eclipse.student.view.ui;

import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;

import edu.kit.kastel.eclipse.common.view.ui.AbstractResultTab;
import edu.kit.kastel.eclipse.student.view.controllers.StudentViewController;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ResultsDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.client.websocket.WebsocketCallback;
import edu.kit.kastel.sdq.eclipse.grading.api.util.Pair;

public class ResultTab extends AbstractResultTab implements ArtemisStudentTab, WebsocketCallback {
	private StudentViewController viewController;

	public ResultTab(StudentViewController viewController) {
		this.viewController = viewController;
	}

	@Override
	public void create(TabFolder tabFolder) {
		super.createTabFolder(tabFolder);
	}

	@Override
	public void reset() {
		super.resetView();
	}

	@Override
	protected Pair<ResultsDTO, List<Feedback>> getCurrentResultAndFeedback() {
		return this.viewController.getFeedbackExcerise();
	}

	@Override
	protected IExercise getCurrentExercise() {
		return this.viewController.getCurrentSelectedExercise();
	}

	@Override
	public void handleSubmission(Object payload) {
		Display.getDefault().syncExec(this::loadingStarted);
	}

	@Override
	public void handleResult(Object payload) {
		Display.getDefault().syncExec(() -> {
			this.loadingFinished();
			this.reloadFeedbackForExcerise();
		});

	}

	@Override
	public void handleException(Throwable e) {
		Display.getDefault().syncExec(() -> {
			this.loadingFinished();
			log.error(e.getMessage(), e);
		});

	}

	@Override
	public void callExercisesEvent() {
		this.reloadFeedbackForExcerise();
	}

	@Override
	public void callExamEvent() {
		this.reset();
	}

	@Override
	public void setViewController(StudentViewController viewController) {
		this.viewController = viewController;
	}

	@Override
	protected String getCurrentProjectNameForAnnotations() {
		return this.viewController.getCurrentProjectNameInEclipse();
	}
}
