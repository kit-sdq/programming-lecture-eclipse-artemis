/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.grading.api.controller;

/**
 * Observer to handle confirm dialogs.
 */
public interface IConfirmObserver {

	/**
	 * Opens confirm dialog. Returns true if confirmed, false otherwise.
	 * 
	 * @return true if confirmed by user.
	 */
	boolean confirm(String msg);
}
