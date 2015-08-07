package com.example.constants;

import java.io.File;

import android.os.Environment;

public enum MediaConst {
	APP_NAME("DocTouch"),
	MEDIA_DIR(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + APP_NAME),

	// String Constants
	FILEPATH("filePath"),
	MEDIA_SENT("Media Sent."),
	
	// Used in MPlayer.class and EditImageActivity.class
	IMAGE("Image"),
	POSITION("Position");
	
	private String strValue;
	private MediaConst(String value) {
		this.strValue = value;
	}
	
	
	@Override
	public String toString() {
		return this.strValue;
	}
	
}
