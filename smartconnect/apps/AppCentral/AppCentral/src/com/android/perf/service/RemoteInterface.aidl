package com.android.perf.service;

import com.android.perf.service.IRemoteServiceCallback;

interface RemoteInterface{
	String getSayHello();
	String getSayHelloByName(String name);
}