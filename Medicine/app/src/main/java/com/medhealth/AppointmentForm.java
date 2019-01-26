package com.medhealth;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import Config.ConstValue;

@SuppressWarnings("deprecation")
@SuppressLint("Registered")
public class AppointmentForm extends AppCompatActivity {

    RelativeLayout submitDetailLayout ,successLayout,appointmentLayout;
    Button uploadBtn;
    Date date;
    String doctorId;
    ProgressDialog dialog;
    TextView nameEt,mobileEt,addressEt,successTv;
    protected void onCreate (@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.appoinment_form);
        getSupportActionBar().setTitle("Appointment");
        doctorId  = getIntent().getStringExtra("DoctorId");
        Log.d("DoctorId",doctorId);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please Wait.....");
        dialog.setCancelable(false);
        appointmentLayout = findViewById(R.id.appointment_layout);
        appointmentLayout.setVisibility(View.VISIBLE);
        successLayout = findViewById(R.id.succes_layout);
        successLayout.setVisibility(View.GONE);
        successTv = findViewById(R.id.success_tv);
        uploadBtn = findViewById(R.id.upload_btn);
        nameEt = findViewById(R.id.name_et);
        mobileEt = findViewById(R.id.mobile_et);
        addressEt = findViewById(R.id.address_et);
        submitDetailLayout = findViewById(R.id.submit_detail_layout);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("Clicked","CLicked!!!!");

                if (nameEt.getText().toString().length()<1){
                    nameEt.setError("should not empty");
                }else if (mobileEt.getText().toString().length()<10){
                    mobileEt.setError("should not less than 10 digits  ");
                }else if (addressEt.getText().toString().length()<1 ){
                    addressEt.setError("should not empty");
                }
                else {
                    dialog.show();
                    hitUrlForUploadAppointmentDetail();
                   // progressBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void hitUrlForUploadAppointmentDetail()
    {
        Log.d("Clicked","CLicked!!!!");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstValue.APPOINTMENT_FORM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                successLayout.setVisibility(View.VISIBLE);
                appointmentLayout.setVisibility(View.GONE);
                dialog.dismiss();
                try {
                    JSONObject jsonObject =  new JSONObject(response);
                    if (!jsonObject.getBoolean("error")){
                        String desc = jsonObject.getString("description");
                        successTv.setText(desc);
                        //Toast.makeText(AppointmentForm.this, ""+desc, Toast.LENGTH_SHORT).show();
                        /*Intent intent = new Intent(AppointmentForm.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);*/
                    }else {
                        Toast.makeText(AppointmentForm.this, "Something Went Wrong...", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                params.put("doctorId",doctorId);
                params.put("name",nameEt.getText().toString());
                params.put("mobile",mobileEt.getText().toString());
                params.put("address",addressEt.getText().toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(AppointmentForm.this);
        requestQueue.add(stringRequest);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

