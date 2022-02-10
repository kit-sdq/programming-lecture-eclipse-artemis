package edu.kit.kastel.sdq.eclipse.grading.api.client;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IStudentExam;

public interface IExamArtemisClient {
	public IStudentExam findExamForSummary(ICourse course, IExam exam) throws ArtemisClientException;
	public IStudentExam conductExam(ICourse course, IExam exam) throws ArtemisClientException;
	public IStudentExam startExam(IExam exam) throws ArtemisClientException;
}
