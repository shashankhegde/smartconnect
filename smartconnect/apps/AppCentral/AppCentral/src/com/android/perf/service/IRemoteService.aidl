package com.android.perf.service;

import com.android.perf.service.IRemoteServiceCallback;

interface IRemoteService {

    void registerCallback(IRemoteServiceCallback cb);
    void unregisterCallback(IRemoteServiceCallback cb);
    String onService(int msg);
}