package edu.kit.kastel.sdq.eclipse.grading.core;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BackendStateMachine {

	public enum State {
		ERROR_STATE													(),
		NO_STATE													(),
		COURSE_SET													(),
		COURSE_EXERCISE_SET											(),
		COURSE_EXERCISE_SUBMISSION_SET								(),
		COURSE_EXERCISE_SUBMISSION_SET_SUBMISSION_STARTED			(),
		COURSE_EXERCISE_SUBMISSION_SET_SUBMISSION_SAVED				();
	}

	private State currentState;
	private Map<State, Set<State>> transitions;

	public BackendStateMachine() {
		this.currentState = State.NO_STATE;

		this.transitions = new EnumMap<>(State.class);
		this.setUpTransitions();
	}

	/**
	 * Define the state transitions. See docs for a diagram!
	 */
	private void setUpTransitions() {
		this.transitions.put(State.ERROR_STATE, 										new HashSet<>(List.of()));
		this.transitions.put(State.NO_STATE, 											new HashSet<>(List.of(
																							State.COURSE_SET)));
		this.transitions.put(State.COURSE_SET, 											new HashSet<>(List.of(
																							State.COURSE_EXERCISE_SET)));
		this.transitions.put(State.COURSE_EXERCISE_SET, 								new HashSet<>(List.of(
																							State.COURSE_SET,
																							State.COURSE_EXERCISE_SUBMISSION_SET,
																							State.COURSE_EXERCISE_SUBMISSION_SET_SUBMISSION_STARTED)));
		this.transitions.put(State.COURSE_EXERCISE_SUBMISSION_SET, 						new HashSet<>(List.of(
																							State.COURSE_EXERCISE_SUBMISSION_SET_SUBMISSION_STARTED)));
		this.transitions.put(State.COURSE_EXERCISE_SUBMISSION_SET_SUBMISSION_STARTED, 	new HashSet<>(List.of(
																							State.COURSE_EXERCISE_SUBMISSION_SET_SUBMISSION_STARTED,
																							State.COURSE_EXERCISE_SUBMISSION_SET_SUBMISSION_SAVED,
																							State.COURSE_EXERCISE_SET)));
		this.transitions.put(State.COURSE_EXERCISE_SUBMISSION_SET_SUBMISSION_SAVED, 	new HashSet<>(List.of(
																							State.COURSE_EXERCISE_SUBMISSION_SET_SUBMISSION_SAVED,
																							State.COURSE_EXERCISE_SET)));
	}

	/**
	 * Try transition from current State to given state. If impossible, currentState is set to {@link State#ERROR_STATE}.
	 * @param nextState
	 * @throws NoTransitionException if there is no transition from this state to the next state.
	 */
	public void transition(State nextState) throws NoTransitionException {
		if (!this.transitions.get(this.currentState).contains(nextState)) {
			final String message = "Transition from " + this.currentState + " to " + nextState + " not defined!";
			this.currentState = State.ERROR_STATE;
			throw new NoTransitionException(message);
		}
		this.currentState = nextState;
	}
}
