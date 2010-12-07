package com.android.perf.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class RemoteStub extends Service {

	final RemoteCallbackList<IRemoteServiceCallback> mCallbacks = new RemoteCallbackList<IRemoteServiceCallback>();

	int mValue = 0;
	NotificationManager mNM; 
	
	// IRemoteService (RemoteInterfaceBinder)
	private final RemoteInterface.Stub mBinder = new RemoteInterface.Stub(){
		
		public String getSayHello() throws RemoteException {
			return "Hello?";
		}
		
		public String getSayHelloByName(String name) throws RemoteException {
			return "Hello? " + name + ", Welcome!";
		}
		
		public boolean registerCallback(IRemoteServiceCallback cb){
			boolean flag = false;
			if (cb!=null){
				flag = mCallbacks.register(cb);
				if (flag){
					Log.d("RemoteStub", "registerCallback Succeeded");
				}else {
					Log.d("RemoteStub", "registerCallback Failed");
				}
			}
			return flag;
		}
		
		public void unregisterCallback(IRemoteServiceCallback cb){
			if (cb!=null) mCallbacks.unregister(cb);
		}
		
		// Message Handler
		public String onService(int msg){
			switch(msg){
				// Implement inner Service
			
			}
			return null;
		}
	};
	
    private final ISecondary.Stub mSecondaryBinder = new ISecondary.Stub() {
        public int getPid() {
            return Process.myPid();
        }
        public void basicTypes(int anInt, long aLong, boolean aBoolean,
                float aFloat, double aDouble, String aString) {
        }
    };
    
    private static final int REPORT_MSG = 1;
	
    private final Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                
                // It is time to bump the value!
                case REPORT_MSG: {
                    // Up it goes.
                    int value = ++mValue;
                    
                    // Broadcast to all clients the new value.
                    final int N = mCallbacks.beginBroadcast();
                    Log.d("RemoteStub", "mCallbacks.beginBroadcast : "+String.valueOf(N));
                    
                    for (int i=0; i<N; i++) {
                        try {
                            mCallbacks.getBroadcastItem(i).valueChanged(value);
                            Log.d("RemoteStub", "BroadcastItem : "+value);
                        } catch (RemoteException e) {
                            // The RemoteCallbackList will take care of removing
                            // the dead object for us.
                        }
                    }
                    mCallbacks.finishBroadcast();
                    
                    // Repeat every 1 second.
                    sendMessageDelayed(obtainMessage(REPORT_MSG), 1*1000);
                } break;
                default:
                    super.handleMessage(msg);
            }
        }
    };
    
	// onBind
	@Override
	public IBinder onBind(Intent intent) {
        // Select the interface to return.  If your service only implements
        // a single interface, you can just return it here without checking
        // the Intent.
        if (IRemoteService.class.getName().equals(intent.getAction())) {
            return mBinder;
        }
        if (ISecondary.class.getName().equals(intent.getAction())) {
            return mSecondaryBinder;
        }
        if (RemoteInterface.class.getName().equals(intent.getAction())) {
            return mBinder;
        }
        return null;
	}	
	
	// Message Call-back
//	private void sendMessageCallback(int msg){
//		
//		final int N = mCallbacks.beginBroadcast();
//		for (int i=0;i<N;i++){
//			try{
//				mCallbacks.getBroadcastItem(i).MessageCallback(msg);
//			}catch(RemoteException e){
//				e.printStackTrace();
//			}
//		}
//		mCallbacks.finishBroadcast();
//	}
	
    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.
        showNotification();
        
        // While this service is running, it will continually increment a
        // number.  Send the first message that is used to perform the
        // increment.
        mHandler.sendEmptyMessage(REPORT_MSG);
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(R.string.remote_service_started);

        // Tell the user we stopped.
        Toast.makeText(this, R.string.remote_service_stopped, Toast.LENGTH_SHORT).show();
        
        // Unregister all callbacks.
        mCallbacks.kill();
        
        // Remove the next pending message to increment the counter, stopping
        // the increment loop.
        mHandler.removeMessages(REPORT_MSG);
    }
    
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.remote_service_started);

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.icon, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, DummyApp.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.remote_service_label),
                       text, contentIntent);

        // Send the notification.
        // We use a string id because it is a unique number.  We use it later to cancel.
        mNM.notify(R.string.remote_service_started, notification);
    }

}