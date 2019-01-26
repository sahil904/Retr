package com.medhealth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.medi.R.id;
import com.medi.R;

import models.CartModel;
import models.ImageModel;
import util.Common;
import util.ConnectionDetector;
import Config.ConstValue;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class CheckoutActivity extends AppCompatActivity {
	public SharedPreferences settings;
	public ConnectionDetector cd;
	EditText txtCity,txtName,txtAddress,txtZipcode,txtEmail, txtPhone;
	Button btnSave;
	ProgressDialog dialog;
	Common common;
	ArrayList<CartModel> cartList;
	ImageView logo;
	Bitmap bitmap;
	ArrayList<ImageModel> list;
	RecyclerView recyclerView;
	String selectedImagePath;
	Button close;

	@SuppressLint("CutPasteId") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkout);
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		recyclerView=findViewById(R.id.recyler_camera);
		list=new ArrayList<>();

		logo = findViewById(R.id.logo);
		close=findViewById(R.id.close);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		 common = new Common();

		 settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
		 cd=new ConnectionDetector(this);
		 cartList = (ArrayList<CartModel>) getIntent().getSerializableExtra("cart_list");

		// Log.d("cartList",String.valueOf(cartList.size()));

		// Log.d("checkPrice",cartList.get(0).salePrice);
		 txtCity = (EditText)findViewById(R.id.editCity);
		 txtName = (EditText)findViewById(R.id.editFname);
		 txtPhone = (EditText)findViewById(R.id.editPhone);
		 txtAddress = (EditText)findViewById(R.id.editAddress);
		 txtZipcode = (EditText)findViewById(R.id.editZipcode);
		 txtEmail = (EditText)findViewById(R.id.editEmail);
			

		 txtName.setText(settings.getString("user_name", ""));

		 txtEmail.setText(settings.getString("user_email", ""));
		 txtPhone.setText(settings.getString("user_mobile", ""));
	    
		 txtAddress.setText(settings.getString("user_address", ""));
			
	    
		 txtZipcode.setText(settings.getString("user_zipcode", ""));
	        

		 txtCity.setText(settings.getString("user_city", ""));
		 btnSave = (Button)findViewById(id.btnsave);
		 btnSave.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {

					// TODO Auto-generated method stub

					if(txtCity.getText().toString().length()==0)
					{
						txtCity.setError("Enter City");
					}else if(txtEmail.getText().toString().length()==0)
					{
						txtEmail.setError("Enter Email");
					}
					else if (txtName.getText().toString().length()==0) {
						txtName.setError("Enter Name");
					}
					else if (txtAddress.getText().toString().length()==0) {
						txtAddress.setError("Enter Address");
					}
					
					else if (txtZipcode.getText().toString().length()==0) {
						txtZipcode.setError("Enter Zipcode");
					}
					else{

						new ProfileupdateTask().execute(true);

						settings.edit().putString("order_name", txtName.getText().toString()).commit();
						settings.edit().putString("order_city", txtCity.getText().toString()).commit();
						settings.edit().putString("order_email", txtEmail.getText().toString()).commit();
						settings.edit().putString("order_zipcode", txtZipcode.getText().toString()).commit();
						settings.edit().putString("order_address", txtAddress.getText().toString()).commit();
						settings.edit().putString("order_phone", txtPhone.getText().toString()).commit();
						}
				}
			});

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			return;
		}
		Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
		String city =locationhere(location.getLatitude(),location.getLongitude());
		txtAddress.setText(city);
	}

	private String locationhere(double lat,double lon){
		String cityname="";
		String sublocality="";
		Geocoder geocoder=new Geocoder(this,Locale.getDefault());
		List<Address> addresses;
		try {
			addresses=geocoder.getFromLocation(lat,lon,10);
			if (addresses.size()>=0){for (Address adr:addresses){
				if (adr.getLocality()!=null && adr.getLocality().length()>0){
					cityname=adr.getLocality()+" "+adr.getSubLocality();
					break;
				}
			}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cityname;
	}
	class ProfileupdateTask extends AsyncTask<Boolean, Void, String> {
		String txtphone,txtcity,txtname,txtaddress,txtzipcode,txtemail,txtusername;
		@Override
		protected String doInBackground(Boolean... params) {
			String responceString = null;
			List<NameValuePair> nameVapluePairs = new ArrayList<NameValuePair>(2);

			nameVapluePairs.add(new BasicNameValuePair("mobile", txtphone));
			nameVapluePairs.add(new BasicNameValuePair("city",txtcity));
			nameVapluePairs.add(new BasicNameValuePair("name",txtname));
			nameVapluePairs.add(new BasicNameValuePair("address",txtaddress));
			nameVapluePairs.add(new BasicNameValuePair("zipcode",txtzipcode));
			nameVapluePairs.add(new BasicNameValuePair("email",txtemail));
			nameVapluePairs.add(new BasicNameValuePair("username",txtusername));
			nameVapluePairs.add(new BasicNameValuePair("id",settings.getString("userid","")));

			JSONObject jObj = common.sendJsonData(ConstValue.JSON_PROFILE_UPDATE, nameVapluePairs);
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
		protected void onPostExecute(String result) {

			Intent intent = new Intent(CheckoutActivity.this,NewPlaceOrderActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.putExtra("cart_list",cartList);
			intent.putExtra("total_amount",getIntent().getStringExtra("total_amount"));
			startActivity(intent);
			finish();

			// TODO Auto-generated method stub
			dialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			txtphone = txtPhone.getText().toString();
			txtcity =txtCity.getText().toString();
			txtname =txtName.getText().toString();
			txtaddress = txtAddress.getText().toString();
			txtzipcode = txtZipcode.getText().toString();
			txtemail =txtEmail.getText().toString();
			dialog = ProgressDialog.show(CheckoutActivity.this, "",getString(R.string.loading), true);
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}else if(id == android.R.id.home){
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

}