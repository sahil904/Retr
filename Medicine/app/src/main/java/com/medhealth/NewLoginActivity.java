package com.medhealth;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import Config.ConstValue;


@SuppressWarnings("ALL")
public class NewLoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText numberEt;
    RelativeLayout nextButton,doneButton;
    RelativeLayout numMainLayout,passMainLayout;
    TextView sendTextTv,resendOtp;
    EditText otpEt;
    SharedPreferences settings;
    String titleNews,desc;
    int enteredOtp = 12;
    ProgressDialog dialog;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_otp);
        hitUrlForGetAName();
        settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
        numberEt = findViewById(R.id.number_et);
        nextButton = findViewById(R.id.next_layout);
        doneButton = findViewById(R.id.done_layout);
        numMainLayout = findViewById(R.id.num_main_layout);
        passMainLayout = findViewById(R.id.pass_main_layout);
        sendTextTv = findViewById(R.id.send_text_tv);
        resendOtp = findViewById(R.id.resend_tv);
        otpEt = findViewById(R.id.otp_et);
        nextButton.setOnClickListener(this);
        doneButton.setOnClickListener(this);
        resendOtp.setOnClickListener(this);

        numberEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 10){
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    nextButton.setBackgroundColor(Color.parseColor("#f36f2a"));
                    Log.d("count",String.valueOf(count));
                }else {
                    nextButton.setBackgroundColor(Color.parseColor("#cfcfcf"));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.next_layout:
                numberEt.clearFocus();
                  if (isNumValid()){
                      checkAndRequestPermissions();
                      numMainLayout.setVisibility(View.GONE);
                      passMainLayout.setVisibility(View.VISIBLE);
                      updatePassLayout();
                  }
                break;

            case R.id.resend_tv:
                hitUrlForOtp();
                break;

            case R.id.done_layout:
                char forth =0, fifth=0,sixth=0,seventh=0;
                String newEntered="";

                try {
                    //enteredOtp = Integer.parseInt(otpEt.getText().toString().trim());
                    if(otpEt.getText().toString().length()==8){
                        forth=otpEt.getText().charAt(4);
                        fifth=otpEt.getText().charAt(5);
                        sixth=otpEt.getText().charAt(6);
                        seventh=otpEt.getText().charAt(7);
                    }
                    else {
                        Toast.makeText(this,"Please write 8 digit OTP",Toast.LENGTH_SHORT).show();
                    }
                    newEntered=String.valueOf(forth)+String.valueOf(fifth)+String.valueOf(sixth)+String.valueOf(seventh);
                    if (settings.getInt("otp_id", 0000) == Integer.parseInt(newEntered)) {
                        hitUrlForLogin();
                    } else {
                        Toast.makeText(this, "wrong otp", Toast.LENGTH_SHORT).show();
                    }
                }catch (NumberFormatException exp){
                         Log.d("NFE",String.valueOf(exp));
                }
                break;
        }
    }

    private void updatePassLayout(){

        hitUrlForOtp();
        sendTextTv.setText("We have Send an SMS at +91"+numberEt.getText().toString()+" with an Alternative Password");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        otpEt.requestFocus();
    }

    private boolean isNumValid(){
        if (numberEt.getText().toString().equalsIgnoreCase("")){
            numberEt.setError("can't be empty");
            return false;
        }else if (numberEt.getText().toString().length()!=10){
            numberEt.setError("number not valid");
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (passMainLayout.getVisibility() == View.VISIBLE){
            passMainLayout.setVisibility(View.GONE);
            numMainLayout.setVisibility(View.VISIBLE);
        }else {
            super.onBackPressed();
        }
    }
    // For hit Otp url
    private void hitUrlForOtp(){

      //  String text = "OTP For MEDHEALTH App Verification Is:-  ";
      //  String sendMsg = text.replaceAll(" ","%20");
      //  Log.d("sendMsg",sendMsg);
      //  sendMsg = sendMsg+String.valueOf(randomOtp());


        //String urlstring = ConstValue.OTP_URL+"BUSNSS&key=010kf1p20jJJEm8kE9JZcn9Ab8WiN0&sndr=BUSNSS&rpt=1&ph="+numberEt.getText().toString() +"&text="+sendMsg;

        //"&sendto=919xxxx&message=hello";


        //String urlstring = ConstValue.OTP_URL+"&sendto="+numberEt.getText().toString()+"&message="+sendMsg;

        //String urlstring = ConstValue.OTP_URL+"&msisdn="+numberEt.getText().toString()+"&sid=MEDHLT"+"&msg="+sendMsg+"&fl=0&gwid=2";

        final String msg = "Your verification code is ##OTP##"+String.valueOf(randomOtp());
        Log.d("msg",msg);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstValue.OTP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(NewLoginActivity.this, "OTP has been send.....", Toast.LENGTH_LONG).show();

                Log.d("otpResponse",response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("authkey","250614AbRRdqx2x95c08ccb1");
                hashMap.put("message",msg);
                hashMap.put("sender","Pradee");
                hashMap.put("mobile",numberEt.getText().toString());
                return hashMap;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(NewLoginActivity.this);
        requestQueue.add(stringRequest);

    }

    private void hitUrlForLogin(){

        dialog = ProgressDialog.show(NewLoginActivity.this, "",getString(R.string.loading), true);

        String urlstring = ConstValue.NEW_LOGIN_URL;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlstring, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    Log.d("ResponseLogin",String.valueOf(response));

                    if(jsonObject.getString("responce").equalsIgnoreCase("success")) {
                        JSONObject data = jsonObject.getJSONObject("data");
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

                        Intent intent = new Intent(NewLoginActivity.this, MainActivity.class);
                        intent.putExtra("title",titleNews);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }

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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("mobile",numberEt.getText().toString());
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(NewLoginActivity.this);
        requestQueue.add(stringRequest);
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
                        settings.edit().putString("icon",newsObj.getString("icon")).apply();
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
        RequestQueue requestQueue = Volley.newRequestQueue(NewLoginActivity.this);
        requestQueue.add(stringRequest);
    }

    private  boolean checkAndRequestPermissions() {

        int permissionSendMessage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS);
        int receiveSMS = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS);
        int readSMS = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.RECEIVE_MMS);
        }
        if (readSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_SMS);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("RECEIVER","Bro adcastCalled");
            if (intent.getAction().equalsIgnoreCase("otp_medical_app")) {
                 String message = intent.getStringExtra("message");
                Log.d("MESSAGE IS",message);
                message = message.trim();
                otpEt.setText(message.substring(message.length()-4));
                if (settings.getInt("otp_id", 0000) == Integer.parseInt(otpEt.getText().toString())) {
                    hitUrlForLogin();
                } else {
                }


//                String[] sps = message.split("  ");
//                String otp = sps[1];
//                Log.d("Otp",String.valueOf(otp.length()));
//                otp = otp.replaceAll("  ","");
//                otpEt.setText(otp);
            }
        }
    };

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp_medical_app"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
   private int randomOtp(){
       Random rnd = new Random();
       int n = 1000 + rnd.nextInt(9000);
       settings.edit().putInt("otp_id",n).commit();
       return n;
   }
}
