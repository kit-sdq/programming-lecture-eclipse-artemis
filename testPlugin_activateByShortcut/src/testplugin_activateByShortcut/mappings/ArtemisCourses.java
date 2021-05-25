package testplugin_activateByShortcut.mappings;

/**
 * Test for unauthorized access to artemis rest api.
 */
public class ArtemisCourses {
	
	private String title;
	
	private String path;
	
	private String message;

	public String getTitle() {
		return title;
	}

	public String getPath() {
		return path;
	}

	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return "ArtemisCourses{title=" + title + ", path=" + path + ", message=" + message + "}";
	}
}
