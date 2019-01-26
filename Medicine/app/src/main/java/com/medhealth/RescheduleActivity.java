package com.medhealth;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import Config.ConstValue;
import alert.DeliveryTimeAlert;
import imgLoader.AnimateFirstDisplayListener;
import models.DeliveryTimeModel;
import models.MyOrderModel;
import util.ObjectSerializer;


public class RescheduleActivity extends AppCompatActivity{

    TextView title,price,selectedDate,selectedTime;
    ImageView productImage;
    RelativeLayout daySelectLayout,timeSelectLayout,updateLayout;
    ArrayList<DeliveryTimeModel> deliveryTimeList;
    SharedPreferences settings;
    DeliveryTimeAlert deliveryTimeAlert;
    ProgressDialog dialog;
    MyOrderModel myOrderModel;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    DisplayImageOptions options;
    ImageLoaderConfiguration imgconfig;
    HashMap<String,String> site_settings;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reshudule_activity);
        title = findViewById(R.id.proTitle);
        price = findViewById(R.id.proTotalPrice);
        selectedDate = findViewById(R.id.delivery_day);
        selectedTime = findViewById(R.id.delivery_time);
        productImage = findViewById(R.id.pro_image);
        daySelectLayout = findViewById(R.id.day_select_layout);
        timeSelectLayout = findViewById(R.id.time_select_layout);
        updateLayout = findViewById(R.id.update_layout);
        settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
        site_settings = new HashMap<String, String>();
        try {
            site_settings = (HashMap<String, String>) ObjectSerializer.deserialize(settings.getString("site_settings", ObjectSerializer.serialize(new HashMap<String, String>())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        myOrderModel = (MyOrderModel) getIntent().getSerializableExtra("my_order");

        File cacheDir = StorageUtils.getCacheDirectory(RescheduleActivity.this);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnFail(R.drawable.loading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();
        imgconfig = new ImageLoaderConfiguration.Builder(RescheduleActivity.this)
                .build();
        ImageLoader.getInstance().init(imgconfig);

        ImageLoader.getInstance().displayImage(ConstValue.PRO_IMAGE_BIG_PATH+myOrderModel.orderImage, productImage, options, animateFirstListener);

        initializeDialog();
            hitUrlForDeliveryTime();
            title.setText(myOrderModel.orderName);
            price.setText(myOrderModel.orderPrice);
            selectedDate.setText(myOrderModel.deliveryDate);
            selectedTime.setText(myOrderModel.deliveryTime);





        daySelectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showDateDialog(selectedDate);
            }
        });

        timeSelectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeDialog();
            }
        });

        updateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    showProgressDialog();

                        hitUrlForRescheduleOrder();

                }
        });

    }
    private void hitUrlForRescheduleOrder(){
        String urlString = ConstValue.RESCHEDULE_ORDER;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("Response",response);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("responce").equals("success")){
                        Intent resultIntent = new Intent();
                        myOrderModel.deliveryTime = selectedTime.getText().toString();
                        myOrderModel.deliveryDate = selectedDate.getText().toString();
                        resultIntent.putExtra("my_order",myOrderModel);
                        setResult(RESULT_OK,resultIntent);
                        finish();
                    }
                    hideProgressDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public String getBodyContentType() {
                return super.getBodyContentType();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("delivery_time",selectedTime.getText().toString());
                params.put("delivery_date",selectedDate.getText().toString());
                params.put("orderid",myOrderModel.orderId);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(RescheduleActivity.this);
        requestQueue.add(stringRequest);
    }

    private void hitUrlForDeliveryTime(){
        deliveryTimeList = new ArrayList<>();
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

            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(RescheduleActivity.this);
        requestQueue.add(stringRequest);
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
//                selDateTimeStamp = String.valueOf(calendar2.getTimeInMillis());
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
        DatePickerDialog dpDialog=new DatePickerDialog(RescheduleActivity.this, listener, year, month, day);
        long now = System.currentTimeMillis()-1000;
        dpDialog.getDatePicker().setMinDate(now+(1000*60*60*24));
        dpDialog.getDatePicker().setMaxDate(now+(1000*60*60*24*(Integer.parseInt(site_settings.get("Max Delivery Days")))));
        dpDialog.show();
    }

    public void showTimeDialog() {
        if (deliveryTimeAlert == null) {

            deliveryTimeAlert = new DeliveryTimeAlert(RescheduleActivity.this,deliveryTimeList);

//            deliveryTimeAlert.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        }
        if (!deliveryTimeAlert.isShowing()) {
            deliveryTimeAlert.show();
        }
    }

    private void initializeDialog(){
        dialog = new ProgressDialog(RescheduleActivity.this);
        dialog.setTitle("Updating Product");
    }

    private void showProgressDialog(){
        if (dialog!=null){
            dialog.show();
        }
    }

    public void  setTime(String selTime){
        selectedTime.setText(selTime);
        if (deliveryTimeAlert.isShowing()) {
            deliveryTimeAlert.cancel();
        }
    }


    private void hideProgressDialog(){
        if (dialog.isShowing()){
            dialog.cancel();
        }
    }

}
