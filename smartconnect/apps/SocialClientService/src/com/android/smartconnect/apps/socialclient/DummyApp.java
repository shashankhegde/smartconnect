package com.android.smartconnect.apps.socialclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DummyApp extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
	
	private static final String TAG = "DUMMY SERVICE";
	private static boolean serviceRunning = false;
	Button startButton, stopButton;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    
        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        
        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
    }

	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.startButton:
				if (!serviceRunning) {
					serviceRunning = true;
					Log.d(TAG, "onClick: Starting Service");
					startService(new Intent(this, DummyService.class));
				}
				break;
			case R.id.stopButton:
				if (serviceRunning) {
					serviceRunning = false;
					Log.d(TAG, "onClick: Stopping Service");
					stopService(new Intent(this, DummyService.class));	
				}
				break;
		}		
	}
}