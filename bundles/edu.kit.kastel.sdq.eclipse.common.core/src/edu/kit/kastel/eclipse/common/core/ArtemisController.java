/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.core;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.EclipseArtemisConstants;
import edu.kit.kastel.eclipse.common.api.artemis.ILockResult;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.Feedback;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ICourse;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExam;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExerciseGroup;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ISubmission;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.User;
import edu.kit.kastel.eclipse.common.api.controller.AbstractController;
import edu.kit.kastel.eclipse.common.api.controller.IArtemisController;
import edu.kit.kastel.eclipse.common.api.controller.IViewInteraction;
import edu.kit.kastel.eclipse.common.client.rest.RestClientManager;

public abstract class ArtemisController extends AbstractController implements IArtemisController {
	protected final Map<Integer, ILockResult> lockResults;
	protected final RestClientManager clientManager;
	protected List<ICourse> courses;

	protected ArtemisController(final String host, final String username, final String password, final IViewInteraction handler) {
		super(handler);
		this.clientManager = new RestClientManager(host, username, password);
		this.lockResults = new HashMap<>();
		if (host != null && !host.isBlank()) {
			this.checkVersion();
			this.loginOrNotify();
		}
	}

	protected abstract List<ICourse> fetchCourses();

	@Override
	public User getUserLogin() {
		var login = this.clientManager.getAuthenticationClient();
		if (login == null) {
			return null;
		}
		return login.getUser();
	}

	@Override
	public final List<ICourse> getCourses() {
		if (this.courses == null) {
			this.courses = this.fetchCourses();
		}
		return this.courses;
	}

	@Override
	public List<Feedback> getAllFeedbacksGottenFromLocking(ISubmission submission) {
		ILockResult lockResult = this.lockResults.get(submission.getSubmissionId());
		if (lockResult == null) {
			this.error("No Lock found for submission=" + submission.getSubmissionId(), null);
			return List.of();
		}
		return lockResult.getLatestFeedback();
	}

	@Override
	public List<ISubmission> getBegunSubmissions(IExercise exercise) {
		try {
			return this.clientManager.getSubmissionArtemisClient().getSubmissions(exercise);
		} catch (ArtemisClientException e) {
			this.error(e.getMessage(), e);
			return List.of();
		}
	}

	protected ICourse getCourseByShortName(final String courseShortName) {
		List<ICourse> filteredCourses = this.getCourses().stream().filter(course -> course.getShortName().equals(courseShortName)).toList();
		if (filteredCourses.isEmpty()) {
			this.error("No course found for courseShortName=" + courseShortName, null);
			return null;
		}
		if (filteredCourses.size() > 1) {
			this.error("Multiple courses found for courseShortName=" + courseShortName, null);
			return null;
		}
		return filteredCourses.iterator().next();
	}

	@Override
	public List<String> getCourseShortNames() {
		return this.getCourses().stream().map(ICourse::getShortName).toList();
	}

	@Override
	public List<String> getExamTitles(final String courseShortName) {
		try {
			ICourse course = this.getCourseByShortName(courseShortName);
			if (course == null) {
				return List.of();
			}

			return course.getExams().stream().map(IExam::getTitle).toList();
		} catch (final Exception e) {
			this.error(e.getMessage(), e);
			return List.of();
		}
	}

	@Override
	public List<IExercise> getExercises(final ICourse course, boolean withExamExercises) {
		if (course == null) {
			return List.of();
		}
		try {
			List<IExercise> allExercises = new ArrayList<>(course.getExercises());
			if (withExamExercises) {

				for (IExam e : course.getExams()) {
					for (IExerciseGroup g : e.getExerciseGroups()) {
						allExercises.addAll(g.getExercises());
					}
				}

			}
			return allExercises;
		} catch (final Exception e) {
			this.error(e.getMessage(), e);
			return List.of();
		}
	}

	@Override
	public LocalDateTime getCurrentDate() {
		try {
			return this.clientManager.getUtilArtemisClient().getTime();
		} catch (ArtemisClientException e) {
			return LocalDateTime.now();
		}
	}

	@Override
	public List<IExercise> getExercisesFromExam(final String examTitle) {
		return this.getExercisesFromExam(examTitle, this.getCourses());
	}

	private List<IExercise> getExercisesFromExam(final String examTitle, List<ICourse> courses) {
		IExam foundExam = null;

		for (ICourse course : courses) {
			List<IExam> filteredExams;
			try {
				filteredExams = course.getExams().stream().filter(exam -> exam.getTitle().equals(examTitle)).toList();
			} catch (final Exception e) {
				this.error(e.getMessage(), e);
				continue;
			}
			if (filteredExams.size() == 1) {
				IExam exam = filteredExams.iterator().next();
				if (exam.getTitle().equals(examTitle)) {
					foundExam = exam;
				}
			}
		}
		if (foundExam == null) {
			this.error("No exam found for examTitle=" + examTitle, null);
			return List.of();
		}
		try {
			return foundExam.getExerciseGroups().stream().map(IExerciseGroup::getExercises).flatMap(Collection::stream).toList();
		} catch (final Exception e) {
			this.error(e.getMessage(), e);
			return List.of();
		}

	}

	protected Entry<ICourse, IExam> filterGetExamObjectFromLoadedCourses(String examTitle, List<ICourse> courses) {
		for (ICourse course : courses) {
			List<IExam> filteredExams;
			try {
				filteredExams = course.getExams().stream().filter(exam -> exam.getTitle().equals(examTitle)).toList();
			} catch (final Exception e) {
				this.error(e.getMessage(), e);
				continue;
			}
			if (filteredExams.size() == 1) {
				IExam exam = filteredExams.iterator().next();
				if (exam.getTitle().equals(examTitle)) {
					return Map.entry(course, exam);
				}
			}
		}
		return null;
	}

	private void loginOrNotify() {
		try {
			this.clientManager.login();
		} catch (ArtemisClientException e) {
			this.error(e.getMessage(), e);
		}
	}

	private void checkVersion() {
		try {
			var currentVersion = this.clientManager.getUtilArtemisClient().getVersion();
			if (currentVersion.compareTo(EclipseArtemisConstants.MINIMUM_ARTEMIS_VERSION_INCLUSIVE) == -1) {
				String response = """
						This version of Eclipse Artemis has Artemis %s as minimum requirement.
						Your Artemis instance runs %s
						Proceed on your own responsibility.
						""";
				this.warn(response.formatted(EclipseArtemisConstants.MINIMUM_ARTEMIS_VERSION_INCLUSIVE, currentVersion));
			} else if (currentVersion.compareTo(EclipseArtemisConstants.MAXIMUM_ARTEMIS_VERSION_EXCLUSIVE) != -1) {
				String response = """
						This version of Eclipse Artemis has Artemis %s as maximum (exclusive) requirement.
						Your Artemis instance runs %s
						Proceed on your own responsibility.
						""";
				this.warn(response.formatted(EclipseArtemisConstants.MAXIMUM_ARTEMIS_VERSION_EXCLUSIVE, currentVersion));
			}
		} catch (ArtemisClientException e) {
			this.error(e.getMessage(), e);
		}

	}
}
