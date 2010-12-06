package com.android.smartconnect.requestmanager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class RequestManagerService extends Service {

	private IRequestManager.Stub iRequestManager = null;
	private RequestQueue iRequestQueue = null;
	private Timer iTimer = null;
	private TimerTask iTimerTask = null;
	private RequestProcessor iRequestProcessor = null;
	
	private int iInterval = 5*60*1000; 	// 5 min in ms
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return iRequestManager;
	}
	
	public void onCreate() {
		if(iRequestManager == null) {
			iRequestManager = new IRequestManagerStubImpl();
		}
	}
	
	private class IRequestManagerStubImpl extends IRequestManager.Stub {

		@Override
		public int GetData(String aUrl, RequestCallback aCallback)
				throws RemoteException {

			if(iRequestQueue == null) {
				iRequestQueue = new RequestQueue();
			}
			
			UrlRequest newUrlRequest = new UrlRequest();
			try {
				newUrlRequest.iUrl = new URL(aUrl);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			newUrlRequest.iCallback = aCallback;
			iRequestQueue.add(newUrlRequest);
			
			if(iTimer == null) {
				iTimer = new Timer();
				iTimer.schedule(iTimerTask, iInterval);
			}
			return 0;
		}

	}
	
	private class RequestTimerTask extends TimerTask {

		@Override
		public void run() {
			assert(iRequestProcessor == null);
			
			if(iRequestProcessor == null) {
				iRequestProcessor = new RequestProcessor(iRequestQueue);
			}
			
			iRequestProcessor.doInBackground(null);
		}
		
	}

}
