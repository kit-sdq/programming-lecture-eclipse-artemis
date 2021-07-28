package edu.kit.kastel.sdq.eclipse.grading.client.rest;

class IDToken {

	private String idToken;
	
	IDToken(final String idToken) {
		this.idToken = idToken;
	}
	
	public String getHeaderString() {
		return "Bearer " + idToken;
	}
}
