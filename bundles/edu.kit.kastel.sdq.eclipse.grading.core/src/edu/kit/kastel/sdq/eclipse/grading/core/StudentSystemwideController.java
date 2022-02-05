package edu.kit.kastel.sdq.eclipse.grading.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ResultsDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.AbstractController;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.IArtemisController;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.IStudentSystemwideController;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.ISystemwideController;

public class StudentSystemwideController extends SystemwideController implements IStudentSystemwideController {

	@Override
	public boolean loadExerciseForStudent() {
		if (this.nullCheckMembersAndNotify(true, true, false)) {
			this.warn("No excercise is selected");
			return false;
		}
		this.updateConfigFile();

		// perform download. Revert state if that fails.
		if (!this.getArtemisGUIController().loadExerciseInWorkspaceForStudent(this.course, this.exercise,
				this.projectFileNamingStrategy)) {
			this.backendStateMachine.revertLatestTransition();
			return false;
		}
		return true;
	}

	@Override
	public boolean submitSolution() {
		if (this.nullCheckMembersAndNotify(true, true, false)) {
			this.warn("No excercise is selected");
			return false;
		}

		if (isSelectedExerciseExpired()) {
			this.error("Can't submit exercise. Excerise is out-of-date, it was due to: "
					+ this.exercise.getDueDate().toGMTString(), null);
			return false;
		}

		if (!this.confirm(
				"Your solutions will be submitted for the selected exercise. Make sure all files are saved.")) {
			return false;
		}

		if (!this.getArtemisGUIController().submitSolution(this.course, this.exercise,
				this.projectFileNamingStrategy)) {
			this.warn("Your Solution was not submitted");
			return false;
		}
		this.info("Your solution was successfully submitted");
		return true;
	}

	@Override
	public boolean cleanWorkspace() {
		if (this.nullCheckMembersAndNotify(true, true, false)) {
			this.warn("No excercise is selected");
			return false;
		}
		if (!this.confirm("Your changes will be deleted. Are you sure?")) {
			return false;
		}

		Optional<Set<String>> deletedFiles = this.getArtemisGUIController().cleanWorkspace(this.course, this.exercise,
				this.projectFileNamingStrategy);
		if (deletedFiles.isEmpty()) {
			this.warn("ERROR, occured while cleaning the workspace");
			return false;
		}
		this.info("Your workspace was successfully cleaned. \n" + "Following files have been reset: \n"
				+ deletedFiles.get());
		return true;

	}

	@Override
	public Map<ResultsDTO, List<Feedback>> getFeedbackExcerise() {
		if (this.nullCheckMembersAndNotify(true, true, false)) {
			this.warn("No excercise is selected");
			return new HashMap<>();
		}

		return this.getArtemisGUIController().getFeedbackExcerise(this.course, this.exercise);
	}

	@Override
	public boolean isSelectedExerciseExpired() {
		if (exercise != null) {
			if (exercise.getDueDate() != null) {
				return exercise.getDueDate().before(getCurrentDate());
			} else {
				return false;
			}
		}
		return true;
	}

	private Date getCurrentDate() {
		return this.artemisGUIController.getCurrentDate();
	}

	@Override
	public IExercise getCurrentSelectedExercise() {
		return exercise;
	}
	
	@Override
	public IExam setExam(String examName) {
		Optional<IExam> examOpt;
		try {
			examOpt = this.course.getExams().stream().filter(exam -> examName.equals(exam.getTitle())).findFirst();
			if (examOpt.isPresent()) {
				this.examName = examName;
			}
			return examOpt.orElse(null);
		} catch (ArtemisClientException e) {
			this.error("Can not set exam!", e);
			return null;
		}
	}
	
	@Override
	public IExam getExam() {
		return exam.getExam();
	}
	
	@Override
	public IExam startExam() {
		if(exam != null) {
			if (this.confirm("Are you sure to start the exam?")) {
				exam = this.artemisGUIController.startExam(course, exam.getExam());
				return exam.getExam();
			}
		}
		return null;
	}
	
	@Override
	public List<IExercise> getExerciseShortNamesFromExam(String examShortName) {
		if(exam == null || !exam.getExam().getTitle().equals(examShortName))
			exam = this.artemisGUIController.getExercisesFromExam(examShortName);
		return exam.getExercises();
	}
	
	@Override
	public void setExerciseIdWithSelectedExam(final String exerciseShortName) throws ArtemisClientException {
		List<IExercise> exercises = new ArrayList<>();
		// Normal exercises
		if(exam == null) {
			this.course.getExercises().forEach(exercises::add);
		} else {
			exam.getExercises().forEach(exercises::add);
		}

		for (IExercise ex : exercises) {
			if (ex.getShortName().equals(exerciseShortName)) {
				this.exercise = ex;
				return;
			}
		}

		this.error("No Exercise with the given shortName \"" + exerciseShortName + "\" found.", null);
	}

}
