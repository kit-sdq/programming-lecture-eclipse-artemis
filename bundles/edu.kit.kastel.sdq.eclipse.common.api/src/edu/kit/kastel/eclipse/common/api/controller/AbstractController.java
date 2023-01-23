/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.api.controller;

import java.util.Objects;

public abstract class AbstractController implements IController {
	private final IViewInteraction viewInteractionHandler;

	protected AbstractController(IViewInteraction viewInteractionHandler) {
		this.viewInteractionHandler = Objects.requireNonNull(viewInteractionHandler);
	}

	/**
	 * Alert all observers
	 *
	 * @param errorMsg
	 * @param cause
	 */
	protected void error(String errorMsg, Throwable cause) {
		this.viewInteractionHandler.error(errorMsg, cause);
	}

	/**
	 * Alert all observers
	 *
	 * @param infoMsg
	 */
	protected void info(String infoMsg) {
		this.viewInteractionHandler.info(infoMsg);
	}

	/**
	 * Alert all observers
	 *
	 * @param warningMsg
	 */
	protected void warn(String warningMsg) {
		this.viewInteractionHandler.warn(warningMsg);
	}

	public boolean confirm(String msg) {
		return this.viewInteractionHandler.confirm(msg);
	}

	@Override
	public final IViewInteraction getViewInteractionHandler() {
		return this.viewInteractionHandler;
	}

}
