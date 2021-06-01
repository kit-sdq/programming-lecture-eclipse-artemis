package edu.kit.kastel.sdq.eclipse.grading.core.config;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;


public class JsonFileConfigDao implements ConfigDao {

	private File configFile;
	private JsonConfigFileMapped jsonConfigFileMapped;
	
	public JsonFileConfigDao(File configFile) {
		this.configFile = configFile;
	}

	@Override
	public Collection<ExerciseConfig> getExerciseConfigs() throws IOException {
		// TODO Auto-generated method stub
		parseIfNotAlreadyParsed();
		return jsonConfigFileMapped.getExerciseConfigs();
	}

	
	private void parseIfNotAlreadyParsed() throws IOException {
		if (this.jsonConfigFileMapped == null) {
			SimpleModule module = new SimpleModule("JsonConfigFileDeserializer");
			module.addDeserializer(JsonConfigFileMapped.class, new JsonConfigFileDeserializer());
			this.jsonConfigFileMapped = new ObjectMapper().registerModule(module).readValue(configFile, JsonConfigFileMapped.class);
		}
	}

}
