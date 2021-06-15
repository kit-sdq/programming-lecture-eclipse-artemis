package edu.kit.kastel.sdq.eclipse.grading.core.annotation;

import java.util.Collection;
import java.util.Optional;

import edu.kit.kastel.sdq.eclipse.grading.api.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;

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
	public void addAnnotation(IMistakeType mistakeType, int startLine, int endLine, String fullyClassifiedClassName,
			Optional<String> customMessage, Optional<Double> customPenalty);
	
	/**
	 * 
	 * @return all annotations already made for the current assessment.
	 */
	public Collection<IAnnotation> getAnnotations();
	
	/**
	 * Remove an existent annotation
	 * @param annotationId	unique annotation identifier
	 */
	public void removeAnnotation(int annotationId);

	/**
	 * Get an existent annotation by id
	 * @param annotationId	unique annotation identifier
	 * 
	 * @return the annotation
	 */
	public IAnnotation getAnnotation(int annotationId);

	/**
	 * Modify an annotation in the database.
	 * @param annatationId
	 * @param customMessage
	 * @param customPenalty
	 */
	void modifyAnnotation(int annatationId, Optional<String> customMessage, Optional<Double> customPenalty);	
	
	/**
	 * Write current state to disk. This is to be called upon each of the other methods calls!
	 * (Explicit serialization needs to be done by the caller if they change Annotations) TODO maybe not the best style..
	 */
	public void serialize();
}
