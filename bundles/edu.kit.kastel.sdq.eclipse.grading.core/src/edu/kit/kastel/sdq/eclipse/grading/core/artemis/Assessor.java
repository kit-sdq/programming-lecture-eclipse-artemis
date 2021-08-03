package edu.kit.kastel.sdq.eclipse.grading.core.artemis;

/**
 * 
 * This is only used to construct a JSON Object to be sent as the assessment by the {@link AnnotationMapper}
 */
public class Assessor {
	private int id;
	private String login;
	private String firstName;
	private String lastName;
	private String email;
	private boolean activated;
	private String langKey;
	private String lastNotificationRead;
	private String name;
	private String participantIdentifier;	
	
	public Assessor(int id, String login, String firstName, String lastName, String email, boolean activated,
			String langKey, String lastNotificationRead, String name, String participantIdentifier) {
		this.id = id;
		this.login = login;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.activated = activated;
		this.langKey = langKey;
		this.lastNotificationRead = lastNotificationRead;
		this.name = name;
		this.participantIdentifier = participantIdentifier;
	}
	
	
}
