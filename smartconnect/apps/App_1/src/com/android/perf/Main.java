package com.android.perf;

//import java.util.Locale;

//import cs290.test.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainpage);

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
