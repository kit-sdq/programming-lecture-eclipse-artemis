package edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;

public interface IExam extends Serializable {

	int getExamId();

	boolean hasSecondCorrectionRound();

	List<IExerciseGroup> getExerciseGroups() throws ArtemisClientException;

	String getTitle();

	boolean isExamExpired(Date currentDate);

	Date getStartDate();

	boolean isStarted();

	Date getEndDate();
}
