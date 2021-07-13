package edu.kit.kastel.sdq.eclipse.grading.client.mappings.exam;

import java.util.Collection;

import edu.kit.kastel.sdq.eclipse.grading.api.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.IExerciseGroup;

public class ArtemisExerciseGroup implements IExerciseGroup {

	private int exerciseGroupId;
	private Collection<IExercise> exercises;
	private String title;
	private boolean isMandatory;

	public ArtemisExerciseGroup(int exerciseGroupId, Collection<IExercise> exercises, String title,
			boolean isMandatory) {
		super();
		this.exerciseGroupId = exerciseGroupId;
		this.exercises = exercises;
		this.title = title;
		this.isMandatory = isMandatory;
	}

	@Override
	public int getExerciseGroupId() {
		return this.exerciseGroupId;
	}

	@Override
	public Collection<IExercise> getExercises() {
		return this.exercises;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public boolean isMandatory() {
		return this.isMandatory;
	}

	@Override
	public String toString() {
		return "ArtemisExerciseGroup [exerciseGroupId=" + this.exerciseGroupId + ", exercises=" + this.exercises + ", title="
				+ this.title + ", isMandatory=" + this.isMandatory + "]";
	}
}
