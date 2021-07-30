package edu.kit.kastel.sdq.eclipse.grading.core.artemis;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IFeedback;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.core.model.annotation.Annotation;

/**
 * Deserialize Annotation from a Feedback of
 * <li> type: MANUAL_UNREFERENCED
 * <li> text: CLIENT_DATA
 * <li> detailText: $THE_JSON_BLOB
 *
 */
public class AnnotationDeserializer {

	private static final String FEEDBACK_TEXT = "CLIENT_DATA";
	private Collection<IMistakeType> mistakeTypes;

	public AnnotationDeserializer(Collection<IMistakeType> mistakeTypes) {
		this.mistakeTypes = mistakeTypes;
	}

	private Annotation convert(Annotation annotation) throws ArtemisProcessingException {
		final List<IMistakeType> matchingMistakeTypes =
				this.mistakeTypes.stream()
					.filter(mistakeType -> mistakeType.getButtonName().equals(annotation.getMistakeTypeString()))
					.collect(Collectors.toList());
		if (matchingMistakeTypes.size() != 1) {
			throw new ArtemisProcessingException("Annotation Deserialization not possible: Found " + matchingMistakeTypes.size() + " matching mistake Types containing data instead of 1. "
					+ "(For Annotation " + annotation + ")");
		}
		annotation.setMistakeType(matchingMistakeTypes.get(0));
		return annotation;
	}

	public Collection<IAnnotation> deserialize(Collection<IFeedback> feedbacks) throws JsonProcessingException {
		final List<IFeedback> matchingFeedbacks = feedbacks.stream()
				.filter(feedback -> {
					String text = feedback.getText();
					return (text != null && text.equals(FEEDBACK_TEXT));
				})
				.collect(Collectors.toList());

		if (matchingFeedbacks.size() == 0) {
			return List.of();
		}

//		final Collection<IAnnotation> deserializedAnnotations = new ArrayList<>(annotationArray.length);
		JsonProcessingException[] foundException = {null};
		Collection<IAnnotation> deserializedAnnotations = matchingFeedbacks.stream()
			.map(IFeedback::getDetailText)	// get the json blob
			.map(feedbackDetailText -> {
				try {
					return new ObjectMapper().readValue(feedbackDetailText, Annotation[].class);
				} catch (JsonProcessingException e) {
					foundException[0] = e;
					return new Annotation[0];
				}
			}) //transform the json blob to annotations
			.map(Arrays::asList)
			.reduce(new LinkedList<>(), (annotationList1, annotationList2) -> {	//collect into one single list
				List<Annotation> resultList = new LinkedList<>();
				resultList.addAll(annotationList1);
				resultList.addAll(annotationList2);
				return resultList;
			})
			.stream()
			.map(IAnnotation.class::cast)
			.collect(Collectors.toList());

		if (foundException[0] != null) {
			throw foundException[0];
		}
		return deserializedAnnotations;



		//Old times, TODO delete
//		final String jsonBlob = matchingFeedbacks.get(0).getDetailText();
//
//		Annotation[] annotationArray = new ObjectMapper()
//				.readValue(jsonBlob, Annotation[].class);
//
//		Collection<IAnnotation> deserializedAnnotations = new ArrayList<>(annotationArray.length);
//		for (Annotation annotation : annotationArray) {
//			deserializedAnnotations.add(IAnnotation.class.cast(this.convert(annotation)));
//		}
//		return deserializedAnnotations;
	}
}
