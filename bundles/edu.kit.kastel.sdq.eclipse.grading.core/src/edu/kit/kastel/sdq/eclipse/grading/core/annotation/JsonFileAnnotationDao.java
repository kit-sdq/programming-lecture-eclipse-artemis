package edu.kit.kastel.sdq.eclipse.grading.core.annotation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import edu.kit.kastel.sdq.eclipse.grading.api.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;

public class JsonFileAnnotationDao implements AnnotationDao {

	private final Set<IAnnotation> annotations;
	//always contains the newest ID. (-1 == no IDs)
	private int idCounter;
	
	//TODO params: File path, ..? Implement serializing stuff
	public JsonFileAnnotationDao() {
		this.annotations = new HashSet<>();
		
		//TODO need to consider starting from a File state, too!
		this.idCounter = -1;
		
	}
	
	@Override
	public void addAnnotation(IMistakeType mistakeType, int startLine, int endLine, String fullyClassifiedClassName,
			Optional<String> customMessage, Optional<Double> customPenalty) {
		this.annotations.add(
				new Annotation(this.getNewId(), mistakeType, startLine, endLine, fullyClassifiedClassName, customMessage, customPenalty));
		
		this.serialize();
	}

	@Override
	public Collection<IAnnotation> getAnnotations() {
		return Collections.unmodifiableSet(annotations);
	}

	@Override
	public IAnnotation getAnnotation(int annotationId) {
		return this.annotations.stream()
				.filter(annotation -> annotation.getId() == annotationId)
				.findAny()
				.orElseThrow();
	}

	@Override
	public void removeAnnotation(int annotationId) {		
		final IAnnotation foundAnnotation = this.getAnnotation(annotationId);
		this.annotations.remove(foundAnnotation);
		
		this.serialize();
	}

	@Override
	public void serialize() {
		// TODO implement
	}
	
	private int getNewId() {
		return ++idCounter;
	}
}
