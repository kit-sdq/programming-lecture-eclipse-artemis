package edu.kit.kastel.sdq.eclipse.grading.api;

import java.util.Collection;

public interface IExercise {
	//TODO noch mehr Stuff
	
	public int getExerciseId();
	
	public String getTitle();
	
	public String getShortName();
	
	public String getTestRepositoryUrl();
	
	public Collection<ISubmission> getSubmissions();
}
