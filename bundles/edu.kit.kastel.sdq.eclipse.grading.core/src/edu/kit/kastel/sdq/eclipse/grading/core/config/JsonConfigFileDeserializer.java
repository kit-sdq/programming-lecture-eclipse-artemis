package edu.kit.kastel.sdq.eclipse.grading.core.config;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.*;

import edu.kit.kastel.sdq.eclipse.grading.core.model.MistakeType;
import edu.kit.kastel.sdq.eclipse.grading.core.model.PenaltyRule;
import edu.kit.kastel.sdq.eclipse.grading.core.model.RatingGroup;
import edu.kit.kastel.sdq.eclipse.grading.core.model.ThresholdPenaltyRule;

public class JsonConfigFileDeserializer extends StdDeserializer<JsonConfigFileMapped> {
	
	private static final String RATING_GROUPS_KEY = "ratingGroups";
	private static final String MISTAKE_TYPES_KEY = "mistakeTypes";
//	private static final String MISTAKE_TYPES_KEY = "templates";		//TODO only for lazyness

	private static final String RATING_GROUP_DISPLAY_NAME_KEY = "displayName";
	
	
	
	
	public JsonConfigFileDeserializer() {
		super(JsonConfigFileMapped.class); //todo is this right?
//		super(null); 						//todo is this right?
	}

	@Override
	public JsonConfigFileMapped deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		Collection<ExerciseConfig> exerciseConfigs = new LinkedList<ExerciseConfig>();
		JsonNode rootNode = p.getCodec().readTree(p);
		
		rootNode.fields().forEachRemaining(exerciseConfigNameAndNode -> {
			final JsonNode exerciseConfigNode = exerciseConfigNameAndNode.getValue();
			
			final String exerciseName = exerciseConfigNameAndNode.getKey();
			final JsonNode ratingGroupsNode = exerciseConfigNode.get(RATING_GROUPS_KEY);
			final JsonNode mistakeTypesNode = exerciseConfigNode.get(MISTAKE_TYPES_KEY);
			
			
			final Collection<RatingGroup> ratingGroups = parseRatingGroupsNode(ratingGroupsNode);
			final Collection<MistakeType> mistakeTypes = parseMistakeTypesNode(mistakeTypesNode, ratingGroups);
			
			exerciseConfigs.add(new ExerciseConfig(
					exerciseName, 
					parseRatingGroupsNode(ratingGroupsNode),
					mistakeTypes)
			);
		});		
		
		return new JsonConfigFileMapped(exerciseConfigs);
	}
	
	/**
	 * 
	 * @param ratingGroupsNode
	 * @param mistakeTypes for generating the association
	 * @return a ratingGroupNode with no mistake types associated yet!
	 */
	private Collection<RatingGroup> parseRatingGroupsNode(JsonNode ratingGroupsNode) {
		List<RatingGroup> ratingGroups = new LinkedList<RatingGroup>();
		
		ratingGroupsNode.fields().forEachRemaining(ratingGroupNameAndNode -> {
			//TODO implement
			final String ratingGroupShortName = ratingGroupNameAndNode.getKey();
			final JsonNode ratingGroupNode = ratingGroupNameAndNode.getValue();
			
			ratingGroups.add(new RatingGroup(ratingGroupShortName, ratingGroupNode.get(RATING_GROUP_DISPLAY_NAME_KEY).asText()));
			
		});
		
		return ratingGroups;		
	}
	
	private Collection<MistakeType> parseMistakeTypesNode(JsonNode mistakeTypesNode, Collection<RatingGroup> ratingGroups) {
		List<MistakeType> mistakeTypes = new LinkedList<MistakeType>();
		
		mistakeTypesNode.fields().forEachRemaining(mistakeTypeNameAndNode -> {
			//TODO implement
			final String mistakeTypeName = mistakeTypeNameAndNode.getKey();
			final JsonNode mistakeTypeNode = mistakeTypeNameAndNode.getValue();
			
			
			Optional<RatingGroup> ratingGroupOptional = ratingGroups.stream().filter(ratingGroup -> 
				ratingGroup.getShortName().equals(mistakeTypeNode.get("appliesTo").asText())
			).findFirst();
			
			if (ratingGroupOptional.isEmpty()) {
				//TODO maybe not do a lambda so we can throw checked exceptions...
				throw new RuntimeException("no rating group could be associated with mistakeType " + mistakeTypeName);
			}
			
			mistakeTypes.add(new MistakeType(mistakeTypeName, 
					mistakeTypeNode.get("button").asText(), mistakeTypeNode.get("message").asText(), 
					ratingGroupOptional.get(), 
					parsePenaltyRuleNode(mistakeTypeNode.get("penaltyRule")))	//TODO penalty parsing! Need to modify the config.json!
			);
			
		});
		
		return mistakeTypes;
	}
	
	private PenaltyRule parsePenaltyRuleNode(JsonNode penaltyRuleNode) {
		//TODO implement more open to extension!
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
