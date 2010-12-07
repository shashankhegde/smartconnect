package com.android.perf.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class DummyService extends Service {

	private static final String TAG = "DUMMY SERVICE";
	
	Thread t1, t2, t3;
	
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
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Toast.makeText(this, "STARTED: Dummy Service", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart()");
	
		/* Start Traffic in a separate thread */
		//(new Thread(new TrafficMaker())).start();
		t1 = new Thread(new TrafficMaker("http://www.google.com", 20000));
		t2 = new Thread(new TrafficMaker("http://www.cs.ucsb.edu/~kevinfrancis", 5000));
		t3 = new Thread(new TrafficMaker("http://www.yahoo.com", 10000));
		
		t1.start();
		t2.start();
		t3.start();
	}
}
