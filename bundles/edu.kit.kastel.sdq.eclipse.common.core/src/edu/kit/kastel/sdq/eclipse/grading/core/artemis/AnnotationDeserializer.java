/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.grading.core.artemis;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.core.model.annotation.Annotation;

/**
 * Deserialize Annotation from a Feedback of
 * <li>type: MANUAL_UNREFERENCED
 * <li>text: CLIENT_DATA
 * <li>detailText: $THE_JSON_BLOB
 *
 */
public class AnnotationDeserializer {

	private static final String FEEDBACK_TEXT = "CLIENT_DATA";
	private Map<String, IMistakeType> mistakeTypesMap;

	public AnnotationDeserializer(List<IMistakeType> mistakeTypes) {
		this.mistakeTypesMap = new HashMap<>();
		mistakeTypes.forEach(mistakeType -> this.mistakeTypesMap.put(mistakeType.getId(), mistakeType));
	}

	/**
	 * Deserialize a given Collection of IFeedbacks (that contain json blobs in the
	 * detailText field) into our model Annotations.
	 *
	 * @param feedbacks
	 * @return
	 * @throws IOException
	 */
	public List<IAnnotation> deserialize(List<Feedback> feedbacks) throws IOException {
		final List<Feedback> matchingFeedbacks = feedbacks.stream().filter(feedback -> {
			if (feedback == null) {
				return false;
			}
			String text = feedback.getText();
			return text != null && text.equals(FEEDBACK_TEXT);
		}).collect(Collectors.toList());

		if (matchingFeedbacks.isEmpty()) {
			return List.of();
		}

		JsonProcessingException[] foundException = { null };
		List<Annotation> deserializedAnnotations = matchingFeedbacks.stream().map(Feedback::getDetailText) // get the json blob
				.map(feedbackDetailText -> { // transform the json blob to multiple annotations
					try {

						return new ObjectMapper().readValue(feedbackDetailText, Annotation[].class);
					} catch (JsonProcessingException e) {
						foundException[0] = e;
						return new Annotation[0];
					}
				}).map(Arrays::asList).flatMap(List::stream) // Stream of List of annotations ==> Stream of annotations.
				.collect(Collectors.toList());

		if (foundException[0] != null) {
			throw foundException[0];
		}

		// add mistaketypes!
		for (Annotation annotation : deserializedAnnotations) {
			final String mistakeTypeName = annotation.getMistakeTypeId();
			if (!this.mistakeTypesMap.containsKey(mistakeTypeName)) {
				throw new IOException("Trying to deserialize MistakeType \"" + mistakeTypeName + "\". It was not found in local config!");
			}
			annotation.setMistakeType(this.mistakeTypesMap.get(mistakeTypeName));
		}

		return deserializedAnnotations.stream().map(IAnnotation.class::cast).collect(Collectors.toList());
	}
}
