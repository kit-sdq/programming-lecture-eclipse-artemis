package edu.kit.kastel.sdq.eclipse.grading.core.annotation;

import java.util.Optional;

import edu.kit.kastel.sdq.eclipse.grading.api.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;

public class Annotation implements IAnnotation {

	private final int id;
	private final IMistakeType mistakeType;
	private final int startLine;
	private final int endLine;
	private final String fullyClassifiedClassName;
	
	private Optional<String> customMessage;
	private Optional<Double> customPenalty;
	
	public Annotation(int id, IMistakeType mistakeType, int startLine, int endLine, String fullyClassifiedClassName, 
			Optional<String> customMessage, Optional<Double> customPenalty) {
		//TODO IDs uniqueness controlled by other class.
		this.id = id;
		this.mistakeType = mistakeType;
		this.startLine = startLine;
		this.endLine = endLine;
		this.fullyClassifiedClassName = fullyClassifiedClassName;	
		
		this.customMessage = customMessage == null ? Optional.empty() : customMessage;
		this.customPenalty = customPenalty == null ? Optional.empty() : customPenalty;
		
	}
	
	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public IMistakeType getMistakeType() {
		return this.mistakeType;
	}

	@Override
	public int getStartLine() {
		return this.startLine;
	}

	@Override
	public int getEndLine() {
		return this.endLine;
	}

	@Override
	public String getFullyClassifiedClassName() {
		return this.fullyClassifiedClassName;
	}

	@Override
	public Optional<String> getCustomMessage() {
		return this.customMessage;
	}

	@Override
	public Optional<Double> getCustomPenalty() {
		return this.customPenalty;
	}

	protected void setCustomMessage(String customMessage) {
		this.customMessage = Optional.of(customMessage);
	}
	
	protected void setCustomPenalty(double customPenalty) {
		this.customPenalty = Optional.of(customPenalty);		
	}
	
	@Override
	public boolean equals(Object otherAnnotation) {
		return (IAnnotation.class.isInstance(otherAnnotation)) 
				? this.getId() == ((IAnnotation)otherAnnotation).getId()
				: false;
	}

	@Override
	public String toString() {
		return "Annotation [id=" + id + ", mistakeType=" + mistakeType + ", startLine=" + startLine + ", endLine="
				+ endLine + ", fullyClassifiedClassName=" + fullyClassifiedClassName + ", customMessage="
				+ customMessage + ", customPenalty=" + customPenalty + "]";
	}
}
