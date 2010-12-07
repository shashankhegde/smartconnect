package com.android.perf;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.perf.service.IRemoteService;
import com.android.perf.service.IRemoteServiceCallback;
import com.android.perf.service.ISecondary;
import com.android.perf.service.RemoteInterface;

public class Main extends Activity implements OnClickListener {
	
    /** The primary interface we will be calling on the service. */
    IRemoteService mService = null; //1
    
    /** Another interfaces we use on the service. */
    ISecondary mSecondaryService = null; //2
    static RemoteInterface mRemoteInterfaceService = null; //3
    
    Button mKillButton;
    TextView mCallbackText;

    private boolean mIsBound = false;
	
	private static final String TAG = "SmartConnect Request";
//	private static boolean serviceRunning = false;
	Button startButton, stopButton;	
	Button ButtonTest, ButtonPick2, ButtonSubmit;
	Button stop = null;
	Button buttonBind, buttonUnbind;
	RadioButton RadioButton01, RadioButton02;
	private EditText EditText_url;
	static TextView tv01=null;
	static TextView tv02=null;
	static TextView callback=null;
	
	String url = "www.google.com";
	
//	private Handler mHandler = null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainpage);
		
        // Watch for button clicks.
        buttonBind = (Button)findViewById(R.id.bind);
        buttonBind.setOnClickListener(mBindListener);
        buttonUnbind = (Button)findViewById(R.id.unbind);
        buttonUnbind.setOnClickListener(mUnbindListener);
        mKillButton = (Button)findViewById(R.id.kill);
        mKillButton.setOnClickListener(mKillListener);
        mKillButton.setEnabled(false);
        
        mCallbackText = (TextView)findViewById(R.id.callbackText);
        mCallbackText.setText("Not attached.");
        
        tv01 = (TextView)findViewById(R.id.tv01);
        tv02 = (TextView)findViewById(R.id.tv02);
        callback = (TextView)findViewById(R.id.callback);
        stop = (Button)findViewById(R.id.stop);

        // OLD
		// UI Constructor -- I now have all my UI in the Java memory
		ButtonTest = (Button) findViewById(R.id.ButtonTest);
		RadioButton01 = (RadioButton) findViewById(R.id.RadioButton01);
		RadioButton02 = (RadioButton) findViewById(R.id.RadioButton02);
		ButtonSubmit = (Button) findViewById(R.id.ButtonSubmit);
				
		EditText_url = (EditText) findViewById(R.id.EditText_url);			
		
		EditText_url.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
						url = EditText_url.getText().toString();
						EditText_url.setText(url);
						
					return true;
				}
				return false;
			}
		});
		
