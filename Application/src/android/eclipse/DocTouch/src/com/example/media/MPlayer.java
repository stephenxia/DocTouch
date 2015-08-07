package com.example.media;

import java.io.File;
import java.io.IOException;

import com.example.constants.MediaConst;
import com.example.doctouch.R;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class MPlayer extends FragmentActivity implements SurfaceHolder, OnClickListener {
	
	// Activity codes
	private static final int CAPTURE_EDIT_IMAGE =  0;
	
	private VideoView videoView;
	private String filePath;
	private Uri fileUri;
	private MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mplayer);
		Button captureVideoImage = (Button)findViewById(R.id.capture_video_image);
		captureVideoImage.setOnClickListener(this);
		
		// Set videoview and start
		videoView = (VideoView) findViewById(R.id.video_view);
		
		// Allow for looping.
		videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.start();
                videoView.pause();
            }
        });
		
		DisplayMetrics dm = new DisplayMetrics();
		MPlayer.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		videoView.setMinimumWidth(dm.widthPixels);
		videoView.setMinimumHeight(dm.heightPixels);
		filePath = getIntent().getExtras().getString(MediaConst.FILEPATH.toString());
		fileUri = Uri.parse(filePath);
		videoView.setVideoURI(fileUri);
		//mediaMetadataRetriever.setDataSource(MPlayer.this, fileUri);
		videoView.setMediaController(new cMediaController(MPlayer.this));
		videoView.requestFocus();
		videoView.start();
	}
	
	@Override
	public void onClick(View v) {
		
		// Begin patient video recording session
		if(v.getId() == R.id.capture_video_image) {
			
			// Pause the video and get current position in milliseconds
			videoView.pause();
			int currentPosition = videoView.getCurrentPosition();
			
			// Create new intent with path to the file and the position of in the video.
			Intent intent = new Intent(MPlayer.this, EditImageActivity.class);
			Bundle extras = new Bundle();
			extras.putString(MediaConst.FILEPATH.toString(), filePath);
			extras.putInt(MediaConst.POSITION.toString(), currentPosition);
			intent.putExtras(extras);
			startActivityForResult(intent, CAPTURE_EDIT_IMAGE);
			
			/*
			// Get the frame at time(microseconds).
			Bitmap bmFrame = mediaMetadataRetriever.getFrameAtTime(currentPosition * 1000);
			
			// Display error if nothing is in the captured frame.
			if(bmFrame == null) {
				Toast.makeText(MPlayer.this, "Error: Frame is null", Toast.LENGTH_SHORT).show();
				Log.d("Error: ", "Frame is null");
			}
			
			// Otherwise, display the image in an editor
			else {
				AlertDialog.Builder captureDialog = new AlertDialog.Builder(MPlayer.this);
				ImageView capturedImageView = new ImageView(MPlayer.this);
				capturedImageView.setImageBitmap(bmFrame);
				LayoutParams capturedImageViewLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				capturedImageView.setLayoutParams(capturedImageViewLayoutParams);
				captureDialog.setView(capturedImageView);
                captureDialog.show();
			}*/
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case CAPTURE_EDIT_IMAGE:
			if(resultCode == RESULT_OK) {
				
			}
		}
	}

	@Override
	public void addCallback(Callback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeCallback(Callback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isCreating() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setType(int type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFixedSize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSizeFromLayout() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFormat(int format) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setKeepScreenOn(boolean screenOn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Canvas lockCanvas() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Canvas lockCanvas(Rect dirty) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unlockCanvasAndPost(Canvas canvas) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Rect getSurfaceFrame() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Surface getSurface() {
		// TODO Auto-generated method stub
		return null;
	}
}