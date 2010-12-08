package com.android.smartconnect.requestmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
	//private RequestTimerTask iTimerTask = null;
	private RequestProcessor iRequestProcessor = null;
	
	private int iInterval = 5*60*1000; 	// 5 min in ms
	
	private LogHelper iLogger = null;
/*	
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
*/
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return iRequestManager;
	}
	
	public void onCreate() {
		if(iRequestManager == null) {
			iRequestManager = new IRequestManagerStubImpl();
		}
		
		iLogger = new LogHelper("RequestManager.log");
		
		(new Thread(new RequestQueueTimeManager())).start();
	}
	
	private class IRequestManagerStubImpl extends IRequestManager.Stub {

		@Override
		public int GetData(String aUrl, long aRequestId, RequestCallback aCallback)
				throws RemoteException {

			Log.i("IRequestManagerStubImpl","GetData() " + aUrl);
			
			if(iRequestQueue == null) {
				iRequestQueue = new RequestQueue(MAX_REQUESTS);
			}

			
			UrlRequest newUrlRequest = new UrlRequest();
			try {
				newUrlRequest.iUrl = new URL(aUrl);
				newUrlRequest.iCallback = aCallback;
				newUrlRequest.iRequestId = aRequestId;
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i("REQ MANAGER SERVICE", "Malformed URL Exception");
			}
			newUrlRequest.iCallback = aCallback;
			
			Log.i("REQ MANAGER SERVICE", "Will add url to request queue");
			iRequestQueue.add(newUrlRequest);

			iLogger.addLog(iRequestQueue.size() + " REQ " + aUrl + aRequestId);
			
			return 0;
		}

	}
/*	
	private class RequestTimerTask extends TimerTask {

		public RequestTimerTask() {
			super();
		}
		
		@Override
		public void run() {

			Log.i("RequestManagerService","run()");
			
	
			List<RequestProcessor> iRequestProcessors = new ArrayList<RequestProcessor>();
			
			int size = iRequestQueue.size();
			for( int i=0; i<size; i++) {
				iRequestProcessors.add(new RequestProcessor());
				UrlRequest req = iRequestQueue.remove();
				iRequestProcessors.get(i).doInBackground(req);
			}
			
			for(int i=0; i<size; i++) {
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
*/	
	// Request queue time manager:  Flushes the queue at regular intervals
	private class RequestQueueTimeManager implements Runnable {
		
		private Thread t = null;
		@Override
		public void run() {
			while (true) {
				Log.i("REQUEST MANAGER", "Flushing requests");
				FlushRequests();
				try {
					Log.i("REQUEST MANAGER", "SLEEPING");
					Thread.sleep(iInterval);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
/*				try {
					if (t != null)
						t.stop();

					t = new Thread(new RequestQueueFlusher());
					t.start();
					Thread.sleep(iInterval);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
*/			}
		}
	}

	private void FlushRequests() {
		
		if (iRequestQueue == null) {
			Log.i("REQUEST MANAGER", "iRequestQueue is null. RETURNING");
			return;
		}

		int size = iRequestQueue.size();
		iLogger.addLog("Flushing Requests " + size);
		int i;
		
		for (i=0; i<size; i++) {
			UrlRequest req = iRequestQueue.remove();
			RequestExecutor rexec = new RequestExecutor(req);
			(new Thread(rexec)).start();
		}
		/*
		List<RequestProcessor> iRequestProcessors = new ArrayList<RequestProcessor>();
		int size = iRequestQueue.size();
		for( int i=0; i<size; i++) {
			iRequestProcessors.add(new RequestProcessor());
			UrlRequest req = iRequestQueue.remove();
			iRequestProcessors.get(i).doInBackground(req);
		}
		
		for(int i=0; i<size; i++) {
			try {
				iRequestProcessors.get(i).wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		*/
		
	}
	
	private class RequestExecutor implements Runnable {
		
		URL url;
		RequestCallback callBackFunc;
		long iRequestId;
		
		public RequestExecutor(UrlRequest requestObject) {
			this.url = requestObject.iUrl;
			this.callBackFunc = requestObject.iCallback;
			this.iRequestId = requestObject.iRequestId;
		}
		
		@Override
		public void run() {
			String urlData;
			Log.i("REQUEST MANAGER", "Requesting Data " + url.toString() + " " + iRequestId);

			try {
				urlData = wgetPage();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			
			Log.i("REQUEST MANAGER", "Data received " + urlData.length());

			if(true) {
				try {
					callBackFunc.onDataReceived(iRequestId,urlData);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

/*			LogHelper logHelper = new LogHelper(url.toExternalForm());
			logHelper.addLog("RECV | " + urlData.length());
*/		}
		
		private String wgetPage() throws MalformedURLException, IOException {
			
			String urlData = "";
			String inputLine;
			
			BufferedReader in = new BufferedReader(
						new InputStreamReader(
						url.openStream()));

			while ((inputLine = in.readLine()) != null)
				urlData += inputLine + '\n';
			
			in.close();
			
			return urlData;
		}
	}
	/*
	// Request Queue Flusher: To flush the queue
	private class RequestQueueFlusher implements Runnable {
		public void run() {
			// Flush Request Queue
			List<RequestProcessor> iRequestProcessors = new ArrayList<RequestProcessor>();
			
			int size = iRequestQueue.size();
			for( int i=0; i<size; i++) {
				iRequestProcessors.add(new RequestProcessor());
				UrlRequest req = iRequestQueue.remove();
				iRequestProcessors.get(i).doInBackground(req);
			}
			
			for(int i=0; i<size; i++) {
				try {
					iRequestProcessors.get(i).wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
	*/

}
