/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.api.client;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ICourse;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExam;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IStudentExam;

/**
 * REST-Client to execute calls concerning exams.
 */
public interface IExamArtemisClient {
	/**
	 * If the given exam has already been started, it returns all the exercises of
	 * the exam.
	 */
	IStudentExam findExamForSummary(ICourse course, IExam exam) throws ArtemisClientException;

	/**
	 * Starts the given exam. Returns the exam object and all its exercises.
	 */
	IStudentExam startExam(ICourse course, int studentExamId, IExam exam) throws ArtemisClientException;
}
