package com.medhealth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Config.ConstValue;
import models.FruitsModel;
import models.NewModelArray;
import models.ServiceModel;

@SuppressWarnings("ALL")
public class ServiceItemDetailsActivity extends AppCompatActivity {

    TextView searched_pro_title, textCurrency, textDiscount, txtunit, txtgm, price_text_dialog, value1, textDiscountFlag , text_discount_symbol;
    RelativeLayout  addLayout, quantityLayout, decrese, increse,service_add_layout;

    TextView service_textDiscount, totalPriceTv ,service_desc_txt,service_visiting_charge,service_searched_pro_title;
    ImageView service_searched_pro_title_image;
    String cartQty = " ";
    private ProgressDialog dialog;
ServiceModel model=new ServiceModel();
    ArrayList<ServiceModel> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details_serives);
        service_desc_txt = findViewById(R.id.service_desc_txt);
        getSupportActionBar().setTitle("Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();

        service_add_layout = findViewById(R.id.service_add_layout);

        service_add_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ServiceItemDetailsActivity.this, ServiceCheckoutActivity.class);
                startActivity(intent);
                finish();
            }
        });
        service_searched_pro_title_image = findViewById(R.id.service_searched_pro_title_image);
        searched_pro_title = findViewById(R.id.searched_pro_title);
        service_visiting_charge = findViewById(R.id.service_visiting_charge);
        service_searched_pro_title=findViewById(R.id.service_searched_pro_title);
        service_textDiscount = findViewById(R.id.service_textDiscount);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading data......");
        progressDialog.show();



        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstValue.Service_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                arrayList = new ArrayList<>();
                progressDialog.dismiss();
                try {
                    Log.d("getresponse", response);
                    JSONArray jsonArray=new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    JSONArray array = jsonObject.getJSONArray("data");
                    JSONObject jsonObject1 = array.getJSONObject(0);
                    Log.d("datacheck", array.getString(0));


                    String title = jsonObject1.getString("title");
                    Log.d("title", title);

                    String service = jsonObject1.getString("service_charge");
                    Log.d("title1", service);
                    String visting = jsonObject1.getString("visiting_charge");
                    Log.d("title2", visting);
                    String image = jsonObject1.getString("image");
                    String description = jsonObject1.getString("description");

                    Log.d("title3", image);

                    //  Log.d("image", image);


//                        model.image=jsonObject1.getString("image");
//                    model.visting=jsonObject1.getString("visiting_charge");
//                    model.service=jsonObject1.getString("service_charge");
//                    model.title=jsonObject1.getString("title");

                    Log.d("myget", model.toString());
                    model.setImage(image);
                    model.setTitle(title);
                    model.setVisting(visting);
                    model.setService(service);
                    model.setDescription(description);
                    Picasso.get().load(image).into(service_searched_pro_title_image);
                    arrayList.add(model);

                    service_searched_pro_title.setText(model.getTitle());

                    service_textDiscount.setText(model.getService());
                    service_visiting_charge.setText(model.getVisting());
                    service_desc_txt.setText(model.getDescription());


                }

                catch (JSONException e) {
                    e.printStackTrace();
                }




            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ServiceItemDetailsActivity.this, "somthing Wrong", Toast.LENGTH_SHORT).show();
            }

        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map=new HashMap<>();
                map.put("key",ConstValue.Service_key);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
        requestQueue.getCache().clear();


}
}
