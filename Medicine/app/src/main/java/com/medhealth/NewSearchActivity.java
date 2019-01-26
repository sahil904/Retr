package com.medhealth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import Config.ConstValue;
import models.FruitsModel;
import models.HomeMainModel;
import models.HomeModel;
import models.NewModelArray;

@SuppressWarnings("deprecation")
public class NewSearchActivity extends AppCompatActivity {
    EditText searchItem;
    ImageView clear;
    RecyclerView searchRecycler;
    ArrayList<FruitsModel> arrayList;
    String userIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_search);
        getSupportActionBar().setTitle("Search");
        searchItem = findViewById(R.id.search_item);
        clear = findViewById(R.id.clear_image);
        searchRecycler = findViewById(R.id.search_recycler);
        searchRecycler.setLayoutManager(new LinearLayoutManager(this));
        SharedPreferences userPref = getSharedPreferences(ConstValue.MAIN_PREF, 0);
        userIds =  userPref.getString("userid", "0");
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //urlString = ConstValue.JSON_PRODUCTS + "&id=6" + ids + "&userId=29" + "&search=" + query;

        searchItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (count>2)
                    hitUrlForSearch(String.valueOf(s));
                }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void hitUrlForSearch(String query) {
        arrayList = new ArrayList<>();
        arrayList.clear();
        String  urlString = ConstValue.JSON_PRODUCTS + "&userId="+userIds + "&search=" + query;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("responseSearch",response);

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i<jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        FruitsModel fruitsModel = new FruitsModel();
                        fruitsModel.catId = jsonObject.getString("category_id");
                        fruitsModel.cod = jsonObject.getString("cod");
                        fruitsModel.deliverycharge = jsonObject.getString("deliverycharge");
                        fruitsModel.description = jsonObject.getString("description");
                        fruitsModel.emi = jsonObject.getString("emi");
                        fruitsModel.id = jsonObject.getString("id");
                        fruitsModel.image = jsonObject.getString("image");
                        fruitsModel.on_date = jsonObject.getString("on_date");
                        fruitsModel.slug = jsonObject.getString("slug");
                        fruitsModel.status = jsonObject.getString("status");
                        fruitsModel.tax = jsonObject.getString("tax");
                        fruitsModel.title = jsonObject.getString("title");


                        ArrayList<NewModelArray> priceArray = new ArrayList<>();
                        JSONArray itemArray = jsonObject.getJSONArray("discounts");
                        for (int j = 0; j < itemArray.length(); j++) {
                            NewModelArray newModelArray = new NewModelArray();
                            JSONObject itemObj = itemArray.getJSONObject(j);
                            newModelArray.cartquantity = itemObj.getString("cartquantity");
                            newModelArray.currency = itemObj.getString("currency");
                            newModelArray.discount = itemObj.getString("discount");
                            newModelArray.gmqty = itemObj.getString("gmqty");
                            newModelArray.id = itemObj.getString("id");
                            newModelArray.market_price = itemObj.getString("market_price");
                            newModelArray.product_id = itemObj.getString("product_id");
                            newModelArray.sale_price = itemObj.getString("sale_price");
                            newModelArray.slug = itemObj.getString("slug");
                            newModelArray.unit = itemObj.getString("unit");
                            priceArray.add(newModelArray);
                        }
                        fruitsModel.productArray = priceArray;
                        arrayList.add(fruitsModel);
                    }
                    } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (arrayList.size()>0) {
                    SearchAdapter searchAdapter = new SearchAdapter(arrayList, NewSearchActivity.this);
                    searchRecycler.setAdapter(searchAdapter);
                }else {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

        };
        RequestQueue requestQueue = Volley.newRequestQueue(NewSearchActivity.this);
        requestQueue.add(stringRequest);
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>{
        ArrayList<FruitsModel> al;
        Context context;
        public SearchAdapter(ArrayList<FruitsModel> al, Context context) {
            this.al = al;
            this.context = context;

        }
        @Override
        public SearchAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.search_item,parent,false);
            SearchViewHolder searchViewHolder = new SearchViewHolder(view);
            return searchViewHolder ;
        }
        @Override
        public void onBindViewHolder(final SearchAdapter.SearchViewHolder holder, int position) {
            final FruitsModel fruitsModel = al.get(position);
            holder.search_txt.setText(fruitsModel.title);
            Log.d("name",fruitsModel.title);

            holder.search_layout_.setTag(position);
            holder.search_layout_.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (int) holder.search_layout_.getTag();
                    FruitsModel fruitsModel1 = al.get(pos);
                    Intent intent = new Intent(context,SearchedProductDetail.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("possss",pos);
                    bundle.putSerializable("searchProduct",fruitsModel1);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                    finish();
                }
            });
        }
        @Override
        public int getItemCount() {
            return al.size();
        }
        public class SearchViewHolder extends RecyclerView.ViewHolder {

            TextView search_txt;
            RelativeLayout search_layout_;
            public SearchViewHolder(View itemView) {
                super(itemView);
                search_txt = itemView.findViewById(R.id.search_txt);
                search_layout_ = itemView.findViewById(R.id.search_layout_);
            }
        }
    }
}
