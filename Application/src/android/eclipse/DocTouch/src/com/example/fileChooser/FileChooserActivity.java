package com.example.fileChooser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.constants.MediaConst;
import com.example.constants.ServerInformation;
import com.example.doctouch.R;
import com.example.media.MPlayer;
import com.example.videoSession.VideoSessionActivity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class FileChooserActivity extends ListActivity {
    
    private File currentDir;// = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName);
    private FileArrayAdapter adapter;
    
    // Status of user (medical professional or not).
    String med_prof;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        med_prof = getIntent().getExtras().getString(ServerInformation.MED_PROF.toString());
        currentDir = new File(MediaConst.MEDIA_DIR.toString());
        
        // Create the storage directory if it does not exist
	    if (!currentDir.exists()){
	    	if (!currentDir.mkdirs()){
	    		Log.d(MediaConst.APP_NAME.toString(), "failed to create directory");
	        }
	    }
	    
        fill(currentDir);
    }
    private void fill(File f)
    {
        File[]dirs = f.listFiles();
         this.setTitle("Current Dir: "+f.getName());
         List<Option>dir = new ArrayList<Option>();
         List<Option>fls = new ArrayList<Option>();
         try{
             for(File ff: dirs)
             {
                if(ff.isDirectory())
                    dir.add(new Option(ff.getName(),"Folder",ff.getAbsolutePath()));
                else
                {
                    fls.add(new Option(ff.getName(),"File Size: "+ff.length(),ff.getAbsolutePath()));
                }
             }
         }catch(Exception e)
         {
             
         }
         Collections.sort(dir);
         Collections.sort(fls);
         dir.addAll(fls);
         if(!f.getName().equalsIgnoreCase(MediaConst.APP_NAME.toString()))
             dir.add(0,new Option("..","Parent Directory",f.getParent()));
         adapter = new FileArrayAdapter(FileChooserActivity.this, R.layout.file_chooser,dir);
         this.setListAdapter(adapter);
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        Option o = adapter.getItem(position);
        if(o.getData().equalsIgnoreCase("folder")||o.getData().equalsIgnoreCase("parent directory")){
                currentDir = new File(o.getPath());
                fill(currentDir);
        }
        else
        {
            onFileClick(o);
        }
    }
    
    /**
     * onFileClick:
     * 
     * File is chosen.
     * 
     * @param o
     */
    private void onFileClick(Option o)
    {
    	/*
    	Bundle bundle = new Bundle();
    	bundle.putString(MediaConst.FILEPATH.toString(), o.getPath());
    	Intent intent = new Intent(FileChooserActivity.this, MPlayer.class);
    	
    	intent.putExtras(bundle);
    	startActivity(intent);*/
    	final String filePath = o.getPath();
    	
    	if(med_prof.equals(ServerInformation.NOT_MED_PROF.toString())) {
    		new AlertDialog.Builder(this)
    		.setMessage(
    				"What Would you like to do?")
    		.setPositiveButton("Send",
    				new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface arg0, int arg1) {
						
    						// Finish activity
    						Bundle returnData = new Bundle();
    						returnData.putString(MediaConst.FILEPATH.toString(), filePath);
    						Intent intent = new Intent();
    						intent.putExtras(returnData);
    						setResult(RESULT_OK, intent);
    						finish();
    					}
    			})
    		.setNeutralButton("Review Video",
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						// Begin viewing video
				    	Bundle bundle = new Bundle();
				    	bundle.putString(MediaConst.FILEPATH.toString(), filePath);
				    	Intent intent = new Intent(FileChooserActivity.this, MPlayer.class);
				    	
				    	intent.putExtras(bundle);
				    	startActivity(intent);
					}
				})
			.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						
						// Don't do anything if cancelled.
						return;
					}
				}).show();
        //Toast.makeText(this, "File Clicked: "+o.getName(), Toast.LENGTH_SHORT).show();
    	}
    	else if (med_prof
    			.equals(ServerInformation.IS_MED_PROF.toString())) {
    		// Begin viewing video
	    	Bundle bundle = new Bundle();
	    	bundle.putString(MediaConst.FILEPATH.toString(), filePath);
	    	Intent intent = new Intent(FileChooserActivity.this, MPlayer.class);
	    	
	    	intent.putExtras(bundle);
	    	startActivity(intent);
    	}
    }
}