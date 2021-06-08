package edu.kit.kastel.sdq.eclipse.grading.api;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

/**
 * TODO Zwischenberechnung der mistakes (pro Klasse e.g.) zurück an die GUI.
 *
 */
public interface IAssessmentController {

	/**
	 * 
	 * @return all mistake types.
	 * @throws IOException 
	 */
	public Collection<IMistakeType> getMistakes() throws IOException;
	
	/**
	 * Add an annotation to the current assessment.
	 * 
	 * TODO 
	 * <li> additional param: mistake type!
	 * <li> maybe return int (id)
	 * 
	 * @param startLine annotation start
	 * @param endLine	annotation end
	 * @param className	unique name of the respective Class to be annotated TODO (maybe) path/ "workspace URI"
	 * 		 instead of class name (other langs etc)
	 * @param customMessage	custom message set by tutor
	 * @param customPenalty custom penalty set by tutor
	 */
	public void addAnnotation(int startLine, int endLine, String fullyClassifiedClassName, Optional<String> customMessage, Optional<Double> customPenalty);
	
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
