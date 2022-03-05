package edu.kit.kastel.sdq.eclipse.grading.core;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.jface.preference.IPreferenceStore;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.PreferenceConstants;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IStudentExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ParticipationDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ResultsDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.client.websocket.WebsocketCallback;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.IArtemisController;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.IStudentArtemisController;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.IStudentSystemwideController;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.IWebsocketController;
import edu.kit.kastel.sdq.eclipse.grading.core.messages.Messages;

public class StudentSystemwideController extends SystemwideController implements IStudentSystemwideController {

	private IStudentExam exam;
	private IWebsocketController websocketController;
	private IStudentArtemisController artemisGUIController;
	private String artemisHost;

	public StudentSystemwideController(final IPreferenceStore preferenceStore) {
		super(preferenceStore.getString(PreferenceConstants.ARTEMIS_USER), //
				preferenceStore.getString(PreferenceConstants.ARTEMIS_PASSWORD));
		createControllers(preferenceStore.getString(PreferenceConstants.ARTEMIS_URL), //
				preferenceStore.getString(PreferenceConstants.ARTEMIS_USER), //
				preferenceStore.getString(PreferenceConstants.ARTEMIS_PASSWORD) //
		);
		this.preferenceStore = preferenceStore;

		this.initPreferenceStoreCallback(preferenceStore);
	}

	public StudentSystemwideController(final String artemisHost, final String username, final String password) {
		super(username, password);
		createControllers(artemisHost, username, password);
	}

	private void createControllers(final String artemisHost, final String username, final String password) {
		StudentArtemisController controller = new StudentArtemisController(artemisHost, username, password);
		this.artemisGUIController = controller;
		this.websocketController = controller;
		this.artemisHost = artemisHost;
	}

	@Override
	public void setExerciseId(final String exerciseShortName) throws ArtemisClientException {

		// Normal exercises
		List<IExercise> exercises = this.course.getExercises();

		this.course.getExams().stream().map(e -> artemisGUIController.getExercisesFromStudentExam(e.getTitle()).getExercises())
				.forEach(e -> e.forEach(exercises::add));

		for (IExercise ex : exercises) {
			if (ex.getShortName().equals(exerciseShortName)) {
				this.exercise = ex;
				return;
			}
		}

		this.error("No Exercise with the given shortName \"" + exerciseShortName + "\" found.", null); //$NON-NLS-2$
	}

