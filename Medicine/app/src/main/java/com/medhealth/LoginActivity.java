package com.medhealth;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import util.Common;
import util.ConnectionDetector;
import Config.ConstValue;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medi.R;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi") 
public class LoginActivity extends AppCompatActivity {
	public SharedPreferences settings;
	public ConnectionDetector cd;
	EditText txtPhone, txtPassword;
	Button btnLogin , otpLogin,register;
	String deviceid;
	Common common;
	ProgressDialog dialog;
	String titleNews,desc;

	//AsyncTask<Void, Void, Void> mRegisterTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		hitUrlForGetAName();
		Toolbar toolbar  = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("Login");

        common = new Common();
		settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
		cd=new ConnectionDetector(this);
		register = findViewById(R.id.register);
		txtPhone = (EditText)findViewById(R.id.editPhone);
		txtPassword = (EditText)findViewById(R.id.editPassword);
		btnLogin = (Button)findViewById(R.id.buttonLogin);

		otpLogin = findViewById(R.id.otploginbtn);

		otpLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LoginActivity.this,NewLoginActivity.class));
			}
		});

		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(txtPhone.getText().toString().length()==0)
				{
					txtPhone.setError(getString(R.string.forgotactivity_mobile_no));
				}
				else if (txtPassword.getText().toString().length()==0) {
					txtPassword.setError(getString(R.string.forgotactivity_enter_password));
				}
				else{
					new loginTask().execute(true);
				}
			}
		});

		//Button btnRegister = (Button)findViewById(R.id.buttonRegister);
		register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
				startActivity(intent);
                txtPhone.getText().clear();
                txtPassword.getText().clear();
			}
		});
		Button btnneed = (Button)findViewById(R.id.btnneed);
		btnneed.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
				startActivity(intent);
                txtPhone.getText().clear();
                txtPassword.getText().clear();
			}
		});


		/*btnLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(txtPhone.getText().toString().length()==0)
				{
					txtPhone.setError(getString(R.string.forgotactivity_mobile_no));
				}
				else if (txtPassword.getText().toString().length()==0) {
					txtPassword.setError(getString(R.string.forgotactivity_enter_password));
				}
				else{
						new loginTask().execute(true);
					}
				// TODO Auto-generated method stub

				
			}
		});
*/

	}

	private void hitUrlForGetAName()
	{
		StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstValue.GET_NEWS, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response );
					Log.d("Response",response);
					if (!jsonObject.getBoolean("error")) {
						JSONArray jsonArray = jsonObject.getJSONArray("data");

						JSONObject newsObj = jsonArray.getJSONObject(0);
						//titleNews = newsObj.getString("id");
						titleNews = newsObj.getString("title");
						desc = newsObj.getString("description");
						settings.edit().putString("desc",desc).commit();
						settings.edit().putString("title",titleNews).commit();
						Log.d("NameTitle",titleNews);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d("Error",String.valueOf(error));
			}
		});
		RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
		requestQueue.add(stringRequest);
	}
	class loginTask extends AsyncTask<Boolean, Void, String>{
		String phone,password;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			phone=txtPhone.getText().toString();
			password=txtPassword.getText().toString();
			dialog = ProgressDialog.show(LoginActivity.this, "",getString(R.string.loading), true);
			super.onPreExecute();
		}
		@Override
		protected void onPostExecute(String result) {
			if(result != null){
				Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
			}else{
				startgcmregistration();
				Intent intent = new Intent(LoginActivity.this,MainActivity.class);
				intent.putExtra("title",titleNews);
				//intent.putExtra("drawer_name",settings.getString(" drawer_name"));
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
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
			nameVapluePairs.add(new BasicNameValuePair("mobile", phone));
			nameVapluePairs.add(new BasicNameValuePair("password",password));
			
			JSONObject jObj = common.sendJsonData(ConstValue.JSON_LOGIN, nameVapluePairs);
			Log.d("LoginResponse",String.valueOf(jObj));
			try{
				if(jObj.getString("responce").equalsIgnoreCase("success")){
					JSONObject data = jObj.getJSONObject("data");
					if(!data.getString("id").equalsIgnoreCase("")){
						settings.edit().putString("userid", data.getString("id")).commit();
						settings.edit().putString("username", data.getString("username")).commit();
						settings.edit().putString("user_unique_code", data.getString("unique_code")).commit();
						settings.edit().putString("user_email", data.getString("email")).commit();
						settings.edit().putString("user_name", data.getString("name")).commit();
						settings.edit().putString("user_mobile", data.getString("mobile")).commit();
						settings.edit().putString("user_address", data.getString("address")).commit();
						settings.edit().putString("user_state", data.getString("state")).commit();
						settings.edit().putString("user_country", data.getString("country")).commit();
						settings.edit().putString("user_zipcode", data.getString("zipcode")).commit();
						settings.edit().putString("user_city", data.getString("city")).commit();
						settings.edit().putString("user_password", data.getString("password")).commit();
						settings.edit().putString("user_image", data.getString("image")).commit();
						settings.edit().putString("user_phone_verified", data.getString("phone_verified")).commit();
						settings.edit().putString("user_reg_date", data.getString("reg_date")).commit();
						settings.edit().putString("user_status", data.getString("status")).commit();
						//settings.edit().putString("drawer_name", data.getString("drawer_name")).commit();
					}
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
	public void startgcmregistration(){
	}
	}

