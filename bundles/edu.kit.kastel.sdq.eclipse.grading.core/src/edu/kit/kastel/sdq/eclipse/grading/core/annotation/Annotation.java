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

	private String customMessage;
	private Double customPenalty;

	public Annotation(int id, IMistakeType mistakeType, int startLine, int endLine, String fullyClassifiedClassName,
			String customMessage, Double customPenalty) {
		//TODO IDs uniqueness controlled by other class.
		this.id = id;
		this.mistakeType = mistakeType;
		this.startLine = startLine;
		this.endLine = endLine;
		this.fullyClassifiedClassName = fullyClassifiedClassName;

		this.customMessage = customMessage;
		this.customPenalty = customPenalty;

	}

	@Override
	public boolean equals(Object otherAnnotation) {
		return (IAnnotation.class.isInstance(otherAnnotation))
				? this.getId() == ((IAnnotation)otherAnnotation).getId()
				: false;
	}

	@Override
	public Optional<String> getCustomMessage() {
		return (this.customMessage == null) ? Optional.empty() : Optional.of(this.customMessage);
	}

	@Override
	public Optional<Double> getCustomPenalty() {
		return (this.customPenalty == null) ? Optional.empty() : Optional.of(this.customPenalty);
	}

	@Override
	public int getEndLine() {
		return this.endLine;
	}

	@Override
	public String getClassFilePath() {
		return this.fullyClassifiedClassName;
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

	protected void setCustomMessage(String customMessage) {
		this.customMessage = customMessage;
	}

	protected void setCustomPenalty(double customPenalty) {
		this.customPenalty = customPenalty;
	}

	@Override
	public String toString() {
		return "Annotation [id=" + this.id + ", mistakeType=" + this.mistakeType + ", startLine=" + this.startLine + ", endLine="
				+ this.endLine + ", fullyClassifiedClassName=" + this.fullyClassifiedClassName + ", customMessage="
				+ this.customMessage + ", customPenalty=" + this.customPenalty + "]";
	}
}
