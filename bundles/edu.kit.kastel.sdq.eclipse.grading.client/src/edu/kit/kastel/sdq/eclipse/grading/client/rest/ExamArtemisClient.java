package edu.kit.kastel.sdq.eclipse.grading.client.rest;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;

import com.fasterxml.jackson.databind.JsonNode;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IStudentExam;
import edu.kit.kastel.sdq.eclipse.grading.api.client.IExamArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.exam.ArtemisStudentExam;

public class ExamArtemisClient extends AbstractArtemisClient implements IExamArtemisClient {
	private static final ILog log = Platform.getLog(ExamArtemisClient.class);

	private WebTarget endpoint;
	private String token;

	public ExamArtemisClient(final String hostName, String token) {
		super(hostName);

		this.endpoint = getEndpoint(this.getApiRootURL());
		this.token = token;
	}

	@Override
	public IStudentExam findExamForSummary(ICourse course, IExam exam) throws ArtemisClientException {
		final Response exercisesRsp = this.endpoint.path(COURSES_PATHPART).path(String.valueOf(course.getCourseId())).path(EXAMS_PATHPART)
				.path(String.valueOf(exam.getExamId())).path(STUDENT_EXAM_PATH).path("summary").request().header(AUTHORIZATION_NAME, this.token).buildGet()
				.invoke();

		this.throwIfStatusUnsuccessful(exercisesRsp);

		// get the part of the json that we want to deserialize
		final JsonNode exercisesAndParticipationsJsonNode = this.readTree(exercisesRsp.readEntity(String.class));
		return this.read(exercisesAndParticipationsJsonNode.toString(), ArtemisStudentExam.class);
	}

	@Override
	public IStudentExam conductExam(ICourse course, IExam exam) throws ArtemisClientException {
		final Response exercisesRsp = this.endpoint.path(COURSES_PATHPART).path(String.valueOf(course.getCourseId())).path(EXAMS_PATHPART)
				.path(String.valueOf(exam.getExamId())).path(STUDENT_EXAM_PATH).path("conduction").request().header(AUTHORIZATION_NAME, this.token).buildGet()
				.invoke();

		this.throwIfStatusUnsuccessful(exercisesRsp);

		// get the part of the json that we want to deserialize
		final JsonNode exercisesAndParticipationsJsonNode = this.readTree(exercisesRsp.readEntity(String.class));
		return this.read(exercisesAndParticipationsJsonNode.toString(), ArtemisStudentExam.class);
	}

	@Override
	public IStudentExam startExam(IExam exam) throws ArtemisClientException {
		final Response exercisesRsp = this.endpoint.path(EXAMS_PATHPART).path(String.valueOf(exam.getExamId())).path("start").request()
				.header(AUTHORIZATION_NAME, this.token).buildGet().invoke();

		this.throwIfStatusUnsuccessful(exercisesRsp);

		// get the part of the json that we want to deserialize
		final JsonNode exercisesAndParticipationsJsonNode = this.readTree(exercisesRsp.readEntity(String.class));
		return this.read(exercisesAndParticipationsJsonNode.toString(), ArtemisStudentExam.class);
	}

}
