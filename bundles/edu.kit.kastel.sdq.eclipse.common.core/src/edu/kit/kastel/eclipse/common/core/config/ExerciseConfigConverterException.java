/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.core.config;

import java.io.Serial;

public class ExerciseConfigConverterException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 3010383336620803292L;

	public ExerciseConfigConverterException(String message) {
		super(message);
	}
}
