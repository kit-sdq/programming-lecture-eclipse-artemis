/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.client.mappings.stats;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Timing(@JsonProperty int inTime, @JsonProperty int late) {
}
