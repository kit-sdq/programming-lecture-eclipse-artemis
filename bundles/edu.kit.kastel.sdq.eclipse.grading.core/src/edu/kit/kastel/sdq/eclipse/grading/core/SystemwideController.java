package edu.kit.kastel.sdq.eclipse.grading.core;

import org.eclipse.jface.preference.IPreferenceStore;

import edu.kit.kastel.sdq.eclipse.grading.api.PreferenceConstants;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.AbstractController;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.IArtemisController;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.IExerciseArtemisController;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.ISystemwideController;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.naming.ProjectFileNamingStrategies;

public abstract class SystemwideController extends AbstractController implements ISystemwideController {
	protected ICourse course;
	protected IExercise exercise;
	protected IPreferenceStore preferenceStore;
	protected IProjectFileNamingStrategy projectFileNamingStrategy;
	protected IExerciseArtemisController exerciseController;
	
	protected SystemwideController(String username, String password) {
		this.projectFileNamingStrategy = ProjectFileNamingStrategies.DEFAULT.get();
		exerciseController = new ExerciseArtemisController(username, password);
	}
	protected abstract void refreshArtemisController(String url, String user, String pass);
		
	protected void initPreferenceStoreCallback(final IPreferenceStore preferenceStore) {
		// change preferences
		this.preferenceStore.addPropertyChangeListener(event -> {
			boolean trigger = false;
			trigger |= PreferenceConstants.ARTEMIS_URL.equals(event.getProperty());
			trigger |= PreferenceConstants.ARTEMIS_USER.equals(event.getProperty());
			trigger |= PreferenceConstants.ARTEMIS_PASSWORD.equals(event.getProperty());

			if (!trigger) {
				return;
			}

			String url = preferenceStore.getString(PreferenceConstants.ARTEMIS_URL);
			String user = preferenceStore.getString(PreferenceConstants.ARTEMIS_USER);
			String pass = preferenceStore.getString(PreferenceConstants.ARTEMIS_PASSWORD);

			this.refreshArtemisController(url, user, pass);
		});
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
