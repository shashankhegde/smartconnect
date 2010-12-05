package com.android.smartconnect.apps.slashdot;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

public class Slashdot extends Activity {
	
	TextView iUpdates = null;
	RequestHandler iRequestHandler = null;
	
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
	        iRequestHandler.StartUpdateCheck();
        }
        
        iUpdates.setText(data);
        
    }
}