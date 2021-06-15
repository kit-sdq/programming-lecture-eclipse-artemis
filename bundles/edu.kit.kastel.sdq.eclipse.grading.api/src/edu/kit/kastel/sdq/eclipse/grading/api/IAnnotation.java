package edu.kit.kastel.sdq.eclipse.grading.api;

import java.util.Collection;
import java.util.Optional;

/**
 * An annotation is one specific occurrence of a IMistakeType.
 * There might be multiple Annotation
 * 
 * You may define a custom penalty which might be used by some MistakeType (more precise: by its PenaltyRule).
 * Also, you may define a custom message.
 */
public interface IAnnotation {

	public IMistakeType getMistakeType();
	
	public int getId();
	
	public int getStartLine();
	
	public int getEndLine();
	
	public String getFullyClassifiedClassName();
	
	public Optional<String> getCustomMessage();
	
	public Optional<Double> getCustomPenalty();
}
