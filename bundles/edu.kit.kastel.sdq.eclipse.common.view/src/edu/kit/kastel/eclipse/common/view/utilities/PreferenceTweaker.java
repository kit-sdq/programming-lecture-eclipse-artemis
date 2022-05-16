/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.view.utilities;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.osgi.service.prefs.BackingStoreException;

import edu.kit.kastel.eclipse.common.view.activator.CommonActivator;
import edu.kit.kastel.sdq.eclipse.common.api.PreferenceConstants;

@SuppressWarnings("unused") // as not all constants are used
public final class PreferenceTweaker {

	private static final ILog log = Platform.getLog(PreferenceTweaker.class);

	private static final String JDT_IGNORE = "ignore";
	private static final String JDT_INFO = "info";
	private static final String JDT_WARNING = "warning";
	private static final String JDT_ERROR = "error";
	private static final String JDT_ENABLED = "enabled";
	private static final String JDT_DISABLED = "disabled";

	private PreferenceTweaker() {
		throw new IllegalAccessError();
	}

	public static void tweakPreferences() {
		var preferences = CommonActivator.getDefault().getPreferenceStore();
		if (!preferences.getBoolean(PreferenceConstants.GENERAL_OVERRIDE_DEFAULT_PREFERENCES)) {
			return;
		}

		log.info("Tweaking default eclipse preferences");

		tweakEditorPreferences();
		tweakJdtPreferences();
	}

	private static void tweakEditorPreferences() {
		var editorPreferences = EditorsUI.getPreferenceStore();

		// Enable Line Numbers by default
		editorPreferences.setValue(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_LINE_NUMBER_RULER, true);
	}

	private static void tweakJdtPreferences() {
		// Modify problem severity
		// Full list can be found here:
		// https://git.eclipse.org/c/platform/eclipse.platform.swt.git/plain/examples/org.eclipse.swt.examples.browser.demos/.settings/org.eclipse.jdt.core.prefs

		var jdtPreferenceNode = InstanceScope.INSTANCE.getNode(JavaCore.PLUGIN_ID);

		//
		// Not required for normal grading
		//
		jdtPreferenceNode.put(JavaCore.COMPILER_PB_MISSING_SERIAL_VERSION, JDT_IGNORE);
		jdtPreferenceNode.put(JavaCore.COMPILER_PB_POTENTIALLY_MISSING_STATIC_ON_METHOD, JDT_IGNORE);

		//
		// Probably bugs
		//
		// variable hides another variable
		jdtPreferenceNode.put(JavaCore.COMPILER_PB_FIELD_HIDING, JDT_INFO);
		jdtPreferenceNode.put(JavaCore.COMPILER_PB_LOCAL_VARIABLE_HIDING, JDT_WARNING);

		// access to static member via object-reference is discouraged
		jdtPreferenceNode.put(JavaCore.COMPILER_PB_INDIRECT_STATIC_ACCESS, JDT_WARNING);

		// method-parameters should not be re-assigned
		jdtPreferenceNode.put(JavaCore.COMPILER_PB_PARAMETER_ASSIGNMENT, JDT_WARNING);

		// show locations of auto-boxing (as unboxing can cause unexpected
		// NullPointerExceptions)
		jdtPreferenceNode.put(JavaCore.COMPILER_PB_AUTOBOXING, JDT_INFO);

		// closeables should be closed the intended way
		jdtPreferenceNode.put(JavaCore.COMPILER_PB_UNCLOSED_CLOSEABLE, JDT_INFO);
		jdtPreferenceNode.put(JavaCore.COMPILER_PB_EXPLICITLY_CLOSED_AUTOCLOSEABLE, JDT_INFO);

		// every part of the code should be reachable
		jdtPreferenceNode.put(JavaCore.COMPILER_PB_DEAD_CODE, JDT_WARNING);
		jdtPreferenceNode.put(JavaCore.COMPILER_PB_DEAD_CODE_IN_TRIVIAL_IF_STATEMENT, JDT_WARNING);

		// probably wrong variable-names used
		jdtPreferenceNode.put(JavaCore.COMPILER_PB_COMPARING_IDENTICAL, JDT_WARNING);
		jdtPreferenceNode.put(JavaCore.COMPILER_PB_POSSIBLE_ACCIDENTAL_BOOLEAN_ASSIGNMENT, JDT_WARNING);
		jdtPreferenceNode.put(JavaCore.COMPILER_PB_UNLIKELY_EQUALS_ARGUMENT_TYPE, JDT_WARNING);

		//
		// style
		//
		// every statement should contain code
		jdtPreferenceNode.put(JavaCore.COMPILER_PB_EMPTY_STATEMENT, JDT_WARNING);
		// Overriding equals without hashCode is discouraged
		jdtPreferenceNode.put(JavaCore.COMPILER_PB_MISSING_HASHCODE_METHOD, JDT_INFO);

		// if a method is used like a static method, it should be one
		jdtPreferenceNode.put(JavaCore.COMPILER_PB_MISSING_STATIC_ON_METHOD, JDT_INFO);

		// created objects may be used. Otherwise they are useless
		jdtPreferenceNode.put(JavaCore.COMPILER_PB_UNUSED_OBJECT_ALLOCATION, JDT_WARNING);

		// encourage the use of the diamond-operator
		jdtPreferenceNode.put(JavaCore.COMPILER_PB_REDUNDANT_TYPE_ARGUMENTS, JDT_INFO);

		// only exceptions thrown in a method should be declared
		jdtPreferenceNode.put(JavaCore.COMPILER_PB_UNUSED_DECLARED_THROWN_EXCEPTION, JDT_WARNING);
		jdtPreferenceNode.put(JavaCore.COMPILER_PB_UNUSED_DECLARED_THROWN_EXCEPTION_WHEN_OVERRIDING, JDT_DISABLED);

		// only required parameters should be passed to a method
		jdtPreferenceNode.put(JavaCore.COMPILER_PB_UNUSED_PARAMETER, JDT_WARNING);
		jdtPreferenceNode.put(JavaCore.COMPILER_PB_UNUSED_PARAMETER_WHEN_IMPLEMENTING_ABSTRACT, JDT_DISABLED);
		jdtPreferenceNode.put(JavaCore.COMPILER_PB_UNUSED_PARAMETER_WHEN_OVERRIDING_CONCRETE, JDT_DISABLED);

		// it should be obvious what parameter is passed to varargs
		jdtPreferenceNode.put(JavaCore.COMPILER_PB_VARARGS_ARGUMENT_NEED_CAST, JDT_ERROR);

		jdtPreferenceNode.put(JavaCore.COMPILER_PB_REDUNDANT_NULL_CHECK, JDT_INFO);

		// disable the use of @SupressWarnings
		jdtPreferenceNode.put(JavaCore.COMPILER_PB_SUPPRESS_WARNINGS, JDT_DISABLED);

		// save and load the modified preferences
		try {
			jdtPreferenceNode.sync();
			jdtPreferenceNode.flush();
		} catch (BackingStoreException e) {
			log.warn("Could not save modified eclipse preferences.", e);
		}
	}

}
