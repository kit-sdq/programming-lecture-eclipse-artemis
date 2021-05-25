package testplugin_activateByShortcut;

import java.util.Scanner;

public class CredentialsGetter {

	private CredentialsGetter() {}
	
	public static Pair<String, String> getCredentials() {
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter username- ");
		String username = sc.next();
		System.out.print("Enter password- ");
		String password= sc.next();
		System.out.print("heres ya creds");
		return new Pair<String, String>(username, password);
	}
}
