/**
 * For storing constants for the server where all user information is stored.
 */
package com.example.server;

public enum ServerInformation {
	LOGIN_URL("http://10.119.77.64:80/webservice/login.php"),
	REGISTER_URL("http://10.119.77.64:80/webservice/register.php"),
	STORAGE_FRONT_END_URL("http://10.119.77.64:80/storage/randomPost.php"),
	STORAGE_URL("http://10.119.77.64:80/storage/"),
	REQUEST_URL("http://10.119.77.64:80/storage/requestFiles.php"),
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
