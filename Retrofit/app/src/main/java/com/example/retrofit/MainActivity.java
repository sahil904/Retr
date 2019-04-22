package com.example.retrofit;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.retrofit.helper.EndlessRecyclerViewScrollListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity implements Callback<ModelClassforretro> {
    RecyclerView recylerview;
    ArrayList<ModelClassforretro.DataBean> arrayList;
    CultureCatAdapter cultureCatAdapter;
    private boolean loading = true;
    private int PAGE = 1;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager linearLayoutManager;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arrayList = new ArrayList<>();
        recylerview = findViewById(R.id.recylerview);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recylerview.setLayoutManager(linearLayoutManager);
        cultureCatAdapter = new CultureCatAdapter(arrayList);
        recylerview.setAdapter(cultureCatAdapter);
        progressBar = findViewById(R.id.pro);
        CallAPi(PAGE);
//        recylerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                if (dy > 0) //check for scroll down
//                {
//
//                    visibleItemCount = linearLayoutManager.getChildCount();
//                    totalItemCount = linearLayoutManager.getItemCount();
//                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
//
//                    if (loading) {
//                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
//                            loading = false;
//                            fetchdata();
//                            //Log.v("...", "Last Item Wow !");
//                            //Do pagination.. i.e. fetch new data
//                        }
//                    }
//                }
////                super.onScrolled(recyclerView, dx, dy);
//            }
//
//            private void fetchdata() {
//                progressBar.setVisibility(View.VISIBLE);
//                for (int i = 0; i < 5; i++) {
//                    // arrayList.add(Math.floor(Math.random()*100))
//                    cultureCatAdapter.notifyDataSetChanged();
//                    progressBar.setVisibility(View.GONE);
//                }
//            }
//        });
        recylerview.setOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                progressBar.setVisibility(View.VISIBLE);
                MainActivity.this.PAGE++;
                CallAPi(PAGE);
            }
        });

    }

    public void CallAPi(int id) {
Retro.api().MODEL_CALL(0).enqueue(this);

    }
        @Override
        public void onResponse (Response < ModelClassforretro > response, Retrofit retrofit){
        response.body();
            Log.d("kgjghj", response.body().toString());
//response.body().getData().get(0).getId();
//String image  = response.body().getData().get().getCategory_id();
            //  Picasso.get().load(response.body().getData().get(0).getImage()).into();
progressBar.setVisibility(View.GONE);
            if (response.body() != null) {
                ModelClassforretro modelClassforretro = response.body();
                arrayList.addAll(modelClassforretro.getData());
                cultureCatAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onFailure (Throwable t){

        }

}
