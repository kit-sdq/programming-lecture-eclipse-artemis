package edu.kit.kastel.sdq.eclipse.grading.core;

/**
 * Thrown by {@link BackendStateMachine#transition(BackendStateMachine.State)}.
 *
 */
public class NoTransitionException extends Exception {
	private static final long serialVersionUID = 4825035520929282391L;

	public NoTransitionException(String message) {
		super(message);
	}
}
