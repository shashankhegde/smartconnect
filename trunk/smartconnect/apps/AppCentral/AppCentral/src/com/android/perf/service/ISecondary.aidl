package com.android.perf.service;

interface ISecondary {
    /**
     * Request the PID of this service, to do evil things with it.
     */
    int getPid();
    
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}
