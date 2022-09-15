/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.core;

import org.eclipse.jface.preference.IPreferenceStore;

import edu.kit.kastel.eclipse.common.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ICourse;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.controller.AbstractController;
import edu.kit.kastel.eclipse.common.api.controller.IArtemisController;
import edu.kit.kastel.eclipse.common.api.controller.IExerciseArtemisController;
import edu.kit.kastel.eclipse.common.api.controller.ISystemwideController;
import edu.kit.kastel.eclipse.common.core.artemis.naming.ProjectFileNamingStrategies;

public abstract class SystemwideController extends AbstractController implements ISystemwideController {
	protected ICourse course;
	protected IExercise exercise;
	protected IPreferenceStore preferenceStore;
	protected IProjectFileNamingStrategy projectFileNamingStrategy;
	protected IExerciseArtemisController exerciseController;

	protected SystemwideController(IPreferenceStore preferenceStore) {
		this.projectFileNamingStrategy = ProjectFileNamingStrategies.DEFAULT.get();
		var loginController = createController(preferenceStore);
		exerciseController = new ExerciseArtemisController(loginController.getUserLogin(), preferenceStore);
	}

	protected abstract IArtemisController createController(IPreferenceStore preferenceStore);

	protected abstract void refreshArtemisController(IPreferenceStore preferenceStore);

	protected void initPreferenceStoreCallback(final IPreferenceStore preferenceStore) {
		// TODO DTHF1: For now we disable that as it causes problems with the login
//		this.preferenceStore.addPropertyChangeListener(event -> {
//			boolean trigger = false;
//			trigger |= PreferenceConstants.GENERAL_ARTEMIS_URL.equals(event.getProperty());
//			trigger |= PreferenceConstants.GENERAL_ADVANCED_ARTEMIS_USER.equals(event.getProperty());
//			trigger |= PreferenceConstants.GENERAL_ADVANCED_ARTEMIS_PASSWORD.equals(event.getProperty());
//
//			if (!trigger) {
//				return;
//			}
//
//			this.refreshArtemisController(preferenceStore);
//		});
	}

	protected boolean nullCheckMembersAndNotify(boolean checkCourseID, boolean checkExerciseID) {
		String alert = "[";
		boolean somethingNull = false;
		if (checkCourseID && this.course == null) {
			alert += "Course is not set ";
			somethingNull = true;
		}
		if (checkExerciseID && this.exercise == null) {
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
	public IExerciseArtemisController getExerciseArtemisController() {
		return exerciseController;
	}

}
