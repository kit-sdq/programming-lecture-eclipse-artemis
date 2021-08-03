package edu.kit.kastel.sdq.eclipse.grading.client.git;

public class GitException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public GitException(String msg) {
		super(msg);
	}

	public GitException(String msg, Throwable cause) {
		super(msg, cause);
	}
}