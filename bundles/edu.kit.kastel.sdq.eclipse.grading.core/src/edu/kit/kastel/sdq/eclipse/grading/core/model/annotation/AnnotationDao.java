package edu.kit.kastel.sdq.eclipse.grading.core.model.annotation;

import java.util.Collection;

import edu.kit.kastel.sdq.eclipse.grading.api.model.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;

/**
 * Holder of Annotation State.
 * Writes to DB on each method call.
 * TODO read from file? Extra Method for that?
 *
 */
public interface AnnotationDao {

	/**
	 * Add an annotation to the current assessment.
	 *
	 * @param annotation the annotation to be added
	 */
	void addAnnotation(int annotationID, IMistakeType mistakeType, int startLine, int endLine, String fullyClassifiedClassName,
			String customMessage, Double customPenalty, int markerCharStart, int markerCharEnd) throws AnnotationException;

	/**
	 * Get an existent annotation by id
	 * @param annotationId	unique annotation identifier
	 *
	 * @return the annotation
	 */
	IAnnotation getAnnotation(int annotationId);

	/**
	 *
	 * @return all annotations already made for the current assessment.
	 */
	Collection<IAnnotation> getAnnotations();

	/**
	 * Modify an annotation in the database.
	 * @param annatationId
	 * @param customMessage
	 * @param customPenalty
	 */
	void modifyAnnotation(int annatationId, String customMessage, Double customPenalty);

	/**
	 * Remove an existent annotation
	 * @param annotationId	unique annotation identifier
	 */
	void removeAnnotation(int annotationId);

	/**
	 * Write current state to disk. This is to be called upon each of the other methods calls!
	 * (Explicit serialization needs to be done by the caller if they change Annotations) TODO maybe not the best style..
	 */
	void serialize();
}