//		ComponentName cn = this.startService(new Intent("com.android.perf.service.RemoteInterface"));
//		startBindService();
		
		/** App_1 */
		startBindService();

		stop = (Button) this.findViewById(R.id.stop);

		stop.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {

				stopBindService();
			}
		});

		// Define button listeners
		ButtonTest.setOnClickListener(this);
		ButtonSubmit.setOnClickListener(this);		
	}
	
	private void startBindService(){
		
		boolean bindFlag01 = bindService(new Intent("com.remoteStub.IRemoteService"), mConnection, Context.BIND_AUTO_CREATE);
		Log.d("App_1", "RemoteInterface-bind : "+String.valueOf(bindFlag01));
		
		boolean bindFlag02 = bindService(new Intent("com.remoteStub.ISecondary"), mSecondaryConnection, Context.BIND_AUTO_CREATE);
		Log.d("App_1", "RemoteInterface-bind : "+String.valueOf(bindFlag02));
		
		boolean bindFlag03 = bindService(new Intent("com.remoteStub.RemoteInterface"), mThirdConnection, Context.BIND_AUTO_CREATE);
		Log.d("App_1", "RemoteInterface-bind : "+String.valueOf(bindFlag03));
		
		callback.setText("Binding..");
		mIsBound = true;
	};
	
	// Disconnect Connection
	private void stopBindService(){
		if(mIsBound){
			
			if (mService != null){
				try{
					mService.unregisterCallback(mCallback);
				}catch(RemoteException e){
					// Nothing to do if the service has crashed
				}
			}
			unbindService(mSecondaryConnection);
			// TODO svc1 & 3
		}
		callback.setText("Unbinding...");
	};
	
	// 1st connection
    private ServiceConnection mConnection = new ServiceConnection() {

    	public void onServiceConnected(ComponentName className,
                IBinder service) {
    		
            mService = IRemoteService.Stub.asInterface(service);
            mKillButton.setEnabled(true);
            mCallbackText.setText("Attached...");

            try {
                boolean flag = mService.registerCallback(mCallback);
                Log.d("App_1,", "mService.registerCallback : " + String.valueOf(flag));
                
            } catch (RemoteException e) {
            	Log.e("App_1", e.toString());
            }
            
            Toast.makeText(Main.this, R.string.remote_service_connected,
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {

            mService = null;
            mKillButton.setEnabled(false);
            mCallbackText.setText("Disconnected...");

            Toast.makeText(Main.this, R.string.remote_service_disconnected,
                    Toast.LENGTH_SHORT).show();
        }
    };
    
    // 2nd connection
    private ServiceConnection mSecondaryConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder service) {

            mSecondaryService = ISecondary.Stub.asInterface(service);
            mKillButton.setEnabled(true);
        }

        public void onServiceDisconnected(ComponentName className) {
            mSecondaryService = null;
            mKillButton.setEnabled(false);
        }
    };
    
    // 3rd connection
    private ServiceConnection mThirdConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder service) {

            mRemoteInterfaceService = RemoteInterface.Stub.asInterface(service);
            
            if(mRemoteInterfaceService != null){
            	printHello();
            	Log.d("App_1", "ServiceConnection(3)-mRemoteInterfaceService is not null");
            }else {
            	Log.d("App_1", "ServiceConnection(3)-mRemoteInterfaceService is null");
            }
            mKillButton.setEnabled(true);
        }

        public void onServiceDisconnected(ComponentName className) {
            mRemoteInterfaceService = null;
            mKillButton.setEnabled(false);
        }
    };

    private OnClickListener mBindListener = new OnClickListener() {
        public void onClick(View v) {
            // Establish a couple connections with the service, binding
            // by interface names.  This allows other applications to be
            // installed that replace the remote service by implementing
            // the same interface.
//            bindService(new Intent(IRemoteService.class.getName()),
//                    mConnection, Context.BIND_AUTO_CREATE);
//            bindService(new Intent(ISecondary.class.getName()),
//                    mSecondaryConnection, Context.BIND_AUTO_CREATE);
        	bindService(new Intent("com.android.perf.service.RemoteInterface"),
                    mConnection, Context.BIND_AUTO_CREATE);
            bindService(new Intent("com.android.perf.service.RemoteInterface"),
                    mSecondaryConnection, Context.BIND_AUTO_CREATE);
        	
            mIsBound = true;
            mCallbackText.setText("Binding...");
        }
    };

    private OnClickListener mUnbindListener = new OnClickListener() {
        public void onClick(View v) {
            if (mIsBound) {
                // If we have received the service, and hence registered with
                // it, then now is the time to unregister.
                if (mService != null) {
                    try {
                        mService.unregisterCallback(mCallback);
                    } catch (RemoteException e) {
                        // There is nothing special we need to do if the service
                        // has crashed.
                    }
                }
                
                // Detach our existing connection.
                unbindService(mConnection);
                unbindService(mSecondaryConnection);
                mKillButton.setEnabled(false);
                mIsBound = false;
                mCallbackText.setText("Unbinding...");
            }
        }
    };

    private OnClickListener mKillListener = new OnClickListener() {
        public void onClick(View v) {
            // To kill the process hosting our service, we need to know its
            // PID.  Conveniently our service has a call that will return
            // to us that information.
            if (mSecondaryService != null) {
                try {
                    int pid = mSecondaryService.getPid();
                    // Note that, though this API allows us to request to
                    // kill any process based on its PID, the kernel will
                    // still impose standard restrictions on which PIDs you
                    // are actually able to kill.  Typically this means only
                    // the process running your application and any additional
                    // processes created by that app as shown here; packages
                    // sharing a common UID will also be able to kill each
                    // other's processes.
                    Process.killProcess(pid);
                    mCallbackText.setText("Killed service process.");
                } catch (RemoteException ex) {
                    // Recover gracefully from the process hosting the
					// server dying.
					// Just for purposes of the sample, put up a notification.
					Toast.makeText(Main.this, R.string.remote_call_failed,
							Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    
	public static void printHello() {
    
    		String sayHello = "";
    		String sayHelloByName = "";
    
    		// call Remode Method
    		try {
    			sayHello = mRemoteInterfaceService.getSayHello();
    			sayHelloByName = mRemoteInterfaceService.getSayHelloByName("Jay");
    
    			callback.setText("exec printHello");
    
    		} catch (RemoteException e) {
    			e.printStackTrace();
    		}
    		tv01.setText(sayHello);
    		tv02.setText(sayHelloByName);
        };
    
    private IRemoteServiceCallback mCallback = new IRemoteServiceCallback.Stub() {
        /**
         * This is called by the remote service regularly to tell us about
         * new values.  Note that IPC calls are dispatched through a thread
         * pool running in each process, so the code executing here will
         * NOT be running in our main thread like most other things -- so,
         * to update the UI, we need to use a Handler to hop over there.
         */
        public void valueChanged(int value) {
        	Log.d("App_1", String.valueOf(value));
        	Log.d("App_1", String.valueOf(mHandler.obtainMessage(BUMP_MSG, value, 0)));
            mHandler.sendMessage(mHandler.obtainMessage(BUMP_MSG, value, 0)); // >>***
        }
    };
    
    private static final int BUMP_MSG = 1;
    
    private Handler mHandler = new Handler() {
        @Override 
        public void handleMessage(Message msg) { // ***>>

        	switch (msg.what) {
        	
                case BUMP_MSG:
                    mCallbackText.setText("Received from service: " + msg.arg1);
                    break;
                    
                default:
                    super.handleMessage(msg);
            }
        }
        
    };
	
	@Override
	public void onClick(View src) {
		switch (src.getId()) {
		case R.id.ButtonTest:
//			if (!serviceRunning) {
//				serviceRunning = true;
			Intent intent = new Intent(this, DummyService.class);
			Log.d(TAG, "onClick: Starting Service");
			startService(intent);
			break;
//			}
			
		case R.id.ButtonSubmit:
			Intent intent5 = new Intent(this, WebFacebook.class);
			startActivity(intent5);
			break;
		}
	}
}