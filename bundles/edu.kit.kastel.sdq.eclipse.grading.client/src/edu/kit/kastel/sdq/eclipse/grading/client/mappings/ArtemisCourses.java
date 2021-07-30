package edu.kit.kastel.sdq.eclipse.grading.client.mappings;

/**
 * Test for unauthorized access to artemis rest api.
 */
public class ArtemisCourses {

	private String title;

	private String path;

	private String message;

	public String getMessage() {
		return this.message;
	}

	public String getPath() {
		return this.path;
	}

	public String getTitle() {
		return this.title;
	}

	@Override
	public String toString() {
		return "ArtemisCourses{title=" + this.title + ", path=" + this.path + ", message=" + this.message + "}";
	}
}
