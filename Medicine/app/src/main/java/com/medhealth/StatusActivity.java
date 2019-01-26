package com.medhealth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medi.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Config.ConstValue;

public class StatusActivity extends AppCompatActivity{
    TextView statusText;
    Button btn1;
    Button btn2;
    Button btn3;
    SharedPreferences settings;
    String status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status_online_activity);
        statusText = findViewById(R.id.status_text);
        btn1 = (Button)findViewById(R.id.button);
        btn2 = (Button)findViewById(R.id.button2);
        btn3 = (Button)findViewById(R.id.button3);
        status = getIntent().getStringExtra("transStatus");
        Log.d("STatus",status);

        settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatusActivity.this,MainActivity.class);
                intent.putExtra("fragment_position",3);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatusActivity.this,MainActivity.class);
                intent.putExtra("fragment_position",1);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatusActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        hitUrlForCheckStatus();
        
    }
    private void hitUrlForCheckStatus(){

        String url = ConstValue.CHECK_PAYMENT_STATUS+"&order_id="+getIntent().getStringExtra("order_id");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")) {
                        if (jsonObject.getString("status").equals("success")) {
                            statusText.setText("Your Order Has Been Successfully Placed....");
                            hitUrlForClearCart(settings.getString("userid", "00"));
                        }else {
                            statusText.setText("Your Order Has Been Cancelled....");
                            btn1.setVisibility(View.GONE);
                            btn2.setVisibility(View.GONE);
                            btn3.setVisibility(View.GONE);
                        }
                    }else {
                        hitUrlForClearCart(settings.getString("userid", "00"));
                    }
                    /*else {
                        statusText.setText("Your Order Has Been Cancelled....");
                        btn1.setVisibility(View.GONE);
                        btn2.setVisibility(View.GONE);
                        btn3.setVisibility(View.GONE);
                    }*/
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
        RequestQueue requestQueue = Volley.newRequestQueue(StatusActivity.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(StatusActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    private void hitUrlForClearCart(final String userId){

       // String url = ConstValue.CLEAR_CART+"&userId="+userId;

        Log.d("successUrl",ConstValue.CLEAR_CART+userId);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstValue.CLEAR_CART, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error",String.valueOf(error));
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                params.put("userId",userId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(StatusActivity.this);
        requestQueue.add(stringRequest);
    }
}
