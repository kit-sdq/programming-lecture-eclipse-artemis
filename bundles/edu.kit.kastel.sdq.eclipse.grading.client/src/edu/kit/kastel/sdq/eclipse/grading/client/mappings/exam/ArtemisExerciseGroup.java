package edu.kit.kastel.sdq.eclipse.grading.client.mappings.exam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExerciseGroup;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.ArtemisExercise;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.IMappingLoader;

public class ArtemisExerciseGroup implements IExerciseGroup, Serializable {
    private static final long serialVersionUID = 1797252671567588724L;

    @JsonProperty(value = "id")
    private int exerciseGroupId;
    @JsonProperty
    private String title;
    @JsonProperty
    private boolean isMandatory;
    @JsonProperty
    private Collection<ArtemisExercise> exercises;

    /**
     * For Auto-Deserialization Need to call this::init thereafter!
     */
    public ArtemisExerciseGroup() {
    }

    @Override
    public int getExerciseGroupId() {
        return this.exerciseGroupId;
    }

    @Override
    public Collection<IExercise> getExercises() {
        return new ArrayList<>(exercises);
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    public void init(IMappingLoader client) {
        if (exercises == null) {
            exercises = List.of();
            return;
        }
        this.exercises = this.exercises.stream()
            .filter(exercise -> exercise.getShortName() != null) // happens sometimes...
            .collect(Collectors.toList());
        for (ArtemisExercise artemisExercise : this.exercises) {
            artemisExercise.init(client);
        }
    }

    @Override
    public boolean isMandatory() {
        return this.isMandatory;
    }

    @Override
    public String toString() {
        return "ArtemisExerciseGroup [exerciseGroupId=" + this.exerciseGroupId + ", exercises=" + this.exercises
                + ", title=" + this.title + ", isMandatory=" + this.isMandatory + "]";
    }
}
