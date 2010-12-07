package com.android.smartconnect.apps.slashdot;

import com.android.smartconnect.requestmanager.IRequestManager;
import com.android.smartconnect.requestmanager.RequestCallback;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.android.smartconnect.requestmanager.RequestCallback;

public class RequestManagerHelper {
	
	private static final String SERVICE_PACKAGE = "com.android.smartconnect.requestmanager";
	private static final String SERVICE_NAME = "com.android.smartconnect.requestmanager.RequestManagerService";
	private Intent iServiceIntent;
	private Context iParentContext;
	private UpdateServiceConnection iUpdateServiceConnection;
	private boolean iServiceBound;
	public IRequestManager iRequestManager;
	public boolean iUpdatePending;
	boolean iServiceStarted = false;
	private String iUrl;
	private IDataReceiver iDataReceiver;

	public RequestManagerHelper(Context aParentContext) {
		iParentContext = aParentContext;
		
		StartService();
		DoBindService();
	}
	
	public void Cleanup() {
		UnbindService();
	}
	
	public void SendRequest(String aUrl, IDataReceiver aDataReceiver) {
		iUrl = aUrl;
		iDataReceiver = aDataReceiver;
		StartUpdate();
	}
	
	protected int StartService() {
		iServiceIntent = new Intent();

		iServiceIntent.setClassName(SERVICE_PACKAGE, SERVICE_NAME);
		
		ComponentName s = iParentContext.startService(iServiceIntent);
		if(s != null)
			iServiceStarted = true;
		
		if(!iServiceStarted)
			return -1;
		
		return 0;
	}
	
	protected int StopService() {
		iParentContext.stopService(iServiceIntent);
		return 0;
	}
	
	protected int DoBindService() {
		Intent i = new Intent();
		i.setClassName(SERVICE_PACKAGE, SERVICE_NAME);
		
		iUpdateServiceConnection = new UpdateServiceConnection();
		
		iServiceBound = iParentContext.bindService(i,(ServiceConnection)iUpdateServiceConnection,Context.BIND_AUTO_CREATE);
		if(!iServiceBound) {
			Log.i("RequestHandler", "Error binding to the service");
			return -1;
		}
		
		Log.i("RequestHandler", "Bound to the service!");

		return 0;
	}
	
	protected int UnbindService() {
		iParentContext.unbindService(iUpdateServiceConnection);
		return 0;
	}
	
	private class UpdateServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			iRequestManager = IRequestManager.Stub.asInterface(service);
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
		if(iRequestManager == null) {
			iUpdatePending = true;
			return;
		}
		try {
			iRequestManager.GetData(iUrl, iCallback);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private RequestCallback iCallback = new RequestCallback.Stub() {

		@Override
		public void onDataReceived(String aData) throws RemoteException {
			Log.i("Slashdot::RequestManagerHelper","onDataReceived()");
			if(iDataReceiver != null) {
				iDataReceiver.onDataReceived(aData);
			}
		}
	};

}
