package com.android.smartconnect.requestmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

public class RequestProcessor extends AsyncTask<UrlRequest, Integer, Integer> {

	private URLConnection iUrlConnection;
	
	public RequestProcessor () {
		super();
	}
	
	@Override
	protected Integer doInBackground(UrlRequest... params) {
		
			Log.i("RequestProcessor", "doInBackground() " + params[0].iUrl.toString());
			UrlRequest req = params[0];
			String data = GetData(req.iUrl);
			if(req.iCallback != null) {
				try {
					req.iCallback.onDataReceived(req.iRequestId,data.length());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			this.notify();
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
