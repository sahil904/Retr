package fragments;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import Config.ConstValue;
import ss.com.bannerslider.ImageLoadingService;
import ss.com.bannerslider.Slider;
import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;
import util.ScrollerSpeed;

@SuppressWarnings("ALL")
public class BookLabTest extends Fragment {

    EditText nameEt,mobileEt,addressEt,landmarkEt;
    TextView successTv;
    ProgressDialog progressDialog;
    RelativeLayout submitLayout,successLayout,labTestLayout;
    ProgressBar progressBar;
    ViewPager viewPager;
    TabLayout tabLayout;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private static final Integer[] IMAGES = {R.drawable.booklab1,R.drawable.booklab2,R.drawable.booklab3,R.drawable.booklab4};



    public BookLabTest() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

       View  view = inflater.inflate(R.layout.fragment_book_blood_test, container, false);

       initPager(view);

        nameEt = view.findViewById(R.id.name_bl_et);
        mobileEt = view.findViewById(R.id.mobile_bl_et);
        addressEt = view.findViewById(R.id.address_bl_et);
        successTv = view.findViewById(R.id.success_tv);
        successLayout = view.findViewById(R.id.success_layout_lab);
        labTestLayout = view.findViewById(R.id.lab_test_layout);
        labTestLayout.setVisibility(View.VISIBLE);
        successLayout.setVisibility(View.GONE);
        progressBar = new ProgressBar(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Submitting Please Wait.....");
        progressDialog.setCancelable(false);


        submitLayout = view.findViewById(R.id.submit_bl_layout);
        submitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameEt.getText().toString().length()<1){
                    nameEt.setError("can't be empty");
                }else if (mobileEt.getText().toString().length()<10){
                    mobileEt.setError("can't be less than 10 digits");
                }else if (addressEt.getText().toString().length()<1){
                    addressEt.setError("can't be empty");
                }else {
                    progressDialog.show();
                    hitUrlForLabBook();
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });
        return view;
    }
    private void initPager(View view)
    {
        viewPager = view.findViewById(R.id.pager);
        tabLayout = view.findViewById(R.id.tabDots_);
        tabLayout.setupWithViewPager(viewPager, true);
        final ArrayList<Integer> al = new ArrayList<>();
        al.add(R.drawable.booklab1);
        al.add(R.drawable.booklab2);
        al.add(R.drawable.booklab3);
        al.add(R.drawable.booklab4);
        PagerAdapter adapter = new PhotosAdapter(al , getActivity());
        viewPager.setAdapter(adapter);
        try {
        android.view.animation.Interpolator sInterpolator = new android.view.animation.Interpolator() {
            @Override
            public float getInterpolation(float input) {
                return input;
            }
        };
        Field mScroller;
        mScroller = ViewPager.class.getDeclaredField("mScroller");
        mScroller.setAccessible(true);
        ScrollerSpeed scroller = new ScrollerSpeed(viewPager.getContext(), sInterpolator);
        mScroller.set(viewPager, scroller);
    } catch (NoSuchFieldException e) {
    } catch (IllegalArgumentException e) {
    } catch (IllegalAccessException e) {
    }

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == al.size()) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2500, 2500);
    }

    class PhotosAdapter extends PagerAdapter {
        ArrayList<Integer> list;
        Context context;

        public PhotosAdapter(ArrayList<Integer> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.image_item,container,false);
            ImageView imageView = view.findViewById(R.id.imageView);
            Log.d("Images", String.valueOf(list.get(position)));
            imageView.setImageResource(list.get(position));
            container.addView(view,0);
            return view;
        }
    }


    private void hitUrlForLabBook()
    {
        Log.d("Clicked","Cklicked!!!!!!");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstValue.LAB_BOOKING, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                progressBar.setVisibility(View.GONE);
                successLayout.setVisibility(View.VISIBLE);
                labTestLayout.setVisibility(View.GONE);
                try {
                    JSONObject  jsonObject = new JSONObject(response);
                    Log.d("Response",response);
                    if (jsonObject.getString("responce").equalsIgnoreCase("false")){
                        successTv.setText(jsonObject.getString("data"));
                        nameEt.getText().clear();
                        mobileEt.getText().clear();
                        addressEt.getText().clear();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                progressBar.setVisibility(View.GONE);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                params.put("name",nameEt.getText().toString());
                params.put("address",addressEt.getText().toString());
                params.put("mobile",mobileEt.getText().toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }
}
