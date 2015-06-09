/**
 * For storing constants for the server where all user information is stored.
 */
package com.example.server;

public enum ServerInformation {
	SERVER_IP("10.127.79.48"),
	LOGIN_URL("http://" + SERVER_IP + "/webservice/login.php"),
	REGISTER_URL("http://" + SERVER_IP + "/webservice/register.php"),
	STORAGE_FRONT_END_URL("http://" + SERVER_IP + "/storage/randomPost.php"),
	STORAGE_URL("http://" + SERVER_IP + "/storage/"),
	REQUEST_URL("http://" + SERVER_IP + "/storage/requestFiles.php"),
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
