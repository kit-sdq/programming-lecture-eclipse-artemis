package edu.kit.kastel.sdq.eclipse.grading.core;

import java.util.HashSet;
import java.util.Set;

import edu.kit.kastel.sdq.eclipse.grading.api.alerts.IAlertObservable;
import edu.kit.kastel.sdq.eclipse.grading.api.alerts.IAlertObserver;

public class AlertObservable implements IAlertObservable {

	private Set<IAlertObserver> observers;

	public AlertObservable() {
		this.observers = new HashSet<>();
	}

	@Override
	public void addAlertObserver(IAlertObserver alertObserver) {
		this.observers.add(alertObserver);
	}

	void error(String errorMsg, Throwable cause) {
		this.observers.forEach(observer -> observer.error(errorMsg, cause));
		this.printToConsoleIfNoObserversRegistered(errorMsg, cause);
	}

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

	void warn(String warningMsg) {
		this.observers.forEach(observer -> observer.warn(warningMsg));
		this.printToConsoleIfNoObserversRegistered(warningMsg, null);
	}
}
