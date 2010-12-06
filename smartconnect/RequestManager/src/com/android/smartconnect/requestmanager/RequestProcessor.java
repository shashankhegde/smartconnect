package com.android.smartconnect.requestmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

public class RequestProcessor extends AsyncTask<Integer, Integer, Integer> {

	private RequestQueue iRequestQueue = null;
	private URLConnection iUrlConnection;
	
	RequestProcessor(RequestQueue aRequestQueue) {
		iRequestQueue = aRequestQueue;
	}
	
	@Override
	protected Integer doInBackground(Integer... params) {
		
		while(iRequestQueue.size() != 0) {
			UrlRequest req = iRequestQueue.remove();
			String data = GetData(req.iUrl);
			if(req.iCallback != null) {
				try {
					req.iCallback.onDataReceived(data);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	private String GetData(URL aUrl) {
		Log.i(getClass().getName(), "GetData()");
		if(iUrlConnection == null) {
			try {
				Log.i("RequestHandler", "Opening Connection");
				iUrlConnection = aUrl.openConnection();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		BufferedReader reader = null;
		try {
			Log.i(getClass().getName(), "Creating Reader");
			 reader = new BufferedReader(new InputStreamReader(iUrlConnection.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String response = "";
		if(reader != null) {
			String line = "";
			try {
				while((line = reader.readLine()) != null) {
					//Log.i(getPackageName()+":"+getClass().getName(), line);
					response += line;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		iUrlConnection = null;
		return response;
	}
	
}
