package edu.kit.kastel.sdq.eclipse.grading.core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import edu.kit.kastel.sdq.eclipse.grading.api.IArtemisGUIController;
import edu.kit.kastel.sdq.eclipse.grading.api.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.ISystemwideController;
import edu.kit.kastel.sdq.eclipse.grading.core.config.ConfigDao;
import edu.kit.kastel.sdq.eclipse.grading.core.config.JsonFileConfigDao;

public class SystemwideController implements ISystemwideController {

	private final Map<String, IAssessmentController> assessmentControllers;
	private final IArtemisGUIController artemisGUIController;

	private ConfigDao configDao;

	public SystemwideController(final File configFile, final String artemisHost, final String username, final String password) {
		this.setConfigFile(configFile);
		this.assessmentControllers = new HashMap<>();

		this.artemisGUIController = new ArtemisGUIController(this, artemisHost, username, password);
	}

	@Override
	public IArtemisGUIController getArtemisGUIController() {
		return this.artemisGUIController;
	}

	@Override
	public IAssessmentController getAssessmentController(String exerciseName) {
		if (!this.assessmentControllers.containsKey(exerciseName)) {
			this.assessmentControllers.put(exerciseName, new AssessmentController(this, exerciseName));
		}
		return this.assessmentControllers.get(exerciseName);
	}

	/**
	 *
	 * @return this system's configDao.
	 */
	protected ConfigDao getConfigDao() {
		return this.configDao;
	}

	@Override
	public void setConfigFile(File newConfigFile) {
		this.configDao = new JsonFileConfigDao(newConfigFile);
	}
}
