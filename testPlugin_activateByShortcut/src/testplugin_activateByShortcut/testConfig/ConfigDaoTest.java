package testplugin_activateByShortcut.testConfig;

import java.io.IOException;

import edu.kit.kastel.sdq.eclipse.grading.core.config.ConfigDao;
import edu.kit.kastel.sdq.eclipse.grading.core.config.ExerciseConfig;

public class ConfigDaoTest {

	ConfigDao configDao;

	public ConfigDaoTest(ConfigDao configDao) {
		this.configDao = configDao;
	}

	public void run() {
		System.out.println("Running ConfigDaoTest");
		try {
			ExerciseConfig config = this.configDao.getExerciseConfig();
			System.out.println("Gotten Config: " + config);
			System.out.println("  |--" + config);
			config.getRatingGroups().forEach(ratingGroup -> {
				System.out.println("    |--" + ratingGroup);
			});
			config.getMistakeTypes().forEach(mistakeType -> {
				System.out.println("    |--" + mistakeType);
			});
		} catch (IOException e) {
			System.out.print("[Erreur - ConfigDaoTest]: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
