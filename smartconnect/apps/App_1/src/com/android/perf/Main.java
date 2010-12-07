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
    IRemoteService mService = null;
    /** Another interface we use on the service. */
    ISecondary mSecondaryService = null;
    
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
	static TextView callBack=null;
	
	String url = "www.google.com";
	static RemoteInterface mRemoteInterfaceService = null;
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
        
        mCallbackText = (TextView)findViewById(R.id.callback);
        mCallbackText.setText("Not attached.");

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

		// Define button listeners
		ButtonTest.setOnClickListener(this);
		ButtonSubmit.setOnClickListener(this);		
	}
	
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
            mService = IRemoteService.Stub.asInterface(service);
            mKillButton.setEnabled(true);
            mCallbackText.setText("Attached...");

            // We want to monitor the service for as long as we are
            // connected to it.
            try {
                mService.registerCallback(mCallback);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }
            
            // As part of the sample, tell the user what happened.
            Toast.makeText(Main.this, R.string.remote_service_connected,
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            mKillButton.setEnabled(false);
            mCallbackText.setText("Disconnected.");

            // As part of the sample, tell the user what happened.
            Toast.makeText(Main.this, R.string.remote_service_disconnected,
                    Toast.LENGTH_SHORT).show();
        }
    };
    
    private ServiceConnection mSecondaryConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // Connecting to a secondary interface is the same as any
            // other interface.
            mSecondaryService = ISecondary.Stub.asInterface(service);
            mKillButton.setEnabled(true);
        }

        public void onServiceDisconnected(ComponentName className) {
            mSecondaryService = null;
            mKillButton.setEnabled(false);
        }
    };

    private OnClickListener mBindListener = new OnClickListener() {
        public void onClick(View v) {
            // Establish a couple connections with the service, binding
            // by interface names.  This allows other applications to be
            // installed that replace the remote service by implementing
            // the same interface.
            bindService(new Intent(IRemoteService.class.getName()),
                    mConnection, Context.BIND_AUTO_CREATE);
            bindService(new Intent(ISecondary.class.getName()),
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
    
    private IRemoteServiceCallback mCallback = new IRemoteServiceCallback.Stub() {
        /**
         * This is called by the remote service regularly to tell us about
         * new values.  Note that IPC calls are dispatched through a thread
         * pool running in each process, so the code executing here will
         * NOT be running in our main thread like most other things -- so,
         * to update the UI, we need to use a Handler to hop over there.
         */
        public void valueChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(BUMP_MSG, value, 0));
        }
    };
    
    private static final int BUMP_MSG = 1;
    
    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
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