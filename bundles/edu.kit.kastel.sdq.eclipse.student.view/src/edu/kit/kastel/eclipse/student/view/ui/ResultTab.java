/* Licensed under EPL-2.0 2021-2022. */
package edu.kit.kastel.eclipse.student.view.ui;

import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;

import edu.kit.kastel.eclipse.common.api.artemis.mapping.Feedback;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.client.websocket.WebsocketCallback;
import edu.kit.kastel.eclipse.common.api.util.Pair;
import edu.kit.kastel.eclipse.common.view.ui.AbstractResultTab;
import edu.kit.kastel.eclipse.student.view.controllers.StudentViewController;

public class ResultTab extends AbstractResultTab implements WebsocketCallback {
	private StudentViewController viewController;

	public ResultTab(StudentViewController viewController, TabFolder tabFolder) {
		super(tabFolder, true);
		this.viewController = viewController;
	}

	public void reset() {
		super.resetView();
	}

	@Override
	protected Pair<String, List<Feedback>> getCurrentResultAndFeedback() {
		var details = this.viewController.getFeedbackExcerise();
		if (details.isEmpty()) {
			return new Pair<>(null, null);
		}
		return new Pair<>(details.first().completionDateAsString(), details.second());
	}

	@Override
	protected IExercise getCurrentExercise() {
		return this.viewController.getCurrentSelectedExercise();
	}

	@Override
	protected String getCurrentProjectNameForAnnotations() {
		return this.viewController.getCurrentProjectNameInEclipse();
	}

	@Override
	protected String getCurrentSourceDirectoryRelative() {
		return "src/";
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
			this.log.error(e.getMessage(), e);
		});

	}

	public void callExercisesEvent() {
		this.reloadFeedbackForExcerise();
	}

	public void callExamEvent() {
		this.reset();
	}

	public void setViewController(StudentViewController viewController) {
		this.viewController = viewController;
	}

}
