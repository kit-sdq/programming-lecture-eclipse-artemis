/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.api.controller;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;

public abstract class AbstractController implements IController {
	private final ILog log = Platform.getLog(this.getClass());

	private IViewInteraction viewInteractionHandler = null;

	@Override
	public final void setViewInteractionHandler(IViewInteraction viewInteractionHandler) {
		this.viewInteractionHandler = viewInteractionHandler;
	}

	/**
	 * Alert all observers
	 *
	 * @param errorMsg
	 * @param cause
	 */
	protected void error(String errorMsg, Throwable cause) {
		final Throwable nonNullCause = cause == null ? new Exception() : cause;
		if (this.viewInteractionHandler != null) {
			this.viewInteractionHandler.error(errorMsg, nonNullCause);
		}
		this.printToConsoleIfNoObserversRegistered(errorMsg, nonNullCause);
	}

	/**
	 * Alert all observers
	 *
	 * @param infoMsg
	 */
	protected void info(String infoMsg) {
		if (this.viewInteractionHandler != null) {
			this.viewInteractionHandler.info(infoMsg);
		}
		this.printToConsoleIfNoObserversRegistered(infoMsg, null);
	}

	/**
	 * Alert all observers
	 *
	 * @param warningMsg
	 */
	protected void warn(String warningMsg) {
		if (this.viewInteractionHandler != null) {
			this.viewInteractionHandler.warn(warningMsg);
		}
		this.printToConsoleIfNoObserversRegistered(warningMsg, null);
	}

	public boolean confirm(String msg) {
		if (this.viewInteractionHandler != null) {
			return this.viewInteractionHandler.confirm(msg);
		}
		return false;
	}

	private void printToConsoleIfNoObserversRegistered(String msg, Throwable cause) {
		if (this.viewInteractionHandler == null) {
			this.log.error(msg, cause);
		}
	}

	public final IViewInteraction getViewInteractionHandler() {
		return this.viewInteractionHandler;
	}

}
