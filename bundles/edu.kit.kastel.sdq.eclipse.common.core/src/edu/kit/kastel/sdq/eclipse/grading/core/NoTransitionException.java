/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.grading.core;

/**
 * Thrown by {@link BackendStateMachine#transition(BackendStateMachine.State)}.
 *
 */
public final class NoTransitionException extends Exception {
	private static final long serialVersionUID = 4825035520929282391L;

	public NoTransitionException(String message) {
		super(message);
	}
}
