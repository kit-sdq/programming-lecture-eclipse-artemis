package edu.kit.kastel.sdq.eclipse.grading.core;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.kit.kastel.sdq.eclipse.grading.api.backendstate.State;
import edu.kit.kastel.sdq.eclipse.grading.api.backendstate.Transition;

public class BackendStateMachine {




	private State currentState;
	private Map<State, Set<Transition>> transitionsNew;

	public BackendStateMachine() {
		this.currentState = State.NO_STATE;

		this.transitionsNew = new EnumMap<>(State.class);
		this.setUpTransitions();
	}

	public void applyTransition(Transition transition) throws NoTransitionException {
		if (!this.transitionsNew.get(this.currentState).contains(transition)) {
			final String message = "State transition " + transition.toString() + " (from " + this.currentState + " to " + transition.getTo() + ") not defined.";
			this.currentState = State.ERROR_STATE;
			throw new NoTransitionException(message);
		}

		this.currentState = transition.getTo();
	}

	public Set<Transition> getCurrentlyPossibleTransitions() {
		return this.transitionsNew.get(this.currentState);
	}

	/**
	 * Define the state transitions. See docs for a diagram!
	 */
	private void setUpTransitions() {
		this.transitionsNew.put(State.ERROR_STATE, new HashSet<>(List.of()));
		this.transitionsNew.put(State.NO_STATE, new HashSet<>(List.of(Transition.SET_COURSE_ID_AND_GET_EXERCISE_SHORT_NAMES)));
		this.transitionsNew.put(State.COURSE_SET, new HashSet<>(List.of(
				Transition.SET_EXERCISE_ID,
				Transition.SET_COURSE_ID_AND_GET_EXERCISE_SHORT_NAMES)));
		this.transitionsNew.put(State.COURSE_EXERCISE_SET, new HashSet<>(List.of(
				Transition.SET_COURSE_ID_AND_GET_EXERCISE_SHORT_NAMES,
				Transition.SET_EXERCISE_ID,
				Transition.START_ASSESSMENT,
				Transition.START_CORRECTION_ROUND_1,
				Transition.START_CORRECTION_ROUND_2,
				Transition.SET_ASSESSED_SUBMISSION_BY_PROJECT_NAME)));
		this.transitionsNew.put(State.COURSE_EXERCISE_SUBMISSION_SET, new HashSet<>(List.of(
				Transition.LOAD_AGAIN,
				Transition.SET_ASSESSED_SUBMISSION_BY_PROJECT_NAME,
				Transition.SET_EXERCISE_ID,
				Transition.SET_COURSE_ID_AND_GET_EXERCISE_SHORT_NAMES)));
		this.transitionsNew.put(State.COURSE_EXERCISE_SUBMISSION_SET_SUBMISSION_STARTED, new HashSet<>(List.of(
				Transition.RELOAD_ASSESSMENT,
				Transition.SAVE_ASSESSMENT,
				Transition.SUBMIT_ASSESSMENT,
				Transition.ON_ZERO_POINTS_FOR_ASSESSMENT)));
		this.transitionsNew.put(State.COURSE_EXERCISE_SUBMISSION_SET_SUBMISSION_SAVED, new HashSet<>(List.of(
				Transition.SAVE_ASSESSMENT,
				Transition.SUBMIT_ASSESSMENT,
				Transition.RELOAD_ASSESSMENT,
				Transition.ON_ZERO_POINTS_FOR_ASSESSMENT)));
	}
}
