/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.api;

import edu.kit.kastel.eclipse.common.api.util.Version;

public final class EclipseArtemisConstants {
	private EclipseArtemisConstants() {
		throw new IllegalAccessError();
	}

	public static final Version MINIMUM_ARTEMIS_VERSION_INCLUSIVE = new Version(6, 0, 0);
	public static final Version MAXIMUM_ARTEMIS_VERSION_EXCLUSIVE = new Version(7, 0, 0);

	public static final String GRADING_WIKI_URL = "https://kit-sdq.github.io/programming-lecture-eclipse-artemis.docs/";
}
