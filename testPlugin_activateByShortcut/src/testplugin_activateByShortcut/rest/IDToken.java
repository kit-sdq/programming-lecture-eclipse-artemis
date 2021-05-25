package testplugin_activateByShortcut.rest;

class IDToken {

	private String id_token;
	
	IDToken(final String id_token) {
		this.id_token = id_token;
	}
	
	public String getHeaderString() {
		return "Bearer " + id_token;
	}
}
