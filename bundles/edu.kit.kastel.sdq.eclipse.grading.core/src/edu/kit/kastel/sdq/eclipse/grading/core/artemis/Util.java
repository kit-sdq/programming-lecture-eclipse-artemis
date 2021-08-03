package edu.kit.kastel.sdq.eclipse.grading.core.artemis;

public class Util {

	/**
	 * TODO macht das hier Kommas? ==> Mit Punkt trennen und Locale einstellen
	 * @return a double with two decimal points
	 */
	public static String formatDouble(Double d) {
		return String.format("%,.2f", d);
	}

	/**
	 *
	 * @param d
	 * @return whether a double value is deemed as zero, by calculating "delta_to_zero < 0.001D"
	 */
	public static boolean isZero(double d) {
		return  (0D + Math.abs(d)) < 0.001D;
	}

	private Util() {}
}
