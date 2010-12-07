package com.android.perf.service;

import com.android.perf.service.IRemoteServiceCallback;

interface IRemoteService {

    boolean registerCallback(IRemoteServiceCallback cb);
    boolean unregisterCallback(IRemoteServiceCallback cb);
    String onService(int msg);
}