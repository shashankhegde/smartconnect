package com.android.smartconnect.apps.slashdot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
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
	
	LogHelper iLogger = null;
	private Context iParentContext = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return iBackgroundService;
	}
	
	private IBackgroundService.Stub iBackgroundService = new IBackgroundService.Stub() {
		

		@Override
		public int CheckUpdate(String aUrl, int aIntervalInSecs) throws RemoteException {
			iParentContext = getApplicationContext();
			iUpdateInterval = aIntervalInSecs;
			if(iUpdateChecker == null) {
				URL url = null;
				try {
					url = new URL(aUrl);
					iUpdateChecker = new UpdateChecker(url,iParentContext);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(iUpdateThreadHandler == null) {
				iUpdateThreadHandler = new Handler();
			}
			
			iUpdateChecker.StartContinuousUpdate();
			iUpdateThreadHandler.removeCallbacks(iUpdateChecker);
			iUpdateThreadHandler.postDelayed(iUpdateChecker, 1);
			
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

		@Override
		public int SetUpdateInterval(int aIntervalInSecs)
				throws RemoteException {
			iUpdateInterval = aIntervalInSecs;
			Log.i("BackgroundService","Setting Update Interval to: " + String.valueOf(iUpdateInterval));
			if(iUpdateThreadHandler != null && iUpdateChecker != null) {
				iUpdateThreadHandler.removeCallbacks(iUpdateChecker);
				iUpdateThreadHandler.postDelayed(iUpdateChecker, iUpdateInterval);
			}
			return 0;
		}
	};
	
	class UpdateChecker implements Runnable, IDataReceiver {

		private URL iUrl = null;
		private URLConnection iUrlConnection = null;
		private boolean iUpdateContinuously = false;
		private RequestManagerHelper iHelper = null;
		private Context iParentContext;
		
		public UpdateChecker(URL aUrl, Context aParentContext) {
			iUrl = aUrl;
			iParentContext = aParentContext;
		}

		public void StartContinuousUpdate() {
			iUpdateContinuously = true;
		}
		
		public void StopContinuousUpdate() {
			iUpdateContinuously = false;
		}
		
		@Override
		public void run() {
			runRemote();
			//runLocal();
		}
		
		public void runRemote() {
			if(iHelper == null) {
				iHelper = new RequestManagerHelper(this.iParentContext);
			}
			iHelper.SendRequest(iUrl.toString(),this);
		}

		@Override
		public void onDataReceived(String aData) {
			if(iUpdateContinuously) {
				Log.i("BackgroundService","onDataReceived()");
				iUpdateThreadHandler.postDelayed(iUpdateChecker, iUpdateInterval*1000);
			}
		}
		
		public void runLocal() {
			
			if(iLogger == null) {
				iLogger = new LogHelper("slashdor.org");
			}
			iLogger.addLog("SEND");
			String data = GetData();
			iLogger.addLog("RECV | " + data.length());
			
			if (iCallback != null) {
				try {
					iCallback.onUpdateReceived(data);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(iUpdateContinuously) {
				Log.i("BackgroundService","Update Interval : " + String.valueOf(iUpdateInterval));
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
}
