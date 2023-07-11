/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import edu.kit.kastel.eclipse.common.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.eclipse.common.api.controller.AbstractController;
import edu.kit.kastel.eclipse.common.api.controller.IArtemisController;
import edu.kit.kastel.eclipse.common.api.controller.IExerciseArtemisController;
import edu.kit.kastel.eclipse.common.api.controller.ISubmissionLifecycleCallback;
import edu.kit.kastel.eclipse.common.api.controller.ISystemwideController;
import edu.kit.kastel.eclipse.common.api.controller.IViewInteraction;
import edu.kit.kastel.eclipse.common.core.artemis.naming.ProjectFileNamingStrategies;
import edu.kit.kastel.sdq.artemis4j.api.artemis.Course;
import edu.kit.kastel.sdq.artemis4j.api.artemis.Exercise;

public abstract class SystemwideController extends AbstractController implements ISystemwideController {
	protected final List<ISubmissionLifecycleCallback> buildCompletedCallbacks;

	protected Course course;
	protected Exercise exercise;
	protected IPreferenceStore preferenceStore;
	protected final IProjectFileNamingStrategy projectFileNamingStrategy;
	protected final IExerciseArtemisController exerciseController;

	protected SystemwideController(IPreferenceStore preferenceStore, IViewInteraction handler) {
		super(handler);
		this.buildCompletedCallbacks = new ArrayList<>();
		this.projectFileNamingStrategy = ProjectFileNamingStrategies.DEFAULT.get();
		var loginController = this.createController(preferenceStore, handler);
		this.exerciseController = new ExerciseArtemisController(handler, loginController.getUserLogin(), preferenceStore);
	}

	protected abstract IArtemisController createController(IPreferenceStore preferenceStore, IViewInteraction handler);

	protected boolean nullCheckMembersAndNotify(boolean checkCourseId, boolean checkExerciseId) {
		String alert = "[";
		boolean somethingNull = false;
		if (checkCourseId && this.course == null) {
			alert += "Course is not set ";
			somethingNull = true;
		}
		if (checkExerciseId && this.exercise == null) {
			alert += "Exercise is not set ";
			somethingNull = true;
		}
		if (somethingNull) {
			alert += "]";
			this.warn(alert);
		}
		return somethingNull;
	}

	@Override
	public void addSubmissionBuildListener(ISubmissionLifecycleCallback callback) {
		this.buildCompletedCallbacks.add(callback);
	}
}
