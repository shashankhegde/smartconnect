package com.android.smartconnect.apps.socialclient;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Timestamp;
import java.util.Date;
import java.util.Random;

import android.os.IBinder;
import android.os.RemoteException;

import com.android.smartconnect.requestmanager.IRequestManager;
import com.android.smartconnect.requestmanager.RequestCallback;
import com.android.smartconnect.requestmanager.RequestManagerService;

public class TrafficMaker implements Runnable {
	
	private long intervalMilliseconds;
	private URL url;
	private LogHelper logHelper;
	private IRequestManager reqManagerService;
	private long iVariance;
	private Random iRandomNumberGenerator;
	
	public TrafficMaker(IRequestManager service) {
		this("http://www.google.com", 5000, 0,service);
	}
	
	public TrafficMaker(String urlstring, long intervalMilliseconds, long aVariance, IRequestManager service) {
		try {
			this.url = new URL(urlstring);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		this.intervalMilliseconds = intervalMilliseconds;
		this.logHelper = new LogHelper(urlstring);
		this.reqManagerService = service;
		this.iVariance = aVariance;
		iRandomNumberGenerator = new Random((new Date()).getTime());
	}
	
/*	class IncomingHandler implements RequestCallback {// TODO Auto-generated method stub

		public void onDataReceived(String arg0) throws RemoteException {
			logHelper.addLog("RECV | " + arg0.length() + ";");
		}

		public IBinder asBinder() {
			// TODO Auto-generated method stub
			return null;
		}
	    
	}
*/
	RequestCallback iIncomingHandler = new RequestCallback.Stub() {
		
		public void onDataReceived(String arg0) throws RemoteException {
			logHelper.addLog("RECV | " + arg0.length() + ";");
		}
	};
	
	public void run() {
		
/*		if(iIncomingHandler == null) {
			iIncomingHandler = new IncomingHandler();
		}
*/		
		int bytesRead = 0;
		while (true) {
			try {
				String strUrl = url.toExternalForm();
				logHelper.addLog("SEND");
				//bytesRead = wgetPage();
				//logHelper.addLog("RECV | " + bytesRead);

				reqManagerService.GetData(strUrl, iIncomingHandler);

				int extraDelay = iRandomNumberGenerator.nextInt((int) iVariance/2);
				if( iRandomNumberGenerator.nextBoolean() == false )
					extraDelay *= -1;
				
				Thread.sleep(intervalMilliseconds+extraDelay);
			} catch (Exception e) {
				logHelper.addLog("++Exception: " + e.toString());
				if (reqManagerService == null) {
					logHelper.addLog("reqManager is null");
				}
				break;
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
	