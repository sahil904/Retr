package com.medhealth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medi.R;
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
import java.util.ArrayList;
import java.util.HashMap;

import Config.ConstValue;
import fragments.MyCart;
import imgLoader.AnimateFirstDisplayListener;
import models.CartModel;
import util.ConnectionDetector;
import util.ObjectSerializer;


@SuppressWarnings("deprecation")
public class NewViewCartActivity extends AppCompatActivity {
    public SharedPreferences settings;
    public ConnectionDetector cd;
    ArrayList<CartModel> cartList;
    RecyclerView cartRecyclerView;
    CartAdapter cartAdapter;
    TextView textTotalPrice,textTotalQuantity,deliveryCharge ,grand_total;
    HashMap<String,String> site_settings;
    RelativeLayout emptyCartLayout,stripLayout ,noInternetLayout;
    TextView stripTv;
    RelativeLayout checkout;
    ProgressDialog dialog;
    ProgressDialog dialogs;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_view_cart);
        grand_total = findViewById(R.id.grand_total);
        noInternetLayout = findViewById(R.id.no_internet_layout);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_viewcart));
        stripLayout = findViewById(R.id.strips);
        stripTv = findViewById(R.id.strip_tv);
        cartList = new ArrayList<>();
        cd = new ConnectionDetector(getApplicationContext());


        settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);

        site_settings = new HashMap<String,String>();
        try {
            site_settings = (HashMap<String,String>) ObjectSerializer.deserialize(settings.getString("site_settings", ObjectSerializer.serialize(new HashMap<String, String>())));
        }catch (IOException e) {
            e.printStackTrace();
        }
        stripTv.setText("Shop For More Than Rs. "+site_settings.get("Max Purchase Order")+"For Free Delivery");
