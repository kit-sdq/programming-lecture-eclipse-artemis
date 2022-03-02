package edu.kit.kastel.sdq.eclipse.grading.api.controller;

/**
 * Observer to handle confirm dialogs. 
 */
public interface IConfirmObserver {
	boolean confirm(String msg);
}
