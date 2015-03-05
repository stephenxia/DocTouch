/**
 * For storing constants for the server where all user information is stored.
 */
package com.example.server;

public enum ServerInformation {
	LOGIN_URL("http://192.168.2.11:80/webservice/login.php"),
	REGISTER_URL("http://192.168.2.11:80/webservice/register.php"),
	IS_MED_PROF("1"),
	NOT_MED_PROF("0");
	
	String value;
	private ServerInformation(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return this.value;
	}
}
