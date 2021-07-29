package edu.kit.kastel.sdq.eclipse.grading.client.rest;

class IDToken {

	private String idTokenValue;

	IDToken(final String idTokenValue) {
		this.idTokenValue = idTokenValue;
	}

	public String getHeaderString() {
		return "Bearer " + this.idTokenValue;
	}
}