	@Override
	public boolean loadExerciseForStudent() {
		if (this.nullCheckMembersAndNotify(true, true)) {
			this.warn("No excercise is selected");
			return false;
		}

		Optional<ParticipationDTO> participation = this.artemisGUIController.getParticipation(course, exercise);
		if (participation.isEmpty()) {
			this.warn("Can not create participation for exercise.");
			return false;
		}
		// perform download. Revert state if that fails.
		try {
			this.exerciseController.loadExerciseInWorkspaceForStudent(this.course, this.exercise, this.projectFileNamingStrategy,
					participation.get().getRepositoryUrl());
			return true;
		} catch (ArtemisClientException e) {
			this.error(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public boolean submitSolution() {
		if (this.nullCheckMembersAndNotify(true, true)) {
			this.warn("No excercise is selected");
			return false;
		}

		if (isSelectedExerciseExpired()) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime dueDate = convertToLocalDateTimeViaInstant(this.exercise.getDueDate());
			this.error("Can't submit exercise. Excerise is out-of-date, it was due to: " + dueDate.format(formatter), null);
			return false;
		}

		if (!this.confirm(Messages.STUDENT_ARTMIS_CONTROLLER_SUBMITTING_SOLUTION)) {
			return false;
		}

		try {
			return this.exerciseController.commitAndPushExercise(course, exercise, projectFileNamingStrategy);
		} catch (ArtemisClientException e) {
			this.error(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public boolean cleanWorkspace() {
		if (this.nullCheckMembersAndNotify(true, true)) {
			this.warn("No excercise is selected");
			return false;
		}
		if (!this.confirm(Messages.STUDENT_ARTMIS_CONTROLLER_CLEAN)) {
			return false;
		}

		Optional<Set<String>> deletedFiles = this.exerciseController.cleanWorkspace(this.course, this.exercise, this.projectFileNamingStrategy);
		if (deletedFiles.isEmpty()) {
			this.warn("Can't clean selected exercise " + exercise.getShortName() //
					+ ".\n Exercise not found in workspace. \n Please load exercise first");
			return false;
		}
		this.info(Messages.STUDENT_ARTMIS_CONTROLLER_CLEAN_SUCCESSFUL + deletedFiles.get());
		return true;

	}

	@Override
	public Map<ResultsDTO, List<Feedback>> getFeedbackExcerise() {
		if (this.nullCheckMembersAndNotify(true, true)) {
			return new HashMap<>();
		}

		return this.artemisGUIController.getFeedbackForExercise(this.course, this.exercise);
	}

	@Override
	public boolean isSelectedExerciseExpired() {
		if (exercise != null) {
			if (exercise.getDueDate() != null) {
				LocalDateTime dueDate = convertToLocalDateTimeViaInstant(this.exercise.getDueDate());
				return dueDate.isBefore(getCurrentDate());
			} else {
				return false;
			}
		}
		return true;
	}

	private LocalDateTime getCurrentDate() {
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
			return examOpt.orElse(null);
		} catch (ArtemisClientException e) {
			this.error("Can not set exam!", e);
			return null;
		}
	}

	@Override
	public IStudentExam getExam() {
		return exam;
	}

	@Override
	public IStudentExam startExam() {
		if (exam != null) {
			return this.artemisGUIController.startExam(course, exam.getExam());
		}
		return null;
	}

	@Override
	public List<IExercise> getExerciseShortNamesFromExam(String examShortName) {
		if (exam == null || exam.getExam() == null || !exam.getExam().getTitle().equals(examShortName))
			exam = this.artemisGUIController.getExercisesFromStudentExam(examShortName);
		return exam.getExercises();
	}

	@Override
	public void setExamToNull() {
		exam = null;
	}

	@Override
	public void setExerciseIdWithSelectedExam(final String exerciseShortName) throws ArtemisClientException {
		List<IExercise> exercises = new ArrayList<>();
		// Normal exercises
		if (exam != null) {
			exam.getExercises().forEach(exercises::add);
		}

		this.course.getExercises().forEach(exercises::add);

		for (IExercise ex : exercises) {
			if (ex.getShortName().equals(exerciseShortName)) {
				this.exercise = ex;
				return;
			}
		}

		this.error("No Exercise with the given shortName \"" + exerciseShortName + "\" found.", null); //$NON-NLS-2$
	}

	@Override
	public boolean connectToWebsocket(WebsocketCallback callback) {
		return this.websocketController.connectToWebsocket(callback);
	}

	@Override
	public List<String> setCourseIdAndGetExerciseShortNames(final String courseShortName) throws ArtemisClientException {
		for (ICourse c : this.getArtemisController().getCourses()) {
			if (c.getShortName().equals(courseShortName)) {
				this.course = c;
				return c.getExercises().stream().map(IExercise::getShortName).collect(Collectors.toList());
			}
		}
		this.error("No Course with the given shortName \"" + courseShortName + "\" found.", null); //$NON-NLS-2$
		return List.of();
	}

	@Override
	protected void refreshArtemisController(String url, String user, String pass) {
		this.createControllers(url, user, pass);
	}

	@Override
	public IArtemisController getArtemisController() {
		return artemisGUIController;
	}

	@Override
	public String getExamUrlForCurrentExam() {
		if (this.exam == null || this.exam.getExam() == null || this.course == null) {
			return artemisHost;
		}
		return String.format(artemisHost + "/courses/%d/exams/%d", this.course.getCourseId(), this.exam.getExam().getExamId());
	}

	private LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
		return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	@Override
	public boolean resetSelectedExercise() {
		if (this.nullCheckMembersAndNotify(true, true)) {
			this.warn("No excercise is selected");
			return false;
		}
		if (!this.confirm(Messages.STUDENT_ARTMIS_CONTROLLER_CLEAN)) {
			return false;
		}
		try {
			this.exerciseController.deleteExercise(course, exercise, projectFileNamingStrategy);
			this.loadExerciseForStudent();
		} catch (ArtemisClientException e) {
			this.error(e.getMessage(), e);
			return false;
		}

		this.info(Messages.STUDENT_ARTMIS_CONTROLLER_CLEAN_SUCCESSFUL);
		return true;
	}

	@Override
	public boolean isSelectedExerciseInWorkspace() {
		return this.exerciseController.isExerciseInWorkspace(course, exercise, projectFileNamingStrategy);
	}

	@Override
	public void resetBackendState() {
		this.exercise = null;
	}
}
