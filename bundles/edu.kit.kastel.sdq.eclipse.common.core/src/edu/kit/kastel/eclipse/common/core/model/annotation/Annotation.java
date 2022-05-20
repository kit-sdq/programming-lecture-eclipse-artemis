/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.core.model.annotation;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.eclipse.common.api.model.IAnnotation;
import edu.kit.kastel.eclipse.common.api.model.IMistakeType;

public class Annotation implements IAnnotation {

	private final String uuid;
	private IMistakeType mistakeType;
	private String mistakeTypeId;
	private final int startLine;
	private final int endLine;
	private final String fullyClassifiedClassName;

	private String customMessage;
	private Double customPenalty;

	public Annotation(String uuid, IMistakeType mistakeType, int startLine, int endLine, String fullyClassifiedClassName, String customMessage,
			Double customPenalty) {
		this.uuid = uuid;
		this.mistakeType = mistakeType;
		this.startLine = startLine;
		this.endLine = endLine;
		this.fullyClassifiedClassName = fullyClassifiedClassName;

		this.customMessage = customMessage;
		this.customPenalty = customPenalty;
	}

	/**
	 * This Constructor is ONLY FOR DESERIALIZATION!
	 */
	@JsonCreator
	public Annotation(@JsonProperty("uuid") String uuid, @JsonProperty("startLine") int startLine, @JsonProperty("endLine") int endLine,
			@JsonProperty("classFilePath") String classFilePath, @JsonProperty("customMessageForJSON") String customMessage,
			@JsonProperty("customPenaltyForJSON") Double customPenalty, @JsonProperty("mistakeTypeId") String mistakeTypeId) {
		this.uuid = uuid == null ? IAnnotation.createID() : uuid;
		this.startLine = startLine;
		this.endLine = endLine;
		this.fullyClassifiedClassName = classFilePath;
		this.mistakeTypeId = mistakeTypeId;

		this.customMessage = customMessage;
		this.customPenalty = customPenalty;
	}

	@Override
	public String getClassFilePath() {
		return this.fullyClassifiedClassName;
	}

	@Override
	@JsonIgnore
	public Optional<String> getCustomMessage() {
		return this.customMessage == null ? Optional.empty() : Optional.of(this.customMessage);
	}

	public String getCustomMessageForJSON() {
		return this.customMessage;
	}

	@Override
	@JsonIgnore
	public Optional<Double> getCustomPenalty() {
		return this.customPenalty == null ? Optional.empty() : Optional.of(this.customPenalty);
	}

	public Double getCustomPenaltyForJSON() {
		return this.customPenalty;
	}

	@Override
	public int getEndLine() {
		return this.endLine;
	}

	@Override
	public String getUUID() {
		return this.uuid;
	}

	@Override
	@JsonIgnore
	public IMistakeType getMistakeType() {
		return this.mistakeType;
	}

	public String getMistakeTypeId() {
		return this.mistakeType != null ? this.mistakeType.getId() : this.mistakeTypeId;
	}

	@Override
	public int getStartLine() {
		return this.startLine;
	}

	/**
	 * This Method is ONLY FOR DESERIALIZATION! If mistakeType is already set, this
	 * has no effect.
	 */
	public void setMistakeType(IMistakeType mistakeType) {
		if (this.mistakeType != null) {
			return;
		}
		this.mistakeType = mistakeType;
	}

	@Override
	public String toString() {
		return "Annotation [id=" + this.uuid + ", mistakeType=" + this.mistakeType + ", startLine=" + this.startLine + ", endLine=" + this.endLine
				+ ", fullyClassifiedClassName=" + this.fullyClassifiedClassName + ", customMessage=" + this.customMessage + ", customPenalty="
				+ this.customPenalty + "]";
	}
}
