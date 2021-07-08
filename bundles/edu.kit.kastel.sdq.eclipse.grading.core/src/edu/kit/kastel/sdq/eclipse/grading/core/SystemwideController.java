package edu.kit.kastel.sdq.eclipse.grading.core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import edu.kit.kastel.sdq.eclipse.grading.api.IArtemisGUIController;
import edu.kit.kastel.sdq.eclipse.grading.api.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.ISubmission;
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
		Integer courseID = null;
		Integer exerciseID = null;
		for (ICourse course : this.getArtemisGUIController().getCourses()) {
			for (IExercise exercise : course.getExercises()) {
				Optional<ISubmission> submissionOptional = exercise.getSubmissions().stream().filter(submission -> submission.getSubmissionId() == submissionID).findAny();
				if (submissionOptional.isPresent()) {
					courseID = course.getCourseId();
					exerciseID = exercise.getExerciseId();
				}
			}
		}
		if (courseID == null) throw new RuntimeException("No course found with the submissionID \"" + submissionID + "\".");

		return this.getAssessmentController(submissionID, exerciseConfigName, courseID, exerciseID);
	}


	@Override
	public IAssessmentController getAssessmentController(int submissionID, String exerciseConfigName, int courseID,
			int exerciseID) {
		if (!this.assessmentControllers.containsKey(submissionID)) {
			this.assessmentControllers.put(submissionID, new AssessmentController(this, courseID, exerciseID, submissionID, exerciseConfigName));
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
