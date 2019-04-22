package fragments;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medhealth.MyApplication;
import com.medhealth.NewProductActivity;
import com.medhealth.NewServiceActivity;
import com.medi.R;
import com.medhealth.SplashActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import Config.ConstValue;
import imgLoader.AnimateFirstDisplayListener;
import models.BannarModel;
import models.HomeModel;
import util.ConnectionDetector;
import util.ObjectSerializer;
import util.RecyclerItemClickListener;
import util.ScrollerSpeed;

@SuppressWarnings("deprecation")
public class HomeNew extends Fragment {
    ImageSliderAdapter imageSliderAdapter;
    TabLayout tabDots;
    ViewPager viewPager;
    Timer timer;
    int currentPage;
    RecyclerView home_recycler_view;
    ArrayList<HashMap<String, String>> categoryArray;
    ArrayList<HomeModel> homeList;
    ArrayList<HomeModel> homeSet;
    ArrayList<BannarModel> bannerList;
    public SharedPreferences settings;
    HomeAdapter homeAdapter;
    RelativeLayout progressLayout, noInternetLayout;
    ConnectionDetector cd;
    HashMap<String, String> site_settings;
CardView service;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_new, container, false);
        homeList = new ArrayList<>();
        bannerList = new ArrayList<>();
        home_recycler_view = view.findViewById(R.id.home_recycler_view);
        viewPager = view.findViewById(R.id.viewPager);
        tabDots = view.findViewById(R.id.tabDots);

        /*service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewServiceActivity.class);
                startActivity(intent);
            }
        });*/
        //hitUrlForGetAName();
        try {
            Interpolator sInterpolator = new Interpolator() {
                @Override
                public float getInterpolation(float v) {
                    return v;
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
                if (currentPage == bannerList.size()) {
                    currentPage = 0;

                    viewPager.setCurrentItem(currentPage++, false);
                }else{
                    if (currentPage!=viewPager.getCurrentItem()+1){
                        currentPage = viewPager.getCurrentItem()+1;
                    }

                    viewPager.setCurrentItem(currentPage++, true);
                }
            }
        };
        timer = new Timer(); // This will create a new Thread
        timer .schedule(new TimerTask() { // task to be scheduled

            @Override
            public void run() {
                handler.post(Update);
            }
        }, 1000, 3000);

        settings = getActivity().getSharedPreferences(ConstValue.MAIN_PREF, 0);
        categoryArray = new ArrayList<>();
        try {
            categoryArray = (ArrayList<HashMap<String,String>>) ObjectSerializer.deserialize(settings.getString("categoryname", ObjectSerializer.serialize(new ArrayList<HashMap<String,String>>())));
        }catch (IOException e) {
            e.printStackTrace();
        }
        homeSet = new ArrayList<>();

        progressLayout = view.findViewById(R.id.progress_layout);
        noInternetLayout = view.findViewById(R.id.no_internet_layout);
        tabDots.setupWithViewPager(viewPager,true);
        home_recycler_view.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), home_recycler_view ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever

                            Log.d("position",String.valueOf(position));

                            int selpos = position;
                            Intent intent = new Intent(getActivity(), NewProductActivity.class);
                            intent.putExtra("list_data",homeList);
                            intent.putExtra("position", selpos);
                            startActivity(intent);


                    }
                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        cd = new ConnectionDetector(getActivity());
        if (cd.isConnectingToInternet()) {
          //  service.setVisibility (View.VISIBLE);

            noInternetLayout.setVisibility(View.GONE);
            hitUrlForHomeData();

        } else {
            noInternetLayout.setVisibility(View.VISIBLE);
        }
        return view;


    }

    private void hitUrlForHomeData() {

        progressLayout.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstValue.JSON_HOME_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {



                        Log.d("HomeResponse", responseStr);
                        JSONObject respondeObj = null;
                        try {
                            respondeObj = new JSONObject(responseStr);

                            JSONObject cartObject = respondeObj.getJSONObject("myCart");

                            MyApplication.getInstance().setTotalPrice(cartObject.getString("totalPrice"));
                            MyApplication.getInstance().setTotalQuantity(cartObject.getString("myQty"));

                            JSONObject settingObject = respondeObj.getJSONObject("setting");
                            if (!settingObject.getBoolean("error")) {
                                JSONArray dataArray = settingObject.getJSONArray("data");

                                site_settings = new HashMap<String, String>();

                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject obj = dataArray.getJSONObject(i);
                                    site_settings.put(obj.getString("title"), obj.getString("value"));
                                }
                            }
                            JSONObject bannerMainObject = respondeObj.getJSONObject("banner");
                            if (!bannerMainObject.getBoolean("error")) {
                                int spanCount = bannerMainObject.getInt("count");
                                JSONArray bannerArray = bannerMainObject.getJSONArray("data");
                                for (int b = 0; b < bannerArray.length(); b++) {
                                    BannarModel bannerModel = new BannarModel();
                                    JSONObject bannerObject = bannerArray.getJSONObject(b);
                                    bannerModel.bannarId = bannerObject.getString("id");
                                    bannerModel.bannarImage = bannerObject.getString("image");
                                    bannerModel.bannarStatus = bannerObject.getString("status");
                                    bannerModel.bannarTitle = bannerObject.getString("title");
                                    bannerList.add(bannerModel);
                                    Log.d("imagse" ,bannerList.get(b).bannarImage);
                                }
                                if (getActivity()!=null){
                                    imageSliderAdapter = new ImageSliderAdapter(getActivity(),bannerList);
                                    viewPager.setAdapter(imageSliderAdapter);
                                }
                            }
                            JSONObject catMainObject = respondeObj.getJSONObject("categories");
                            if (!catMainObject.getBoolean("error")) {
                                int spanCount = catMainObject.getInt("count");
                                JSONArray catArray = catMainObject.getJSONArray("data");
                                Log.d("cat_catArray", String.valueOf(catArray));

                                for (int i = 0; i < 4; i++) {
                                    JSONObject obj = catArray.getJSONObject(i);
                                    HashMap<String, String> map = new HashMap<String, String>();


                                    map.put("id", obj.getString("id"));
                                    map.put("name", obj.getString("name"));
                                    map.put("slug", obj.getString("slug"));
                                    map.put("description", obj.getString("description"));
                                    map.put("icon", obj.getString("icon"));

                                    HomeModel homeModel = new HomeModel();
                                    homeModel.id = obj.getString("id");
                                    homeModel.title = obj.getString("name");
                                    homeModel.image = obj.getString("icon");
                                    homeModel.status = obj.getString("status");
                                    homeModel.slug = obj.getString("slug");
                                    homeModel.descriiption = obj.getString("description");
                                    homeList.add(homeModel);
                                    categoryArray.add(map);
                                }
                                if (getActivity()!=null){
                                    home_recycler_view.setLayoutManager(new GridLayoutManager(getActivity(),2));
                                    homeAdapter = new HomeAdapter(homeList, getActivity());
                                    home_recycler_view.setAdapter(homeAdapter);
                                }
                            }
                            progressLayout.setVisibility(View.GONE);
                            settings.edit().putString("categoryname", ObjectSerializer.serialize(categoryArray)).commit();
                            settings.edit().putString("site_settings", ObjectSerializer.serialize(site_settings)).commit();

                        } catch (JSONException exp) {
                            Log.d("JsonExp", String.valueOf(exp));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       // progressLayout.setVisibility(View.GONE);
                        Log.d("Error", String.valueOf(error));
                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", settings.getString("userid", "00"));
                Log.d("dshdsf",settings.getString("userid", "00"));


                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }


    public class ImageSliderAdapter extends PagerAdapter {



        private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
        LayoutInflater inflater;
        ArrayList<BannarModel> bannerList;
        DisplayImageOptions options;
        ImageLoaderConfiguration imgconfig;

        public ImageSliderAdapter(Context context, ArrayList<BannarModel> bannerList) {
            this.bannerList = bannerList;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

            imgconfig = new ImageLoaderConfiguration.Builder(context)
                    .build();
            ImageLoader.getInstance().init(imgconfig);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View itemView = inflater.inflate(R.layout.image_item, container, false);
            final ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            ImageLoader.getInstance().displayImage(ConstValue.SLIDER_IMAGE_BIG_PATH + bannerList.get(position).bannarImage, imageView, options, animateFirstListener);

            Log.d("IMAGELOADING",ConstValue.SLIDER_IMAGE_BIG_PATH+bannerList.get(position).bannarImage);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public int getCount() {
            return bannerList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            ((ViewPager) container).removeView((View) object);
        }
    }


    private class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ListViewHolder> {
        private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
        private ArrayList<HomeModel> homeSetList;
        Context context;
        DisplayImageOptions options;
        ImageLoaderConfiguration imgconfig;

        public HomeAdapter(ArrayList<HomeModel> data, Context context) {
            this.context = context;
            this.homeSetList = data;

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

            imgconfig = new ImageLoaderConfiguration.Builder(context)
                    .build();
            ImageLoader.getInstance().init(imgconfig);
        }

        @Override
        public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_category,parent , false);
            ListViewHolder  listViewHolder = new ListViewHolder(view);
            return listViewHolder;
        }

        @Override
        public void onBindViewHolder(ListViewHolder holder, int position) {
            HomeModel object = homeSetList.get(position);
            ImageLoader.getInstance().displayImage(ConstValue.CAT_IMAGE_BIG_PATH+object.image, ((ListViewHolder) holder).catImage, options, animateFirstListener);
            ((ListViewHolder) holder).catName.setText(object.title);


        }

        //        @Override
//        public void onBindViewHolder(ViewGroup holder, int position) {
//            HomeMainModel object = homeSetList.get(position);
//
//            ImageLoader.getInstance().displayImage(ConstValue.CAT_IMAGE_BIG_PATH + object.homeModel.image,
//
//        }
        @Override
        public int getItemCount() {
            return homeSetList.size();
        }


        public class ListViewHolder extends RecyclerView.ViewHolder {
            ImageView catImage;
            TextView catName;
            public ListViewHolder(View itemView) {
                super(itemView);
                catImage = itemView.findViewById(R.id.cat_image);
                catName = itemView.findViewById(R.id.cat_name);
            }
        }
    }
}
