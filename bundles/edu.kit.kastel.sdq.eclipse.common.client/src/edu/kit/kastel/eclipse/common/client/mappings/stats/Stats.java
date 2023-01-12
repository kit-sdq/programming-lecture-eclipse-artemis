/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.client.mappings.stats;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Stats(@JsonProperty Timing numberOfSubmissions, //
		@JsonProperty Timing[] numberOfAssessmentsOfCorrectionRounds, //
		@JsonProperty int totalNumberOfAssessmentLocks //
) {
}
