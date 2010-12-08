/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/shashank/cs276/project/smartconnect/smartconnect/requestmanager/src/com/android/smartconnect/requestmanager/RequestCallback.aidl
 */
package com.android.smartconnect.requestmanager;
public interface RequestCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.android.smartconnect.requestmanager.RequestCallback
{
private static final java.lang.String DESCRIPTOR = "com.android.smartconnect.requestmanager.RequestCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.android.smartconnect.requestmanager.RequestCallback interface,
 * generating a proxy if needed.
 */
public static com.android.smartconnect.requestmanager.RequestCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.android.smartconnect.requestmanager.RequestCallback))) {
return ((com.android.smartconnect.requestmanager.RequestCallback)iin);
}
return new com.android.smartconnect.requestmanager.RequestCallback.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_onDataReceived:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
int _arg1;
_arg1 = data.readInt();
this.onDataReceived(_arg0, _arg1);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.android.smartconnect.requestmanager.RequestCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void onDataReceived(long aRequestId, int aDataLen) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(aRequestId);
_data.writeInt(aDataLen);
mRemote.transact(Stub.TRANSACTION_onDataReceived, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
}
static final int TRANSACTION_onDataReceived = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void onDataReceived(long aRequestId, int aDataLen) throws android.os.RemoteException;
}
