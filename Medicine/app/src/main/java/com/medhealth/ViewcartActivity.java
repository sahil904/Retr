package com.medhealth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import fragments.MyCart;
import util.ConnectionDetector;
import Config.ConstValue;
import adapters.ViewCartAdapter;
import util.Interfac;
import util.ObjectSerializer;

import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.medi.R;

public class ViewcartActivity extends AppCompatActivity {

	public SharedPreferences settings;
	public ConnectionDetector cd;
	ArrayList<HashMap<String, String>> cartArray;
	ViewCartAdapter adapter;
	ListView listProduct;
	LinearLayout checkout;

	TextView txtQty,txtitemtotal,txttotalprice;
	TextView txtDeliverycharge;
	TextView txtTotalPrice;
	HashMap<String,String> site_settings;
	MyCart cart ;
	String delivery_charge;
	RelativeLayout emptyCartLayout;
	@SuppressLint("CutPasteId") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewcart);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		cd = new ConnectionDetector(getApplicationContext());
		settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
        emptyCartLayout = findViewById(R.id.empty_cart_layout);
		site_settings = new HashMap<String,String>();
		try {
			site_settings = (HashMap<String,String>) ObjectSerializer.deserialize(settings.getString("site_settings", ObjectSerializer.serialize(new HashMap<String, String>())));
		}catch (IOException e) {
			e.printStackTrace();
		}

		cartArray = new ArrayList<HashMap<String,String>>();
		cart = new MyCart(getApplicationContext());
		cartArray = cart.get_items();

		txttotalprice=(TextView)findViewById(R.id.textView2);

		listProduct = (ListView)findViewById(R.id.listView1);
		checkout = (LinearLayout)findViewById(R.id.footerlayout);
		//code to add header when cart is empty
		if(cartArray.size()==0){
			listProduct.setVisibility(View.GONE);
			emptyCartLayout.setVisibility(View.VISIBLE);
			checkout.setVisibility(checkout.GONE);


		}else {
			listProduct.setVisibility(View.VISIBLE);
			emptyCartLayout.setVisibility(View.GONE);
			checkout.setVisibility(checkout.VISIBLE);
		}


		Interfac interfac = new Interfac() {

			@Override
			public void change() {

			}
		};
		adapter = new ViewCartAdapter(getApplicationContext(), cartArray,interfac);
		listProduct.setAdapter(adapter);
		checkout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				double ordertotal = cart.get_order_total();
				double min_value = Double.parseDouble(site_settings.get(getString(R.string.viewcartactivity_min_purchase_order)));
				if (ordertotal > min_value) {
					Intent intent = new Intent(ViewcartActivity.this, CheckoutActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(getApplicationContext(), getString(R.string.viewcartactivity_please_order_minimum_of_amount) + site_settings.get(getString(R.string.viewcartactivity_min_purchase_order)), Toast.LENGTH_LONG).show();
				}
			}
		});

		listProduct.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				// TODO Auto-generated method stub
				if(cartArray.isEmpty()){

				}
				else {
					txtQty = (TextView) view.findViewById(R.id.textqty);

					showPopUpPhone(position);
				}
			}
		});

		updateTotalPrice();

	}
	public void updateTotalPrice(){
		cart = new MyCart(getApplicationContext());
		cartArray = cart.get_items();
		txtitemtotal=(TextView)findViewById(R.id.textView1);
		Integer totalitem = (Integer) cartArray.size();
		txtitemtotal.setText(totalitem.toString());

		Double totalprice = cart.get_order_total();
		double max_value = Double.parseDouble(site_settings.get(getString(R.string.viewcartactivity_max_purchase_order)));
		if (totalprice >= max_value) {
			delivery_charge = "0.0";
		}else{
			delivery_charge = site_settings.get(getString(R.string.viewcartactivity_delivery_charge));
		}
		txtDeliverycharge = (TextView)findViewById(R.id.delivery_charge);
		txtDeliverycharge.setText(delivery_charge);
		txtTotalPrice = (TextView)findViewById(R.id.textTotalPrice);
		txtTotalPrice.setText(getString(R.string.viewcartactivity_total_price)+totalprice);

	}
	private void showPopUpPhone(final int position) {
		AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
		helpBuilder.setTitle(getString(R.string.viewcartactivity_product_qty));
		final EditText input = new EditText(this);
		input.setSingleLine();
		input.setText(txtQty.getText());
		input.setHint(getString(R.string.viewcartactivity_qty)); //Set the text we want to edit
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		helpBuilder.setView(input);


		//Save button
		helpBuilder.setPositiveButton(getString(R.string.viewcartactivity_save),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//Save info here
						HashMap<String, String> map = new HashMap<String, String>();
						map = cartArray.get(position);
						map.put("qty", input.getText().toString());

						MyCart cart = new MyCart(getApplicationContext());
						cart.update_item_cart(map, position);
						txtQty.setText(input.getText());
						updateTotalPrice();
					}
				});



		//Cancel button
		helpBuilder.setNegativeButton(getString(R.string.viewcartactivity_cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Do nothing, just close the dialog box


			}
		});
		// Remember, create doesn't show the dialog
		AlertDialog helpDialog = helpBuilder.create();
		helpDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.viewcart, menu);
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
		else if(id==R.id.emptycart){
			MyCart emptycart = new MyCart(getApplicationContext());
			emptycart.empty_cart();
			Toast.makeText(getApplicationContext(),getString(R.string.viewcartactivity_all_product_removed),Toast.LENGTH_SHORT).show();

			onBackPressed();


		}
		else if(id == android.R.id.home){
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}
}
