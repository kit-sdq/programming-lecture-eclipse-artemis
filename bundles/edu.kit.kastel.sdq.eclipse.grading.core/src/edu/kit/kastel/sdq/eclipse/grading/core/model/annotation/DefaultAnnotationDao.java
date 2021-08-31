package edu.kit.kastel.sdq.eclipse.grading.core.model.annotation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import edu.kit.kastel.sdq.eclipse.grading.api.model.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;

public class DefaultAnnotationDao implements IAnnotationDao {

	private final Set<IAnnotation> annotations;

	public DefaultAnnotationDao() {
		this.annotations = new HashSet<>();
	}

	@Override
	public void addAnnotation(int annotationID, IMistakeType mistakeType, int startLine, int endLine, String fullyClassifiedClassName,
			String customMessage, Double customPenalty, int markerCharStart, int markerCharEnd) throws AnnotationException {
		if (this.idExists(annotationID)) throw new AnnotationException("ID " + annotationID + " already exists!");

		this.annotations.add(
				new Annotation(annotationID, mistakeType, startLine, endLine, fullyClassifiedClassName, customMessage, customPenalty, markerCharStart, markerCharEnd));
	}

	@Override
	public IAnnotation getAnnotation(int annotationId) {
		return this.annotations.stream()
				.filter(annotation -> annotation.getId() == annotationId)
				.findAny()
				.orElseThrow();
	}

	@Override
	public Collection<IAnnotation> getAnnotations() {
		return Collections.unmodifiableSet(this.annotations);
	}

	private boolean idExists(int annotationID) {
		return this.annotations.stream()
				.anyMatch(annotation -> annotation.getId() == annotationID);
	}

	@Override
	public void modifyAnnotation(int annatationId, String customMessage, Double customPenalty) {
		final IAnnotation oldAnnotation = this.getAnnotation(annatationId);
		final IAnnotation newAnnotation = new Annotation(
				oldAnnotation.getId(), oldAnnotation.getMistakeType(),
				oldAnnotation.getStartLine(), oldAnnotation.getEndLine(),
				oldAnnotation.getClassFilePath(),
				customMessage, customPenalty,
				oldAnnotation.getMarkerCharStart(), oldAnnotation.getMarkerCharEnd());

		this.annotations.remove(oldAnnotation);
		this.annotations.add(newAnnotation);
	}

	@Override
	public void removeAnnotation(int annotationId) {
		if (this.idExists(annotationId)) {
			this.annotations.remove(this.getAnnotation(annotationId));
		}
	}

}
