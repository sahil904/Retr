package com.medhealth;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import Config.ConstValue;
import alert.DeliveryTimeAlert;
import models.CartModel;
import models.DeliveryTimeModel;
import util.ObjectSerializer;
import utility.AvenuesParams;

/**
 * Created by Lnkt on 06-02-2018.
 */

@SuppressWarnings("ALL")
public class NewPlaceOrderActivity extends AppCompatActivity implements View.OnClickListener {

    public SharedPreferences settings;
    TextView totalItems, totalPrice, deliveryCharges, totalAmountTv, totalPayableTv;
    Button btnSave;
    ProgressDialog dialog;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    HashMap<String, String> site_settings;
    ArrayList<CartModel> cartList;
    RelativeLayout selectDateLayout;
    TextView dateTv;
    String selDateTimeStamp;
    AppCompatCheckBox instantCheckBox;
    RelativeLayout scheduleDeliveryLayout;
    RelativeLayout daySelectLayout, timeSelectLayout;
    TextView selectedDayTv, selectedTimeTv;
    ArrayList<DeliveryTimeModel> deliveryTimeList;
    DeliveryTimeAlert deliveryTimeAlert;
    String selDate;
    String selTime;
    EditText promoEt;
    RelativeLayout applyLayout;
    boolean isPromoCodeApplied;
    TextView applyTv;
    long couponAppliedPrice = 0;
    TextView cutPrice, instantDeliveryPrice;
    boolean isTimeSelected;
    boolean isDateSelected;
    Date date;
    int discountPrice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placeorder);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dateTv = findViewById(R.id.date_tv);
        totalAmountTv = findViewById(R.id.total_amount_tv);
        // cutPrice = findViewById(R.id.);
        totalPayableTv = findViewById(R.id.total_payable_tv);
        settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);

        //DateFormat dateFormat = DateFormat.getDateTimeInstance() ;
        date = new Date();
        DateFormat.getDateTimeInstance().format(date);
        Log.d("timeStamp", DateFormat.getDateTimeInstance().format(date));

       /* Date date = new Date(location.getTime());
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        Log.d("timeStamp",dateFormat.format(date));
*/
        site_settings = new HashMap<String, String>();
        try {
            site_settings = (HashMap<String, String>) ObjectSerializer.deserialize(settings.getString("site_settings", ObjectSerializer.serialize(new HashMap<String, String>())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        initializeDialog();
        initializemsgDialog();


        cartList = (ArrayList<CartModel>) getIntent().getSerializableExtra("cart_list");
        totalPrice = findViewById(R.id.textTotalPrice);
        deliveryCharges = findViewById(R.id.delivery_charge);
        selectDateLayout = findViewById(R.id.date_layout);
        instantCheckBox = findViewById(R.id.instant_delivery_check_box);
        scheduleDeliveryLayout = findViewById(R.id.schedule_delivery_layout);
        selectedDayTv = findViewById(R.id.delivery_day);
        selectedTimeTv = findViewById(R.id.delivery_time);
        daySelectLayout = findViewById(R.id.day_select_layout);
        timeSelectLayout = findViewById(R.id.time_select_layout);
        applyLayout = findViewById(R.id.apply_layout);
        applyLayout.setOnClickListener(this);
        promoEt = findViewById(R.id.promo_et);
        applyTv = findViewById(R.id.apply_tv);
        instantDeliveryPrice = findViewById(R.id.instant_delivery_price);
        instantDeliveryPrice.setText(site_settings.get("Instant Charge"));
        instantCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    scheduleDeliveryLayout.setVisibility(View.GONE);
                    float totalAmount = Float.parseFloat(MyApplication.getInstance().getTotalPrice());
                    float instantCharge = Float.parseFloat(site_settings.get("Instant Charge"));
                    totalAmount = totalAmount + instantCharge;
                    totalPayableTv.setText(String.valueOf(Math.round(totalAmount)));
                } else {
                    scheduleDeliveryLayout.setVisibility(View.VISIBLE);
                    double max_value = Double.parseDouble(site_settings.get(getString(R.string.placeorderactivity_max_purchase_order)));

                    Double purchasedPrice = Double.parseDouble(MyApplication.getInstance().getTotalPrice());
                    if (max_value <= purchasedPrice) {
                        double totalAmount = purchasedPrice;
                        totalPayableTv.setText(String.valueOf(Math.round(totalAmount)));
                    } else {
                        double totalAmount = purchasedPrice;
                        totalAmount = totalAmount + Double.parseDouble(site_settings.get("Delivery Charge"));
                        totalPayableTv.setText(String.valueOf(Math.round(totalAmount)));
                    }


                }
            }
        });
        selectDateLayout.setOnClickListener(this);

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  if (!isTimeSelected) {
                    Snackbar.make(v.getRootView(), "Please Select a Delivery Time!!!", Snackbar.LENGTH_SHORT).show();
                } else if (!isDateSelected) {
                    Snackbar.make(v.getRootView(), "Please Select a Delivery Date!!!", Snackbar.LENGTH_SHORT).show();
                }*/
               {
                    radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    radioButton = (RadioButton) findViewById(selectedId);
                    if (selectedId == R.id.radio0) {
                        for (int k = 0; k < cartList.size(); k++) {

                            Double effected_price = Double.parseDouble(cartList.get(k).salePrice) * Integer.parseInt(cartList.get(k).cartQuantity);
                            Log.d("EFFECTED_PRICE", String.valueOf(effected_price));
                        }
                        showProgressDialog();
                        hitUrlForPlaceOrder(settings.getString("userid", "00"), String.valueOf(1));
//                    new PlaceorderActivity.OrderTask().execute(true);
                    } else {
//                        Intent intent = new Intent(NewPlaceOrderActivity.this, PaywithPaypal.class);
//                        startActivity(intent);
                        showProgressDialog();
                        hitUrlForPlaceOrder(settings.getString("userid", "00"), String.valueOf(0));
                    }
                }
            }
        });
        daySelectLayout.setOnClickListener(this);

        timeSelectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showTimeDialog();
            }
        });
        setCurrentDate();
        totalAmountTv.setText(MyApplication.getInstance().getTotalPrice());
        totalPayableTv.setText(getIntent().getStringExtra("total_amount"));
        deliveryTimeList = new ArrayList<>();
        hitUrlForDeliveryTime();
    }

       /* double max_value  = Double.parseDouble(site_settings.get(getString(R.string.placeorderactivity_max_purchase_order)));
        Double purchasedPrice = Double.parseDouble(MyApplication.getInstance().getTotalPrice());
        if (max_value <= purchasedPrice){
            deliveryCharges.setText("Delivery Charges 00 Rs");
        }else {
            deliveryCharges.setText("Delivery Charges+"+site_settings.get("Delivery Charge")+"Rs");
        }

        instantDeliveryPrice.setText(site_settings.get("Instant Charge")+"Rs");
    }*/

    private void setCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        selDateTimeStamp = String.valueOf((calendar.getTimeInMillis() - 1000) + (1000 * 60 * 60 * 24));
        calendar.setTimeInMillis(Long.parseLong(selDateTimeStamp));
        int year = calendar.get(Calendar.YEAR);

        int monthOfYear = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        String month, date;
        String selDate = "";

        monthOfYear = monthOfYear + 1;
        if (monthOfYear < 10) {
            month = String.valueOf("-0" + monthOfYear);

        } else {
            month = String.valueOf("-" + monthOfYear);
        }

        if (dayOfMonth < 10) {
            date = String.valueOf("0" + dayOfMonth);
        } else {
            date = String.valueOf(dayOfMonth);
        }
        selectedDayTv.setText(String.valueOf(date + month + "-" + year));
        isDateSelected = true;

    }

    private void hitUrlForPlaceOrder(final String userId, final String radiobutton) {


        for (int k = 0; k < cartList.size(); k++) {

            // Double effected_price = Double.parseDouble(cartList.get(k).salePrice) * Integer.parseInt(cartList.get(k).cartQuantity);


            Log.d("order_item[" + String.valueOf(k) + "]", cartList.get(k).id);
            Log.d("order_item_qty[" + String.valueOf(k) + "]", cartList.get(k).cartQuantity);
            Log.d("order_item_gmqty[" + String.valueOf(k) + "]", cartList.get(k).gmQty);
            Log.d("order_item_price[" + String.valueOf(k) + "]", String.valueOf(cartList.get(k).salePrice));
            Log.d("order_item_type[" + String.valueOf(k) + "]", cartList.get(k).unit);
            Log.d("order_discount_id[" + String.valueOf(k) + "]", cartList.get(k).disId);
        }

        Log.d("mobile", settings.getString("order_phone".toString(), ""));
        Log.d("city", settings.getString("order_city".toString(), ""));
        Log.d("email", settings.getString("order_email".toString(), ""));
        Log.d("person_name", settings.getString("order_name".toString(), ""));
        Log.d("zipcode", settings.getString("order_zipcode".toString(), ""));
        Log.d("address", settings.getString("order_address".toString(), ""));
        Log.d("user_id", settings.getString("userid".toString(), ""));
        Log.d("totalitem", String.valueOf(MyApplication.getInstance().getTotalQuantity()));
        Log.d("cod", radiobutton);

        if (instantCheckBox.isChecked()) {
            Log.d("delivery_instant", "1");
            Log.d("delivery_time", "Instant");
            Log.d("delivery_charge", site_settings.get("Instant Charge"));
            if (isPromoCodeApplied) {
                long totalFPrice = couponAppliedPrice + Long.parseLong(site_settings.get("Instant Charge"));
                Log.d("totalprice", String.valueOf(totalFPrice));
                Log.d("apply_coupon", "1");
            } else {
                long totalFPrice = Math.round(Double.parseDouble(MyApplication.getInstance().getTotalPrice())) + Long.parseLong(site_settings.get("Instant Charge"));
                Log.d("totalprice", String.valueOf(totalFPrice));
                Log.d("apply_coupon", "0");
            }
        } else {
            Log.d("delivery_instant", "0");
            Log.d("delivery_time", selectedTimeTv.getText().toString());
            double max_value = Double.parseDouble(site_settings.get(getString(R.string.placeorderactivity_max_purchase_order)));
            Double purchasedPrice = Double.parseDouble(MyApplication.getInstance().getTotalPrice());
            if (max_value <= purchasedPrice) {
                Log.d("delivery_charge", "0");
            } else {
                Log.d("delivery_charge", site_settings.get("Delivery Charge"));
            }

            if (isPromoCodeApplied) {

                if (max_value <= purchasedPrice) {

                    Log.d("totalprice1", String.valueOf(couponAppliedPrice));
                } else {
                    long totalFPrice = couponAppliedPrice + Long.parseLong(site_settings.get("Delivery Charge"));
                    Log.d("totalprice2", String.valueOf(totalFPrice));
                }
                Log.d("apply_coupon", "1");
            } else {
                if (max_value <= purchasedPrice) {
                    Log.d("totalprice3", MyApplication.getInstance().getTotalPrice());

                } else {
                    long totalFPrice = Math.round(Double.parseDouble(MyApplication.getInstance().getTotalPrice())) + Long.parseLong(site_settings.get("Delivery Charge"));
                    Log.d("totalprice4", String.valueOf(totalFPrice));
                }

                Log.d("apply_coupon", "0");
            }

        }
        Log.d("delivery_date", selectedDayTv.getText().toString());


        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstValue.JSON_ADD_ORDER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideProgressDialog();
                Log.d("Response", response);

                Log.d("url", ConstValue.JSON_ADD_ORDER);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("responce").equals("success")) {
                        if (radiobutton.equalsIgnoreCase("1")) {
                            Intent intent = new Intent(NewPlaceOrderActivity.this, CompleteOrderActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(NewPlaceOrderActivity.this, WebViewActivity.class);
                            // intent.putExtra("userId",settings.getString("userid".toString(),""));
                            intent.putExtra(AvenuesParams.ACCESS_CODE, "AVXE78FF44AK91EXKA");
                            intent.putExtra(AvenuesParams.MERCHANT_ID, "177918");
                            intent.putExtra(AvenuesParams.ORDER_ID, jsonObject.getString("order_id"));
                            intent.putExtra(AvenuesParams.CURRENCY, "INR");
                            //intent.putExtra(AvenuesParams.AMOUNT,"1");
                            intent.putExtra(AvenuesParams.AMOUNT, totalPayableTv.getText().toString());
                            intent.putExtra(AvenuesParams.REDIRECT_URL, "http://www.medhealth.co.in/payment/ccavResponseHandler.php");
                            intent.putExtra(AvenuesParams.CANCEL_URL, "http://www.medhealth.co.in/payment/ccavResponseHandler.php");
                            intent.putExtra(AvenuesParams.RSA_KEY_URL, "http://www.medhealth.co.in/payment/GetRSA.php");
                            startActivity(intent);
                        }
                        finish();
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
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                for (int k = 0; k < cartList.size(); k++) {

                    Double effected_price = Double.parseDouble(cartList.get(k).salePrice) * Integer.parseInt(cartList.get(k).cartQuantity);
                    params.put("order_item[" + String.valueOf(k) + "]", cartList.get(k).id);
                    params.put("order_item_qty[" + String.valueOf(k) + "]", cartList.get(k).cartQuantity);
                    params.put("order_item_gmqty[" + String.valueOf(k) + "]", cartList.get(k).gmQty);
                    params.put("order_item_price[" + String.valueOf(k) + "]", String.valueOf(effected_price));
                    params.put("order_item_type[" + String.valueOf(k) + "]", cartList.get(k).unit);
                    params.put("order_discount_id[" + String.valueOf(k) + "]", cartList.get(k).disId);
                }
                params.put("mobile", settings.getString("order_phone".toString(), ""));
                params.put("city", settings.getString("order_city".toString(), ""));
                params.put("email", settings.getString("order_email".toString(), ""));
                params.put("person_name", settings.getString("order_name".toString(), ""));
                params.put("zipcode", settings.getString("order_zipcode".toString(), ""));
                params.put("address", settings.getString("order_address".toString(), ""));
                params.put("user_id", settings.getString("userid".toString(), ""));
                params.put("totalitem", String.valueOf(MyApplication.getInstance().getTotalQuantity()));

                params.put("cod", radiobutton);

                if (instantCheckBox.isChecked()) {
                    params.put("delivery_instant", "1");
                    params.put("delivery_time", "Instant");
                    params.put("delivery_charge", site_settings.get("Instant Charge"));

                    if (isPromoCodeApplied) {
                        long totalFPrice = couponAppliedPrice + Long.parseLong(site_settings.get("Instant Charge"));
                        params.put("totalprice", String.valueOf(totalFPrice));
                        params.put("apply_coupon", "1");
                    } else {
                        // settings.edit().putString("promoPrice", String.valueOf(0)).apply();
                        long totalFPrice = Math.round(Double.parseDouble(MyApplication.getInstance().getTotalPrice())) + Long.parseLong(site_settings.get("Instant Charge"));
                        params.put("totalprice", String.valueOf(totalFPrice));
                        params.put("apply_coupon", "0");
                    }

                } else {
                    params.put("delivery_instant", "0");
                    params.put("delivery_time", selectedTimeTv.getText().toString());

                    double max_value = Double.parseDouble(site_settings.get(getString(R.string.placeorderactivity_max_purchase_order)));
                    Double purchasedPrice = Double.parseDouble(MyApplication.getInstance().getTotalPrice());

                    if (max_value <= purchasedPrice) {

                        params.put("delivery_charge", "0");
                    } else {
                        params.put("delivery_charge", site_settings.get("Delivery Charge"));
                    }

                    if (isPromoCodeApplied) {
                        if (max_value <= purchasedPrice) {

                            params.put("totalprice", String.valueOf(couponAppliedPrice));

                        } else {
                            long totalFPrice = couponAppliedPrice + Long.parseLong(site_settings.get("Delivery Charge"));
                            params.put("totalprice", String.valueOf(totalFPrice));
                        }
                        settings.edit().putString("promoPrice", String.valueOf(discountPrice)).apply();
                        params.put("apply_coupon", "1");
                        params.put("promoPrice",String.valueOf(discountPrice));
                    } else {
                        if (max_value <= purchasedPrice) {

                            params.put("totalprice", MyApplication.getInstance().getTotalPrice());

                        } else {
                            float totalFPrice = Math.round(Double.parseDouble(MyApplication.getInstance().getTotalPrice())) + Long.parseLong(site_settings.get("Delivery Charge"));
                            params.put("totalprice", String.valueOf(totalFPrice));
                        }
                        settings.edit().putString("promoPrice", String.valueOf(0)).apply();
                        params.put("apply_coupon", "0");
                        params.put("promoPrice","0");
                    }
                }

                params.put("delivery_date", selectedDayTv.getText().toString());
                params.put("order_date",DateFormat.getDateTimeInstance().format(new Date()));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(NewPlaceOrderActivity.this);
        requestQueue.add(stringRequest);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.placeorder, menu);
        return true;
    }*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        Log.d("CLICKED",String.valueOf("Show DETECT"));
        switch (view.getId()){


            case R.id.day_select_layout:
                showDateDialog(selectedDayTv);
                break;

            case R.id.apply_layout:
                if (!isPromoCodeApplied) {
                    if (promoEt.getText().toString().equalsIgnoreCase("")) {
                        promoEt.setError("can't be empty");
                    } else {
                        hideSoftKeyboard();
                        showProgressDialog();
                        hitUrlForCheckCoupon(view);
                    }
                }else {
                    showSnackBar(view,"You have All Ready Applied Coupon",ContextCompat.getColor(NewPlaceOrderActivity.this, R.color.navigationbar));
                }
                break;
        }
    }

    private void initializeDialog(){
        dialog = new ProgressDialog(NewPlaceOrderActivity.this);
        dialog.setTitle("Applying Promo Code");
    }
    private void initializemsgDialog(){
        dialog = new ProgressDialog(NewPlaceOrderActivity.this);
        dialog.setMessage("Loading Please Wait...");
    }

    private void showProgressDialog(){
        if (dialog!=null){
            dialog.show();
        }
    }

    private void hideProgressDialog(){
        if (dialog.isShowing()){
            dialog.cancel();
        }
    }

    private void showDateDialog(final TextView tv){

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                Calendar calendar2 = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                selDateTimeStamp = String.valueOf(calendar2.getTimeInMillis());
//                    if (startOrEnd.equalsIgnoreCase("start")){
//                        startTimeStamp = String.valueOf(calendar2.getTimeInMillis());
//                    }else if (startOrEnd.equalsIgnoreCase("end")){
//                        endTimeStamp = String.valueOf(calendar2.getTimeInMillis());
//                    }
                String month,date;
                String selDate = "";

                monthOfYear = monthOfYear+1;
                if (monthOfYear<10) {
                    month= String.valueOf("-0" + monthOfYear);

                }
                else {
                    month = String.valueOf("-" + monthOfYear);
                }

                if (dayOfMonth<10){
                    date = String.valueOf("0" + dayOfMonth);
                    selDate = String.valueOf("0" + dayOfMonth);
                }else {
                    date = String.valueOf( dayOfMonth);
                    selDate = String.valueOf(dayOfMonth);
                }
                tv.setText(String.valueOf(date+month  +"-"+year));

                //  endDate = String.valueOf(year +  month +  date);
                //  Log.d("date choosed End",endDate);

            }};
        DatePickerDialog dpDialog=new DatePickerDialog(NewPlaceOrderActivity.this, listener, year, month, day);
        dpDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        long now = System.currentTimeMillis() - 1000;
        dpDialog.getDatePicker().setMinDate(now+(1000*60*60*24));
        dpDialog.getDatePicker().setMaxDate(now+(1000*60*60*24*(Integer.parseInt(site_settings.get("Max Delivery Days")))));
        dpDialog.show();
    }
    private void hitUrlForCheckCoupon(final View view){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstValue.JSON_CHECK_COUPON, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("promoResponse",response);
                hideProgressDialog();
                try {
                    isPromoCodeApplied = true;
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("responce").equalsIgnoreCase("success")){
                        JSONObject dataObject = jsonObject.getJSONObject("data");
                        discountPrice = dataObject.getInt("discount");

                        Double price = Double.parseDouble(MyApplication.getInstance().getTotalPrice())-discountPrice;

                        couponAppliedPrice = Math.round(price);
                        Log.d("couponAppliedPrice", String.valueOf(couponAppliedPrice));

                        updatePrice();
                        applyTv.setText("APPLIED");
                        showSnackBar(view,"Code Applied SuccessFully",ContextCompat.getColor(NewPlaceOrderActivity.this, R.color.navigationbar));
                    }else {
                        isPromoCodeApplied = false;
                        showSnackBar(view,"Invalid Code!!",ContextCompat.getColor(NewPlaceOrderActivity.this, R.color.orange_color));
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
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("code",promoEt.getText().toString());
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(NewPlaceOrderActivity.this);
        requestQueue.add(stringRequest);
    }

    private void hitUrlForDeliveryTime(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstValue.JSON_DELIVERY_TIME_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("responce").equalsIgnoreCase("success")){
                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        for (int i=0; i<dataArray.length();i++){
                            JSONObject deliveryObject = dataArray.getJSONObject(i);
                            DeliveryTimeModel deliveryTimeModel = new DeliveryTimeModel();
                            deliveryTimeModel.deliveryId = deliveryObject.getString("id");
                            deliveryTimeModel.deliveryTime = deliveryObject.getString("del_time");
                            deliveryTimeList.add(deliveryTimeModel);
                        }
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

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(NewPlaceOrderActivity.this);
        requestQueue.add(stringRequest);
    }

    public void showTimeDialog() {

        if (deliveryTimeAlert == null) {

            deliveryTimeAlert = new DeliveryTimeAlert(this,deliveryTimeList);

        }
        if (!deliveryTimeAlert.isShowing()) {
            deliveryTimeAlert.show();
        }
    }

    public void  setTime(String selTime){

        selectedTimeTv.setText(selTime);
        isTimeSelected = true;
        if (deliveryTimeAlert.isShowing()) {
            deliveryTimeAlert.cancel();
        }
    }

    private void hideSoftKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void updatePrice(){
        //totalPayableTv.setText(MyApplication.getInstance().getTotalPrice());
        //cutPrice.setVisibility(View.VISIBLE);

        double max_value = Double.parseDouble(site_settings.get(getString(R.string.placeorderactivity_max_purchase_order)));

        Double purchasedPrice = Double.parseDouble(MyApplication.getInstance().getTotalPrice());
        if (purchasedPrice >= max_value) {
            totalPayableTv.setText(String.valueOf(couponAppliedPrice));
        }else {
            totalPayableTv.setText(String.valueOf(couponAppliedPrice + Long.parseLong(site_settings.get("Delivery Charge"))));
        }
        //cutPrice.setPaintFlags(cutPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    private void showSnackBar(View view,String message,int colorId){
        Snackbar snackbar;
        snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(colorId);
        snackbar.show();
    }

}