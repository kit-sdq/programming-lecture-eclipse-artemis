package edu.kit.kastel.sdq.eclipse.grading.core.config;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation of {@link ConfigDao} using a json file.
 *
 */
public class JsonFileConfigDao implements ConfigDao {

	private File configFile;
	private Collection<ExerciseConfig> exerciseConfigs;

	public JsonFileConfigDao(File configFile) {
		this.configFile = configFile;
	}

	@Override
	public Collection<ExerciseConfig> getExerciseConfigs() throws IOException {
		this.parseIfNotAlreadyParsed();
		return this.exerciseConfigs;
	}


	private void parseIfNotAlreadyParsed() throws IOException {
		if (this.exerciseConfigs != null) {
			return;
		}

		ExerciseConfig[] configs = new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.readValue(this.configFile, ExerciseConfig[].class);
		this.exerciseConfigs = Arrays.asList(configs);
	}
}
