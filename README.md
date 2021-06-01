# programming-lecture-eclipse-artemis-grading
Eclipse-Plugin for grading with the artemis project


## Configuration
See testPlugin_activateByShortcut/src/main/resources/config.json for an example configuration (TODO Path will change..)

## Extending

### Creating a new PenaltyRule

1. Derive from PenaltyRule and implement.
2. Add a case statement in JsonConfigFileDeserializer.java. (TODO make easier?).
