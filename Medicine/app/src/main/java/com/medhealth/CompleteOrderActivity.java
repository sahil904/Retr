package com.medhealth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.medi.R;

import util.Common;
import util.ConnectionDetector;

public class CompleteOrderActivity extends AppCompatActivity {
	public SharedPreferences settings;
	public ConnectionDetector cd;
	Common common;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_complete_order);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Button btn1 = (Button)findViewById(R.id.button);
		btn1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CompleteOrderActivity.this,MainActivity.class);
				intent.putExtra("fragment_position",3);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
			}
		});

		Button btn2 = (Button)findViewById(R.id.button2);
		btn2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CompleteOrderActivity.this,MainActivity.class);
				intent.putExtra("fragment_position",1);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
			}
		});
		Button btn3 = (Button)findViewById(R.id.button3);
		btn3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CompleteOrderActivity.this,MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
			}
		});
	}
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.placeorder, menu);
		return true;
	}*/

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		 if(id == android.R.id.home){
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
