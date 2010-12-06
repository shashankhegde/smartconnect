package com.android.perf;

//import java.util.Locale;

//import cs290.test.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.android.perf.service.IRemoteService;
import com.android.perf.service.IRemoteServiceCallback;
import com.android.perf.service.RemoteInterface;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class Main extends Activity implements OnClickListener {

	private static final String TAG = "RequestActivity";

	// UI Objects
	Button ButtonPick1, ButtonPick2, ButtonDate, ButtonTime, ButtonSubmit, ButtonFriend;
	RadioButton RadioButton01, RadioButton02, driverchk, passengerchk;
	TextView TextView_name;
	// EditText - Find Address with typed content
	private EditText EditText_dpt;
	private EditText EditText_date;
	private EditText EditText_time;
	
	// TODO deprecate!
	private static String address1 = "Input Address Here!";
	private static String address2 = "Input Address Here!";
	
	// Date and Time Obj
	DatePicker date;
	TimePicker time;
	Calendar c = Calendar.getInstance();

	String url = "http://www.google.com/maps/api/geocode/json?address=$s&sensor=true";

//	private List<Address> address;
	
	static final int DATE_DIALOG_ID = 0;
	static final int TIME_DIALOG_ID = 1;
	
	/** RemoteProxy*/
	static RemoteInterface mRemoteInterfaceService = null;
	private IRemoteService mService = null;
	private Handler mHandler = null;
	
	
	static TextView tv01=null;
	static TextView tv02=null;
	static TextView callBack=null;
	
	Button stop = null;
	
	boolean mlsBound = false;
	
	// call 'the Remote Method'
	// use mRemoteInterfaceService obj to call methods of RemoteStub
	// Must be implemented
	
	// Service Component
	private IRemoteServiceCallback mCallback = new IRemoteServiceCallback.Stub(){
		public void MessageCallback(int msg){
			mHandler.sendEmptyMessage(msg);
		}
	};
	
	private ServiceConnection mSecondaryConnection = new ServiceConnection(){
		
		public void onServiceConnected(ComponentName name, IBinder service){
			mRemoteInterfaceService = RemoteInterface.Stub.asInterface(service);
			
			if(mRemoteInterfaceService != null){
				printHello();
				Log.d("RemoteProxy", "ServiceConnection-mRemoteInterfaceService is not null");
			}else{
				Log.d("RemoteProxy", "ServiceConnection-mRemoteInterfaceService is null");
			}
		}
		
		public void onServiceDisconnected(ComponentName name){
			mRemoteInterfaceService = null;
		}
	};
	/**RemoteProxy end*/
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainpage);
		
		
		/** RemoteProxy */
		tv01 = (TextView) this.findViewById(R.id.sayHello);
		tv02 = (TextView) this.findViewById(R.id.sayHelloByName);
		callBack = (TextView) this.findViewById(R.id.callBack);

		stop = (Button) this.findViewById(R.id.stop);

		stop.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {

				stopBindService();
			}
		});

		// Call the access of Remote Method
		ComponentName cn = this.startService(new Intent(
				"com.remoteStub.RemoteInterface"));
