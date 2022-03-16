package edu.kit.kastel.sdq.eclipse.grading.api.controller;

public interface IController {
	void addAlertObserver(IAlertObserver observer);

	void addConfirmObserver(IConfirmObserver observer);
}
