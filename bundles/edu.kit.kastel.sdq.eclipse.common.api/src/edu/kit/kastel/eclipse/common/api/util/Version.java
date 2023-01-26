/* Licensed under EPL-2.0 2023. */
package edu.kit.kastel.eclipse.common.api.util;

public record Version(int major, int minor, int micro) implements Comparable<Version> {

	public static Version fromString(String versionString) {
		String[] parts = versionString.split("\\.");
		if (parts.length != 3)
			throw new IllegalArgumentException("Provided version string does not match X.Y.Z : " + versionString);
		return new Version(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
	}

	@Override
	public int compareTo(Version o) {
		int majorCompare = Integer.compare(major, o.major);
		if (majorCompare != 0)
			return majorCompare;

		int minorCompare = Integer.compare(minor, o.minor);
		if (minorCompare != 0)
			return minorCompare;

		return Integer.compare(micro, o.micro);
	}

	@Override
	public String toString() {
		return "%d.%d.%d".formatted(major, minor, micro);
	}

}