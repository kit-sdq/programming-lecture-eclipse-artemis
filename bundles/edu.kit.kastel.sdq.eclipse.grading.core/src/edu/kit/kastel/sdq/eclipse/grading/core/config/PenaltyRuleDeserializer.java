package edu.kit.kastel.sdq.eclipse.grading.core.config;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import edu.kit.kastel.sdq.eclipse.grading.core.model.PenaltyRule;
import edu.kit.kastel.sdq.eclipse.grading.core.model.ThresholdPenaltyRule;

public class PenaltyRuleDeserializer extends StdDeserializer<PenaltyRule> {

	protected PenaltyRuleDeserializer() {
		super(PenaltyRule.class);
	}

	@Override
	public PenaltyRule deserialize(JsonParser parser, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		final JsonNode penaltyRuleNode = parser.getCodec().readTree(parser);
		final String penaltyRuleShortName = penaltyRuleNode.get("shortName").asText();
		switch (penaltyRuleShortName) {
			case ThresholdPenaltyRule.SHORT_NAME:
				return new ThresholdPenaltyRule(penaltyRuleNode.get("threshold").asInt(), penaltyRuleNode.get("penalty").asDouble());
			// Add cases here to add new PenaltyRule!
				
			default:
				throw new RuntimeException("No PenaltyRule Subclass defined for penaltyRule " + penaltyRuleShortName);
		}
	}

}
