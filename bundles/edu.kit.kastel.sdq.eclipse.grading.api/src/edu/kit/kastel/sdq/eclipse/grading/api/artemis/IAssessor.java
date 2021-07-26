package edu.kit.kastel.sdq.eclipse.grading.api.artemis;

/**
 * Artemis Object Mapping: Maps some values of the Artemis Assessor object
 * retrieved via {@link edu.kit.kastel.sdq.eclipse.grading.api.AbstractArtemisClient}.
 *
 */
public interface IAssessor {

	boolean getActivated();
	String getEmail();
	String getFirstName();
	int getId();
	String getLangKey();
	String getLastName();
	String getLastNotificationRead();
	String getLogin();
	String getName();
	String getParticipantIdentifier();
}