try {
    if (Float.parseFloat(MyApplication.getInstance().getTotalPrice()) >= Float.parseFloat(site_settings.get("Max Purchase Order"))) {
        stripLayout.setBackgroundColor(Color.parseColor("#24af20"));
    } else {
        stripLayout.setBackgroundColor(Color.parseColor("#fde0a4"));
    }
}catch (Exception e){}

        initializeDialog();
        cartRecyclerView = findViewById(R.id.cart_recycler_view);
        LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(NewViewCartActivity.this, LinearLayoutManager.VERTICAL, false);
        cartRecyclerView.setLayoutManager(linearLayoutManager);

        textTotalPrice = findViewById(R.id.textTotalPrice);
        deliveryCharge = findViewById(R.id.delivery_charge);
        emptyCartLayout = findViewById(R.id.empty_cart_layout);
        try {
            textTotalPrice.setText(MyApplication.getInstance().getTotalPrice());
            String price = MyApplication.getInstance().getTotalPrice();
            float p = Float.parseFloat(price);
            if (p >= Float.parseFloat(site_settings.get("Max Purchase Order"))) {
                grand_total.setText(MyApplication.getInstance().getTotalPrice());
                deliveryCharge.setText("00");
            }
            else {
                grand_total.setText(String.valueOf(Float.parseFloat(price) + Float.parseFloat(site_settings.get("Delivery Charge"))));
                deliveryCharge.setText(site_settings.get("Delivery Charge"));
            }
        }catch (Exception e){}
        //textTotalQuantity.setText(MyApplication.getInstance().getTotalQuantity());

        if (cd.isConnectingToInternet()){
            showProgressDialog();
            noInternetLayout.setVisibility(View.GONE);
            hitUrlForViewCart(settings.getString("userid","29"));
        }else {
            noInternetLayout.setVisibility(View.VISIBLE);
            //NO Internet Layout
        }
        checkout = findViewById(R.id.footerlayout);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(NewViewCartActivity.this, CheckoutActivity.class);
                    intent.putExtra("cart_list" , cartList);
                    intent.putExtra("total_amount",String.valueOf(Math.round(Float.parseFloat(grand_total.getText().toString()))));
                    startActivity(intent);
            }
        });
    }

    private void hitUrlForViewCart(final String userId)
    {
        String url =  ConstValue.GET_CART_URL+"&userId="+userId;

        StringRequest stringRequest = new StringRequest(Request.Method.POST ,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                cartList.clear();
                hideProgressDialog();
                Log.d("ViewCart",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if ( !jsonObject.getBoolean("error")){
                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        for (int i =0 ; i<dataArray.length(); i++){
                            CartModel cartModel = new CartModel();
                            JSONObject cartObj = dataArray.getJSONObject(i);
                            cartModel.cartQuantity = cartObj.getString("cartquantity");
                            cartModel.categoryId = cartObj.getString("category_id");
                            cartModel.cod = cartObj.getString("cod");
                            cartModel.currency = cartObj.getString("currency");
                            cartModel.deliveryCharge = cartObj.getString("deliverycharge");
                            cartModel.description = cartObj.getString("description");
                            cartModel.discount = cartObj.getString("discount");
                            cartModel.disId = cartObj.getString("dis_id");
                            cartModel.emi = cartObj.getString("emi");
                            cartModel.gmQty = cartObj.getString("gmqty");
                            cartModel.id = cartObj.getString("id");
                            cartModel.image = cartObj.getString("image");
                            cartModel.marketPrice = cartObj.getString("market_price");
                            cartModel.onDate = cartObj.getString("on_date");
                            cartModel.salePrice = cartObj.getString("sale_price");
                            cartModel.slug = cartObj.getString("slug");
                            cartModel.status = cartObj.getString("status");
                            cartModel.tax = cartObj.getString("tax");
                            cartModel.title = cartObj.getString("title");
                            cartModel.unit = cartObj.getString("unit");
                            cartList.add(cartModel);
                        }
                        if (cartList.size()>0) {
                            cartAdapter = new CartAdapter(NewViewCartActivity.this, cartList);
                            cartRecyclerView.setAdapter(cartAdapter);
                            cartAdapter.notifyDataSetChanged();
                        }else {
                            emptyCartLayout.setVisibility(View.VISIBLE);
                            checkout.setVisibility(View.GONE);
                        }
                    }else {
                        emptyCartLayout.setVisibility(View.VISIBLE);
                        checkout.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", String.valueOf(error));
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(NewViewCartActivity.this);
        requestQueue.add(stringRequest);
    }

    private class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{
        Context context;
        ArrayList<CartModel> cartList;
        DisplayImageOptions options;
        ImageLoaderConfiguration imgConfig;
        private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

        public class CartViewHolder extends RecyclerView.ViewHolder{
            ImageView productImage ,delete_image;
            TextView productTitle,productPrice,productOriginalPrice,discountFlag,productQuantity,myQuantity,productUnit,productCurrency;
            RelativeLayout incrementLayout,decrementLayout;
            public CartViewHolder(View itemView) {
                super(itemView);
                delete_image = itemView.findViewById(R.id.delete_image);
                productImage = itemView.findViewById(R.id.pro_image);
                productTitle = itemView.findViewById(R.id.proTitle);
                productPrice = itemView.findViewById(R.id.txtprice);
                productOriginalPrice = itemView.findViewById(R.id.txtOriginalPrice);
                discountFlag = itemView.findViewById(R.id.textDiscountFlag);
                productQuantity = itemView.findViewById(R.id.txtquantity);
                myQuantity = itemView.findViewById(R.id.sel_quantity_text);
                productUnit = itemView.findViewById(R.id.txtunit);
                productCurrency = itemView.findViewById(R.id.textCurrency);
                incrementLayout = itemView.findViewById(R.id.increse);
                decrementLayout = itemView.findViewById(R.id.decrese);


                delete_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(context);
                        } else {
                            builder = new AlertDialog.Builder(context);
                        }
                        builder.setTitle("Delete Items!!!")
                                .setMessage("Are you want to delete this Item?")
                                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete

                                        int selQuantity = Integer.parseInt(myQuantity.getText().toString());

                                        Log.d("qty", String.valueOf(selQuantity));

                                        if (!cartList.get(getAdapterPosition()).discount.equalsIgnoreCase("") && !cartList.get(getAdapterPosition()).discount.equalsIgnoreCase("0")) {
                                            Double effectedPrice = calculateMoney(getAdapterPosition(),String.valueOf(selQuantity))*selQuantity;
                                            Double d = Double.parseDouble(String.valueOf(effectedPrice));
                                            productPrice.setText(String.valueOf(Math.round(effectedPrice)));
                                            decrementCart(calculateMoney(getAdapterPosition(),String.valueOf(selQuantity)), selQuantity);
                                        }else {
                                            Double price2 = selQuantity * Double.parseDouble(cartList.get(getAdapterPosition()).salePrice);
                                            productPrice.setText(String.valueOf(Math.round(price2)));
                                            decrementCart(Double.parseDouble(cartList.get(getAdapterPosition()).salePrice), selQuantity);
                                        }
                                        initializeDialogs();
                                        showProgressDialogs();
                                        hitUrlToAddProductInCart(settings.getString("userid","29"),cartList.get(getAdapterPosition()).id,cartList.get(getAdapterPosition()).disId,String.valueOf(0));
                                        cartList.remove(getAdapterPosition());
                                        myQuantity.setText(String.valueOf(0));
                                        cartAdapter.notifyItemRemoved(getAdapterPosition());
                                        if (cartList.size()==0){
                                            emptyCartLayout.setVisibility(View.VISIBLE);
                                            checkout.setVisibility(View.GONE);
                                        }
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                });

                incrementLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                     Log.d("incrementLayout",String.valueOf(getAdapterPosition()));

                     int selQuantity = Integer.parseInt(myQuantity.getText().toString())+1;


                     Log.d("qty",String.valueOf(Integer.parseInt(productQuantity.getText().toString())));

                        Log.d("qty2",String.valueOf(selQuantity));

                       myQuantity.setText(String.valueOf(selQuantity));


                        int proQty = selQuantity*Integer.parseInt(cartList.get(getAdapterPosition()).gmQty);


                        if (!cartList.get(getAdapterPosition()).discount.equalsIgnoreCase("") && !cartList.get(getAdapterPosition()).discount.equalsIgnoreCase("0")) {
                            Double effectedPrice = calculateMoney(getAdapterPosition(),String.valueOf(selQuantity))*selQuantity;
                            productPrice.setText(String.valueOf(Math.round(effectedPrice)));

                            incrementCart(calculateMoney(getAdapterPosition(),String.valueOf(selQuantity)));

                        }else {
                            Double price2 = selQuantity * Double.parseDouble(cartList.get(getAdapterPosition()).salePrice);
                            productPrice.setText(String.valueOf(Math.round(price2)));
                            incrementCart(Double.parseDouble(cartList.get(getAdapterPosition()).salePrice));
                        }

                        productOriginalPrice.setText(String.valueOf(selQuantity * Double.parseDouble(cartList.get(getAdapterPosition()).marketPrice)));

                        initializeDialogs();
                        showProgressDialogs();
                        hitUrlToAddProductInCart(settings.getString("userid","29"),cartList.get(getAdapterPosition()).id,cartList.get(getAdapterPosition()).disId,String.valueOf(selQuantity));
                    }
                });

                decrementLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (Integer.parseInt(myQuantity.getText().toString()) > 1){
                            int selQuantity = Integer.parseInt(myQuantity.getText().toString())-1;
                            myQuantity.setText(String.valueOf(selQuantity));

                            int proQty = selQuantity * Integer.parseInt(cartList.get(getAdapterPosition()).gmQty);

                            if (!cartList.get(getAdapterPosition()).discount.equalsIgnoreCase("") && !cartList.get(getAdapterPosition()).discount.equalsIgnoreCase("0")) {
                                Double effectedPrice = calculateMoney(getAdapterPosition(),String.valueOf(selQuantity))*selQuantity;
                                productPrice.setText(String.valueOf( Math.round(effectedPrice)));
                                decrementCart(calculateMoney(getAdapterPosition(),String.valueOf(selQuantity)),1);

                            }else {
                                Double price2 = selQuantity * Double.parseDouble(cartList.get(getAdapterPosition()).salePrice);
                                productPrice.setText(String.valueOf(Math.round(price2)));
                                decrementCart(Double.parseDouble(cartList.get(getAdapterPosition()).salePrice),1);
                            }
                            productOriginalPrice.setText(String.valueOf(selQuantity * Double.parseDouble(cartList.get(getAdapterPosition()).marketPrice)));
                            initializeDialogs();
                            showProgressDialogs();
                            hitUrlToAddProductInCart(settings.getString("userid","29"),cartList.get(getAdapterPosition()).id,cartList.get(getAdapterPosition()).disId,String.valueOf(selQuantity));

                        }else if (myQuantity.getText().toString().equalsIgnoreCase("1")){
                            if (!cartList.get(getAdapterPosition()).discount.equalsIgnoreCase("") && !cartList.get(getAdapterPosition()).discount.equalsIgnoreCase("0")) {
                                decrementCart(calculateMoney(getAdapterPosition(),String.valueOf(1)),1);
                            }else {
                                decrementCart(Double.parseDouble(cartList.get(getAdapterPosition()).salePrice),1);
                            }
                            myQuantity.setText(String.valueOf(0));
                            initializeDialogs();
                            showProgressDialogs();

                            hitUrlToAddProductInCart(settings.getString("userid","29"),cartList.get(getAdapterPosition()).id,cartList.get(getAdapterPosition()).disId,String.valueOf(0));
                            cartList.remove(getAdapterPosition());
                            cartAdapter.notifyItemRemoved(getAdapterPosition());
                            if (cartList.size()==0){
                                emptyCartLayout.setVisibility(View.VISIBLE);
                                checkout.setVisibility(View.GONE);
                            }
                        }
                    }
                });
            }
        }
        private Double calculateMoney(int position,String selQty) {

                Double price = Double.parseDouble(cartList.get(position).salePrice);
                return price;
        }

        public CartAdapter (Context context, ArrayList<CartModel> cartList){
            Log.d("cartList22",String.valueOf(cartList.size()));
             this.context = context;
             this.cartList = cartList;
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
            imgConfig = new ImageLoaderConfiguration.Builder(context)
                    .build();
            ImageLoader.getInstance().init(imgConfig);

            /*settings = context.getSharedPreferences("Products", 0);*/
        }

        @Override
        public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_cart_item,parent,false);
            return new CartViewHolder(view);
        }
        @Override
        public void onBindViewHolder(CartViewHolder holder, int position) {
            ImageLoader.getInstance().displayImage(ConstValue.PRO_IMAGE_BIG_PATH+cartList.get(position).image, holder.productImage, options, animateFirstListener);
            holder.productTitle.setText(cartList.get(position).title);
            //holder.productCurrency.setText();
            holder.productCurrency.setTextColor(Color.RED);
            holder.productCurrency.setPaintFlags(holder.productOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.myQuantity.setText(cartList.get(position).cartQuantity);

            Double price2 = Double.parseDouble(cartList.get(position).salePrice);
            price2 = price2 * Double.parseDouble(cartList.get(position).cartQuantity);
            holder.productPrice.setText(String.valueOf(price2));
            holder.productOriginalPrice.setText(String.valueOf(Math.round(Float.parseFloat(cartList.get(position).marketPrice))));

            if(!cartList.get(position).discount.equalsIgnoreCase("") && !cartList.get(position).discount.equalsIgnoreCase("0")){
                Double marketPrice = Double.parseDouble(cartList.get(position).marketPrice);
                marketPrice = marketPrice * Double.parseDouble(cartList.get(position).cartQuantity);
                Double salePrice = Double.parseDouble(cartList.get(position).salePrice);
                salePrice = salePrice * Double.parseDouble(cartList.get(position).cartQuantity);

                holder.productOriginalPrice.setPaintFlags(holder.productOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.productOriginalPrice.setVisibility(View.VISIBLE);
                holder.productOriginalPrice.setTextColor(Color.RED);
                holder.productOriginalPrice.setText(String.valueOf(Math.round(marketPrice)));
                holder.productPrice.setText(String.valueOf(Math.round(salePrice)));

                holder.discountFlag.setVisibility(View.VISIBLE);
                holder.discountFlag.setText(String.valueOf(Math.round(Float.parseFloat(cartList.get(position).discount))+"% off"));
                holder.incrementLayout.setTag(String.valueOf(position));
                holder.decrementLayout.setTag(String.valueOf(position));
                holder.productUnit.setText(cartList.get(position).unit);
            }else {
                holder.discountFlag.setVisibility(View.GONE);
                holder.productOriginalPrice.setVisibility(View.GONE);
            }
            try {
                int quantity = Integer.parseInt(cartList.get(position).gmQty);
                int selQuantity = quantity*Integer.parseInt(cartList.get(position).cartQuantity);
                holder.productQuantity.setText(String.valueOf(quantity));

            }catch (NumberFormatException exp){

            }
        }
        @Override
        public int getItemCount() {
            return cartList.size();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.viewcart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
       else if(id==R.id.emptycart){

            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(NewViewCartActivity.this);
            } else {
                builder = new AlertDialog.Builder(NewViewCartActivity.this);
            }
            builder.setTitle("Delete Items!!!")
                    .setMessage("Are you want to delete Item?")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    showProgressDialog();
                                    hitUrlForDeleteCart();
                                    MyCart emptycart = new MyCart(getApplicationContext());
                                    emptycart.empty_cart();
                                }
                                })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                // do nothing
            }
        })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        }
        else if(id == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    private void hitUrlForDeleteCart(){
        String urlString = ConstValue.DELETE_CART+"&userId="+settings.getString("userid","29");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();
                        Log.d("ISDDDD",response);
                        MyApplication.getInstance().setTotalPrice(String.valueOf(0));
                        MyApplication.getInstance().setTotalQuantity(String.valueOf(0));
                        Toast.makeText(getApplicationContext(),getString(R.string.viewcartactivity_all_product_removed),Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();

            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(NewViewCartActivity.this);
        requestQueue.add(stringRequest);
    }
    /*private void hitUrlForDeleteSingleItem(final String userId , final String productId ){



    }*/

    private void hitUrlToAddProductInCart(final String userId, final String productId,final String disId, final String quantity){
        String url = ConstValue.JSON_ADD_PRODUCT_TO_SERVER+"&userId="+userId+"&id="+productId+"&discount_id="+disId+"&cartquantity="+quantity;
        Log.d("ISDDDD",String.valueOf(userId+","+productId+ ", " +disId+","+quantity));
        Log.d("addUrl",url);
        Log.d("URLSS",ConstValue.JSON_ADD_PRODUCT_TO_SERVER);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialogs();
                        hitUrlForViewCart(settings.getString("userid","29"));

                        Log.d("ISDDDD",response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //hideProgressDialogs();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(NewViewCartActivity.this);
        requestQueue.add(stringRequest);
    }
    private void incrementCart(Double incrementedPrice)
    {
        Double price = Double.valueOf(MyApplication.getInstance().getTotalPrice());
        Double totalAdd = price+incrementedPrice;
        MyApplication.getInstance().setTotalPrice(String.valueOf(Math.round(totalAdd)));
        MyApplication.getInstance().setTotalQuantity(String.valueOf(Integer.parseInt(MyApplication.getInstance().getTotalQuantity())+1));
        textTotalPrice.setText(MyApplication.getInstance().getTotalPrice());
        if (Float.parseFloat(MyApplication.getInstance().getTotalPrice()) >= Float.parseFloat(site_settings.get("Max Purchase Order"))){
            grand_total.setText(String.valueOf(Float.parseFloat(MyApplication.getInstance().getTotalPrice())));
        }
        else {
            grand_total.setText(String.valueOf(Float.parseFloat(MyApplication.getInstance().getTotalPrice()) + Float.parseFloat(site_settings.get("Delivery Charge"))));
        }
        //pro.setText(MyApplication.getInstance().getTotalQuantity());
        if (Float.parseFloat(MyApplication.getInstance().getTotalPrice()) >= Float.parseFloat(site_settings.get("Max Purchase Order"))){
            stripLayout.setBackgroundColor(Color.parseColor("#24af20"));
        }
        else {
            stripLayout.setBackgroundColor(Color.parseColor("#fde0a4"));
        }

    }

    private void decrementCart(Double decrementedPrice,int selQty){

        Double price = Double.valueOf(MyApplication.getInstance().getTotalPrice());
        decrementedPrice = decrementedPrice*selQty ;
        Double totalAdd = price - decrementedPrice ;
        MyApplication.getInstance().setTotalPrice(String.valueOf(Math.round(totalAdd)));
        MyApplication.getInstance().setTotalQuantity(String.valueOf(Integer.parseInt(MyApplication.getInstance().getTotalQuantity()) - selQty));
        textTotalPrice.setText(MyApplication.getInstance().getTotalPrice());
        if (Float.parseFloat(MyApplication.getInstance().getTotalPrice()) >= Float.parseFloat(site_settings.get("Max Purchase Order"))){

            grand_total.setText(String.valueOf(Float.parseFloat(MyApplication.getInstance().getTotalPrice())));
        }
        else {
            grand_total.setText(String.valueOf(Float.parseFloat(MyApplication.getInstance().getTotalPrice()) + Float.parseFloat(site_settings.get("Delivery Charge"))));
        }
        //textTotalQuantity.setText(MyApplication.getInstance().getTotalQuantity());


        if (Float.parseFloat(MyApplication.getInstance().getTotalPrice()) >= Float.parseFloat(site_settings.get("Max Purchase Order"))){
            stripLayout.setBackgroundColor(Color.parseColor("#24af20"));
        }
        else {
            stripLayout.setBackgroundColor(Color.parseColor("#fde0a4"));
        }
    }
    private void   deleteItem(int items){
        items = items - 1 ;
        Double price = Double.valueOf(MyApplication.getInstance().getTotalPrice());
        MyApplication.getInstance().setTotalQuantity(String.valueOf(Integer.parseInt(MyApplication.getInstance().getTotalQuantity()) - items));
      //  textTotalQuantity.setText(MyApplication.getInstance().getTotalQuantity());


    }

    private void initializeDialog(){
        dialog= new ProgressDialog(NewViewCartActivity.this);
        dialog.setMessage("Loading Please Wait...");
        dialog.setCancelable(false);
    }

    private void showProgressDialog(){
        if (dialog!=null){
            dialog.show();
        }
    }

    private void hideProgressDialog(){
        if (dialog.isShowing()){
            dialog.cancel();
        }
    }

    private void initializeDialogs() {
        dialogs = new ProgressDialog(NewViewCartActivity.this,R.style.ProgrssTheme);
        dialogs.setCancelable(false);
        dialogs.setProgressStyle(android.R.style.Widget_ProgressBar_Large);



    }
    private void showProgressDialogs() {
        if (dialogs != null) {
            dialogs.show();
        }
    }

    private void hideProgressDialogs() {
        dialogs.cancel();


    }
}
