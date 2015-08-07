/**
 * For storing constants for the server where all user information is stored.
 */
package com.example.constants;

public enum ServerInformation {
	SERVER_IP("54.186.110.240:80"),
	LOGIN_URL("http://" + SERVER_IP + "/webservice/loginAndroid.php"),
	REGISTER_URL("http://" + SERVER_IP + "/webservice/register.php"),
	STORAGE_FRONT_END_URL("http://" + SERVER_IP + "/storage/uploadFiles.php"),
	STORAGE_URL("http://" + SERVER_IP + "/storage/"),
	REQUEST_URL("http://" + SERVER_IP + "/storage/requestFiles.php"),
	IS_MED_PROF("1"),
	NOT_MED_PROF("0"),
	
	// String Constants
	MED_PROF("med_prof");
	
	String value;
	private ServerInformation(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return this.value;
	}
}
