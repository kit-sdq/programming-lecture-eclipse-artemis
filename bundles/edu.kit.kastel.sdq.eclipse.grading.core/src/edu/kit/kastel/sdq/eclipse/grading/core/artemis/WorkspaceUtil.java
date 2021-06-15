package edu.kit.kastel.sdq.eclipse.grading.core.artemis;

import org.eclipse.core.internal.resources.NatureManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.internal.IMavenConstants;

public class WorkspaceUtil {

	private WorkspaceUtil() {}
	
	/**
	 * Create a new eclipse project given a projectName which corresponds to an EXISTING project in the workspace.
	 * Natures are Maven and Java
	 * @param projectName
	 */
	public static final void createEclipseProject(final String projectName) {
		final IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription("exercise-1-testAufgabe1_submission-5-uyduk");
		
		final String[] natures = {JavaCore.NATURE_ID, IMavenConstants.NATURE_ID};
		description.setNatureIds(natures);
		
		// and save it
		//TODO 2. Create Project with
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("exercise-1-testAufgabe1_submission-5-uyduk");
		try {
			project.create(description, null);
			
			//add natures now?
			
			project.open(null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
