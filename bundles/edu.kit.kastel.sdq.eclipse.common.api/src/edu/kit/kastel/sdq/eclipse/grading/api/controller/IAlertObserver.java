package edu.kit.kastel.sdq.eclipse.grading.api.controller;

/**
 * Instances can subscribe to an instance of
 * {@link edu.kit.kastel.sdq.eclipse.grading.api.alerts.IAlertObservable} to be
 * alerted whenever something occurs in the backend that is out of the ordinary
 * (since no throws declarations are used in interface methods).
 *
 */
public interface IAlertObserver {

	/**
	 * @param cause, can be <b>null</b>
	 */
	void error(String errorMsg, Throwable cause);

	void info(String infoMsg);

	void warn(String warningMsg);
}
