/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.core.config;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation of {@link ConfigDAO} using a json file.
 *
 */
public class JsonFileConfigDao implements ConfigDAO {

	private File configFile;
	private ExerciseConfig exerciseConfig;

	public JsonFileConfigDao(File configFile) {
		this.configFile = configFile;
	}

	@Override
	public ExerciseConfig getExerciseConfig() throws IOException {
		this.parseIfNotAlreadyParsed();
		return this.exerciseConfig;
	}

	private void parseIfNotAlreadyParsed() throws IOException {
		if (this.exerciseConfig != null) {
			return;
		}

		ExerciseConfig config = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(this.configFile,
				ExerciseConfig.class);
		this.exerciseConfig = config;
	}
}
