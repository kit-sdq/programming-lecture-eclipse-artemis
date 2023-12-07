/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.api.controller;

/**
 * Instances can subscribe to an instance to be alerted whenever something
 * occurs in the backend that is out of the ordinary (since no throws
 * declarations are used in interface methods).
 *
 */
public interface IViewInteraction {
	void error(String errorMsg, Throwable cause);

	void error(String errorMsg);

	void info(String infoMsg);

	void warn(String warningMsg);
}
