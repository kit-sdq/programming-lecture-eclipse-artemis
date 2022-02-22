package edu.kit.kastel.sdq.eclipse.grading.api.controller;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractController implements IController {
	private List<IAlertObserver> observers = new ArrayList<>();
	private IConfirmObserver confirmObserver;

	@Override
	public void addAlertObserver(IAlertObserver observer) {
		this.observers.add(observer);
	}

	@Override
	public void addConfirmObserver(IConfirmObserver observer) {
		this.confirmObserver = observer;
	}

	/**
	 * Alert all observers
	 *
	 * @param errorMsg
	 * @param cause
	 */
	protected void error(String errorMsg, Throwable cause) {
		final Throwable nonNullCause = cause == null ? new Exception() : cause;
		this.observers.forEach(observer -> observer.error(errorMsg, nonNullCause));
		this.printToConsoleIfNoObserversRegistered(errorMsg, nonNullCause);
	}

	/**
	 * Alert all observers
	 *
	 * @param infoMsg
	 */
	protected void info(String infoMsg) {
		this.observers.forEach(observer -> observer.info(infoMsg));
		this.printToConsoleIfNoObserversRegistered(infoMsg, null);
	}

	/**
	 * Alert all observers
	 *
	 * @param warningMsg
	 */
	protected void warn(String warningMsg) {
		this.observers.forEach(observer -> observer.warn(warningMsg));
		this.printToConsoleIfNoObserversRegistered(warningMsg, null);
	}

	public boolean confirm(String msg) {
		if (confirmObserver != null) {
			return this.confirmObserver.confirm(msg);
		}
		return false;
	}

	private void printToConsoleIfNoObserversRegistered(String msg, Throwable cause) {
		if (this.observers.isEmpty()) {
			System.err.println(msg);
			if (cause != null) {
				cause.printStackTrace(System.err);
			}
		}
	}

}
