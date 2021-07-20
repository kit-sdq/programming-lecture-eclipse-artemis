package edu.kit.kastel.sdq.eclipse.grading.api.alerts;

public interface IAlertObservable {

	void addAlertObserver(IAlertObserver alertObserver);

	void removeAlertObserver(IAlertObserver alertObserver);

}
