package edu.kit.kastel.sdq.eclipse.grading.core.artemis;

import java.util.Locale;

public class Util {

	/**
	 * Formats a double by cutting after 2 decimal points and adding "readability
	 * commas" Examples:
	 * <li>12345.6789 ==> "12,345.67"
	 * <li>25.22314 ==> "25.22"
	 * 
	 * @return a double with two decimal points
	 */
	public static String formatDouble(Double d) {
		return String.format(Locale.ENGLISH, "%,.2f", d);
	}

	/**
	 *
	 * @param d
	 * @return whether a double value is deemed as zero, by calculating
	 *         "delta_to_zero < 0.001D"
	 */
	public static boolean isZero(double d) {
		return (0D + Math.abs(d)) < 0.001D;
	}

	private Util() {
	}
}
