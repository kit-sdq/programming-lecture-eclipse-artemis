/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.api.controller;

import org.eclipse.core.resources.IProject;

public interface ISubmissionLifecycleCallback {
	void onPhaseCompleted(IProject project);
}
