package com.android.perf;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class WebFacebook extends Activity implements
		android.view.View.OnClickListener {
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
				try {
					Thread.sleep(5000);
					Handler handler = new Handler();
					// handler.post(updateBar);
					handler.postDelayed(new Runnable() {
						public void run() {
						}
					}, 3000);
				} catch (Exception e) {

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

}