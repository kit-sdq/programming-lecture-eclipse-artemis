package edu.kit.kastel.sdq.eclipse.grading.client.rest;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.Constants;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.AssessmentResult;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Assessor;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExerciseGroup;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IStudentExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ParticipationDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ResultsDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.client.AbstractArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.client.git.GitException;
import edu.kit.kastel.sdq.eclipse.grading.client.git.GitHandler;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.ArtemisDashboardCourse;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.ArtemisExercise;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.ArtemisSubmission;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.IMappingLoader;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.exam.ArtemisExam;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.exam.ArtemisExerciseGroup;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.exam.ArtemisStudentExam;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.lock.LockResult;

public class ArtemisClient extends AbstractArtemisClient implements IMappingLoader {

	private static final ILog log = Platform.getLog(ArtemisClient.class);
	
	private WebTarget apiEndpoint;

	public ArtemisClient(final String username, final String password, final String hostName) {
		super(username, password, hostName);

		this.apiEndpoint = ClientBuilder.newBuilder().build().target(this.getApiRoot());
		this.token = null;
	}
	
	@Override
	public void downloadExercise(IExercise exercise, File target, IProjectFileNamingStrategy namer, String repoUrl) throws ArtemisClientException {
		final File projectDirectory = namer.getProjectFileInWorkspace(target, exercise, null);
		try {
			if (projectDirectory.exists()) {
				throw new ArtemisClientException("Could not clone project " + projectDirectory.getName() + ", " + "directory already exists!");
			}

			// Download test repository
			GitHandler.cloneRepo(projectDirectory, repoUrl, Constants.MASTER_BRANCH_NAME);
		} catch (GitException e) {
			throw new ArtemisClientException("Unable to download exercise: " + e.getMessage(), e);
		}
	}

	@Override
	public void downloadExerciseAndSubmission(IExercise exercise, ISubmission submission, File target, IProjectFileNamingStrategy namer)
			throws ArtemisClientException {

		final File projectDirectory = namer.getProjectFileInWorkspace(target, exercise, submission);
		try {
			if (projectDirectory.exists()) {
				throw new ArtemisClientException("Could not clone project " + projectDirectory.getName() + ", " + "directory already exists!");
			}

			// Download test repository
			GitHandler.cloneRepo(projectDirectory, exercise.getTestRepositoryUrl(), Constants.MASTER_BRANCH_NAME);
			// download submission inside the exercise project directory
			GitHandler.cloneRepo(namer.getAssignmentFileInProjectDirectory(projectDirectory), //
					submission.getRepositoryUrl(), Constants.MASTER_BRANCH_NAME);
		} catch (GitException e) {
			throw new ArtemisClientException("Unable to download exercise and submission: " + e.getMessage(), e);
		}

	}

	@Override
	public Assessor getAssessor() throws ArtemisClientException {
		this.checkAuthentication();

		final Response rsp = this.apiEndpoint.path(USERS_PATHPART).path(this.getArtemisUsername()).request().header(AUTHORIZATION_NAME, this.token).buildGet()
				.invoke();
		this.throwIfStatusUnsuccessful(rsp);
		return this.read(rsp.readEntity(String.class), Assessor.class);
	}

	@Override
	public List<ICourse> getCourses() throws ArtemisClientException {
		this.checkAuthentication();
		final Response rsp = this.apiEndpoint.path(COURSES_PATHPART).path("for-dashboard").request().header(AUTHORIZATION_NAME, this.token).buildGet().invoke();
		this.throwIfStatusUnsuccessful(rsp);
		String rspString = rsp.readEntity(String.class);

		ArtemisDashboardCourse[] coursesArray = this.read(rspString, ArtemisDashboardCourse[].class);
		for (var courses : coursesArray) {
			courses.init(this);
		}
		return Arrays.asList(coursesArray);
	}

	@Override
	public List<IExam> getExamsForCourse(ICourse course) throws ArtemisClientException {
		final Response examsRsp = this.apiEndpoint.path(COURSES_PATHPART).path(String.valueOf(course.getCourseId())).path(EXAMS_PATHPART).request()
				.header(AUTHORIZATION_NAME, this.token).buildGet().invoke();
		this.throwIfStatusUnsuccessful(examsRsp);

		ArtemisExam[] examsArray = this.read(examsRsp.readEntity(String.class), ArtemisExam[].class);
		for (ArtemisExam exam : examsArray) {
			exam.init(this, course);
		}
		return Arrays.asList(examsArray);
	}
	
