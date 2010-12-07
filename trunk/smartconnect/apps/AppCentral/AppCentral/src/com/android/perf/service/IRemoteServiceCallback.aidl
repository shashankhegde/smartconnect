package com.android.perf.service;
/*
 * Service sends the result and does not wait for the client to return
 */
oneway interface IRemoteServiceCallback {

    void valueChanged(int value);
}