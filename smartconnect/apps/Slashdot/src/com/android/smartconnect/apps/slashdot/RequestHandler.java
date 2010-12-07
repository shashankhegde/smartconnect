package com.android.smartconnect.apps.slashdot;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class RequestHandler {

	private static final String SERVICE_PACKAGE = "com.android.smartconnect.apps.slashdot";
	private static final String SERVICE_NAME = "com.android.smartconnect.apps.slashdot.BackgroundService";
	
	private URL	iUrl = null;
	private Intent iServiceIntent;
	private Context iParentActivity;
	private boolean iServiceStarted;
	private boolean iServiceBound;
	
	private IBackgroundService iBackgroundService = null;
	private UpdateServiceConnection iUpdateServiceConnection = null;
	private int iIntervalInSecs = 5*60; // 5 min
	
	boolean iUpdatePending = false;
	
	RequestHandler(Activity aParentActivity, String aUrl, int aUpdateInterval) {
		iParentActivity = aParentActivity;
		iIntervalInSecs = aUpdateInterval;
		
		try {
			iUrl = new URL(aUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if(!iServiceStarted) {
			int err = StartService();
			if(err == -1)
				return;
			
			assert(iServiceStarted);
		}
		
		if(!iServiceBound) {
			int err = DoBindService();
			if(err == -1)
				return;
			
			assert(iServiceBound);
		}
		
	}
	
	public void StartUpdateCheck() {
		StartUpdate();
	}
	
	public void Cleanup() {
		UnbindService();
	}
	
	public void SetUpdateInterval(int aInterval) {
		iIntervalInSecs = aInterval;
		
		if(iBackgroundService != null) {
			try {
				iBackgroundService.SetUpdateInterval(aInterval);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected int StartService() {
		iServiceIntent = new Intent();

		iServiceIntent.setClassName(SERVICE_PACKAGE, SERVICE_NAME);
		
		ComponentName s = iParentActivity.startService(iServiceIntent);
		if(s != null)
			iServiceStarted = true;
		
		if(!iServiceStarted)
			return -1;
		
		return 0;
	}
	
	protected int StopService() {
		iParentActivity.stopService(iServiceIntent);
		return 0;
	}
	
	protected int DoBindService() {
		Intent i = new Intent();
		i.setClassName(SERVICE_PACKAGE, SERVICE_NAME);
		
		iUpdateServiceConnection = new UpdateServiceConnection();
		
		iServiceBound = iParentActivity.bindService(i,(ServiceConnection)iUpdateServiceConnection,Context.BIND_AUTO_CREATE);
		if(!iServiceBound) {
			Log.i("RequestHandler", "Error binding to the service");
			return -1;
		}
		
		Log.i("RequestHandler", "Bound to the service!");

		return 0;
	}
	
	protected int UnbindService() {
		iParentActivity.unbindService(iUpdateServiceConnection);
		return 0;
	}
	
	private class UpdateServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			iBackgroundService = IBackgroundService.Stub.asInterface(service);
			if(iUpdatePending) {
				StartUpdate();
				iUpdatePending = false;
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private void StartUpdate() {
		if(iBackgroundService == null) {
			iUpdatePending = true;
			return;
		}
		try {
			iBackgroundService.CheckUpdate(iUrl.toString(), iIntervalInSecs );
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