	@Override
	public List<IExercise> getStudentExercisesForCourse(ICourse artemisCourse) throws ArtemisClientException {
		final Response eexerciseRsp = this.apiEndpoint.path(COURSES_PATHPART).path(String.valueOf(artemisCourse.getCourseId())).path(EXERCISES_PATHPART).request()
				.header(AUTHORIZATION_NAME, this.token).buildGet().invoke();
		this.throwIfStatusUnsuccessful(eexerciseRsp);

		ArtemisExercise[] excerciseArray = this.read(eexerciseRsp.readEntity(String.class), ArtemisExercise[].class);
		for (ArtemisExercise exercise : excerciseArray) {
			exercise.init(this, artemisCourse);
		}
		return Arrays.asList(excerciseArray);
	}

	@Override
	public List<IExerciseGroup> getExerciseGroupsForExam(IExam exam, ICourse course) throws ArtemisClientException {
		this.checkAuthentication();
		final Response rsp = this.apiEndpoint.path(COURSES_PATHPART).path(String.valueOf(course.getCourseId())).path(EXAMS_PATHPART)
				.path(String.valueOf(exam.getExamId())).path("exam-for-assessment-dashboard") // web client does it that way..
				.request().header(AUTHORIZATION_NAME, this.token).buildGet().invoke(); // synchronous variant
		this.throwIfStatusUnsuccessful(rsp);

		// need to retrieve the exerciseGroups array root node to deserialize it!
		JsonNode detailledExamJsonNode = this.readTree(rsp.readEntity(String.class));
		JsonNode exerciseGroupsJsonArray = detailledExamJsonNode.get("exerciseGroups");
		if (exerciseGroupsJsonArray == null) {
			// exam has no exercise groups!
			return List.of();
		}
		if (!exerciseGroupsJsonArray.isArray()) {
			throw new ArtemisClientException(JSON_PARSE_ERROR_MESSAGE_CORRUPT_JSON_STRUCTURE);
		}

		ArtemisExerciseGroup[] exerciseGroupsArray = this.read(exerciseGroupsJsonArray.toString(), ArtemisExerciseGroup[].class);
		for (ArtemisExerciseGroup exerciseGroup : exerciseGroupsArray) {
			exerciseGroup.init(this, course, exam);
		}
		return Arrays.asList(exerciseGroupsArray);
	}

	@Override
	public List<IExercise> getNormalExercisesForCourse(ICourse course) throws ArtemisClientException {
		this.checkAuthentication();

		final Response exercisesRsp = this.apiEndpoint.path(COURSES_PATHPART).path(String.valueOf(course.getCourseId())).path("with-exercises").request()
				.header(AUTHORIZATION_NAME, this.token).buildGet().invoke();

		this.throwIfStatusUnsuccessful(exercisesRsp);

		// get the part of the json that we want to deserialize
		final JsonNode exercisesAndParticipationsJsonNode = this.readTree(exercisesRsp.readEntity(String.class));
		JsonNode exercisesJsonArray = exercisesAndParticipationsJsonNode.get(EXERCISES_PATHPART);
		if (exercisesJsonArray == null) {
			// course has no exercises!
			return List.of();
		}
		if (!exercisesJsonArray.isArray()) {
			throw new ArtemisClientException(JSON_PARSE_ERROR_MESSAGE_CORRUPT_JSON_STRUCTURE);
		}

		ArtemisExercise[] exercisesArray = this.read(exercisesJsonArray.toString(), ArtemisExercise[].class);
		for (ArtemisExercise exercise : exercisesArray) {
			exercise.init(this, course, Optional.empty());
		}

		// Here we filter all programming exercises
		return Arrays.stream(exercisesArray).filter(exercise -> "programming".equals(exercise.getType())).collect(Collectors.toList());
	}

	@Override
	public List<ISubmission> getSubmissions(IExercise exercise, int correctionRound) throws ArtemisClientException {
		this.checkAuthentication();
		boolean isInstructor = exercise.getCourse().isInstructor(this.getAssessor());

		final Response rsp = this.apiEndpoint.path(EXERCISES_PATHPART).path(String.valueOf(exercise.getExerciseId())).path(PROGRAMMING_SUBMISSION_PATHPART) //
				.queryParam("assessedByTutor", !isInstructor) //
				.queryParam("correction-round", correctionRound) //
				.request().header(AUTHORIZATION_NAME, this.token).buildGet().invoke();

		this.throwIfStatusUnsuccessful(rsp);

		final String rspEntity = rsp.readEntity(String.class);
		ArtemisSubmission[] submissionsArray = this.read(rspEntity, ArtemisSubmission[].class);

		for (ArtemisSubmission submission : submissionsArray) {
			submission.init(correctionRound);
		}

		return Arrays.asList(submissionsArray);
	}

