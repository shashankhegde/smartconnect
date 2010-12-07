package com.android.smartconnect.requestmanager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

public class RequestManagerService extends Service {

	protected static final int SCHEDULE_TIMER_TASK = 0;
	public static final int MAX_REQUESTS = 100;
	private IRequestManager.Stub iRequestManager = null;
	private RequestQueue iRequestQueue = null;
	private Timer iTimer = null;
	private RequestTimerTask iTimerTask = null;
	private RequestProcessor iRequestProcessor = null;
	
	private int iInterval = 2*60*1000; 	// 2 min in ms
	
	private Handler iTimerScheduler = new Handler() {
		
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case SCHEDULE_TIMER_TASK:
				if(iTimer == null) {
					iTimer = new Timer();
				}
				iTimerTask = new RequestTimerTask();
				iTimer.schedule(iTimerTask, iInterval);
			}
		}
		
	};
	
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

			Log.i("IRequestManagerStubImpl","GetData() " + aUrl);
			
			if(iRequestQueue == null) {
				iRequestQueue = new RequestQueue(MAX_REQUESTS);
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
			iTimerScheduler.sendEmptyMessage(SCHEDULE_TIMER_TASK);
			
			return 0;
		}

	}
	
	private class RequestTimerTask extends TimerTask {

		public RequestTimerTask() {
			super();
		}
		
		@Override
		public void run() {
			Log.i("RequestManagerService","run()");
			
			List<RequestProcessor> iRequestProcessors = new ArrayList<RequestProcessor>();
			
			int size = iRequestQueue.size();
			for( int i=0; i<iRequestQueue.size(); i++) {
				iRequestProcessors.add(new RequestProcessor());
				UrlRequest req = iRequestQueue.remove();
				iRequestProcessors.get(i).doInBackground(req);
				try {
					iRequestProcessors.get(i).wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			// all requests completed!
			iTimerScheduler.sendEmptyMessage(SCHEDULE_TIMER_TASK);

		}
		
	}

}
