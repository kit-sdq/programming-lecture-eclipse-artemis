package testplugin_activateByShortcut.testConfig;

import edu.kit.kastel.sdq.eclipse.grading.core.config.ConfigDao;
import edu.kit.kastel.sdq.eclipse.grading.core.config.ExerciseConfig;
import edu.kit.kastel.sdq.eclipse.grading.core.config.JsonFileConfigDao;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class ConfigDaoTest {

	ConfigDao configDao;
	
	public ConfigDaoTest(File configFile) {
		this.configDao = new JsonFileConfigDao(configFile);
	}
	
	public void run() {
		System.out.println("Running ConfigDaoTest");
		try {
			Collection<ExerciseConfig> configs = configDao.getExerciseConfigs();
			configs.stream().forEach(config -> {
				System.out.println("  |--" + config);
				config.getRatingGroups().forEach(ratingGroup -> {
					System.out.println("    |--" + ratingGroup);
				});
				config.getMistakeTypes().forEach(mistakeType -> {
					System.out.println("    |--" + mistakeType);
				});
				
			});
		} catch (IOException e) {
			System.out.print("[Erreur - ConfigDaoTest]: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
