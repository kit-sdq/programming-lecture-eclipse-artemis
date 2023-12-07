/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.api.controller;

import java.util.Objects;

public abstract class AbstractController implements IController {
	private final IViewInteraction viewInteractionHandler;

	protected AbstractController(IViewInteraction viewInteractionHandler) {
		this.viewInteractionHandler = Objects.requireNonNull(viewInteractionHandler);
	}

	protected void error(String errorMsg, Throwable cause) {
		this.viewInteractionHandler.error(errorMsg, cause);
	}

	protected void error(String errorMsg) {
		this.viewInteractionHandler.error(errorMsg);
	}

	protected void info(String infoMsg) {
		this.viewInteractionHandler.info(infoMsg);
	}

	protected void warn(String warningMsg) {
		this.viewInteractionHandler.warn(warningMsg);
	}

	@Override
	public final IViewInteraction getViewInteractionHandler() {
		return this.viewInteractionHandler;
	}

}
