package com.android.smartconnect.requestmanager;

import com.android.smartconnect.requestmanager.RequestCallback;

interface IRequestManager {

    int GetData(String aUrl, long aRequestId, RequestCallback aCallback);
}