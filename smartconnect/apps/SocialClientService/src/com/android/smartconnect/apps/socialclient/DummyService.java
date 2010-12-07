package com.android.smartconnect.apps.socialclient;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class DummyService extends Service {

	private static final String TAG = "DUMMY SERVICE";
	
	Thread t1, t2, t3, t4;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		Toast.makeText(this, "CREATED: Dummy Service", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate()");
	}
	
	@Override
	public void onDestroy() {
		Toast.makeText(this, "DESTROYED: Dummy Service", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy()");
		/* Stop traffic */
		t1.stop();
		t2.stop();
		t3.stop();
		t4.stop();
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Toast.makeText(this, "STARTED: Dummy Service", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart()");
	
		/* Start Traffic in a separate thread */
		//(new Thread(new TrafficMaker())).start();
		t1 = new Thread(new TrafficMaker("http://www.google.com", 120000, 10000));	// every 2 min, variance = 10 secs
		t2 = new Thread(new TrafficMaker("http://www.cs.ucsb.edu/", 300000, 30000)); // every 5 min, variance = 30 secs
		t3 = new Thread(new TrafficMaker("http://www.nytimes.com", 300000, 10000));	// every 5 min, variance = 10 secs
		t4 = new Thread(new TrafficMaker("http://www.reddit.com/.rss", 900000, 60000));	// every 15 min, variance = 60 secs
		
		t1.start();
		t2.start();
		t3.start();
		t4.start();
	}
}
