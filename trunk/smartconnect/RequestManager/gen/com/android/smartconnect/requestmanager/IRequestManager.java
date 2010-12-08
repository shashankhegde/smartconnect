/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/shashank/cs276/project/smartconnect/smartconnect/requestmanager/src/com/android/smartconnect/requestmanager/IRequestManager.aidl
 */
package com.android.smartconnect.requestmanager;
public interface IRequestManager extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.android.smartconnect.requestmanager.IRequestManager
{
private static final java.lang.String DESCRIPTOR = "com.android.smartconnect.requestmanager.IRequestManager";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.android.smartconnect.requestmanager.IRequestManager interface,
 * generating a proxy if needed.
 */
public static com.android.smartconnect.requestmanager.IRequestManager asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.android.smartconnect.requestmanager.IRequestManager))) {
return ((com.android.smartconnect.requestmanager.IRequestManager)iin);
}
return new com.android.smartconnect.requestmanager.IRequestManager.Stub.Proxy(obj);
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
case TRANSACTION_GetData:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
long _arg1;
_arg1 = data.readLong();
com.android.smartconnect.requestmanager.RequestCallback _arg2;
_arg2 = com.android.smartconnect.requestmanager.RequestCallback.Stub.asInterface(data.readStrongBinder());
int _result = this.GetData(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.android.smartconnect.requestmanager.IRequestManager
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
public int GetData(java.lang.String aUrl, long aRequestId, com.android.smartconnect.requestmanager.RequestCallback aCallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(aUrl);
_data.writeLong(aRequestId);
_data.writeStrongBinder((((aCallback!=null))?(aCallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_GetData, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_GetData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public int GetData(java.lang.String aUrl, long aRequestId, com.android.smartconnect.requestmanager.RequestCallback aCallback) throws android.os.RemoteException;
}
