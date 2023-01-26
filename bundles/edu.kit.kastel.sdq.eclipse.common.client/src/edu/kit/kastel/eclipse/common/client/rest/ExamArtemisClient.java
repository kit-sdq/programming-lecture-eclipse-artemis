/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.client.rest;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ICourse;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExam;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IStudentExam;
import edu.kit.kastel.eclipse.common.api.client.IExamArtemisClient;
import edu.kit.kastel.eclipse.common.client.mappings.exam.ArtemisStudentExam;

public class ExamArtemisClient extends AbstractArtemisClient implements IExamArtemisClient {
	private WebTarget endpoint;
	private String token;

	public ExamArtemisClient(final String hostName, String token) {
		super(hostName);

		this.endpoint = this.getEndpoint(this.getApiRootURL());
		this.token = token;
	}

	@Override
	public IStudentExam findExamForSummary(ICourse course, IExam exam) throws ArtemisClientException {
		// "/courses/{courseId}/exams/{examId}/start"
		final Response exercisesRsp = this.endpoint.path(COURSES_PATHPART).path(String.valueOf(course.getCourseId())).path(EXAMS_PATHPART)
				.path(String.valueOf(exam.getExamId())).path("start").request().cookie(getAuthCookie(this.token)).buildGet().invoke();
		this.throwIfStatusUnsuccessful(exercisesRsp);
		// get the part of the json that we want to deserialize
		return this.read(exercisesRsp.readEntity(String.class), ArtemisStudentExam.class);
	}

	@Override
	public IStudentExam startExam(ICourse course, IExam exam) throws ArtemisClientException {
		final Response exercisesRsp = this.endpoint.path(COURSES_PATHPART).path(String.valueOf(course.getCourseId())).path(EXAMS_PATHPART)
				.path(String.valueOf(exam.getExamId())).path(STUDENT_EXAM_PATH).path("conduction").request().cookie(getAuthCookie(this.token)).buildGet()
				.invoke();
		this.throwIfStatusUnsuccessful(exercisesRsp);
		return this.read(exercisesRsp.readEntity(String.class), ArtemisStudentExam.class);
	}

}
