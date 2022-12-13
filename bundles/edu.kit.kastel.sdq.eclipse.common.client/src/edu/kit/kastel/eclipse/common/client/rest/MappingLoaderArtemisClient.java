/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.client.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ICourse;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExam;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExerciseGroup;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ISubmission;
import edu.kit.kastel.eclipse.common.api.client.ICourseArtemisClient;
import edu.kit.kastel.eclipse.common.api.client.ISubmissionsArtemisClient;
import edu.kit.kastel.eclipse.common.api.messages.Messages;
import edu.kit.kastel.eclipse.common.client.mappings.ArtemisCourse;
import edu.kit.kastel.eclipse.common.client.mappings.ArtemisDashboardCourse;
import edu.kit.kastel.eclipse.common.client.mappings.ArtemisExercise;
import edu.kit.kastel.eclipse.common.client.mappings.ArtemisExerciseWrapper;
import edu.kit.kastel.eclipse.common.client.mappings.IMappingLoader;
import edu.kit.kastel.eclipse.common.client.mappings.exam.ArtemisExam;
import edu.kit.kastel.eclipse.common.client.mappings.exam.ArtemisExerciseGroup;
import edu.kit.kastel.eclipse.common.client.mappings.exam.ArtemisExerciseGroupWrapper;

public class MappingLoaderArtemisClient extends AbstractArtemisClient implements ICourseArtemisClient, IMappingLoader {
	private WebTarget endpoint;
	private String token;
	private ISubmissionsArtemisClient submissionClient;

	public MappingLoaderArtemisClient(ISubmissionsArtemisClient submissionClient, String hostName, String token) {
		super(hostName);

		this.endpoint = this.getEndpoint(this.getApiRootURL());
		this.token = token;
		this.submissionClient = submissionClient;
	}

	@Override
	public List<ICourse> getCoursesForAssessment() throws ArtemisClientException, ProcessingException {
		Response rsp;
		try {
			rsp = this.endpoint.path(COURSES_PATHPART).request().cookie(getAuthCookie(this.token)).buildGet().invoke();
		} catch (ProcessingException e) {
			throw new ArtemisClientException(Messages.CLIENT_CONNECTION_REFUSED);
		}
		this.throwIfStatusUnsuccessful(rsp);
		String rspString = rsp.readEntity(String.class);

		ArtemisCourse[] coursesArray = this.read(rspString, ArtemisCourse[].class);
		for (ArtemisCourse course : coursesArray) {
			course.init(this);
		}
		return Arrays.asList(coursesArray);
	}

	@Override
	public List<ICourse> getCoursesForStudent() throws ArtemisClientException {
		Response rsp;

		try {
			rsp = this.endpoint.path(COURSES_PATHPART).path("for-dashboard").request().cookie(getAuthCookie(this.token)).buildGet().invoke();
		} catch (ProcessingException e) {
			throw new ArtemisClientException(Messages.CLIENT_CONNECTION_REFUSED);
		}

		this.throwIfStatusUnsuccessful(rsp);
		String rspString = rsp.readEntity(String.class);

		ArtemisDashboardCourse[] coursesArray = this.read(rspString, ArtemisDashboardCourse[].class);
		for (var courses : coursesArray) {
			courses.init(this);
		}
		return Arrays.asList(coursesArray);
	}

	@Override
	public List<IExerciseGroup> getExerciseGroupsForExam(IExam artemisExam, ICourse course) throws ArtemisClientException {
		final Response rsp = this.endpoint.path(COURSES_PATHPART).path(String.valueOf(course.getCourseId())).path(EXAMS_PATHPART)
				.path(String.valueOf(artemisExam.getExamId())).path("exam-for-assessment-dashboard").request().cookie(getAuthCookie(this.token)).buildGet()
				.invoke();
		this.throwIfStatusUnsuccessful(rsp);

		// need to retrieve the exerciseGroups array root node to deserialize it!
		List<ArtemisExerciseGroup> exerciseGroups = this.read(rsp.readEntity(String.class), ArtemisExerciseGroupWrapper.class).getExerciseGroups();

		for (ArtemisExerciseGroup exerciseGroup : exerciseGroups) {
			exerciseGroup.init(this, course, artemisExam);
		}
		return new ArrayList<>(exerciseGroups);
	}

	@Override
	public List<IExam> getExamsForCourse(ICourse artemisCourse) throws ArtemisClientException {
		final Response examsRsp = this.endpoint.path(COURSES_PATHPART).path(String.valueOf(artemisCourse.getCourseId())).path(EXAMS_PATHPART).request()
				.cookie(getAuthCookie(this.token)).buildGet().invoke();
		this.throwIfStatusUnsuccessful(examsRsp);

		ArtemisExam[] examsArray = this.read(examsRsp.readEntity(String.class), ArtemisExam[].class);
		for (ArtemisExam exam : examsArray) {
			exam.init(this, artemisCourse);
		}
		return Arrays.asList(examsArray);
	}

	@Override
	public List<IExercise> getStudentExercisesForCourse(ICourse artemisCourse) throws ArtemisClientException {
		final Response eexerciseRsp = this.endpoint.path(COURSES_PATHPART).path(String.valueOf(artemisCourse.getCourseId())).path(EXERCISES_PATHPART).request()
				.cookie(getAuthCookie(this.token)).buildGet().invoke();
		this.throwIfStatusUnsuccessful(eexerciseRsp);

		ArtemisExercise[] excerciseArray = this.read(eexerciseRsp.readEntity(String.class), ArtemisExercise[].class);
		for (ArtemisExercise exercise : excerciseArray) {
			exercise.init(this, artemisCourse);
		}
		return Arrays.stream(excerciseArray).filter(IExercise::isProgramming).collect(Collectors.toList());
	}

	@Override
	public List<IExercise> getGradingExercisesForCourse(ICourse artemisCourse) throws ArtemisClientException {
		final Response exercisesRsp = this.endpoint.path(COURSES_PATHPART).path(String.valueOf(artemisCourse.getCourseId())).path("with-exercises").request()
				.cookie(getAuthCookie(this.token)).buildGet().invoke();

		this.throwIfStatusUnsuccessful(exercisesRsp);

		// get the part of the json that we want to deserialize
		final List<ArtemisExercise> exercises = this.read(exercisesRsp.readEntity(String.class), ArtemisExerciseWrapper.class).getExercises();

		for (ArtemisExercise exercise : exercises) {
			exercise.init(this, artemisCourse, Optional.empty());
		}

		// Here we filter all programming exercises
		return exercises.stream().filter(IExercise::isProgramming).collect(Collectors.toList());
	}

	@Override
	public ISubmission getSubmissionById(IExercise artemisExercise, int submissionId) throws ArtemisClientException {
		return this.submissionClient.getSubmissionById(artemisExercise, submissionId);
	}

}
