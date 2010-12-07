package com.android.perf.service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class TrafficMaker implements Runnable {
	
	private long intervalMilliseconds;
	private URL url;
	private LogHelper logHelper;
	
	public TrafficMaker() {
		this("http://www.google.com", 5000);
	}
	
	public TrafficMaker(String urlstring, long intervalMilliseconds) {
		try {
			this.url = new URL(urlstring);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		this.intervalMilliseconds = intervalMilliseconds;
		this.logHelper = new LogHelper(urlstring);
	}

	public void run() {
		int bytesRead = 0;
		while (true) {
			try {
				logHelper.addLog("SEND");
				bytesRead = wgetPage();
				logHelper.addLog("RECV | " + bytesRead);
				Thread.sleep(intervalMilliseconds);
			} catch (Exception e) {
				//TODO
			}
		}
	}
	
	private int wgetPage() throws MalformedURLException, IOException {
		
		String urlData = "";
		String inputLine;
		
		BufferedReader in = new BufferedReader(
					new InputStreamReader(
					url.openStream()));

		while ((inputLine = in.readLine()) != null)
			urlData += inputLine + '\n';

		in.close();
		
		return urlData.length();
	}

}
	