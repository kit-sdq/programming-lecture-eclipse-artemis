/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.client.rest;

public class JWTTokenUtils {
	private JWTTokenUtils() {
		throw new IllegalAccessError();
	}

	public static boolean isJWTToken(String storedPasswordOrToken) {
		return storedPasswordOrToken != null && !storedPasswordOrToken.isBlank()
				&& storedPasswordOrToken.matches("^([a-zA-Z0-9_=]+)\\.([a-zA-Z0-9_=]+)\\.([a-zA-Z0-9_\\-\\+\\/=]*)");
	}
}
