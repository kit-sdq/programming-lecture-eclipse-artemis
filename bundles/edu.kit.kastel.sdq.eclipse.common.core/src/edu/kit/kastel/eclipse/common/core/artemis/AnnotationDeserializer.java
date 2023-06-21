/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.core.artemis;

import java.io.IOException;
import java.util.*;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kit.kastel.eclipse.common.api.artemis.mapping.Feedback;
import edu.kit.kastel.eclipse.common.api.model.IAnnotation;
import edu.kit.kastel.eclipse.common.api.model.IMistakeType;
import edu.kit.kastel.eclipse.common.core.model.annotation.Annotation;

/**
 * Deserialize Annotation from a Feedback of
 * <li>type: MANUAL_UNREFERENCED
 * <li>text: CLIENT_DATA
 * <li>detailText: $THE_JSON_BLOB
 *
 */
public class AnnotationDeserializer {

	private static final ILog log = Platform.getLog(AnnotationDeserializer.class);

	private static final String FEEDBACK_TEXT = "CLIENT_DATA";
	private final Map<String, IMistakeType> mistakeTypesMap;
	private final ObjectMapper oom;

	public AnnotationDeserializer(List<IMistakeType> mistakeTypes) {
		this.oom = new ObjectMapper();
		this.mistakeTypesMap = new HashMap<>();
		mistakeTypes.forEach(mistakeType -> this.mistakeTypesMap.put(mistakeType.getIdentifier(), mistakeType));
	}

	/**
	 * Deserialize a given Collection of IFeedbacks (that contain json blobs in the
	 * detailText field) into our model Annotations.
	 */
	public List<IAnnotation> deserialize(List<Feedback> feedbacks) throws IOException {
		final List<Feedback> feedbacksWithAnnotationInformation = feedbacks.stream() //
				.filter(Objects::nonNull) //
				.filter(it -> FEEDBACK_TEXT.equals(it.getText())) //
				.toList();

		final List<Annotation> annotations = readAnnotations(feedbacksWithAnnotationInformation);

		for (Annotation annotation : annotations) {
			final String mistakeTypeName = annotation.getMistakeTypeId();
			if (!this.mistakeTypesMap.containsKey(mistakeTypeName)) {
				throw new IOException("Trying to deserialize MistakeType \"" + mistakeTypeName + "\". It was not found in local config!");
			}
			annotation.setMistakeType(this.mistakeTypesMap.get(mistakeTypeName));
		}

		return new ArrayList<>(annotations);
	}

	private List<Annotation> readAnnotations(List<Feedback> feedbacksWithAnnotationInformation) {
		List<Annotation> annotations = new ArrayList<>();
		for (var feedback : feedbacksWithAnnotationInformation) {
			try {
				List<Annotation> annotationsInFeedback = oom.readValue(feedback.getDetailText(), new TypeReference<>() {
				});
				annotations.addAll(annotationsInFeedback);
			} catch (JsonProcessingException e) {
				log.error(e.getMessage(), e);
			}
		}
		return annotations;
	}
}
