package edu.kit.kastel.sdq.eclipse.grading.core.artemis;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.internal.IMavenConstants;

public class WorkspaceUtil {


	/**
	 * Create a new eclipse project given a projectName which corresponds to an EXISTING project in the workspace.
	 * Natures are Maven and Java
	 * @param projectDirectory
	 */
	public static final void createEclipseProject(final File projectDirectory) {
		createEclipseProject(projectDirectory.getName());
	}

	/**
	 * Create a new eclipse project given a projectName which corresponds to an EXISTING project in the workspace.
	 * Natures are Maven and Java
	 * @param projectName
	 */
	public static final void createEclipseProject(final String projectName) {
		final IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(projectName);

		final String[] natures = {JavaCore.NATURE_ID, IMavenConstants.NATURE_ID};
		description.setNatureIds(natures);

		// and save it
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		try {
			project.create(description, null);
			project.open(null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static final void deleteEclipseProject(final String projectName) throws CoreException {
		ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).delete(true, null);
	}

	private WorkspaceUtil() {}
}
