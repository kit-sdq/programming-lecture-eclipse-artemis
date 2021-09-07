package edu.kit.kastel.sdq.eclipse.grading.client.mappings;

import java.io.Serializable;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;

public class ArtemisCourse implements ICourse, Serializable {
    private static final long serialVersionUID = -2658127210041804941L;

    @JsonProperty(value = "id")
    private int courseId;
    @JsonProperty
    private String title;
    @JsonProperty
    private String shortName;
    private transient Collection<IExercise> exercises;
    private transient Collection<IExam> exams;
    private transient IMappingLoader client;

    /**
     * For Auto-Deserialization Need to call this::init thereafter!
     */
    public ArtemisCourse() {
    }

    public ArtemisCourse(int courseId, String title, String shortName, Collection<IExercise> exercises,
            Collection<IExam> exams) {
        this.courseId = courseId;
        this.title = title;
        this.shortName = shortName;
        this.exercises = exercises;
        this.exams = exams;
    }

    @Override
    public int getCourseId() {
        return this.courseId;
    }

    @Override
    public Collection<IExam> getExams() throws ArtemisClientException {
        if (exams == null) {
            this.exams = client.getExamsForCourse(this);
        }
        return this.exams;
    }

    @Override
    public Collection<IExercise> getExercises() throws ArtemisClientException {
        if (exercises == null) {
            this.exercises = client.getExercisesForCourse(this);
        }
        return this.exercises;
    }

    @Override
    public String getShortName() {
        return this.shortName;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    public void init(IMappingLoader client) {
        this.client = client;

    }

    @Override
    public String toString() {
        return "ArtemisCourse [courseId=" + this.courseId + ", title=" + this.title + ", shortName=" + this.shortName
                + ", exercises=" + this.exercises + ", exams=" + this.exams + "]";
    }
}
