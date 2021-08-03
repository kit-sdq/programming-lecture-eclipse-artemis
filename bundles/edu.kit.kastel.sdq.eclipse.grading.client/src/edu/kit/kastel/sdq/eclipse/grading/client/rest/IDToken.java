package edu.kit.kastel.sdq.eclipse.grading.client.rest;

/**
 * Represents the id_token, a means to authenticate to Artemis. Used in all calls to artemis in the header.
 */
class IDToken {

	private String idTokenValue;

	IDToken(final String idTokenValue) {
		this.idTokenValue = idTokenValue;
	}

	public String getHeaderString() {
		return "Bearer " + this.idTokenValue;
	}
}
