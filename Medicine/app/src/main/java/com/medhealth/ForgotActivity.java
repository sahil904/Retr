package com.medhealth;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.medi.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Config.ConstValue;
import util.Common;
import util.ConnectionDetector;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi") 
public class ForgotActivity extends AppCompatActivity {
	public SharedPreferences settings;
	public ConnectionDetector cd;
	EditText txtPhone;
	Button btnLogin;
	String deviceid;
	Common common;
	ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot);
		getSupportActionBar().hide();
       common = new Common();
		
		settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
		cd=new ConnectionDetector(this);
		
		txtPhone = (EditText)findViewById(R.id.editPhone);

		
		btnLogin = (Button)findViewById(R.id.buttonLogin);
		btnLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(txtPhone.getText().toString().length()==0)
				{
					txtPhone.setError("Enter Mobile No");
				}
				else{
						new loginTask().execute(true);
					}
				// TODO Auto-generated method stub
				
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	class loginTask extends AsyncTask<Boolean, Void, String>{
		String temp_password;
		String phone;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			 phone = txtPhone.getText().toString();
			dialog = ProgressDialog.show(ForgotActivity.this, "", "Loading. Please Wait..", true);
			super.onPreExecute();
		}
		@Override
		protected void onPostExecute(String result) {
			if(result != null){
				Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
			}else{
				if(temp_password!=null && !temp_password.equalsIgnoreCase("")) {
					//Getting intent and PendingIntent instance
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage(txtPhone.getText().toString(), null, "Your reset temp password is : " +temp_password, null, null);
					finish();
				}
			}
			// TODO Auto-generated method stub
			dialog.dismiss();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}
		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
		}
		
		@Override
		protected String doInBackground(Boolean... params) {
			String responceString = null;

			List<NameValuePair> nameVapluePairs = new ArrayList<NameValuePair>(2);

			nameVapluePairs.add(new BasicNameValuePair("mobile",phone));

			JSONObject jObj = common.sendJsonData(ConstValue.JSON_FORGOT, nameVapluePairs);
			try{
				if(jObj.getString("responce").equalsIgnoreCase("success")){

					temp_password = jObj.getString("data");

				}
				else{
					responceString = jObj.getString("error");
				}
			}
			catch(JSONException e){
				responceString = e.getMessage();
			}
			// TODO Auto-generated method stub
			return responceString;
		}
		
				
	}
	}

