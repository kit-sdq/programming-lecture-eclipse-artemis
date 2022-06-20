/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.api.controller;

public interface IController {
	void setViewInteractionHandler(IViewInteraction observer);

	IViewInteraction getViewInteractionHandler();
}
