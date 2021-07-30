package edu.kit.kastel.sdq.eclipse.grading.client.git;

public class GitException extends Exception {

	public GitException(String msg) {
		super(msg);
	}

	public GitException(String msg, Throwable cause) {
		super(msg, cause);
	}
}