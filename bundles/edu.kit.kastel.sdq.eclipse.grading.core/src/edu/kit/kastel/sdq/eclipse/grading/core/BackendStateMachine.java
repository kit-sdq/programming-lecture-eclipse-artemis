package edu.kit.kastel.sdq.eclipse.grading.core;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.kit.kastel.sdq.eclipse.grading.api.backendstate.State;
import edu.kit.kastel.sdq.eclipse.grading.api.backendstate.Transition;

public class BackendStateMachine {

	private State previousState;
	private State currentState;
	private Map<State, Set<Transition>> transitions;

	public BackendStateMachine() {
		this.previousState = State.ERROR_STATE;
		this.currentState = State.NO_STATE;

		this.transitions = new EnumMap<>(State.class);
		this.setUpTransitions();
	}

	public void applyTransition(Transition transition) throws NoTransitionException {
		if (!this.transitions.get(this.currentState).contains(transition)) {
			final String message = "State transition " + transition.toString() + " (from " + this.currentState + " to " + transition.getTo() + ") not defined.";
			this.changeState(State.ERROR_STATE);
			throw new NoTransitionException(message);
		}
		this.changeState(transition.getTo());
	}

	private void changeState(final State nextState) {
		this.previousState = this.currentState;
		this.currentState = nextState;
	}

	public Set<Transition> getCurrentlyPossibleTransitions() {
		return this.transitions.get(this.currentState);
	}

	/**
	 * In case an operation fails, but not erroneously, the previous state must be restored.
	 */
	public void revertLatestTransition() {
		this.currentState = this.previousState;
		this.previousState = State.ERROR_STATE;
	}

	/**
	 * Define the state transitions. See docs for a diagram!
	 */
	private void setUpTransitions() {
		this.transitions.put(State.ERROR_STATE, new HashSet<>(List.of()));
		this.transitions.put(State.NO_STATE, new HashSet<>(List.of(Transition.SET_COURSE_ID_AND_GET_EXERCISE_SHORT_NAMES)));
		this.transitions.put(State.COURSE_SET, new HashSet<>(List.of(
				Transition.SET_EXERCISE_ID,
				Transition.SET_COURSE_ID_AND_GET_EXERCISE_SHORT_NAMES)));
		this.transitions.put(State.COURSE_EXERCISE_SET, new HashSet<>(List.of(
				Transition.SET_COURSE_ID_AND_GET_EXERCISE_SHORT_NAMES,
				Transition.SET_EXERCISE_ID,
				Transition.START_ASSESSMENT,
				Transition.START_CORRECTION_ROUND_1,
				Transition.START_CORRECTION_ROUND_2,
				Transition.SET_ASSESSED_SUBMISSION_BY_PROJECT_NAME)));
		this.transitions.put(State.COURSE_EXERCISE_SUBMISSION_SET, new HashSet<>(List.of(
				Transition.LOAD_AGAIN,
				Transition.SET_ASSESSED_SUBMISSION_BY_PROJECT_NAME,
				Transition.SET_EXERCISE_ID,
				Transition.SET_COURSE_ID_AND_GET_EXERCISE_SHORT_NAMES)));
		this.transitions.put(State.COURSE_EXERCISE_SUBMISSION_SET_SUBMISSION_STARTED, new HashSet<>(List.of(
				Transition.RELOAD_ASSESSMENT,
				Transition.SAVE_ASSESSMENT,
				Transition.SUBMIT_ASSESSMENT,
				Transition.ON_ZERO_POINTS_FOR_ASSESSMENT)));
		this.transitions.put(State.COURSE_EXERCISE_SUBMISSION_SET_SUBMISSION_SAVED, new HashSet<>(List.of(
				Transition.SAVE_ASSESSMENT,
				Transition.SUBMIT_ASSESSMENT,
				Transition.RELOAD_ASSESSMENT,
				Transition.ON_ZERO_POINTS_FOR_ASSESSMENT)));
	}
}
