package com.android.smartconnect.apps.socialclient;

import com.android.smartconnect.requestmanager.IRequestManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;



public class DummyService extends Service {

	private static final String TAG = "DUMMY SERVICE";
	
	private IRequestManager reqManagerService;
	private boolean connectedToReqManager = false;
	
	Thread t1, t2, t3, t4;
	
	ServiceConnection conn = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder boundService) {
			reqManagerService = IRequestManager.Stub.asInterface((IBinder)boundService);
			Log.i("INFO", "Service bound !");
			if (reqManagerService == null) {
				Log.i("INFO", "reqManager is null");
			}
			connectedToReqManager = true;
			Log.i("INFO", "connectedToReqManager ? => " + connectedToReqManager);
		}
		
		public void onServiceDisconnected(ComponentName arg0) {
			Log.i("INFO", "Service Unbound ");
			connectedToReqManager = false;
		}
	};
	
	public boolean getConnectedState() {
		return connectedToReqManager;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		Toast.makeText(this, "CREATED: Dummy Service", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate()");
		
		// Start the Request Manager Service also
		Intent intent = new Intent();
		intent.setClassName("com.android.smartconnect.requestmanager",
					"com.android.smartconnect.requestmanager.RequestManagerService");
		startService(intent);
		
		// Service Connection
		
		// Bind service
		bindService(intent, conn, 0);
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "DESTROYED: Dummy Service", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy()");
		// Stop traffic 
		t1.stop();
		t2.stop();
		t3.stop();
		t4.stop();
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		
		Toast.makeText(this, "STARTED: Dummy Service", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart()");
		
		Log.i("INFO", "Starting threads");
		if (reqManagerService == null)
			Log.i("INFO", "reqManagerService is null");
	
		/* Start Traffic in a separate thread */
		//(new Thread(new TrafficMaker())).start();

		(new Thread(new TrafficKickStarter())).start();
	}
	
	private class TrafficKickStarter implements Runnable {

		public void run() {
			// TODO Auto-generated method stub
			while (connectedToReqManager == false) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				
			t1 = new Thread(new TrafficMaker("http://www.google.com", 120000, 10000,reqManagerService));	// every 2 min, variance = 10 secs
			t2 = new Thread(new TrafficMaker("http://www.cs.ucsb.edu/", 300000, 30000,reqManagerService)); // every 5 min, variance = 30 secs
			t3 = new Thread(new TrafficMaker("http://www.nytimes.com", 300000, 10000,reqManagerService));	// every 5 min, variance = 10 secs
			t4 = new Thread(new TrafficMaker("http://www.reddit.com/.rss", 900000, 60000,reqManagerService));	// every 15 min, variance = 60 secs

			t1.start();
			t2.start();
			t3.start();
			t4.start();
		}
	}
	
	
}
