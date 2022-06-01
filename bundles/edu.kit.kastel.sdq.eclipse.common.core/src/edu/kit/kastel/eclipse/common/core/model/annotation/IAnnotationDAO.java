/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.core.model.annotation;

import java.util.Set;

import edu.kit.kastel.eclipse.common.api.model.IAnnotation;
import edu.kit.kastel.eclipse.common.api.model.IMistakeType;

/**
 * Holder of Annotation State.
 */
public interface IAnnotationDAO {

	/**
	 * Add an annotation to the current assessment.
	 *
	 * @param annotation the annotation to be added
	 */
	void addAnnotation(String annotationUUID, IMistakeType mistakeType, int startLine, int endLine, String fullyClassifiedClassName, String customMessage,
			Double customPenalty) throws AnnotationException;

	/**
	 * Get an existent annotation by id
	 *
	 * @param annotationId unique annotation identifier
	 *
	 * @return the annotation
	 */
	IAnnotation getAnnotation(String annotationId);

	/**
	 *
	 * @return all annotations already made for the current assessment.
	 */
	Set<IAnnotation> getAnnotations();

	/**
	 * Modify an annotation in the database.
	 *
	 * @param annatationId
	 * @param customMessage
	 * @param customPenalty
	 */
	void modifyAnnotation(String annatationId, String customMessage, Double customPenalty);

	/**
	 * Remove an existent annotation
	 *
	 * @param annotationId unique annotation identifier
	 */
	void removeAnnotation(String annotationId);
}
