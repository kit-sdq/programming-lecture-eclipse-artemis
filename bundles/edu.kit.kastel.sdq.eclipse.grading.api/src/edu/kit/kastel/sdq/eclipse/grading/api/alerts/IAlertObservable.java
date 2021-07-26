package edu.kit.kastel.sdq.eclipse.grading.api.alerts;

/**
 * A {@link edu.kit.kastel.sdq.eclipse.grading.api.alerts.IAlertObserver} can subscribe to instances of this to be alerted whenever
 * something occurs in the backend that is out of the ordinary (since no throws declarations are used in interface methods).
 *
 */
public interface IAlertObservable {

	void addAlertObserver(IAlertObserver alertObserver);

	void removeAlertObserver(IAlertObserver alertObserver);
}
