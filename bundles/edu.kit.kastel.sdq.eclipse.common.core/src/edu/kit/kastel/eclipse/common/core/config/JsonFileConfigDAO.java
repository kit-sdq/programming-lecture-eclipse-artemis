/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.core.config;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation of {@link GradingDAO} using a json file.
 *
 */
public class JsonFileConfigDAO implements GradingDAO {

	private ExerciseConfig exerciseConfig;

	private final File configFile;
	private final ObjectMapper oom = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	public JsonFileConfigDAO(File configFile) {
		this.configFile = configFile;
	}

	@Override
	public ExerciseConfig getExerciseConfig() throws IOException, ExerciseConfigConverterException {
		if (this.exerciseConfig == null)
			this.parse();
		return this.exerciseConfig;
	}

	private void parse() throws IOException, ExerciseConfigConverterException {
		this.exerciseConfig = oom.readValue(this.configFile, ExerciseConfig.class);
	}
}
