/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.client.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ICourse;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExam;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExerciseGroup;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ISubmission;
import edu.kit.kastel.eclipse.common.api.client.ICourseArtemisClient;
import edu.kit.kastel.eclipse.common.api.client.ISubmissionsArtemisClient;
import edu.kit.kastel.eclipse.common.client.mappings.ArtemisCourse;
import edu.kit.kastel.eclipse.common.client.mappings.ArtemisExercise;
import edu.kit.kastel.eclipse.common.client.mappings.ArtemisExerciseWrapper;
import edu.kit.kastel.eclipse.common.client.mappings.IMappingLoader;
import edu.kit.kastel.eclipse.common.client.mappings.exam.ArtemisExam;
import edu.kit.kastel.eclipse.common.client.mappings.exam.ArtemisExerciseGroup;
import edu.kit.kastel.eclipse.common.client.mappings.exam.ArtemisExerciseGroupWrapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MappingLoaderArtemisClient extends AbstractArtemisClient implements ICourseArtemisClient, IMappingLoader {
	private ISubmissionsArtemisClient submissionClient;

	private final OkHttpClient client;

	public MappingLoaderArtemisClient(ISubmissionsArtemisClient submissionClient, String hostname, String token) {
		super(hostname);
		this.client = this.createClient(token);
		this.submissionClient = submissionClient;
	}

	@Override
	public List<ICourse> getCoursesForAssessment() throws ArtemisClientException {
		Request request = new Request.Builder().url(this.path(COURSES_PATHPART)).get().build();
		ArtemisCourse[] coursesArray = this.call(this.client, request, ArtemisCourse[].class);
		for (ArtemisCourse course : coursesArray) {
			course.init(this);
		}
		return Arrays.asList(coursesArray);
	}

	@Override
	public List<IExerciseGroup> getExerciseGroupsForExam(IExam artemisExam, ICourse course) throws ArtemisClientException {
		Request request = new Request.Builder() //
				.url(this.path(COURSES_PATHPART, course.getCourseId(), EXAMS_PATHPART, artemisExam.getExamId(), "exam-for-assessment-dashboard")).get().build();

		// need to retrieve the exerciseGroups array root node to deserialize it!
		List<ArtemisExerciseGroup> exerciseGroups = this.call(this.client, request, ArtemisExerciseGroupWrapper.class).getExerciseGroups();

		for (ArtemisExerciseGroup exerciseGroup : exerciseGroups) {
			exerciseGroup.init(this, course, artemisExam);
		}
		return new ArrayList<>(exerciseGroups);
	}

	@Override
	public List<IExam> getExamsForCourse(ICourse artemisCourse) throws ArtemisClientException {
		Request request = new Request.Builder() //
				.url(this.path(COURSES_PATHPART, artemisCourse.getCourseId(), EXAMS_PATHPART)).get().build();

		ArtemisExam[] examsArray = this.call(this.client, request, ArtemisExam[].class);
		for (ArtemisExam exam : examsArray) {
			exam.init(this, artemisCourse);
		}
		return Arrays.asList(examsArray);
	}

	@Override
	public List<IExercise> getGradingExercisesForCourse(ICourse artemisCourse) throws ArtemisClientException {
		Request request = new Request.Builder() //
				.url(this.path(COURSES_PATHPART, artemisCourse.getCourseId(), "with-exercises")).get().build();

		// get the part of the json that we want to deserialize
		final List<ArtemisExercise> exercises = this.call(this.client, request, ArtemisExerciseWrapper.class).getExercises();

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
