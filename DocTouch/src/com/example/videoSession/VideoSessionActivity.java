package com.example.videoSession;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.JSON.JSONParser;
import com.example.doctouch.Home;
import com.example.doctouch.HomeD;
import com.example.doctouch.Login;
import com.example.doctouch.R;
import com.example.server.ServerInformation;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoSessionActivity extends FragmentActivity implements OnClickListener{
	
	// Activity codes.
	static final int REQUEST_TAKE_VIDEO_SESSION = 1;
	static final int REQUEST_PICK_VIDEO = 2;
	
    // JSON element ids from repsonse of php script:
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
	
	// Media type codes
	static final int MEDIA_TYPE_IMAGE = 1;
	static final int MEDIA_TYPE_VIDEO = 2;
	
	// Video Viewer
	private VideoView mVideoView;
	
	// Environment and application variables
	static Context context;
	static final String appName = "DocTouch";
	
	// Internal storage directory.
	static File mediaStorageDir;
	
	// Stores internal captured media
	static Uri fileUri;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_session);
        
        // Initialize application context
        context = this;
        
        // Initialize internal storage directory
        mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName);
        
        mVideoView = (VideoView) findViewById(R.id.video_view);
        
        Button takeVideo = (Button) findViewById(R.id.request_take_video_session);
		takeVideo.setOnClickListener(this);
		
		Button reviewVideosToSend = (Button) findViewById(R.id.request_pick_video);
		reviewVideosToSend.setOnClickListener(this);
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case REQUEST_TAKE_VIDEO_SESSION:
			
			// If video capture was successful
			if(resultCode == RESULT_OK) {
				
				// Scan file to make it appear to the user.
				String savedFilePath = getAbsPathFromURI(VideoSessionActivity.this, fileUri);
				Log.d("Saved Video", savedFilePath);
	            MediaScannerConnection.scanFile(VideoSessionActivity.this, new String[] {getAbsPathFromURI(VideoSessionActivity.this, fileUri)}, null, null);
			}
			
			// Otherwise, just begin the recording session again.
			else if(resultCode == RESULT_CANCELED) {
				new AlertDialog.Builder(this)
				.setMessage(
						"Do you want to continue current recording session?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								// Begin another video recording session.
								beginVideoRecordingSession();
							}
						})
				.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								// Go back to this activity's main screen.
								Toast.makeText(VideoSessionActivity.this, "The recording session was cancelled.", Toast.LENGTH_SHORT).show();
							}
						}).show();
			}
			break;
			
		// For now, just send the video to storage.
		case REQUEST_PICK_VIDEO:
			if(resultCode == RESULT_OK) {
				final String filePath = getAbsPathFromURI(VideoSessionActivity.this, data.getData());
				new Thread(new Runnable() {
					public void run() {
						fileUpload(filePath);
					}
				}).start();
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		
		// Begin patient video recording session
		if(v.getId() == R.id.request_take_video_session) {
			beginVideoRecordingSession();
		}
		else if(v.getId() == R.id.request_pick_video) {
			beginVideoGallery();
		}
	}
	
	/**
	 * beginVideoRecordingSession:
	 * Access the camera to begin the video recording session.
	 */
	public void beginVideoRecordingSession() {
		// Uri fileUri;
		
		// Create intent to take video.
		Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
		takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		
		// Checks for the first activity component that can handle the event.  Only
		// proceed to start activity for video if there is an app that can take video.
		if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO_SESSION);
		}
	}
	
	/**
	 * beginVideoGallery:
	 * Access the gallery where all patient videos recorded through this application are saved.  The
	 * patient should select a video/videos that will be sent to their doctor.
	 */
	public void beginVideoGallery() {
		
		Intent videoChooseIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
		
		//Intent videoChooseIntent = new Intent(Intent.ACTION_GET_CONTENT);
		//videoChooseIntent.setType("video/*");
		startActivityForResult(videoChooseIntent, REQUEST_PICK_VIDEO);
	}
	
	/**
	 * getOutputMediaFileUri:
	 * Create a file Uri for saving an image or video 
	 */
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}
	
	/**
	 * getOutputMediaFile:
	 * Create a File for saving an image or video
	 */
	private static File getOutputMediaFile(int type){
		
        // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d(appName, "failed to create directory");
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	
	/**
	 * getAbsPathFromURI:
	 * 
	 * Returns the absolute path to the file contained in the uri.
	 * 
	 * @param context
	 * @param contentUri
	 * @return
	 */
	public String getAbsPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		String filePath = "";
		try { 
			String[] proj = { MediaStore.Images.Media.DATA };
		    cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
		    
		    // Source is not a local file.
		    if(cursor != null) {
		    	int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		    	cursor.moveToFirst();
		    	filePath = cursor.getString(column_index);
		    }
		    
		    // Source is a local file.
		    else {
		    	filePath = contentUri.getPath();
		    }
		} finally {
			if (cursor != null) {
		      cursor.close();
		    }
		}
		return filePath;
	}
	
	class uploadMedia extends AsyncTask<String, Void, String> {
		
		boolean failure = false;
		
		/**
         * Before starting background thread Show Progress Dialog
         **/
		ProgressDialog pDialog;
		
       @Override
       protected void onPreExecute() {
           super.onPreExecute();
           pDialog = new ProgressDialog(VideoSessionActivity.this);
           pDialog.setMessage("Uploading...");
           pDialog.setIndeterminate(false);
           pDialog.setCancelable(true);
           pDialog.show();
       }

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			 // Check for success tag
           int success;
           int numFiles = args.length;
           for(int i = 0; i < numFiles; i++) {
        	   try {
        		   // Building Parameters
        		   List<NameValuePair> params = new ArrayList<NameValuePair>();
        		   params.add(new BasicNameValuePair("file", args[i]));

        		   Log.d("Upload request!", "starting");
        		   // getting product details by making HTTP request
        		   JSONObject json = jsonParser.makeHttpRequest(ServerInformation.STORAGE_FRONT_END_URL.toString(), "POST", params);

        		   // check your log for json response
        		   Log.d("Upload attempt", json.toString());

        		   // json success tag
        		   success = json.getInt(TAG_SUCCESS);
        		   if (success == 1) {
        			   Log.d("Upload Successful!", json.toString());
        			   return json.getString(TAG_MESSAGE);
        		   }
        		   else {
        			   Log.d("Upload Failure!", json.getString(TAG_MESSAGE));
        			   return json.getString(TAG_MESSAGE);
        		   }
        	   }
        	   catch (JSONException e) {
        		   e.printStackTrace();
        	   }
        	}
           return null;
		}
		/**
        * After completing background task Dismiss the progress dialog
        * **/
       protected void onPostExecute(String file_url) {
    	   
    	   // dismiss the dialog once product deleted
           pDialog.dismiss();
           if (file_url != null) {
           	Toast.makeText(VideoSessionActivity.this, file_url, Toast.LENGTH_LONG).show();
           }
       }
	}
	
	/**
	 * fileUpload:
	 * 
	 * Upload the file given in the input to the server.
	 * 
	 * @param path
	 */
	
	private void fileUpload(String path){
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        DataInputStream inStream = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024;
        String urlString = ServerInformation.STORAGE_FRONT_END_URL.toString();
        
        //ProgressDialog dialog = ProgressDialog.show(VideoSessionActivity.this, "", "Uploading file...", true);
        
        try {
        	//------------------ CLIENT REQUEST
        	FileInputStream fileInputStream = new FileInputStream(new File(path));
        	
        	// open a URL connection to the Servlet
        	URL url = new URL(urlString);
         
        	// Open a HTTP connection to the URL
        	conn = (HttpURLConnection) url.openConnection();
         
        	// Allow Inputs
        	conn.setDoInput(true);
         
        	// Allow Outputs
        	conn.setDoOutput(true);
         
        	// Don't use a cached copy.
        	conn.setUseCaches(false);
        	
        	// Use a post method.
        	conn.setRequestMethod("POST");
        	conn.setRequestProperty("Connection", "Keep-Alive");
        	conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
        	dos = new DataOutputStream( conn.getOutputStream() );
        	dos.writeBytes(twoHyphens + boundary + lineEnd);
        	dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + path + "\"" + lineEnd);
        	dos.writeBytes(lineEnd);
         
        	// create a buffer of maximum size
        	bytesAvailable = fileInputStream.available();
        	bufferSize = Math.min(bytesAvailable, maxBufferSize);
        	buffer = new byte[bufferSize];
         
        	// read file and write it into form...
        	bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        	while (bytesRead > 0) {
        		dos.write(buffer, 0, bufferSize);
        		bytesAvailable = fileInputStream.available();
        		bufferSize = Math.min(bytesAvailable, maxBufferSize);
        		bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        	}
        	
        	// send multipart form data necesssary after file data...
        	dos.writeBytes(lineEnd);
        	dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
         
        	// close streams
        	Log.e("Debug","File is written");
        	fileInputStream.close();
        	dos.flush();
        	dos.close();
        }
        catch (MalformedURLException ex) {
        	//dialog.dismiss();
        	Log.e("Debug", "error: " + ex.getMessage(), ex);
        }
        catch (IOException ioe) {
        	//dialog.dismiss();
        	Log.e("Debug", "error: " + ioe.getMessage(), ioe);
        }
        
        //------------------ read the SERVER RESPONSE
        try {
        	inStream = new DataInputStream (conn.getInputStream());
            String str;
 
            while ((str = inStream.readLine()) != null) {
            	Log.e("Debug","Server Response "+str);
            }
            inStream.close();
        }
        catch (IOException ioex) {
        	//dialog.dismiss();
            Log.e("Debug", "error: " + ioex.getMessage(), ioex);
        }
        //dialog.dismiss();
    }
	
	/*
	public class AsyncHttpPostTask extends AsyncTask<File, Void, String> {

	    private final String TAG = AsyncHttpPostTask.class.getSimpleName();
	    private String server;

	    public AsyncHttpPostTask(final String server) {
	        this.server = server;
	    }

	    @Override
	    protected String doInBackground(File... params) {
	        Log.d(TAG, "doInBackground");
	        HttpClient http = new DefaultHttpClient();
	        HttpPost method = new HttpPost(this.server);
	        method.setEntity(new FileEntity(params[0], "text/plain"));
	        try {
	            HttpResponse response = http.execute(method);
	            BufferedReader rd = new BufferedReader(new InputStreamReader(
	                    response.getEntity().getContent()));
	            final StringBuilder out = new StringBuilder();
	            String line;
	            try {
	                while ((line = rd.readLine()) != null) {
	                    out.append(line);
	                }
	            } catch (Exception e) {}
	            // wr.close();
	            try {
	                rd.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	            // final String serverResponse = slurp(is);
	            Log.d(TAG, "serverResponse: " + out.toString());
	        }
	        catch (Exception e) {
			    // show error
				Log.e("Debug", "error: " + e.getMessage(), e);
			}
	        return null;
	    }
	}
	*/
	/*
	public static int upLoad2Server(String sourceFileUri) {
		  int serverResponseCode = 0;
		  String upLoadServerUri = ServerInformation.STORAGE_URL.toString();
		  // String [] string = sourceFileUri;
		  String fileName = sourceFileUri;

		  HttpURLConnection conn = null;
		  DataOutputStream dos = null;
		  DataInputStream inStream = null;
		  String lineEnd = "\r\n";
		  String twoHyphens = "--";
		  String boundary = "*****";
		  int bytesRead, bytesAvailable, bufferSize;
		  byte[] buffer;
		  int maxBufferSize = 1 * 1024 * 1024;

		  File sourceFile = new File(sourceFileUri);
		  if (!sourceFile.isFile()) {
		   Log.e("Huzza", "Source File Does not exist");
		   return 0;
		  }
		  try { // open a URL connection to the Servlet
		   FileInputStream fileInputStream = new FileInputStream(sourceFile);
		   URL url = new URL(upLoadServerUri);
		   conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
		   conn.setDoInput(true); // Allow Inputs
		   conn.setDoOutput(true); // Allow Outputs
		   conn.setUseCaches(false); // Don't use a Cached Copy
		   conn.setRequestMethod("POST");
		   conn.setRequestProperty("Connection", "Keep-Alive");
		   conn.setRequestProperty("ENCTYPE", "multipart/form-data");
		   conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
		   conn.setRequestProperty("uploaded_file", fileName);
		   dos = new DataOutputStream(conn.getOutputStream());

		   dos.writeBytes(twoHyphens + boundary + lineEnd);
		   dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ fileName + "\"" + lineEnd);
		   dos.writeBytes(lineEnd);

		   bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size
		   Log.i("Huzza", "Initial .available : " + bytesAvailable);

		   bufferSize = Math.min(bytesAvailable, maxBufferSize);
		   buffer = new byte[bufferSize];

		   // read file and write it into form...
		   bytesRead = fileInputStream.read(buffer, 0, bufferSize);

		   while (bytesRead > 0) {
		    dos.write(buffer, 0, bufferSize);
		     bytesAvailable = fileInputStream.available();
		     bufferSize = Math.min(bytesAvailable, maxBufferSize);
		    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
		    }

		   // send multipart form data necesssary after file data...
		   dos.writeBytes(lineEnd);
		   dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

		   // Responses from the server (code and message)
		   serverResponseCode = conn.getResponseCode();
		   String serverResponseMessage = conn.getResponseMessage();

		   Log.i("Upload file to server", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
		   // close streams
		   Log.i("Upload file to server", fileName + " File is written");
		   fileInputStream.close();
		   dos.flush();
		   dos.close();
		  } catch (MalformedURLException ex) {
		   ex.printStackTrace();
		   Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		//this block will give the response of upload link
		  try {
		   BufferedReader rd = new BufferedReader(new InputStreamReader(conn
		     .getInputStream()));
		   String line;
		   while ((line = rd.readLine()) != null) {
		    Log.i("Huzza", "RES Message: " + line);
		   }
		   rd.close();
		  } catch (IOException ioex) {
		   Log.e("Huzza", "error: " + ioex.getMessage(), ioex);
		  }
		  return serverResponseCode;  // like 200 (Ok)

		 } // end upLoad2Server*/
	
	private void uploadFile(String filePath) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(ServerInformation.STORAGE_FRONT_END_URL.toString());

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("browse", filePath));
		nameValuePairs.add(new BasicNameValuePair("start-upload", "1"));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
