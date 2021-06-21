package edu.kit.kastel.sdq.eclipse.grading.api;

/**
 * Represents one type of mistakes from a rating group.
 *
 */
public interface IMistakeType {

	IRatingGroup getRatingGroup();

	String getButtonName();
	
	String getRatingGroupName();
	
	String getMessage();
}
