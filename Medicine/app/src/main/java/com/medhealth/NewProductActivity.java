package com.medhealth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.medi.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Config.ConstValue;
import fragments.CatDynamicFragment;
import models.HomeModel;
import util.Interfac;
import util.ObjectSerializer;

public class NewProductActivity extends AppCompatActivity {
    TextView Qty,Price,Unit;
    RelativeLayout  cart_layout,search_layout,view_cart_layout,stripLayout;
    TextView addtoCart,stripTv;
    //RelativeLayout addtoCart
    TabLayout tabLayout;
    Interfac casAuthentication ;
    ViewPager viewPager;
    private ViewPagerAdapter pagerAdapter = null;
    ArrayList<HomeModel> homeList;
    int selPosition;
    HashMap<String,String> site_settings;
    public SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_new_product);
        Toolbar toolbar = findViewById(R.id.product_tool);
        toolbar.setTitle("Products");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
        site_settings = new HashMap<String,String>();
        try {
            site_settings = (HashMap<String,String>) ObjectSerializer.deserialize(settings.getString("site_settings", ObjectSerializer.serialize(new HashMap<String, String>())));
        }catch (IOException e) {
            e.printStackTrace();
        }
        cart_layout = findViewById(R.id.cart_layout);
        search_layout = findViewById(R.id.search_layout);
        view_cart_layout = findViewById(R.id.view_cart_layout);
        cart_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewProductActivity.this,NewViewCartActivity.class);
                startActivity(intent);
            }
        });
        search_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewProductActivity.this,NewSearchActivity.class);
                startActivity(intent);
            }
        });

        stripTv = findViewById(R.id.strip_tv);
        stripLayout = findViewById(R.id.strip_price_layout);
        stripLayout.setVisibility(View.GONE);
        Qty = findViewById(R.id.totalitem);
        Price = findViewById(R.id.TotalPrice);
        addtoCart = findViewById(R.id.btnAdd);

        try{
            Qty.setText(String.valueOf( MyApplication.getInstance().getTotalQuantity()));
            Price.setText(String.valueOf( MyApplication.getInstance().getTotalPrice() ));

        }catch (NumberFormatException ex){

        }
        stripTv.setText("Shop For More Than Rs. "+site_settings.get("Max Purchase Order")+"For Free Delivery");
        if (Float.parseFloat(MyApplication.getInstance().getTotalPrice()) >= Float.parseFloat(site_settings.get("Max Purchase Order"))){
            stripLayout.setBackgroundColor(Color.parseColor("#de3218"));
        }
        else {
            stripLayout.setBackgroundColor(Color.parseColor("#fde0a4"));
        }
        casAuthentication  = new Interfac() {
            @Override
            public void change() {
                Qty.setText(String.valueOf( MyApplication.getInstance().getTotalQuantity()));
                Price.setText(String.valueOf( MyApplication.getInstance().getTotalPrice() ));
                Log.d("price",MyApplication.getInstance().getTotalPrice());
                if (Float.parseFloat(MyApplication.getInstance().getTotalPrice()) >= Float.parseFloat(site_settings.get("Max Purchase Order"))){
                    stripLayout.setBackgroundColor(Color.parseColor("#de3218"));
                }
                else {
                    stripLayout.setBackgroundColor(Color.parseColor("#fde0a4"));
                }
            }
        };
        view_cart_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewProductActivity.this,NewViewCartActivity.class);
                startActivity(intent);
            }
        });
        homeList = (ArrayList<HomeModel>) getIntent().getSerializableExtra("list_data");

        selPosition = getIntent().getIntExtra("position",0);
        Log.d("selpos", String.valueOf(selPosition));
    }
    @Override
    protected void onStart() {
        super.onStart();
        initUI();
        try{
            Qty.setText(String.valueOf( MyApplication.getInstance().getTotalQuantity()));
            Price.setText(String.valueOf( MyApplication.getInstance().getTotalPrice() ));
            if (Float.parseFloat(MyApplication.getInstance().getTotalPrice()) >= Float.parseFloat(site_settings.get("Max Purchase Order"))){
                stripLayout.setBackgroundColor(Color.parseColor("#de3218"));
            }
            else {
                stripLayout.setBackgroundColor(Color.parseColor("#fde0a4"));
            }

        }catch (NumberFormatException ex){

        }
    }
    private void  initUI(){
        viewPager = findViewById(R.id.viewpager);
        if (viewPager != null){
            initViewPager(viewPager,homeList);
        }
        tabLayout = findViewById(R.id.tabs);
     //  tabLayout.setVisibility(View.GONE);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(selPosition,true);
    }
    private void initViewPager(ViewPager viewPager, ArrayList<HomeModel> homeList){
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        if (homeList != null){

            for (HomeModel homeModel : homeList){

                pagerAdapter.addFragment(CatDynamicFragment.newInstance(homeModel , homeModel.id,homeModel.title, casAuthentication),homeModel.title);
            }

            viewPager.setOffscreenPageLimit(homeList.size());
        }

        viewPager.setAdapter(pagerAdapter);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<android.support.v4.app.Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            if (mFragmentList != null) {
                return mFragmentList.size();
            }else {
                return 0;
            }
        }

        public void addFragment(android.support.v4.app.Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public SpannableString getPageTitle(int position) {

            SpannableString spannableString = new SpannableString(mFragmentTitleList.get(position));

            return spannableString;
        }
    }
}
