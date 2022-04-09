/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.grading.api.controller;

public interface IController {
	void addAlertObserver(IAlertObserver observer);

	void addConfirmObserver(IConfirmObserver observer);
}
