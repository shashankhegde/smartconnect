package com.android.smartconnect.apps.slashdot;

oneway interface IBackgroundServiceCallback {

    void onUpdateReceived(String aData);
    
}