package com.android.smartconnect.apps.slashdot;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class Slashdot extends Activity implements OnTouchListener, OnClickListener {
	
	TextView iUpdates = null;
	RequestHandler iRequestHandler = null;
	SeekBar iUpdateInterval = null;
	Button iBtnStartUpdate = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        iUpdates = (TextView)findViewById(R.id.tvUpdates);
        iUpdates.setText("hello world...");

        String data = "";
        if(iRequestHandler == null ) {
	        iRequestHandler = new RequestHandler(this,"http://rss.slashdot.org/Slashdot/slashdot",30);
        }
        
        iUpdateInterval = (SeekBar)findViewById(R.id.sbInterval);
        iUpdateInterval.setProgress(1); // Default is 1 min
        
        iUpdateInterval.setOnTouchListener((OnTouchListener) this);
        
        iBtnStartUpdate = (Button)findViewById(R.id.btnStartUpdate);
        iBtnStartUpdate.setOnClickListener(this);
        
        iUpdates.setText(data);
        
    }
    
    public void onDestroy() {
    	Log.i(getClass().getName(),"onDestroy()");
    	iRequestHandler.Cleanup();
    	super.onDestroy();
    }

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(v.getId()) {
		case R.id.sbInterval:
			int val = iUpdateInterval.getProgress();
			Log.i(getClass().getName(), "Value : " + String.valueOf(val));
			iRequestHandler.SetUpdateInterval(val*60);
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btnStartUpdate:
			int val = iUpdateInterval.getProgress();
			Log.i(getClass().getName(), "Value : " + String.valueOf(val));
			iRequestHandler.SetUpdateInterval(val*60);
	        iRequestHandler.StartUpdateCheck();
			break;
		}
	}
}