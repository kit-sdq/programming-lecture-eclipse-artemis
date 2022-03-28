/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.grading.core.artemis.naming;

import java.util.function.Supplier;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;

public enum ProjectFileNamingStrategies implements Supplier<IProjectFileNamingStrategy> {
	DEFAULT(new DefaultProjectFileNamingStrategy());

	private final DefaultProjectFileNamingStrategy pfns;

	ProjectFileNamingStrategies(DefaultProjectFileNamingStrategy pfns) {
		this.pfns = pfns;
	}

	@Override
	public IProjectFileNamingStrategy get() {
		return pfns;
	}

}
