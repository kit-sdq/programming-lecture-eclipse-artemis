/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.core.artemis.naming;

import java.util.function.Supplier;

import edu.kit.kastel.eclipse.common.api.artemis.IProjectFileNamingStrategy;

public enum ProjectFileNamingStrategies implements Supplier<IProjectFileNamingStrategy> {
	DEFAULT(new DefaultProjectFileNamingStrategy());

	private final DefaultProjectFileNamingStrategy projectFileNamingStrategy;

	ProjectFileNamingStrategies(DefaultProjectFileNamingStrategy projectFileNamingStrategy) {
		this.projectFileNamingStrategy = projectFileNamingStrategy;
	}

	@Override
	public IProjectFileNamingStrategy get() {
		return this.projectFileNamingStrategy;
	}

}
