package edu.kit.kastel.sdq.eclipse.grading.core;

/**
 * Thrown by {@link BackendStateMachine#transition(BackendStateMachine.State)}.
 *
 */
public class NoTransitionException extends Exception {

	public NoTransitionException(String message) {
		super(message);
	}
}
