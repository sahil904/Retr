package com.medhealth;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

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
public class ServiceNewPlaceOrderActivity extends AppCompatActivity {

    public SharedPreferences settings;
    TextView totalItems, totalPrice, deliveryCharges, totalAmountTv, totalPayableTv;
    Button service_btnSave;
    ProgressDialog dialog;
    private RadioGroup radioGroup;
    private RadioButton service_radioButton;
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
        setContentView(R.layout.service_activity_placeorder);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

}
