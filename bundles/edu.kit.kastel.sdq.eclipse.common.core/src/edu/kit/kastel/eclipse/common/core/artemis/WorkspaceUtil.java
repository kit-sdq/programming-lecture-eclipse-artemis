/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.core.artemis;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.ui.internal.UpdateMavenProjectJob;

@SuppressWarnings("restriction")
public class WorkspaceUtil {

	/**
	 * Create a new eclipse project given a projectName which corresponds to an
	 * EXISTING project in the workspace. Natures are Maven and Java
	 *
	 * @param projectDirectory
	 * @throws CoreException
	 */
	public static final void createEclipseProject(final File projectDirectory) throws CoreException {
		createEclipseProject(projectDirectory.getName());
	}

	/**
	 * Create a new eclipse project given a projectName which corresponds to an
	 * EXISTING project in the workspace. Natures are Maven and Java
	 *
	 * @param projectName
	 */
	public static final void createEclipseProject(final String projectName) throws CoreException {
		final IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(projectName);

		final String[] natures = { JavaCore.NATURE_ID, IMavenConstants.NATURE_ID };
		description.setNatureIds(natures);

		// and save it
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		project.create(null);
		project.open(null);
		project.setDescription(description, null);

		new UpdateMavenProjectJob(new IProject[] { project }).schedule();
	}

	public static final void deleteDirectoryRecursively(final Path directory) throws IOException {
		Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	/**
	 * Delete a given eclipse project via eclipse functionality and, thereafter, on
	 * the file system if anything is left.
	 *
	 * @param projectName
	 * @throws CoreException
	 * @throws IOException
	 */
	public static final void deleteEclipseProject(final String projectName) throws CoreException, IOException {
		final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (project == null || !project.exists()) {
			// doesnt exist ==> nothing to be done
			return;
		}
		File projectLocation = project.getLocation().toFile();
		project.delete(true, null);

		if (projectLocation.exists()) {
			deleteDirectoryRecursively(projectLocation.toPath());
		}
	}

	/**
	 *
	 * @return the current workspace as a file.
	 */
	public static final File getWorkspaceFile() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();

	}

	private WorkspaceUtil() {
	}
}
