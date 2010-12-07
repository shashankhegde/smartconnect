package com.android.perf;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class PerfAnalyzer extends TabActivity {
	


	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
        		
		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab
		
//		setNotification(); // Notification call

		// Create an Intent to launch an Activity for the tab (to be reused)

		intent = new Intent().setClass(this, Main.class);
		spec = tabHost.newTabSpec("mainpage")
				.setIndicator("MAINPAGE", res.getDrawable(R.drawable.icon))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, RemoteServiceBinding.class);
		spec = tabHost.newTabSpec("binding")
				.setIndicator("BIND", res.getDrawable(R.drawable.icon))
				.setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this, WebFacebook.class);
		spec = tabHost.newTabSpec("view")
				.setIndicator("VIEW", res.getDrawable(R.drawable.icon))
				.setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(1);
	}
	

	
	private void setNotification(){
		NotificationManager notiMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification noti = new Notification(
				android.R.drawable.stat_notify_error,
				"Need A Ride - NOTIFICATION",
				System.currentTimeMillis());
		
		Intent intent = new Intent();
		intent.setClassName("com.android.test", "com.android.test.RideViewActivity");
		
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
		
		noti.flags |=Notification.FLAG_ONGOING_EVENT;
		noti.icon = R.drawable.icon;
		noti.setLatestEventInfo(this, "NEED A RIDE", "NEW RIDE UPDATE is available!", contentIntent);
		notiMgr.notify(1, noti);
	}
	

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	} 

	/** MENUS **/
	/**
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
    	new MenuInflater(this).inflate(R.menu.requestmenu, menu);
		return(super.onCreateOptionsMenu(menu));
	}
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) { //identify the menu item
			case R.id.bluetooth:
				Intent bluetooth = new Intent(Test.this,test_myBlue2.class);
			    startActivity(bluetooth);
			    break;
			case R.id.map:
				Intent map = new Intent(Test.this,GoogleMapActivity.class);
			    startActivity(map);
			    break;
			case R.id.login:
				Intent login = new Intent(Test.this,LoginActivity.class);
			    startActivity(login);
				break;
			case R.id.webview:
				Intent webview = new Intent(Test.this,WebViewActivity.class);
			    startActivity(webview);
				break;
			case R.id.friend:
				Intent friend = new Intent(Test.this,FriendList.class);
			    startActivity(friend);
				break;
			case R.id.ride:
//				Intent ride = new Intent(Test.this,RidesActivity.class);
//			    startActivity(ride);
			    break;
			case R.id.request: 
				Intent request = new Intent(Test.this,RequestActivity.class);
			    startActivity(request);
			case R.id.ridelist:
				Intent ridelist = new Intent(Test.this,RidesActivity.class);
			    startActivity(ridelist);
				break;
			case R.id.rideinfo:
				Intent rideinfo = new Intent(Test.this,RideViewActivity.class);
			    startActivity(rideinfo);
				break;
			
		}
//		if (message!=null) {
//			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//			return(true);
//		}
		return(super.onOptionsItemSelected(item));
	}
    */
} 
