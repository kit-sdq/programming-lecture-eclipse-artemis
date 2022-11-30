/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.core.model.annotation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import edu.kit.kastel.eclipse.common.api.model.IAnnotation;
import edu.kit.kastel.eclipse.common.api.model.IMistakeType;

public class AnnotationDAO implements IAnnotationDAO {

	private final Set<IAnnotation> annotations;

	public AnnotationDAO() {
		this.annotations = new HashSet<>();
	}

	@Override
	public void addAnnotation(String annotationId, IMistakeType mistakeType, int startLine, int endLine, String fullyClassifiedClassName, String customMessage,
			Double customPenalty) throws AnnotationException {
		if (this.idExists(annotationId)) {
			throw new AnnotationException("ID " + annotationId + " already exists!");
		}

		this.annotations.add(new Annotation(annotationId, mistakeType, startLine, endLine, fullyClassifiedClassName, customMessage, customPenalty));
	}

	@Override
	public IAnnotation getAnnotation(String annotationId) {
		return this.annotations.stream().filter(annotation -> annotation.getUUID().equals(annotationId)).findAny().orElseThrow();
	}

	@Override
	public Set<IAnnotation> getAnnotations() {
		return Collections.unmodifiableSet(this.annotations);
	}

	private boolean idExists(String annotationId) {
		return this.annotations.stream().anyMatch(annotation -> annotation.getUUID().equals(annotationId));
	}

	@Override
	public void modifyAnnotation(String annatationId, String customMessage, Double customPenalty) {
		final IAnnotation oldAnnotation = this.getAnnotation(annatationId);
		final IAnnotation newAnnotation = new Annotation(oldAnnotation.getUUID(), oldAnnotation.getMistakeType(), oldAnnotation.getStartLine(),
				oldAnnotation.getEndLine(), oldAnnotation.getClassFilePath(), customMessage, customPenalty);

		this.annotations.remove(oldAnnotation);
		this.annotations.add(newAnnotation);
	}

	@Override
	public void removeAnnotation(String annotationId) {
		if (this.idExists(annotationId)) {
			this.annotations.remove(this.getAnnotation(annotationId));
		}
	}

}
