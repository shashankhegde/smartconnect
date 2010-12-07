package com.android.smartconnect.apps.socialclient;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Timestamp;
import java.util.Date;
import java.util.Random;

public class TrafficMaker implements Runnable {
	
	private long intervalMilliseconds;
	private URL url;
	private LogHelper logHelper;
	private long iVariance = 0;
	private Random iRandomNumberGenerator;
	
	public TrafficMaker() {
		this("http://www.google.com", 5000, 0);
	}
	
	public TrafficMaker(String urlstring, long intervalMilliseconds, long aVariance) {
		try {
			this.url = new URL(urlstring);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		this.intervalMilliseconds = intervalMilliseconds;
		this.logHelper = new LogHelper(urlstring);
		this.iVariance = aVariance;
		iRandomNumberGenerator = new Random((new Date()).getTime());
	}

	public void run() {
		int bytesRead = 0;
		while (true) {
			try {
				logHelper.addLog("SEND");
				bytesRead = wgetPage();
				logHelper.addLog("RECV | " + bytesRead);
				int extraDelay = iRandomNumberGenerator.nextInt((int) iVariance/2);
				if( iRandomNumberGenerator.nextBoolean() == false )
					extraDelay *= -1;
				
				Thread.sleep(intervalMilliseconds+extraDelay);
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
	