/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.grading.core.config;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import edu.kit.kastel.sdq.eclipse.grading.core.model.CustomPenaltyRule;
import edu.kit.kastel.sdq.eclipse.grading.core.model.PenaltyRule;
import edu.kit.kastel.sdq.eclipse.grading.core.model.ThresholdPenaltyRule;

/**
 * Deserialize a PenaltyRule which is not trivial since PenaltyRule is abstract
 * and its subclasses have individual constructor signatures. Thus, this must be
 * done manually. Penalty rule types are defined here (see
 * {@link PenaltyRuleType}) and by extending {@link PenaltyRule} in the model
 * package.
 *
 */
public class PenaltyRuleDeserializer extends StdDeserializer<PenaltyRule> {
	private static final long serialVersionUID = 6326274512036616184L;

	/**
	 * A means to construct PenaltyRules without having to edit a switch/case
	 * statement: To add a new enum value you merely need to write a lambda
	 * constructing your new Subclass of PenaltyRule out of your new custom values
	 * which are provided in the penaltyRuleNode.
	 *
	 */
	public enum PenaltyRuleType {
		// Need to add a new enum value with a short Name (that must be used in the
		// config file) and a constructor based on the json node.
		THRESHOLD_PENALTY_RULE_TYPE(ThresholdPenaltyRule.SHORT_NAME,
				penaltyRuleNode -> new ThresholdPenaltyRule(penaltyRuleNode.get("threshold").asInt(), penaltyRuleNode.get("penalty").asDouble())),
		CUSTOM_PENALTY_RULE_TYPE(CustomPenaltyRule.SHORT_NAME, penaltyRuleNode -> new CustomPenaltyRule());

		interface Constructor {
			PenaltyRule construct(final JsonNode penaltyRuleNode);
		}

		public static PenaltyRuleType fromShortName(String shortName) {
			for (PenaltyRuleType penaltyRuleType : PenaltyRuleType.values()) {
				if (penaltyRuleType.getShortName().equalsIgnoreCase(shortName)) {
					return penaltyRuleType;
				}
			}
			return null;
		}

		private Constructor constructor;

		private String shortName;

		PenaltyRuleType(final String shortName, final Constructor constructor) {
			this.shortName = shortName;
			this.constructor = constructor;
		}

		public PenaltyRule construct(final JsonNode penaltyRuleNode) {
			return this.constructor.construct(penaltyRuleNode);
		}

		public String getShortName() {
			return this.shortName;
		}
	}

	protected PenaltyRuleDeserializer() {
		super(PenaltyRule.class);
	}

	@Override
	public PenaltyRule deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {

		final JsonNode penaltyRuleNode = parser.getCodec().readTree(parser);
		final String penaltyRuleShortName = penaltyRuleNode.get("shortName").asText();

		final PenaltyRuleType penaltyRuleType = PenaltyRuleType.fromShortName(penaltyRuleShortName);
		if (penaltyRuleType != null) {
			return penaltyRuleType.construct(penaltyRuleNode);
		}
		throw new IOException("No PenaltyRule Subclass defined for penaltyRule " + penaltyRuleShortName);

	}

}
