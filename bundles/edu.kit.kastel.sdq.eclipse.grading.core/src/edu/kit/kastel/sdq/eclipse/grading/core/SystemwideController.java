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

	private final Map<Integer, IAssessmentController> assessmentControllers;
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
	public IAssessmentController getAssessmentController(int submissionID, String exerciseConfigName) {
		if (!this.assessmentControllers.containsKey(submissionID)) {
			this.assessmentControllers.put(submissionID, new AssessmentController(this, submissionID, exerciseConfigName));
		}
		return this.assessmentControllers.get(submissionID);
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
