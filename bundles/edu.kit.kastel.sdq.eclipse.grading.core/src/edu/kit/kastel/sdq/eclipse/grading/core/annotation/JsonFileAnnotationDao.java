package edu.kit.kastel.sdq.eclipse.grading.core.annotation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import edu.kit.kastel.sdq.eclipse.grading.api.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;

public class JsonFileAnnotationDao implements AnnotationDao {

	private final Set<IAnnotation> annotations;

	//TODO params: File path, ..? Implement serializing stuff
	public JsonFileAnnotationDao() {
		this.annotations = new HashSet<>();
	}

	@Override
	public void addAnnotation(int annotationID, IMistakeType mistakeType, int startLine, int endLine, String fullyClassifiedClassName,
			String customMessage, Double customPenalty) throws Exception {
		if (this.idExists(annotationID)) throw new Exception("ID " + annotationID + " already exists!");

		this.annotations.add(
				new Annotation(annotationID, mistakeType, startLine, endLine, fullyClassifiedClassName, customMessage, customPenalty));

		this.serialize();
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
				customMessage, customPenalty);

		this.annotations.remove(oldAnnotation);
		this.annotations.add(newAnnotation);
		this.serialize();
	}

	@Override
	public void removeAnnotation(int annotationId) {
		final IAnnotation foundAnnotation = this.getAnnotation(annotationId);
		this.annotations.remove(foundAnnotation);

		this.serialize();
	}

	@Override
	public void serialize() {
		// TODO implement (UPDATE: serializing should be done to Artemis!)
	}
}
