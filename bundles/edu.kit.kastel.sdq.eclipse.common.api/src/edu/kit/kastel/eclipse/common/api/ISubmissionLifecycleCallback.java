/* Licensed under EPL-2.0 2023. */
package edu.kit.kastel.eclipse.common.api;

import org.eclipse.core.resources.IProject;

@FunctionalInterface
public interface ISubmissionLifecycleCallback {
	void onPhaseCompleted(IProject project);
}
