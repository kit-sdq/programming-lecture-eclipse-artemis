/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Version;

import edu.kit.kastel.eclipse.common.api.EclipseArtemisConstants;
import edu.kit.kastel.eclipse.common.api.controller.AbstractController;
import edu.kit.kastel.eclipse.common.api.controller.IArtemisController;
import edu.kit.kastel.eclipse.common.api.controller.IViewInteraction;
import edu.kit.kastel.eclipse.common.client.rest.LoginManager;
import edu.kit.kastel.sdq.artemis4j.api.ArtemisClientException;
import edu.kit.kastel.sdq.artemis4j.api.artemis.Course;
import edu.kit.kastel.sdq.artemis4j.api.artemis.Exercise;
import edu.kit.kastel.sdq.artemis4j.api.artemis.User;
import edu.kit.kastel.sdq.artemis4j.api.artemis.assessment.Feedback;
import edu.kit.kastel.sdq.artemis4j.api.artemis.assessment.LockResult;
import edu.kit.kastel.sdq.artemis4j.api.artemis.assessment.Submission;
import edu.kit.kastel.sdq.artemis4j.api.artemis.exam.Exam;
import edu.kit.kastel.sdq.artemis4j.api.artemis.exam.ExerciseGroup;
import edu.kit.kastel.sdq.artemis4j.client.RestClientManager;

public abstract class ArtemisController extends AbstractController implements IArtemisController {
	protected final Map<Integer, LockResult> lockResults;
	protected final RestClientManager clientManager;
	protected List<Course> courses;
	private final Version pluginVersion;

	protected ArtemisController(Version pluginVersion, final String host, final String username, final String password, final IViewInteraction handler) {
		super(handler);
		this.pluginVersion = pluginVersion;
		this.clientManager = new RestClientManager(host.trim(), new LoginManager(host.trim(), username, password));
		this.lockResults = new HashMap<>();
		if (!host.isBlank()) {
			this.checkVersion();
			this.loginOrNotify();
		}
	}

	protected abstract List<Course> fetchCourses();

	@Override
	public User getUserLogin() {
		var login = this.clientManager.getAuthenticationClient();
		if (login == null) {
			return null;
		}
		return login.getUser();
	}

	@Override
	public final List<Course> getCourses() {
		if (this.courses == null) {
			this.courses = this.fetchCourses();
		}
		return this.courses;
	}

	@Override
	public List<Feedback> getAllFeedbacksGottenFromLocking(Submission submission) {
		LockResult lockResult = this.lockResults.get(submission.getSubmissionId());
		if (lockResult == null) {
			this.error("No Lock found for submission=" + submission.getSubmissionId(), null);
			return List.of();
		}
		return lockResult.getLatestFeedback();
	}

	@Override
	public List<Submission> getBegunSubmissions(Exercise exercise) {
		try {
			return this.clientManager.getSubmissionArtemisClient().getSubmissions(exercise);
		} catch (ArtemisClientException e) {
			this.error(e.getMessage(), e);
			return List.of();
		}
	}

	protected Course getCourseByShortName(final String courseShortName) {
		List<Course> filteredCourses = this.getCourses().stream().filter(course -> course.getShortName().equals(courseShortName)).toList();
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
		return this.getCourses().stream().map(Course::getShortName).toList();
	}

	@Override
	public List<String> getExamTitles(final String courseShortName) {
		try {
			Course course = this.getCourseByShortName(courseShortName);
			if (course == null) {
				return List.of();
			}

			return course.getExams().stream().map(Exam::getTitle).toList();
		} catch (final Exception e) {
			this.error(e.getMessage(), e);
			return List.of();
		}
	}

	@Override
	public List<Exercise> getExercisesFromExam(final String examTitle) {
		return this.getExercisesFromExam(examTitle, this.getCourses());
	}

	private List<Exercise> getExercisesFromExam(final String examTitle, List<Course> courses) {
		Exam foundExam = null;

		for (Course course : courses) {
			List<Exam> filteredExams;
			try {
				filteredExams = course.getExams().stream().filter(exam -> exam.getTitle().equals(examTitle)).toList();
			} catch (final Exception e) {
				this.error(e.getMessage(), e);
				continue;
			}
			if (filteredExams.size() == 1) {
				Exam exam = filteredExams.iterator().next();
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
			return foundExam.getExerciseGroups().stream().map(ExerciseGroup::getExercises).flatMap(Collection::stream).toList();
		} catch (final Exception e) {
			this.error(e.getMessage(), e);
			return List.of();
		}

	}

	private void loginOrNotify() {
		try {
			this.clientManager.login();
		} catch (ArtemisClientException e) {
			this.error(e.getMessage(), e);
		}
	}

	private void checkVersion() {
		boolean newerAvailable = this.compareVersions(EclipseArtemisConstants.GITHUB_GRADING_TOOL_VERSION) > 0;

		if (newerAvailable) {
			String response = """
					This version of the Eclipse Artemis Plugin is outdated.
					Please update to Plugin Version %s.
					""";
			this.error(response.formatted(EclipseArtemisConstants.GITHUB_GRADING_TOOL_VERSION));
		}
	}

	private int compareVersions(edu.kit.kastel.sdq.artemis4j.util.Version releaseVersion) {
		int majorCompare = Integer.compare(releaseVersion.major(), this.pluginVersion.getMajor());
		if (majorCompare != 0) {
			return majorCompare;
		}

		int minorCompare = Integer.compare(releaseVersion.minor(), this.pluginVersion.getMinor());
		if (minorCompare != 0) {
			return minorCompare;
		}

		return Integer.compare(releaseVersion.micro(), this.pluginVersion.getMicro());
	}
}
