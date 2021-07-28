# programming-lecture-eclipse-artemis-grading
Eclipse-Plugin for grading with the [Artemis project](https://github.com/ls1intum/Artemis)

## Update Site
The Update Site is located here: https://kit-sdq.github.io/programming-lecture-eclipse-artemis-grading/


## Working with the plugin

## Configuration File
To Configure mistake types, rating groups and whatnot, we use a config file.
See testPlugin_activateByShortcut/src/main/resources/config_v3.json for an example configuration (TODO Path will change..)


## Development


### Setting up Eclipse

1. Use the docs/workingTargetDefinition.target to create the target platform needed for this project.
2. Adjust your Run Configuration accordingly ("Plugins->Select All" will do)

### Creating a new PenaltyRule

1. Derive from PenaltyRule and implement.
2. Add a case statement in edu.kit.kastel.sdq.eclipse.grading.core.config.PenaltyRuleDeserializer.
3. use the new PenaltyRule in your config.json.


