package com.medhealth;

import util.ConnectionDetector;
import Config.ConstValue;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medi.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class SplashActivity extends AppCompatActivity implements Runnable{
	public SharedPreferences settings;

	private static final int SPLASH_TIME_OUT = 3000;
	public ConnectionDetector cd;
	Handler handler;
	String titleNews,desc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		hitUrlForGetAName();

		int secondsDelayed = 2;
		handler = new Handler();
        getSupportActionBar().hide();
		settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
		cd = new ConnectionDetector(this);
		//hitUrlForGetAName();
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
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
/*
    @Override
    protected void onStart() {
        super.onStart();
        //hitUrlForGetAName();
    }*/

    @Override
	protected void onResume() {
		super.onResume();
		handler.postDelayed(this, SPLASH_TIME_OUT);
	}
	@Override
	protected void onPause() {
		super.onPause();
		handler.removeCallbacks(this);
	}

	@Override
	public void run() {
		if (!settings.getString("userid", "").equalsIgnoreCase("")) {
			Intent intent = new Intent(SplashActivity.this, MainActivity.class);
			intent.putExtra("title",titleNews);
			startActivity(intent);
			finish();
		}else {
			Intent intent = new Intent(SplashActivity.this, BeforeLoginActivity.class);
			startActivity(intent);
			finish();
		}
	}
	/*@Override
	protected void onStop() {
		super.onStop();
		if (handler!=null){
			handler.removeCallbacksAndMessages(null);
		}
	}*/

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
						 settings.edit().putString("desc",desc).apply();
						 settings.edit().putString("icon",newsObj.getString("icon")).apply();
						 settings.edit().putString("title",titleNews).apply();
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
		RequestQueue requestQueue = Volley.newRequestQueue(SplashActivity.this);
		requestQueue.add(stringRequest);
	}
}
