package com.android.smartconnect.requestmanager;

import java.util.concurrent.ArrayBlockingQueue;


public class RequestQueue extends ArrayBlockingQueue<UrlRequest> {

	public RequestQueue(int capacity) {
		super(capacity);
	}
}
