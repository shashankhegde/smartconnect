package com.android.smartconnect.requestmanager;

oneway interface RequestCallback {

    void onDataReceived(long aRequestId, int aDataLen);
    
} 