package com.android.perf.service;

import com.android.perf.service.IRemoteServiceCallback;
import com.android.perf.service.RemoteInterface;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

public class RemoteStub extends Service {

	final RemoteCallbackList<IRemoteServiceCallback> mCallbacks = new RemoteCallbackList<IRemoteServiceCallback>();
    
	// IRemoteService (RemoteInterfaceBinder)
	private final RemoteInterface.Stub mRemoteInterfaceBinder = new RemoteInterface.Stub(){
		
		public String getSayHello() throws RemoteException {
			return "Hello?";
		}
		
		public String getSayHelloByName(String name) throws RemoteException {
			return "Hello? " + name + ", Welcome!";
		}
		
		public void registerCallback(IRemoteServiceCallback cb){
			if (cb!=null){
				mCallbacks.register(cb);
			}
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
	
	// onBind
	@Override
	public IBinder onBind(Intent intent) {
		
		if (RemoteInterface.class.getName().equals(intent.getAction())){
			return mRemoteInterfaceBinder;
		}
		return null;
	}	
	
	// Message Call-back
	private void sendMessageCallback(int msg){
		
		final int N = mCallbacks.beginBroadcast();
		for (int i=0;i<N;i++){
			try{
				mCallbacks.getBroadcastItem(i).MessageCallback(msg);
			}catch(RemoteException e){
				e.printStackTrace();
			}
		}
		mCallbacks.finishBroadcast();
	}
	
    @Override
    public void onCreate() {}
    
    @Override
    public void onDestroy() {}

}