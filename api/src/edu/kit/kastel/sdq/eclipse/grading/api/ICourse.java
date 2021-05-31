package edu.kit.kastel.sdq.eclipse.grading.api;

import java.util.Collection;

/**
 * TODO
 * <li> Exams differ? two assessments! other calls?
 *
 */
public interface ICourse {
	//TODO noch id, name usw 
	
	public Collection<IExercise> getExercises();

}
