/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.api.artemis.mapping;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;

public interface IExam extends Serializable {

	int getExamId();

	boolean hasSecondCorrectionRound();

	List<IExerciseGroup> getExerciseGroups() throws ArtemisClientException;

	String getTitle();

	boolean isExamExpired(Date currentDate);

	Date getStartDate();

	Date getEndDate();
}
