This section is about the development in the grading (but also for the
student) edition.

## Setting up Eclipse

0.  Use [Eclipse 2023-03](https://www.eclipse.org/downloads/packages/)
    for development.

1.  In general, you can use any distribution of eclipse. We\'d suggest
    to you to use \"eclipse for eclipse committers\" for Plugin
    development.

2.  Target Platform: Install the development section of the [Update
    Site](https://kit-sdq.github.io/programming-lecture-eclipse-artemis/).

3.  Open project
    -   Clone
            [this](https://github.com/kit-sdq/programming-lecture-eclipse-artemis)
            repository into your eclipse workspace.
    -   Use File \> Open Project from Filesystem to open the
            project.
    -   Hit the \"Directory\...\" button and select the repository
            cloned two steps ago. Now some modules should show up below.
    -   Hit \"Select All\" (which might not actually select all) and
            then \"Finish\".
    -   You now have the project imported. You might see some
            errors. The errors for the \"jvm\" module can be ignored.
            The project builds fine anyways.

4. Target Platform Configuration:
	- Go to Window > Preferences > Plug-In Development > Target Platform.
	- Add a new Target Platform > Select Default > Click Finish
	- Activate the Target Platform (Checkbox)

5. Run Configuration: Create a new eclipse run configuration. Do not edit anything. Just start it and click continue if eclipse shows some warnings.

## Architecture

The architectural idea is based on having three plugins and an API
plugin over which those plugins communicate. That allows for more easily
exchanging view, core/ Backend or client and also clearly defines
borders, making parallel work easier and reducing coupling.

### Core / Backend

Our Backend (core package) provides functionality for

-   managing annotations
-   calculating penalties
-   serializing and deserializing annotations (via artemis client as a
    network interface)
-   mapping plugin-internal state to artemis-internal state
-   keeping track of state

### Artemis Client

The Artemis Client provides certain calls to artemis needed by the
Backend.

### GUI

Here some rules / guides for UI development.

#### Creating new view elements

New view elements (buttons, tabs, etc.) should be added to the
*ArtemisGradingView* class. Every tab has got his own method (e.g
*createBacklogTab(\...)* ).


:warning:

If the new view element is state-dependent. You will have to update the
*updateState()* method. If you create new views use the [Window
Builder](https://www.eclipse.org/windowbuilder/).

#### New calls to the Backend

New calls to the Backend can be realized through the
*ArtemisViewController* class. Then call the method in the view using
the *ArtemisViewController*.

When the class is getting to messy, it would be a good idea to separate
the calls according to the Backend controllers

#### Changing Preferences

The preference page is defined in the *ArtemisPreferencesPage* class. A
new field can be added in the *createFieldEditors()* method. The initial
values are set in the *PreferenceInitializer* class.

An example with the field for the absolute config path:

```java
public void createFieldEditors() {
    var field = new FileFieldEditor(PreferenceConstants.ABSOLUTE_CONFIG_PATH, I18N().config(), parent);
    this.addField(field);
}
```

#### Adding marker attributes

A new attribute to the marker can be added in the plugin.xml. If the
field should appear in the Assessment Annotation View, a class needs to
be created for the field and it must be added to the
*markerSupportGenerator* in the plugin.xml.

To make the name of the attribute easy to change, it should be defined
as constant in the *AssessmentUtilities* class. The attribute should be
set in the *addAssessmentAnnotaion(\...)* method and the
*createMarkerForAnnotation(\...)* method in the *ArtemisViewController*
class.

For examples just look in the plugin.xml at the
*org.eclipse.ui.ide.markerSupport* extension and the
*edu.kit.kastel.eclipse.grading.view.marker* package for the field
classes.

### Creating a new PenaltyRule

1.  Add a Class derived from
    *edu.kit.kastel.eclipse.grading.core.model.PenaltyRule*

2. Add a Constructor for that class in *edu.kit.kastel.eclipse.grading.core.config.PenaltyRuleDeserializer.PenaltyRuleType*.
    * Note that herein, you have access to the penaltyRule\'s
        JsonNode, so you may fetch values you define in your config to
        construct your PenaltyRule:

```java
public enum PenaltyRuleType {
        //Need to add a new enum value with a short Name (that must be used in the config file) and a constructor based on the json node.
        THRESHOLD_PENALTY_RULE_TYPE (ThresholdPenaltyRule.SHORT_NAME, ThresholdPenaltyRule::new),
        CUSTOM_PENALTY_RULE_TYPE (CustomPenaltyRule.SHORT_NAME, penaltyRuleNode -> new CustomPenaltyRule()),
        MY_NEW_PENALTY_RULE_TYPE (MyNewPenaltyRule.SHORT_NAME, penaltyRuleNode -> new MyNewPenaltyRule(penaltyRuleNode));
```

3.  use the new PenaltyRule in your config.json:

``` json
"mistakeTypes" : [
    {
        "shortName": "idk",
        "button": "MyMistakeType",
        "message": "You made a grave mistake.",
        "penaltyRule": {
            "shortName": "myNewPenaltyRule",
            "penalty": 5,
            "penaltyOnMoreThanThreshold": 500,
            "threshold": 4
        },
        "appliesTo": "style"
    }
]
```

### Controllers

There are three Controllers:

- The AssessmentController controlls a single assessment in terms of managing annotations. It provides Methods like
    -   *addAnnotation(..)*
    -   *getAnnotations()*
    -   *resetAndRestartAssessment()*
    -   *\...*

- The ArtemisController handles artemis-related stuff, including
    -   managing Locks and *Feedbacks* which contain data gotten
            from locking a submission.
    -   retrieving information about Courses, Submissions,
            Exercises, Exams, \... from the artemis client
    -   starting, saving and submitting assessments.

-   The SystemwideController holds and manages the Backend state. It
    acts as the main interface to the GUI. All calls relevant to our
    Backend state (see section about the Backend state machine) go
    through here.
