package com.example.media;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.UUID;

import com.example.constants.MediaConst;
import com.example.doctouch.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class EditImageActivity extends FragmentActivity implements OnClickListener {
	private EditImageView drawView;
	private ImageButton currPaint, drawBtn, eraseBtn, saveBtn;
	private float smallBrush, mediumBrush, largeBrush;
	
	// Media directory.
	private File mediaStorageDir;
	
	// extras
	Bundle extras;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editimageactivity);
		
		drawView = (EditImageView)findViewById(R.id.drawing);
		LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
		System.out.println(paintLayout.getChildCount());
		currPaint = (ImageButton)paintLayout.getChildAt(0);
		currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
		smallBrush = getResources().getInteger(R.integer.small_size);
		mediumBrush = getResources().getInteger(R.integer.medium_size);
		largeBrush = getResources().getInteger(R.integer.large_size);
		drawView.setBrushSize(mediumBrush);
		
		// Get the extras and extract bitmap of video frame.
		extras = getIntent().getExtras();
		int currentPosition = extras.getInt(MediaConst.POSITION.toString());
		String filePath = extras.getString(MediaConst.FILEPATH.toString());
		
		// Get the frame at time(microseconds).
		MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
		mediaMetadataRetriever.setDataSource(EditImageActivity.this, Uri.parse(filePath));
		Bitmap bmFrame = mediaMetadataRetriever.getFrameAtTime(currentPosition * 1000);
					
		// Display error if nothing is in the captured frame.
		if(bmFrame == null) {
			Toast.makeText(EditImageActivity.this, "Error: Frame is null", Toast.LENGTH_SHORT).show();
			Log.d("Error: ", "Frame is null");
			
			Intent result = new Intent();
			setResult(RESULT_CANCELED, result);
			finish();
		}
		drawView.setCanvasBitmap(bmFrame);
		
		drawBtn = (ImageButton)findViewById(R.id.draw_btn);
		drawBtn.setOnClickListener(this);
		
		eraseBtn = (ImageButton)findViewById(R.id.erase_btn);
		eraseBtn.setOnClickListener(this);
		
		saveBtn = (ImageButton)findViewById(R.id.save_btn);
		saveBtn.setOnClickListener(this);
		
		// Initialize internal storage directory
        mediaStorageDir = new File(MediaConst.MEDIA_DIR.toString());
	}
	
	/**
	 * paintClicked:
	 * 
	 * Use selected color.
	 * 
	 * @param view
	 */
	public void paintClicked(View view) {
		
		drawView.setErase(false);
		drawView.setBrushSize(drawView.getLastBrushSize());
		
		//update color
		if(view!=currPaint){
			ImageButton imgView = (ImageButton)view;
			String color = view.getTag().toString();
			drawView.setColor(color);
			imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
			currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
			currPaint=(ImageButton)view;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		//draw button selected
		if(v.getId()==R.id.draw_btn){
			final Dialog brushDialog = new Dialog(this);
			brushDialog.setTitle("Brush size:");
			brushDialog.setContentView(R.layout.brush_chooser);
			
			ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
			smallBtn.setOnClickListener(new OnClickListener(){
			    @Override
			    public void onClick(View v) {
			        drawView.setBrushSize(smallBrush);
			        drawView.setLastBrushSize(smallBrush);
			        drawView.setErase(false);
			        brushDialog.dismiss();
			    }
			});
			
			ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
			mediumBtn.setOnClickListener(new OnClickListener(){
			    @Override
			    public void onClick(View v) {
			        drawView.setBrushSize(mediumBrush);
			        drawView.setLastBrushSize(mediumBrush);
			        drawView.setErase(false);
			        brushDialog.dismiss();
			    }
			});
			 
			ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
			largeBtn.setOnClickListener(new OnClickListener(){
			    @Override
			    public void onClick(View v) {
			        drawView.setBrushSize(largeBrush);
			        drawView.setLastBrushSize(largeBrush);
			        drawView.setErase(false);
			        brushDialog.dismiss();
			    }
			});
			
			brushDialog.show();
		}
		
		// Erase button selected: Choose size
		else if(v.getId()==R.id.erase_btn){
			final Dialog brushDialog = new Dialog(this);
			brushDialog.setTitle("Eraser size:");
			brushDialog.setContentView(R.layout.brush_chooser);
			
			ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
			smallBtn.setOnClickListener(new OnClickListener(){
			    @Override
			    public void onClick(View v) {
			        drawView.setErase(true);
			        drawView.setBrushSize(smallBrush);
			        brushDialog.dismiss();
			    }
			});
			ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
			mediumBtn.setOnClickListener(new OnClickListener(){
			    @Override
			    public void onClick(View v) {
			        drawView.setErase(true);
			        drawView.setBrushSize(mediumBrush);
			        brushDialog.dismiss();
			    }
			});
			ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
			largeBtn.setOnClickListener(new OnClickListener(){
			    @Override
			    public void onClick(View v) {
			        drawView.setErase(true);
			        drawView.setBrushSize(largeBrush);
			        brushDialog.dismiss();
			    }
			});
			
			brushDialog.show();
		}
		
		// Save drawing
		else if(v.getId()==R.id.save_btn){
			AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
			saveDialog.setTitle("Save drawing");
			saveDialog.setMessage("Save drawing?");
			saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which){
			    	ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			    	drawView.getCanvasBitmap().compress(Bitmap.CompressFormat.PNG, 100, bytes);
			    	
			    	// Create the storage directory if it does not exist
				    if (! mediaStorageDir.exists()){
				        if (! mediaStorageDir.mkdirs()){
				            Log.d(MediaConst.APP_NAME.toString(), "failed to create directory");
				        }
				    }

				    // Create new file
			    	File fileIn = new File(mediaStorageDir, "test.png");
			    	
					try {
				    	fileIn.createNewFile();
				    	
				    	//write the bytes in file
				    	FileOutputStream fileOut;
						fileOut = new FileOutputStream(fileIn);
						fileOut.write(bytes.toByteArray());
						
						fileOut.close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.d(MediaConst.APP_NAME.toString(), "Error saving file.");
					}
			    	
			    }
			});
			saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which){
			        dialog.cancel();
			    }
			});
			saveDialog.show();
		}
	}
}