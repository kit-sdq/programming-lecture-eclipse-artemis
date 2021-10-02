package edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping;

import java.util.List;

/**
 * Artemis Object Mapping: Maps some values of the Artemis Assessor object
 * retrieved via
 * {@link edu.kit.kastel.sdq.eclipse.grading.api.client.AbstractArtemisClient}.
 *
 */
public interface IAssessor {
	int getId();

	boolean getActivated();

	String getLogin();

	String getName();

	String getFirstName();

	String getLastName();

	String getEmail();

	String getLangKey();

	String getLastNotificationRead();

	String getParticipantIdentifier();

	List<String> getGroups();
}
