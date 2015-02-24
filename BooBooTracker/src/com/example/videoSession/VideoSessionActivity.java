package com.example.videoSession;

import com.example.boobootracker.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.VideoView;

public class VideoSessionActivity extends FragmentActivity implements OnClickListener{
	
	// Activity codes.
	static final int REQUEST_VIDEO_CAPTURE = 1;
	
	// Video Viewer
	private VideoView mVideoView;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_session);
        
        mVideoView = (VideoView) findViewById(R.id.video_view);
        
        Button takeVideo = (Button) findViewById(R.id.take_video_session);
		takeVideo.setOnClickListener(this);
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case REQUEST_VIDEO_CAPTURE:
			
			// If video capture was successful
			if(resultCode == RESULT_OK) {
				Uri videoUri = data.getData();
				mVideoView.setVideoURI(videoUri);
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.take_video_session) {
			
			// Create intent to take video.
			Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			
			// Checks for the first activity component that can handle the event.  Only
			// proceed to start activity for video if there is an app that can take video.
		    if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
		        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
		    }
			
		}
		
	}
}
