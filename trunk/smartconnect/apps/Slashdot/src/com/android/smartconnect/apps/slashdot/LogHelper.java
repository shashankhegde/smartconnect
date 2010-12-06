package com.android.smartconnect.apps.slashdot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import android.os.Environment;

public class LogHelper {

	private String logFilename;
	
	private static boolean mExternalStorageAvailable = false;
	private static boolean mExternalStorageWriteable = false;
	
	public LogHelper() {
		this("DummyService.log");
	}
	
	public LogHelper(String filename) {
		
		this.logFilename = filename.replace('/','_').replace(':', '_');
		
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    mExternalStorageAvailable = true;
		    mExternalStorageWriteable = false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
			//  to know is we can neither read nor write
		    mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
	}
	
	public static boolean externalStorageWritable() {
		return mExternalStorageAvailable && mExternalStorageWriteable;
	}
	
	public void addLog(String msg) {
		if (externalStorageWritable()) {
			try {
				File root = Environment.getExternalStorageDirectory();
				File gpxfile = new File(root, logFilename);
				FileWriter writer = new FileWriter(gpxfile, true);
				
				java.util.Date today = new java.util.Date();
			    
				writer.append( today.getTime() + " | " + msg + "\n");
				writer.flush();
				writer.close();
			} catch (IOException e) {
				//TODO
			}
		}
	}
}
