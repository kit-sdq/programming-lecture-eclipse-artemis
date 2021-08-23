package edu.kit.kastel.sdq.eclipse.grading.core;

import java.util.HashSet;
import java.util.Set;

import edu.kit.kastel.sdq.eclipse.grading.api.alerts.IAlertObservable;
import edu.kit.kastel.sdq.eclipse.grading.api.alerts.IAlertObserver;

public class AlertObservable implements IAlertObservable {

	static class PlaceHolderException extends Exception {
		public PlaceHolderException() {
			super();
		}
	}

	private Set<IAlertObserver> observers;

	public AlertObservable() {
		this.observers = new HashSet<>();
	}

	@Override
	public void addAlertObserver(IAlertObserver alertObserver) {
		this.observers.add(alertObserver);
	}

	/**
	 * Alert all observers
	 * @param errorMsg
	 * @param cause
	 */
	void error(String errorMsg, Throwable cause) {
		Throwable[] nonNullCause = {cause};
		if (cause == null) {
			//retrieve the stacktrace by abusing Throwable functionality
			try { throw new PlaceHolderException(); } catch (PlaceHolderException e) {nonNullCause[0] = e;}
		}
		this.observers.forEach(observer -> observer.error(errorMsg, nonNullCause[0]));
		this.printToConsoleIfNoObserversRegistered(errorMsg, nonNullCause[0]);
	}

	/**
	 * Alert all observers
	 * @param infoMsg
	 */
	void info(String infoMsg) {
		this.observers.forEach(observer -> observer.info(infoMsg));
		this.printToConsoleIfNoObserversRegistered(infoMsg, null);
	}

	private void printToConsoleIfNoObserversRegistered(String msg, Throwable cause) {
		if (this.observers.isEmpty()) {
			System.err.println(msg);
			if (cause != null) {
				cause.printStackTrace(System.err);
			}
		}
	}

	@Override
	public void removeAlertObserver(IAlertObserver alertObserver) {
		this.observers.remove(alertObserver);
	}

	/**
	 * Alert all observers
	 * @param warningMsg
	 */
	void warn(String warningMsg) {
		this.observers.forEach(observer -> observer.warn(warningMsg));
		this.printToConsoleIfNoObserversRegistered(warningMsg, null);
	}
}
