package edu.kit.kastel.sdq.eclipse.grading.core.model.annotation;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.grading.api.model.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;

public class Annotation implements IAnnotation {

	private final int id;
	private IMistakeType mistakeType;
	private String mistakeTypeString;
	private final int startLine;
	private final int endLine;
	private final String fullyClassifiedClassName;

	private String customMessage;
	private Double customPenalty;

	private final int markerCharStart;
	private final int markerCharEnd;

	public Annotation(int id, IMistakeType mistakeType, int startLine, int endLine, String fullyClassifiedClassName,
			String customMessage, Double customPenalty, int markerCharStart, int markerCharEnd) {
		this.id = id;
		this.mistakeType = mistakeType;
		this.startLine = startLine;
		this.endLine = endLine;
		this.fullyClassifiedClassName = fullyClassifiedClassName;

		this.customMessage = customMessage;
		this.customPenalty = customPenalty;

		this.markerCharStart = markerCharStart;
		this.markerCharEnd = markerCharEnd;
	}

	/**
	 * This Constructor is ONLY FOR DESERIALIZATION!
	 */
	@JsonCreator
	public Annotation(
			@JsonProperty("id") int id,
			@JsonProperty("startLine") int startLine,
			@JsonProperty("endLine") int endLine,
			@JsonProperty("classFilePath") String classFilePath,
			@JsonProperty("customMessageForJSON") String customMessage,
			@JsonProperty("customPenaltyForJSON") Double customPenalty,
			@JsonProperty("mistakeTypeString") String mistakeTypeString,
			@JsonProperty("markerCharStart") int markerCharStart,
			@JsonProperty("markerCharEnd") int markerCharEnd) {
		this.id = id;
		this.startLine = startLine;
		this.endLine = endLine;
		this.fullyClassifiedClassName = classFilePath;
		this.mistakeTypeString = mistakeTypeString;

		this.customMessage = customMessage;
		this.customPenalty = customPenalty;

		this.markerCharStart = markerCharStart;
		this.markerCharEnd = markerCharEnd;
	}

	@Override
	public String getClassFilePath() {
		return this.fullyClassifiedClassName;
	}

	@Override
	@JsonIgnore
	public Optional<String> getCustomMessage() {
		return (this.customMessage == null) ? Optional.empty() : Optional.of(this.customMessage);
	}

	public String getCustomMessageForJSON() {
		return this.customMessage;
	}

	@Override
	@JsonIgnore
	public Optional<Double> getCustomPenalty() {
		return (this.customPenalty == null) ? Optional.empty() : Optional.of(this.customPenalty);
	}

	public Double getCustomPenaltyForJSON() {
		return this.customPenalty;
	}

	@Override
	public int getEndLine() {
		return this.endLine;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public int getMarkerCharEnd() {
		return this.markerCharEnd;
	}

	@Override
	public int getMarkerCharStart() {
		return this.markerCharStart;
	}

	@Override
	@JsonIgnore
	public IMistakeType getMistakeType() {
		return this.mistakeType;
	}

	/**
	 *
	 * @return mistakeType::getButtonName (for serialization to artemis)
	 */
	public String getMistakeTypeString() {
		//mistakeTypeString is needed for deserialization
		return this.mistakeType != null ? this.mistakeType.getName() : this.mistakeTypeString;
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

	/**
	 * This Method is ONLY FOR DESERIALIZATION!
	 * If mistakeType is already set, this has no effect.
	 */
	public void setMistakeType(IMistakeType mistakeType) {
		if (this.mistakeType != null) return;
		this.mistakeType = mistakeType;
	}

	@Override
	public String toString() {
		return "Annotation [id=" + this.id + ", mistakeType=" + this.mistakeType + ", startLine=" + this.startLine + ", endLine="
				+ this.endLine + ", fullyClassifiedClassName=" + this.fullyClassifiedClassName + ", customMessage="
				+ this.customMessage + ", customPenalty=" + this.customPenalty + "]";
	}
}
