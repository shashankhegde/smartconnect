package com.android.perf;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class WebFacebook extends Activity implements android.view.View.OnClickListener{
	WebView mWebView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.webview);

		// Creating WebView
		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings();
		mWebView.loadUrl("http://m.naver.com");

		Button reload10Button = (Button) findViewById(R.id.button_reload10);
		Button reload30Button = (Button) findViewById(R.id.button_reload30);
		Button reload50Button = (Button) findViewById(R.id.button_reload50);
//		reload10Button.setOnClickListener(new View.OnClickListener());
		
		reload10Button.setOnClickListener(this);
		reload30Button.setOnClickListener(this);
		reload50Button.setOnClickListener(this);
	}
	@Override
	public void onClick(View src) {
		switch (src.getId()) {
		case R.id.button_reload10:
			for (int i = 0; i < 10; i++) {
				mWebView.loadUrl("http://m.naver.com");
				// Sleep 3 Seconds Here..
				try{
					Thread.sleep(5000);
			    Handler handler = new Handler();
//			    handler.post(updateBar);
			    handler.postDelayed(new Runnable() { 
			         public void run() { 
			         } 
			    }, 3000); 
				}catch(Exception e){
					
				}
			}
			break;
			
		case R.id.button_reload30:
			for (int i = 0; i < 30; i++) {
				mWebView.loadUrl("http://m.naver.com");
			}
			break;
			
		case R.id.button_reload50:
			for (int i = 0; i < 50; i++) {
				mWebView.loadUrl("http://m.naver.com");
			}
			break;
		}
	}

	// setContentView(webView);
	// setContentView(R.layout.webview);

	// final String mimeType = "text/html";
	// final String encoding = "utf-8";
	//
	// WebView wv;

	// getWindow().requestFeature(Window.FEATURE_PROGRESS);

	// final Activity activity = this;
	// webView.setWebChromeClient(new WebChromeClient() {
	// public void onProgressChanged(WebView view, int progress) {
	// // Activities and WebViews measure progress with different scales.
	// // The progress meter will automatically disappear when we reach 100%
	// activity.setProgress(progress * 1000);
	// }
	// });
	// webView.setWebViewClient(new WebViewClient() {
	// public void onReceivedError(WebView view, int errorCode, String
	// description, String failingUrl) {
	// Toast.makeText(activity, "Oh no! " + description,
	// Toast.LENGTH_SHORT).show();
	// }
	// });

	// setContentView(R.layout.main);

	// Loading Web-page
	// webView.loadUrl("touch.facebook.com");

	/**
	 * MENUS
	 * 
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * 
	 *           new MenuInflater(this).inflate(R.menu.menu, menu);
	 *           return(super.onCreateOptionsMenu(menu)); }
	 * 
	 *           public boolean onOptionsItemSelected(MenuItem item) { switch
	 *           (item.getItemId()) { //identify the menu item case R.id.reload:
	 *           // Intent bluetooth = new
	 *           Intent(WebFacebook.this,test_myBlue2.class); //
	 *           startActivity(bluetooth); WebView.reload(); break; // case
	 *           R.id.map: // Intent map = new
	 *           Intent(WebFacebook.this,GoogleMapActivity.class); //
	 *           startActivity(map); // break; // case R.id.login: // Intent
	 *           login = new Intent(WebFacebook.this,LoginActivity.class); //
	 *           startActivity(login); // break; // case R.id.webview: // Intent
	 *           webview = new Intent(WebFacebook.this,WebViewActivity.class);
	 *           // startActivity(webview); // break; // case R.id.friend: //
	 *           Intent friend = new Intent(WebFacebook.this,FriendList.class);
	 *           // startActivity(friend); // break; // case R.id.ride: ////
	 *           Intent ride = new Intent(WebFacebook.this,RidesActivity.class);
	 *           //// startActivity(ride); // break; // case R.id.request: //
	 *           Intent request = new
	 *           Intent(WebFacebook.this,RequestActivity.class); //
	 *           startActivity(request); // case R.id.ridelist: // Intent
	 *           ridelist = new Intent(WebFacebook.this,RidesActivity.class); //
	 *           startActivity(ridelist); // break; // case R.id.rideinfo: //
	 *           Intent rideinfo = new
	 *           Intent(WebFacebook.this,RideViewActivity.class); //
	 *           startActivity(rideinfo); // break;
	 * 
	 *           } // if (message!=null) { // Toast.makeText(this, message,
	 *           Toast.LENGTH_SHORT).show(); // return(true); // }
	 *           return(super.onOptionsItemSelected(item)); }
	 */

}