package com.example.doctouch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.JSON.JSONParser;
import com.example.server.ServerInformation;
import com.example.videoSession.VideoSessionActivity;

import android.support.v4.app.FragmentActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class HomeD extends FragmentActivity implements OnClickListener {
	
	// PHP tag for number of files currently in server
	private static final String TAG_NUMFILES = "numFiles";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homed);
        
        Button checkForUpdates = (Button) findViewById(R.id.check_for_new_updates);
		checkForUpdates.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(v.getId() == R.id.check_for_new_updates) {
			new checkForUpdates().execute();
		}
	}
	
	class checkForUpdates extends AsyncTask<String, String, String> {

		 /**
        * Before starting background thread Show Progress Dialog
        * */
		boolean failure = false;
		ProgressDialog pDialog;
		
		static final String appName = "DocTouch";
		
		// Initialize internal storage directory
        final File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName);

       @Override
       protected void onPreExecute() {
           super.onPreExecute();
           pDialog = new ProgressDialog(HomeD.this);
           pDialog.setMessage("Retrieving Files from Server...");
           pDialog.setIndeterminate(false);
           pDialog.setCancelable(true);
           pDialog.show();
       }

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			
			// JSON parser class
		    JSONParser jsonParser = new JSONParser();
			
		   // Check the number of files available in server
           int numFiles;
           try {
               // Building Parameters
               List<NameValuePair> params = new ArrayList<NameValuePair>();

               Log.d("request!", "starting");
               // getting product details by making HTTP request
               JSONObject json = jsonParser.makeHttpRequest(
                      ServerInformation.REQUEST_URL.toString(), "POST", params);

               // check your log for json response
               Log.d("Checking available files.", json.toString());
               numFiles = json.getInt(TAG_NUMFILES);
               
               // If there are files in the server.
               if (numFiles > 0) {
               	Log.d(numFiles + " files found!", json.toString());
               	
               	// Download all Files.
               	for(int i = 0; i < numFiles; i++) {
               		String downloadFilePath = ServerInformation.STORAGE_URL.toString() + json.getString(String.valueOf(i));
               		downloadFile(downloadFilePath);
               	}
               	Log.d("Success!", "Download finished.");
               }
               
               // Otherwise, don't do anything.
               else{
                    Log.d("No files found!", json.toString());
               }
           } catch (JSONException e) {
               e.printStackTrace();
           }

           return null;

		}
		/**
        * After completing background task Dismiss the progress dialog
        * **/
       protected void onPostExecute(String update) {
           // dismiss the dialog once product deleted
           pDialog.dismiss();
           
           Toast.makeText(HomeD.this, "Update Complete", Toast.LENGTH_LONG).show();
       }
       
       private void downloadFile(String downloadFilePath){
    	   
    	   int downloadedSize = 0;
    	   int totalSize = 0;
    	   
    	   // Create the storage directory if it does not exist
   	       if (! mediaStorageDir.exists()){
   	           if (! mediaStorageDir.mkdirs()){
   	               Log.d(appName, "failed to create directory");
   	           }
   	       }
   	       
   	       // Get the filename from the download path.
   	       String[] filesAndDir = downloadFilePath.split("/");
   	       String filename = filesAndDir[filesAndDir.length - 1];
           
           try {
               URL url = new URL(downloadFilePath);
               HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
    
               urlConnection.setRequestMethod("GET");
               urlConnection.setDoOutput(true);
    
               //connect
               urlConnection.connect();
    
               //create a new file, to save the <span id="IL_AD9" class="IL_AD">downloaded</span> file 
               File file = new File(mediaStorageDir, filename);
               Log.d("File to download", filename);
                   
               FileOutputStream fileOutput = new FileOutputStream(file);
    
               //<span id="IL_AD10" class="IL_AD">Stream</span> used for reading the data from the internet
               InputStream inputStream = urlConnection.getInputStream();
    
               //this is the total size of the file which we are downloading
               totalSize = urlConnection.getContentLength();
                
               //create a buffer...
               byte[] buffer = new byte[1024];
               int bufferLength = 0;
    
               while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                   fileOutput.write(buffer, 0, bufferLength);
                   downloadedSize += bufferLength;
               }
               
               Log.d("Downloaded Size: ", String.valueOf(downloadedSize));
               //close the output stream when complete //
               fileOutput.close();
               Log.d("Is File?", String.valueOf(file.isFile()));
               Log.d("AbsolutePath:", file.getAbsolutePath());
               
               // Scan file to make it appear to the user.
               MediaScannerConnection.scanFile(HomeD.this, new String[] {file.getAbsolutePath()}, null, null);
               /*
               runOnUiThread(new Runnable() {
                   public void run() {
                       // pb.dismiss(); // if you want close it..
                   }
               });*/         
            
           } catch (final MalformedURLException e) {
               showError("Error : MalformedURLException " + e);        
               e.printStackTrace();
           } catch (final IOException e) {
               showError("Error : IOException " + e);          
               e.printStackTrace();
           }
           catch (final Exception e) {
               showError("Error : Please <span id=\"IL_AD8\" class=\"IL_AD\">check your</span> <span id=\"IL_AD11\" class=\"IL_AD\">internet connection</span> " + e);
           }
       }
        
       void showError(final String err){
           runOnUiThread(new Runnable() {
               public void run() {
                   Toast.makeText(HomeD.this, err, Toast.LENGTH_LONG).show();
               }
           });
       }

	}
}