	@Override
	public ISubmission getSubmissionById(IExercise artemisExercise, int submissionId) throws ArtemisClientException {
		List<ISubmission> submissions = this.getSubmissions(artemisExercise);
		ISubmission target = submissions.stream().filter(s -> s.getSubmissionId() == submissionId).findFirst().orElse(null);
		if (target == null) {
			throw new ArtemisClientException("Submission " + submissionId + " not found!");
		}
		return target;
	}

	
	@Override
	public String login() throws ArtemisClientException {
		String payload = this.payload(this.getAuthenticationEntity());
		final Response authenticationResponse = this.apiEndpoint.path("authenticate").request().buildPost(Entity.json(payload)).invoke();

		this.throwIfStatusUnsuccessful(authenticationResponse);
		final String authRspEntity = authenticationResponse.readEntity(String.class);
		final String rawToken = this.readTree(authRspEntity).get("id_token").asText();
		this.token = "Bearer " + rawToken;
		return rawToken;
	}

	@Override
	public void saveAssessment(ParticipationDTO participation, boolean submit, AssessmentResult assessment) throws ArtemisClientException {
		this.checkAuthentication();
		
		String assessmentPayload = this.payload(assessment);
		log.info(String.format("Saving assessment for submission %s with json: %s", assessment.getId(), assessmentPayload));
		
		final Response rsp = this.apiEndpoint.path("participations").path(String.valueOf(participation.getParticipationID())) //
				.path("manual-results") //
				.queryParam("submit", submit) //
				.request().header(AUTHORIZATION_NAME, this.token).buildPut(Entity.json(assessmentPayload)).invoke();
		this.throwIfStatusUnsuccessful(rsp);
	}

	@Override
	public ILockResult startAssessment(ISubmission submission) throws ArtemisClientException {
		this.checkAuthentication();

		final Response rsp = this.apiEndpoint.path(PROGRAMMING_SUBMISSION_PATHPART).path(String.valueOf(submission.getSubmissionId())).path("lock").request()
				.header(AUTHORIZATION_NAME, this.token).buildGet().invoke(); // synchronous variant
		this.throwIfStatusUnsuccessful(rsp);
		return this.read(rsp.readEntity(String.class), LockResult.class);
	}

	@Override
	public Optional<ILockResult> startNextAssessment(IExercise exercise, int correctionRound) throws ArtemisClientException {
		this.checkAuthentication();

		final Response rsp = this.apiEndpoint.path(EXERCISES_PATHPART).path(String.valueOf(exercise.getExerciseId()))
				.path("programming-submission-without-assessment").queryParam("correction-round", correctionRound).queryParam("lock", true).request()
				.header(AUTHORIZATION_NAME, this.token).buildGet().invoke();

		if (!this.isStatusSuccessful(rsp)) {
			// no assessment left!
			return Optional.empty();
		}

		return Optional.of(this.read(rsp.readEntity(String.class), LockResult.class));
	}
	
	@Override
	public ParticipationDTO startParticipationForExercise(ICourse course, IExercise exercise) throws ArtemisClientException {
		this.checkAuthentication();

		final Response exercisesRsp = this.apiEndpoint.path(EXERCISES_PATHPART).path(String.valueOf(exercise.getExerciseId())).path("participations").request()
				.header(AUTHORIZATION_NAME, this.token).buildPost(null).invoke();

		this.throwIfStatusUnsuccessful(exercisesRsp);
		
		// get the part of the json that we want to deserialize
		final JsonNode exercisesAndParticipationsJsonNode = this.readTree(exercisesRsp.readEntity(String.class));
		return this.read(exercisesAndParticipationsJsonNode.toString(), ParticipationDTO.class);

	}
	
	@Override
	public ParticipationDTO resumeParticipationForExercise(ICourse course, IExercise exercise) throws ArtemisClientException {
		this.checkAuthentication();

		final Response exercisesRsp = this.apiEndpoint.path(EXERCISES_PATHPART).path(String.valueOf(exercise.getExerciseId())).path("resume-programming-participation").request()
				.header(AUTHORIZATION_NAME, this.token).buildPut(null).invoke();

		this.throwIfStatusUnsuccessful(exercisesRsp);
		
		// get the part of the json that we want to deserialize
		final JsonNode exercisesAndParticipationsJsonNode = this.readTree(exercisesRsp.readEntity(String.class));
		return this.read(exercisesAndParticipationsJsonNode.toString(), ParticipationDTO.class);

	}
	
	@Override
	public ParticipationDTO getParticipationForExercise(ICourse course, IExercise exercise)
			throws ArtemisClientException {
		this.checkAuthentication();

		final Response exercisesRsp = this.apiEndpoint.path(EXERCISES_PATHPART).path(String.valueOf(exercise.getExerciseId())).path("participation").request()
				.header(AUTHORIZATION_NAME, this.token).buildGet().invoke();

		this.throwIfStatusUnsuccessful(exercisesRsp);
		
		// get the part of the json that we want to deserialize
		final JsonNode exercisesAndParticipationsJsonNode = this.readTree(exercisesRsp.readEntity(String.class));
		return this.read(exercisesAndParticipationsJsonNode.toString(), ParticipationDTO.class);

	}

