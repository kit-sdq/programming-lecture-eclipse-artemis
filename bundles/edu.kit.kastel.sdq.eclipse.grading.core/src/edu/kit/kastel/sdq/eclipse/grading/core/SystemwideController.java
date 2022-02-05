package edu.kit.kastel.sdq.eclipse.grading.core;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.PreferenceConstants;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.AbstractController;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.IArtemisController;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.ISystemwideController;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.naming.ProjectFileNamingStrategies;

public abstract class SystemwideController extends AbstractController implements ISystemwideController {
	protected ICourse course;
	protected IExercise exercise;
	protected IArtemisController artemisGUIController;
	protected IPreferenceStore preferenceStore;
	protected IProjectFileNamingStrategy projectFileNamingStrategy;
	
	public SystemwideController(final IPreferenceStore preferenceStore) {
		this.preferenceStore = preferenceStore;

		// initialize config
		this.updateConfigFile();

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

			this.setArtemisController(new ArtemisController(this, url, user, pass));
		});
	}
	
	protected SystemwideController(final String artemisHost, final String username, final String password) {
		this.artemisGUIController = new ArtemisController(this, artemisHost, username, password);
		this.projectFileNamingStrategy = ProjectFileNamingStrategies.DEFAULT.get();
	}
	
	@Override
	public IArtemisController getArtemisGUIController() {
		return this.artemisGUIController;
	}
	
	@Override
	public void setExerciseId(final String exerciseShortName) throws ArtemisClientException {

		// Normal exercises
		List<IExercise> exercises = this.course.getExercises();
		
		this.course.getExams().stream().map(e -> artemisGUIController.getExercisesFromExam(e.getTitle()).getExercises())
				.forEach(e -> e.forEach(exercises::add));

		for (IExercise ex : exercises) {
			if (ex.getShortName().equals(exerciseShortName)) {
				this.exercise = ex;
				return;
			}
		}

		this.error("No Exercise with the given shortName \"" + exerciseShortName + "\" found.", null);
	}

}
