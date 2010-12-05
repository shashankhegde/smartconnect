package com.android.smartconnect.apps.slashdot;

import com.android.smartconnect.apps.slashdot.IBackgroundServiceCallback;

interface IBackgroundService {

    int CheckUpdate(String aUrl, int aIntervalInSecs);
    int SetOnUpdateCallback(IBackgroundServiceCallback aBackgroundServiceCallback);
    
}