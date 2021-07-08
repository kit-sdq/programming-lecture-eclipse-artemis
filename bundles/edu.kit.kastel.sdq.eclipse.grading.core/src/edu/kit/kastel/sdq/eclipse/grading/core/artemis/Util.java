package edu.kit.kastel.sdq.eclipse.grading.core.artemis;

public class Util {

	/**
	 *
	 * @return a double with two decimal points
	 */
	public static String formatDouble(Double d) {
		return String.format("%,.2f", d);
	}

	public static boolean isZero(double d) {
		return  (0D + Math.abs(d)) < 0.001D;
	}

	private Util() {}
}
