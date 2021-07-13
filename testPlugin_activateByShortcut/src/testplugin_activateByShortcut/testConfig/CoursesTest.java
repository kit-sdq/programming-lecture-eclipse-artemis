package testplugin_activateByShortcut.testConfig;

import java.io.File;
import java.util.Collection;

import edu.kit.kastel.sdq.eclipse.grading.api.IArtemisGUIController;
import edu.kit.kastel.sdq.eclipse.grading.api.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.core.SystemwideController;

public class CoursesTest {

	private IArtemisGUIController guiController;

	public CoursesTest(File configFile, String artemisHost, String username, String password) {
		this.guiController = new SystemwideController(configFile, artemisHost, username, password).getArtemisGUIController();
	}

	public Collection<ICourse> getCoursesTest() {
		return this.guiController.getCourses();
	}
}
