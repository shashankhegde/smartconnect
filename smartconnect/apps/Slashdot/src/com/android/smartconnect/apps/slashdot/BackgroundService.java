package com.android.smartconnect.apps.slashdot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class BackgroundService extends Service {

	IBackgroundServiceCallback iCallback = null;
	int iUpdateInterval = 5*60;	// 5 min
	
	UpdateChecker iUpdateChecker = null;
	Handler iUpdateThreadHandler = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return iBackgroundService;
	}
	
	private IBackgroundService.Stub iBackgroundService = new IBackgroundService.Stub() {
		
		@Override
		public int CheckUpdate(String aUrl, int aIntervalInSecs) throws RemoteException {
			// TODO Auto-generated method stub
			iUpdateInterval = aIntervalInSecs;
			if(iUpdateChecker == null) {
				iUpdateChecker = new UpdateChecker();
			}
			
			if(iUpdateThreadHandler == null) {
				iUpdateThreadHandler = new Handler();
			}
			
			iUpdateThreadHandler.postDelayed(iUpdateChecker, iUpdateInterval*1000);
			return 0;
		}

		@Override
		public int SetOnUpdateCallback(
				IBackgroundServiceCallback aBackgroundServiceCallback)
				throws RemoteException {
			// TODO Auto-generated method stub
			iCallback = aBackgroundServiceCallback;
			return 0;
		}
	};
	
	class UpdateChecker implements Runnable {

		private URL iUrl = null;
		private URLConnection iUrlConnection = null;
		private boolean iUpdateContinuously = false;
		
		public void StartUpdate() {
			iUpdateContinuously = true;
		}
		
		public void StopUpdate() {
			iUpdateContinuously = false;
		}
		
		@Override
		public void run() {
			
			String data = GetData();
			if (iCallback != null) {
				try {
					iCallback.onUpdateReceived(data);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(iUpdateContinuously) {
				iUpdateThreadHandler.postDelayed(iUpdateChecker, iUpdateInterval*1000);
			}
		}

		String GetData() {
			Log.i(getPackageName()+":"+getClass().getName(), "GetData()");
			if(iUrlConnection == null) {
				try {
					Log.i("RequestHandler", "Opening Connection");
					iUrlConnection = iUrl.openConnection();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			BufferedReader reader = null;
			try {
				Log.i(getPackageName()+":"+getClass().getName(), "Creating Reader");
				 reader = new BufferedReader(new InputStreamReader(iUrlConnection.getInputStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(reader != null) {
				String line = "";
				String response = "";
				try {
					while((line = reader.readLine()) != null) {
						Log.i(getPackageName()+":"+getClass().getName(), line);
						response += line;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return response;
			}
			return null;
		}
		
	}
}
