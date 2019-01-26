package com.medhealth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medi.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Config.ConstValue;
import models.FruitsModel;
import models.NewModelArray;
import util.AlertInterface;

@SuppressWarnings("deprecation")
public class SearchedProductDetail extends AppCompatActivity {
    DisplayImageOptions options;
    int selDisPos = 0 ;
    ProgressDialog dialogs;

    ImageView searched_image;
    ImageLoaderConfiguration imgconfig;
    TextView desc_txt,searched_pro_title, textCurrency, search_price, textDiscount, txtunit, txtgm, price_text_dialog, value1, textDiscountFlag , text_discount_symbol;
    RelativeLayout custom_dialog_layout, addLayout, quantityLayout, decrese, increse;
    LinearLayout addToCart;
    TextView totalQtyTv, totalPriceTv;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_product_detail);
        desc_txt = findViewById(R.id.desc_txt);
        text_discount_symbol = findViewById(R.id.text_discount_symbol);
        Bundle bundle = getIntent().getExtras();
        final FruitsModel fruitsModel = (FruitsModel) bundle.getSerializable("searchProduct");

        addToCart = findViewById(R.id.add_to_cart_layout);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchedProductDetail.this, NewViewCartActivity.class);
                startActivity(intent);
                finish();
            }
        });

        initializeDialog();

        searched_image = findViewById(R.id.searched_image);
        searched_pro_title = findViewById(R.id.searched_pro_title);
        textCurrency = findViewById(R.id.textCurrency);
        search_price = findViewById(R.id.search_price);
        textDiscount = findViewById(R.id.textDiscount);
        txtunit = findViewById(R.id.txtunit);
        txtgm = findViewById(R.id.txtgm);
        price_text_dialog = findViewById(R.id.price_text_dialog);
        textDiscountFlag = findViewById(R.id.textDiscountFlag);
        textDiscountFlag.setVisibility(View.GONE);

        totalQtyTv = findViewById(R.id.total_qty);
        totalPriceTv = findViewById(R.id.total_price);

        updateTotalPriceAndQty();

        custom_dialog_layout = findViewById(R.id.custom_dialog_layout);
        addLayout = findViewById(R.id.add_layout);
        quantityLayout = findViewById(R.id.quantity_layout);
        decrese = findViewById(R.id.decrese);
        increse = findViewById(R.id.increse);
        value1 = findViewById(R.id.value1);

        Log.d("curr", fruitsModel.productArray.get(selDisPos).sale_price);

        NewModelArray newModelArray = fruitsModel.productArray.get(selDisPos);
        Log.d("MarketPrice",newModelArray.market_price);

       // desc_txt.setText(fruitsModel.description);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            desc_txt.setText(Html.fromHtml(fruitsModel.description, Html.FROM_HTML_MODE_LEGACY));
        } else {
            desc_txt.setText( Html.fromHtml(fruitsModel.description));
        }
        searched_pro_title.setText(fruitsModel.title);

        search_price.setText(newModelArray.market_price);

        textDiscount.setText(newModelArray.sale_price);

        search_price.setPaintFlags(search_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        if (!newModelArray.discount.equalsIgnoreCase("0") && !newModelArray.discount.equalsIgnoreCase("")) {


            textDiscount.setText(String.valueOf(Math.round(Float.parseFloat(newModelArray.sale_price))));

            textDiscountFlag.setVisibility(View.VISIBLE);

            search_price.setPaintFlags(search_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//
            search_price.setTextColor(Color.RED);
            search_price.setTextSize(13.0f);
            search_price.setVisibility(View.VISIBLE);
//
            textDiscountFlag.setVisibility(View.VISIBLE);
            text_discount_symbol.setPaintFlags(text_discount_symbol.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            text_discount_symbol.setVisibility(View.VISIBLE);
            text_discount_symbol.setTextColor(Color.RED);
//
            text_discount_symbol.setTextSize(13.0f);

            textDiscountFlag.setText(Math.round(Float.parseFloat(newModelArray.discount)) + "% off");
        } else {
            textDiscountFlag.setVisibility(View.GONE);
        }
      Picasso.get()
                .load(ConstValue.PRO_IMAGE_BIG_PATH + fruitsModel.image)
               .placeholder(R.drawable.progress_animation)
                .into(searched_image);

        if (Integer.parseInt(newModelArray.cartquantity) > 0) {
            value1.setText(newModelArray.cartquantity);
            quantityLayout.setVisibility(View.VISIBLE);
            addLayout.setVisibility(View.GONE);
        }

        addLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantityLayout.setVisibility(View.VISIBLE);
                addLayout.setVisibility(View.GONE);
                value1.setText(String.valueOf(Integer.parseInt(value1.getText().toString()) + 1));
                incrementCart(Float.parseFloat(textDiscount.getText().toString()), 1);
                SharedPreferences userPref = getSharedPreferences(ConstValue.MAIN_PREF, 0);
                initializeDialog();
                showProgressDialog();
                hitUrlToAddProductInCart(userPref.getString("userid", "0"), fruitsModel.productArray.get(0).product_id, fruitsModel.productArray.get(selDisPos).id, value1.getText().toString());


            }
        });
        decrese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(value1.getText().toString()) > 1) {
                    value1.setText(String.valueOf(Integer.parseInt(value1.getText().toString()) - 1));
                    decrementCart(Float.parseFloat(textDiscount.getText().toString()), 1);
                    SharedPreferences userPref = getSharedPreferences(ConstValue.MAIN_PREF, 0);
                    Log.d("UserIDs", userPref.getString("userid", "0"));

                    initializeDialog();
                    showProgressDialog();
                    hitUrlToAddProductInCart(userPref.getString("userid", "0"), fruitsModel.productArray.get(0).product_id, fruitsModel.productArray.get(selDisPos).id, value1.getText().toString());

                } else if (Integer.parseInt(value1.getText().toString()) == 1) {
                    value1.setText("0");
                    quantityLayout.setVisibility(View.GONE);
                    addLayout.setVisibility(View.VISIBLE);
                    SharedPreferences userPref = getSharedPreferences(ConstValue.MAIN_PREF, 0);
                    hitUrlToAddProductInCart(userPref.getString("userid", "0"), fruitsModel.productArray.get(0).product_id,fruitsModel.productArray.get(selDisPos).id, value1.getText().toString());
                }
            }
        });
        increse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                value1.setText(String.valueOf(Integer.parseInt(value1.getText().toString()) + 1));
                SharedPreferences userPref = getSharedPreferences(ConstValue.MAIN_PREF, 0);
                initializeDialog();
                showProgressDialog();
                hitUrlToAddProductInCart(userPref.getString("userid", "0"),fruitsModel.id, fruitsModel.productArray.get(selDisPos).id, value1.getText().toString());
                incrementCart(Float.parseFloat(textDiscount.getText().toString()), 1);
            }
        });

        custom_dialog_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> dialogList = new ArrayList<>();


                for (int j = 0; j < fruitsModel.productArray.size(); j++) {

                    final NewModelArray modelForPrice = fruitsModel.productArray.get(j);

                    dialogList.add(String.valueOf(Math.round((Float.parseFloat(modelForPrice.sale_price)))) + "/" + String.valueOf(Integer.parseInt(modelForPrice.gmqty)) + modelForPrice.unit);
                }

                AlertInterface alertInterface = new AlertInterface() {
                    @Override
                    public void select(int positionAlert, String price) {

                        selDisPos = positionAlert-1;
                        search_price.setText(fruitsModel.productArray.get(positionAlert - 1).market_price);
                        textDiscount.setText(fruitsModel.productArray.get(positionAlert - 1).sale_price);
                        price_text_dialog.setText(price);
                        SharedPreferences userPref = getSharedPreferences(ConstValue.MAIN_PREF, 0);
                        String url = ConstValue.CHECK_PRODUCT_IN_CART+"&userId="+userPref.getString("userid", "0")+"&id="+fruitsModel.productArray.get(selDisPos).product_id
                                +"&discount_id="+fruitsModel.productArray.get(positionAlert - 1).id;
                         hitUrlForCheckProductInCart(url);

                    }
                };
                SearchItemDialog searchItemDialog = new SearchItemDialog(SearchedProductDetail.this,dialogList,alertInterface);
                searchItemDialog.show();
            }
        });
        price_text_dialog.setText(String.valueOf(search_price.getText().toString())+"/"+newModelArray.gmqty+newModelArray.unit);
    }
    private void decrementCart(Float decrementedPrice, int decerementQty) {

        Float price = Float.valueOf(MyApplication.getInstance().getTotalPrice());

        Log.d("price,discount DEc", String.valueOf(price + "," + decrementedPrice));

        Float totalSum = price - (decrementedPrice * decerementQty);
        MyApplication.getInstance().setTotalPrice(String.valueOf(Math.round(totalSum)));
        MyApplication.getInstance().setTotalQuantity(String.valueOf(Integer.parseInt(MyApplication.getInstance().getTotalQuantity()) - decerementQty));
        updateTotalPriceAndQty();
    }
    private void incrementCart(Float incrementedPrice, int incrementQty) {
        Log.d("numbr", String.format("%.2f", incrementedPrice));

        Float price = Float.valueOf(MyApplication.getInstance().getTotalPrice());

        Log.d("price,discount ADD", String.valueOf(price + "," + incrementedPrice));

        Float totalSum = price + (incrementedPrice * incrementQty);

        MyApplication.getInstance().setTotalPrice(String.valueOf(Math.round(totalSum)));

        MyApplication.getInstance().setTotalQuantity(String.valueOf(Integer.parseInt(MyApplication.getInstance().getTotalQuantity()) + incrementQty));
        updateTotalPriceAndQty();
    }


    private void hitUrlToAddProductInCart(final String userId, final String productId,String discountId, final String quantity) {
        String url = ConstValue.JSON_ADD_PRODUCT_TO_SERVER+"&userId="+userId+"&id="+productId+"&discount_id="+discountId+"&cartquantity="+quantity;
        Log.d("addTocart","userId"+userId+" productId"+productId+" discountId"+discountId+" qty"+quantity);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("ISDDDD", response);
                        hideProgressDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(SearchedProductDetail.this);
        requestQueue.add(stringRequest);
    }

    private void updateTotalPriceAndQty() {
        totalPriceTv.setText(MyApplication.getInstance().getTotalPrice());
        totalQtyTv.setText(MyApplication.getInstance().getTotalQuantity());
    }


    @SuppressWarnings("deprecation")
    public class SearchItemDialog extends AlertDialog implements DialogInterface.OnDismissListener {
        Context context;
        private ArrayList<String> postItems;
        AlertInterface alertInterface ;
        public SearchItemDialog(@NonNull Context context, ArrayList<String> postItems , AlertInterface alertInterface ) {
            super(context);
            this.context = context;
            this.postItems = postItems;
            this.alertInterface = alertInterface;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.select_pack_recycler);
            RecyclerView select_item_recycler = findViewById(R.id.dialog_recycler);
            LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            SearchItemAdapter cartItemAdapter = new SearchItemAdapter(context,postItems);
            select_item_recycler.setLayoutManager(linearLayoutManager);
            select_item_recycler.setAdapter(cartItemAdapter);
            Log.d("Sizes", String.valueOf(postItems.size()));
            setOnDismissListener(this);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
        }
        public class SearchItemAdapter extends RecyclerView.Adapter<SearchItemAdapter.SearchItemHolder>{

            Context context;
            private ArrayList<String> postItems;

            public SearchItemAdapter(Context context,  ArrayList<String> postItems){
                this.context = context;
                this.postItems = postItems ;
            }
            @Override
            public SearchItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(context).inflate(R.layout.select_pack_item,parent,false);
                return new SearchItemHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull SearchItemHolder holder, int position) {

                holder.dialog_price.setText(postItems.get(position));

                Log.d("price",postItems.get(position));



            }
            @Override
            public int getItemCount() {
                return postItems.size();

            }

            public class SearchItemHolder extends RecyclerView.ViewHolder{
                TextView dialog_price  ;
                public SearchItemHolder(View itemView) {
                    super(itemView);
                    dialog_price = itemView.findViewById(R.id.dialog_price);

                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dismiss();
                            int type = getAdapterPosition();
                            Log.d("type", String.valueOf(type));
                            alertInterface.select((getAdapterPosition()+1) , dialog_price.getText().toString());
                        }
                    });
                }
            }
        }

    }
    private void initializeDialog() {
        dialog = new ProgressDialog(SearchedProductDetail.this,R.style.ProgrssTheme);
        dialog.setCancelable(false);
        dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);



    }
    private void showProgressDialog() {
        if (dialog != null) {
            dialog.show();
        }
    }

    private void hideProgressDialog() {
        dialog.cancel();

    }
    private void hitUrlForCheckProductInCart(String url){
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgressDialog();

                Log.d("CartProduct",response);
                try {
                    JSONObject qtyObj = new JSONObject(response);
                    if (!qtyObj.getBoolean("error")){
                        int  cartQty = qtyObj.getInt("cartquantity");
                        Log.d("cartQty",String.valueOf(cartQty));
                        if (cartQty == 0) {
                            value1.setText(String.valueOf(cartQty));
                            addLayout.setVisibility(View.VISIBLE);
                            quantityLayout.setVisibility(View.GONE);
                        } else {
                            value1.setText(String.valueOf(cartQty));
                            addLayout.setVisibility(View.GONE);
                            quantityLayout.setVisibility(View.VISIBLE);
                        }
                    }else {
                        value1.setText(String.valueOf(0));
                        addLayout.setVisibility(View.VISIBLE);
                        quantityLayout.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error",String.valueOf(error));
                hideProgressDialog();
            }
        }) {
               /* @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("", userId);
                    params.put("", productId);
                    params.put("", discountId);
                    return params;
                }*/
        };
        RequestQueue requestQueue = Volley.newRequestQueue(SearchedProductDetail.this);
        requestQueue.add(stringRequest);
    }

}
