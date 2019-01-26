package com.medhealth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Config;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Config.ConstValue;
import fragments.CatDynamicFragment;
import models.HomeModel;
import models.ServiceModel;
import util.Interfac;
import util.ObjectSerializer;

public class NewServiceActivity extends AppCompatActivity {
    TextView Qty,Price,Unit;
    RelativeLayout service_cart_layout,service_search_layout,view_service_cart_layout,stripLayout;
    TextView addtoCart,stripTv;
    //RelativeLayout addtoCart
    TabLayout tabLayout;
    ViewPager viewPager;
    ArrayList<HomeModel> homeList;
    int selPosition;
    HashMap<String,String> site_settings;
    public SharedPreferences settings;
    RecyclerView recyclerView;
    ArrayList<ServiceModel>arrayList;
    MyAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_new_service);
        Toolbar toolbar = findViewById(R.id.service_tool);
        toolbar.setTitle("services");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
        recyclerView=findViewById(R.id.service_recylerview);
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
                    Log.d("title3", image);

                    //  Log.d("image", image);
                    ServiceModel model = new ServiceModel();

//                        model.image=jsonObject1.getString("image");
//                    model.visting=jsonObject1.getString("visiting_charge");
//                    model.service=jsonObject1.getString("service_charge");
//                    model.title=jsonObject1.getString("title");

                    Log.d("myget", model.toString());
                    model.setImage(image);
                    model.setTitle(title);
                    model.setVisting(visting);
                    model.setService(service);

                    arrayList.add(model);


                    Log.d("recylerview_responce", recyclerView.toString());
                    myAdapter = new MyAdapter(arrayList, getApplicationContext());
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerView.setAdapter(myAdapter);

                }

                catch (JSONException e) {
                    e.printStackTrace();
                }



            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NewServiceActivity.this, "somthing Wrong", Toast.LENGTH_SHORT).show();
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
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        ArrayList<ServiceModel> arrayList;
        Context context;

        public MyAdapter(ArrayList<ServiceModel> arrayList, Context context) {
            this.arrayList = arrayList;
            this.context = context;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view=LayoutInflater.from(context).inflate(R.layout.row_products_services,viewGroup,false);

            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
            ServiceModel model=arrayList.get(i);
            myViewHolder.title.setText(model.getTitle());
            myViewHolder.service.setText(model.getService());
            myViewHolder.visiting.setText(model.getVisting());

            Picasso.get().load(model.getImage()).into(myViewHolder.imageView);
            //   Glide.with(context).load(model.getImage()).into(myViewHolder.imageView);
            Log.d("imageurl", model.getImage());
            myViewHolder.booknow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
Intent intent=new Intent(NewServiceActivity.this,ServiceCheckoutActivity.class);
startActivity(intent);
                }
            });
            myViewHolder.relativeLayout_service.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
Intent intent=new Intent(NewServiceActivity.this,ServiceItemDetailsActivity.class);
startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{
            ImageView imageView;
            TextView title,service,visiting,booknow;
            RelativeLayout relativeLayout_service;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView=itemView.findViewById(R.id.service_image);
                title=itemView.findViewById(R.id.service_title);
                service=itemView.findViewById(R.id.service_price);
                visiting=itemView.findViewById(R.id.visiting_price);
                booknow=itemView.findViewById(R.id.booknow_service);
                relativeLayout_service=itemView.findViewById(R.id.relativeLayout_service);

            }
        }
    }


}
