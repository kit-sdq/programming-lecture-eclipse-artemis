package edu.kit.kastel.sdq.eclipse.grading.api;

import java.util.Collection;
import java.util.Optional;

public interface IAssessmentController {

	/**
	 * 
	 * @return all mistake types.
	 */
	public Collection<IMistake> getMistakes();
	
	/**
	 * Add an annotation to the current assessment.
	 * 
	 * @param startLine annotation start
	 * @param endLine	annotation end
	 * @param className	unique name of the respective Class to be annotated
	 * @param customMessage	custom message set by tutor
	 * @param customPenalty custom penalty set by tutor
	 */
	public void addAnnotation(int startLine, int endLine, String className, Optional<String> customMessage, Optional<Double> customPenalty);
	
	/**
	 * 
	 * @param className
	 * @return all annotations already made for the given class.
	 */
	public Collection<IAnnotation> getAnnotations(String className);
	
	/**
	 * Remove an existent annotation
	 * @param annotationId	unique annotation identifier
	 */
	public void removeAnnotation(int annotationId);
	
	/**
	 * Modify an existent annotation
	 * @param annatationId	unique annotation identifier
	 * @param customMessage	new custom message
	 * @param customPenalty new custom penalty
	 */
	public void modifyAnnotation(int annatationId, Optional<String> customMessage, Optional<Double> customPenalty);	
	
	
	
}
