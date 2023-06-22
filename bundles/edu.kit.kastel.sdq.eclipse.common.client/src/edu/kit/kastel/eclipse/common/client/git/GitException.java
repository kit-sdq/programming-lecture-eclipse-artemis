/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.client.git;

import java.io.Serial;

public class GitException extends Exception {
	@Serial
	private static final long serialVersionUID = 3387200112674404734L;

	public GitException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public GitException(String msg) {
		super(msg);
	}
}