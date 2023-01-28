/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.client.rest;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ICourse;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExam;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IStudentExam;
import edu.kit.kastel.eclipse.common.api.client.IExamArtemisClient;
import edu.kit.kastel.eclipse.common.client.mappings.exam.ArtemisStudentExam;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ExamArtemisClient extends AbstractArtemisClient implements IExamArtemisClient {
	private final OkHttpClient client;

	public ExamArtemisClient(final String hostName, String token) {
		super(hostName);
		this.client = this.createClient(token);
	}

	@Override
	public IStudentExam findExamForSummary(ICourse course, IExam exam) throws ArtemisClientException {
		Request request = new Request.Builder() //
				.url(this.path(COURSES_PATHPART, course.getCourseId(), EXAMS_PATHPART, exam.getExamId(), "start")).get().build();
		return this.call(this.client, request, ArtemisStudentExam.class);
	}

	@Override
	public IStudentExam startExam(ICourse course, IExam exam) throws ArtemisClientException {
		Request request = new Request.Builder() //
				.url(this.path(COURSES_PATHPART, course.getCourseId(), EXAMS_PATHPART, exam.getExamId(), STUDENT_EXAM_PATH, "conduction")).get().build();
		return this.call(this.client, request, ArtemisStudentExam.class);
	}

}
