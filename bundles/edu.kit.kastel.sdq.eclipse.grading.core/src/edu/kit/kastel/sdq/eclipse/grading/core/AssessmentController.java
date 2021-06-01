package edu.kit.kastel.sdq.eclipse.grading.core;

import java.util.Collection;
import java.util.Optional;

import edu.kit.kastel.sdq.eclipse.grading.api.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;

public class AssessmentController implements IAssessmentController {

	@Override
	public Collection<IMistakeType> getMistakes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addAnnotation(int startLine, int endLine, String fullyClassifiedClassName,
			Optional<String> customMessage, Optional<Double> customPenalty) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<IAnnotation> getAnnotations(String className) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeAnnotation(int annotationId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modifyAnnotation(int annatationId, Optional<String> customMessage, Optional<Double> customPenalty) {
		// TODO Auto-generated method stub
		
	}

}