	@Override
	public ParticipationDTO getParticipationWithLatestResultForExercise(int participationId)
			throws ArtemisClientException {
		this.checkAuthentication();

		final Response exercisesRsp = this.apiEndpoint.path(PARTICIPATION_PATHPART).path(Integer.toString(participationId)).path("withLatestResult").request()
				.header(AUTHORIZATION_NAME, this.token).buildGet().invoke();

		this.throwIfStatusUnsuccessful(exercisesRsp);
		
		// get the part of the json that we want to deserialize
		final JsonNode exercisesAndParticipationsJsonNode = this.readTree(exercisesRsp.readEntity(String.class));
		return this.read(exercisesAndParticipationsJsonNode.toString(), ParticipationDTO.class);

	}
	
	@Override
	public IStudentExam startExam(IExam exam) throws ArtemisClientException {
		this.checkAuthentication();

		final Response exercisesRsp = this.apiEndpoint.path(EXAMS_PATHPART).path(String.valueOf(exam.getExamId())).path("start").request()
				.header(AUTHORIZATION_NAME, this.token).buildGet().invoke();

		this.throwIfStatusUnsuccessful(exercisesRsp);
		
		// get the part of the json that we want to deserialize
		final JsonNode exercisesAndParticipationsJsonNode = this.readTree(exercisesRsp.readEntity(String.class));
		return this.read(exercisesAndParticipationsJsonNode.toString(), ArtemisStudentExam.class); 
	}
	
	@Override
	public IStudentExam conductExam(ICourse course, IExam exam) throws ArtemisClientException {
		this.checkAuthentication();

		final Response exercisesRsp = this.apiEndpoint.path(COURSES_PATHPART).path(String.valueOf(course.getCourseId())).path(EXAMS_PATHPART).path(String.valueOf(exam.getExamId())).path(STUDENT_EXAM_PATH).path("conduction").request()
				.header(AUTHORIZATION_NAME, this.token).buildGet().invoke();

		this.throwIfStatusUnsuccessful(exercisesRsp);
		
		// get the part of the json that we want to deserialize
		final JsonNode exercisesAndParticipationsJsonNode = this.readTree(exercisesRsp.readEntity(String.class));
		return this.read(exercisesAndParticipationsJsonNode.toString(), ArtemisStudentExam.class); 
	}

	@Override
	public IStudentExam findExamForSummary(ICourse course, IExam exam) throws ArtemisClientException {
		this.checkAuthentication();

		final Response exercisesRsp = this.apiEndpoint.path(COURSES_PATHPART).path(String.valueOf(course.getCourseId())).path(EXAMS_PATHPART).path(String.valueOf(exam.getExamId())).path(STUDENT_EXAM_PATH).path("summary").request()
				.header(AUTHORIZATION_NAME, this.token).buildGet().invoke();

		this.throwIfStatusUnsuccessful(exercisesRsp);
		
		// get the part of the json that we want to deserialize
		final JsonNode exercisesAndParticipationsJsonNode = this.readTree(exercisesRsp.readEntity(String.class));
		return this.read(exercisesAndParticipationsJsonNode.toString(), ArtemisStudentExam.class); 
	}
	
	@Override
	public Feedback[] getFeedbackForResult(ParticipationDTO participation, ResultsDTO result) throws ArtemisClientException {
		this.checkAuthentication();

		final Response exercisesRsp = this.apiEndpoint.path(PARTICIPATION_PATHPART).path(Integer.toString(participation.getParticipationID())).path(RESULT_PATHPART).path(Integer.toString(result.id)).path("details").request()
				.header(AUTHORIZATION_NAME, this.token).buildGet().invoke();

		this.throwIfStatusUnsuccessful(exercisesRsp);
		
		// get the part of the json that we want to deserialize
		final JsonNode exercisesAndParticipationsJsonNode = this.readTree(exercisesRsp.readEntity(String.class));
		return this.read(exercisesAndParticipationsJsonNode.toString(), Feedback[].class);
	}



	@Override
	public Date getTime() throws ArtemisClientException {

		final Response exercisesRsp = this.rootEndpoint.path("time").request().buildGet().invoke();

		this.throwIfStatusUnsuccessful(exercisesRsp);
		
		final JsonNode exercisesAndParticipationsJsonNode = this.readTree(exercisesRsp.readEntity(String.class));
		
		return this.read(exercisesAndParticipationsJsonNode.toString(), Date.class);
	}

}
