package edu.kit.kastel.sdq.eclipse.grading.api;

public interface IRatingGroup {
	//TODO getName or sth

	String getDisplayName();

	Double getPenaltyLimit();

	String getShortName();

	boolean hasPenaltyLimit();
}