//		Log.d("RemoteProxy", "Start IRemoteService service : " + cn.toString()); // TODO (NullPointerEX)

		// request Bind to communicate constantly
		startBindService();
		/** RemoteProxy End */

		// put values to EditText

		Log.d(TAG, "onCreate started");

		// DatePicker, TimePicker
		c = new GregorianCalendar();		

		// UI Constructor -- I now have all my UI in the Java memory
		ButtonPick1 = (Button) findViewById(R.id.ButtonPick1);
		ButtonDate = (Button) findViewById(R.id.ButtonDate);
		ButtonTime = (Button) findViewById(R.id.ButtonTime);
		RadioButton01 = (RadioButton) findViewById(R.id.RadioButton01);
		RadioButton02 = (RadioButton) findViewById(R.id.RadioButton02);
		ButtonSubmit = (Button) findViewById(R.id.ButtonSubmit);
		
		TextView_name = (TextView) findViewById(R.id.TextView_name);
		
		EditText_dpt = (EditText) findViewById(R.id.EditText_dpt);
		EditText_date = (EditText) findViewById(R.id.EditText_date);
		EditText_time = (EditText) findViewById(R.id.EditText_time);
		
		// UI Setter
		TextView_name.setText("test");
			
		
		EditText_dpt.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {

						EditText_dpt.setText("test");

					return true;
				}
				return false;
			}
		});

		// Define button listeners
		ButtonPick1.setOnClickListener(this);
		ButtonDate.setOnClickListener(this);
		ButtonTime.setOnClickListener(this);
		ButtonSubmit.setOnClickListener(this);

		Log.d(TAG, "onCreate done");
			
	}
	
	/** RemoteProxy */
	
	public static void printHello() {

		String sayHello = "";
		String sayHelloByName = "";

		// call Remode Method
		try {
			sayHello = mRemoteInterfaceService.getSayHello();
			sayHelloByName = mRemoteInterfaceService.getSayHelloByName("Jay");

			callBack.setText("exec printHello");

		} catch (RemoteException e) {
			e.printStackTrace();
		}
		tv01.setText(sayHello);
		tv02.setText(sayHelloByName);
    }
    
	private void startBindService(){
		
		boolean bindFlag02 = bindService(new Intent("com.remoteStub.RemoteInterface"), mSecondaryConnection, Context.BIND_AUTO_CREATE);
		Log.d("RemoteProxy", "RemoteInterface-bind : "+String.valueOf(bindFlag02));
		
		callBack.setText("Binding..");
		mlsBound = true;
	}
	
	// Disconnect Connection
	private void stopBindService(){
		if(mlsBound){
			if (mService != null){
//			if (mRemoteInterfaceService != null){
				try{
					mService.unregisterCallback(mCallback);
				}catch(RemoteException e){
					// Nothing to do if the service has crashed
				}
			}
			unbindService(mSecondaryConnection);
		}
		callBack.setText("Unbinding...");
	}
	
	/** RemoteProxy End */
	
	// Creating Dialog
	@Override
	protected Dialog onCreateDialog(int id){
		int cyear = c.get(Calendar.YEAR);
		int cmonth = c.get(Calendar.MONTH);
		int cday = c.get(Calendar.DAY_OF_MONTH);
		int chour = c.get(Calendar.HOUR_OF_DAY);
		int cmin = c.get(Calendar.MINUTE);
		boolean is24hourview = true;
		
		switch(id){
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this,mDateSetListener,cyear,cmonth,cday);
		
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this,mTimeSetListener,chour,cmin, is24hourview);
		}
		return null;		
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		// onDateSet method
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			c.set( Calendar.YEAR, year );
			c.set( Calendar.MONTH, monthOfYear );
			c.set( Calendar.DAY_OF_MONTH, dayOfMonth );
			
			String strDate;
			strDate = Integer.toString( year ) + "/" +
			Integer.toString( monthOfYear ) + "/" +
			Integer.toString( dayOfMonth );
			EditText_date.setText(strDate);
			
			String date_selected = String.valueOf(monthOfYear + 1) + " /"
					+ String.valueOf(dayOfMonth) + " /" + String.valueOf(year);
			Toast.makeText(Main.this,
					"Selected Date is =" + date_selected, Toast.LENGTH_SHORT)
					.show();
		}
	};
		
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		// onTimeSet method
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            c.set( Calendar.HOUR_OF_DAY, hourOfDay );
            c.set( Calendar.MINUTE, minute );
            
            String strTime;
            strTime =
            Integer.toString( hourOfDay ) + ":" +
            Integer.toString( minute );
            EditText_time.setText(strTime);
			
			String time_selected = String.valueOf(hourOfDay) + " : "
					+ String.valueOf(minute);
			Toast.makeText(Main.this,
					"Selected Time is =" + time_selected, Toast.LENGTH_SHORT)
					.show();
		}
	};
	
	@Override
	public void onClick(View src) {
		switch (src.getId()) {
		case R.id.ButtonPick1:
			Intent intent1 = new Intent(this, WebFacebook.class);
			startActivity(intent1);
			break;

		case R.id.ButtonDate:
//			session.setLastClickedButton(src.getId());
//			RideSessionStore.save(session, this);
//			if (src == ButtonDate)
				showDialog(DATE_DIALOG_ID);
			break;
			
		case R.id.ButtonTime:
//			session.setLastClickedButton(src.getId());
//			RideSessionStore.save(session, this);
//			if (src == ButtonTime)
				showDialog(TIME_DIALOG_ID);
			break;
			
		case R.id.ButtonSubmit:
			Intent intent5 = new Intent(this, WebFacebook.class);
			startActivity(intent5);
			break;
		}
	}

	public Main() {
		// TODO Auto-generated constructor stub
	}

}
