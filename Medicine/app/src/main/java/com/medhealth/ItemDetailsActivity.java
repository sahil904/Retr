package com.medhealth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import Config.ConstValue;
import models.FruitsModel;
import models.NewModelArray;

@SuppressWarnings("ALL")
public class ItemDetailsActivity extends AppCompatActivity {

    TextView searched_pro_title, textCurrency, search_price, textDiscount, txtunit, txtgm, price_text_dialog, value1, textDiscountFlag , text_discount_symbol;
    RelativeLayout  addLayout, quantityLayout, decrese, increse;
    LinearLayout addToCart;
    TextView totalQtyTv, totalPriceTv ,descriptionText;
    ImageView searched_image;
    String cartQty = " ";
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        text_discount_symbol = findViewById(R.id.text_discount_symbol);
        descriptionText = findViewById(R.id.desc_txt);
        getSupportActionBar().setTitle("Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         Bundle bundle =  getIntent().getExtras();

        // Log.d("cartQty",String.valueOf(bundle.getString("qty")));

        final FruitsModel fruitsModel = (FruitsModel) bundle.getSerializable("itemDeatil");


        addToCart = findViewById(R.id.add_to_cart_layout);

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemDetailsActivity.this, NewViewCartActivity.class);
                startActivity(intent);
                finish();
            }
        });
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

        addLayout = findViewById(R.id.add_layout);
        quantityLayout = findViewById(R.id.quantity_layout);
        decrese = findViewById(R.id.decrese);
        increse = findViewById(R.id.increse);
        value1 = findViewById(R.id.value1);


        //value1.setText(String.valueOf(bundle.getString("qty")));


        NewModelArray newModelArray = fruitsModel.productArray.get(0);
        Log.d("MarketPrice",newModelArray.market_price);


        SharedPreferences userPref = getSharedPreferences(ConstValue.MAIN_PREF, 0);
        String userId = userPref.getString("userid", "0");
        String pId = newModelArray.product_id;
        String disId = newModelArray.id;

        Log.d("userId-pId-disId",userId+" "+pId+" "+disId);

        initializeDialog();
        showProgressDialog();

        hitUrlForgetTotalQty(userId ,pId , disId);

        //descriptionText.setText(fruitsModel.description);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                descriptionText.setText(Html.fromHtml(fruitsModel.description, Html.FROM_HTML_MODE_LEGACY));
            } else {
                descriptionText.setText( Html.fromHtml(fruitsModel.description));
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

       /* if (Integer.parseInt(newModelArray.cartquantity) > 0) {
            value1.setText(newModelArray.cartquantity);
            quantityLayout.setVisibility(View.VISIBLE);
            addLayout.setVisibility(View.GONE);
        }*/

        Picasso.get()
                .load(ConstValue.PRO_IMAGE_BIG_PATH + fruitsModel.image)
                .placeholder(R.drawable.progress_animation)
                .into(searched_image);


        addLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantityLayout.setVisibility(View.VISIBLE);
                addLayout.setVisibility(View.GONE);
                try {
                    value1.setText(String.valueOf(Integer.parseInt(value1.getText().toString()) + 1));

                }catch (NumberFormatException e){e.printStackTrace();}

                incrementCart(Float.parseFloat(textDiscount.getText().toString()), 1);

                SharedPreferences userPref = getSharedPreferences(ConstValue.MAIN_PREF, 0);

                initializeDialog();
                showProgressDialog();

                hitUrlToAddProductInCart(userPref.getString("userid", "0"), fruitsModel.productArray.get(0).product_id, fruitsModel.productArray.get(0).id, value1.getText().toString());
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
                    hitUrlToAddProductInCart(userPref.getString("userid", "0"), fruitsModel.productArray.get(0).product_id, fruitsModel.productArray.get(0).id, value1.getText().toString());

                } else if (Integer.parseInt(value1.getText().toString()) == 1) {
                    value1.setText("0");
                    decrementCart(Float.parseFloat(textDiscount.getText().toString()), 1);
                    quantityLayout.setVisibility(View.GONE);
                    addLayout.setVisibility(View.VISIBLE);
                    SharedPreferences userPref = getSharedPreferences(ConstValue.MAIN_PREF, 0);
                    hitUrlToAddProductInCart(userPref.getString("userid", "0"), fruitsModel.productArray.get(0).product_id,fruitsModel.productArray.get(0).id, value1.getText().toString());
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
                hitUrlToAddProductInCart(userPref.getString("userid", "0"),fruitsModel.id, fruitsModel.productArray.get(0).id, value1.getText().toString());
                incrementCart(Float.parseFloat(textDiscount.getText().toString()), 1);
            }
        });
    }

    private void initializeDialog() {

        dialog = new ProgressDialog(ItemDetailsActivity.this,R.style.ProgrssTheme);
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



    private void hitUrlForgetTotalQty(String userId ,String productId , String discountId){

        String url = ConstValue.ITEM_DETAIL+"&userId="+userId+"&productId="+productId+"&discountId="+discountId;
        StringRequest   stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hideProgressDialog();

                Log.d("totalQty",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (!jsonObject.getBoolean("error")){


                       // value1.setText();

                        try {
                            if (Integer.parseInt(jsonObject.getString("TotalQty")) > 0) {

                                value1.setText(jsonObject.getString("TotalQty"));

                                quantityLayout.setVisibility(View.VISIBLE);

                                addLayout.setVisibility(View.GONE);

                            }
                        }catch (NumberFormatException e){
                            e.printStackTrace();
                        }

                      //  cartQty = jsonObject.getString("TotalQty");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(ItemDetailsActivity.this);
        requestQueue.add(stringRequest);
    }
    private void updateTotalPriceAndQty() {
        totalPriceTv.setText(MyApplication.getInstance().getTotalPrice());
        totalQtyTv.setText(MyApplication.getInstance().getTotalQuantity());
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

    private void decrementCart(Float decrementedPrice, int decerementQty) {

        Float price = Float.valueOf(MyApplication.getInstance().getTotalPrice());

        Log.d("price,discount DEc", String.valueOf(price + "," + decrementedPrice));

        Float totalSum = price - (decrementedPrice * decerementQty);

        MyApplication.getInstance().setTotalPrice(String.valueOf(Math.round(totalSum)));
        MyApplication.getInstance().setTotalQuantity(String.valueOf(Integer.parseInt(MyApplication.getInstance().getTotalQuantity()) - decerementQty));
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
                hideProgressDialog();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(ItemDetailsActivity.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return true;
    }

}
