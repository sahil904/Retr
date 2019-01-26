package com.medhealth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import imgLoader.AnimateFirstDisplayListener;
import models.DeliveryTimeModel;
import models.MainOrderModel;
import models.MyOrderModel;
import models.OrderItemModel;
import util.ConnectionDetector;
import util.ObjectSerializer;

/**
 * Created by Lnkt on 13-02-2018.
 */

public class NewOrderDetailActivity extends AppCompatActivity{
    RecyclerView recyclerView;
    ArrayList mainList;
    ArrayList<OrderItemModel> orderItemList;
    ArrayList<DeliveryTimeModel> deliveryTimeList;
    MyOrderModel myOrderModel;
    OrderDetailAdapter adapter;
    HashMap<String,String> site_settings;
    ProgressDialog dialog;
    ConnectionDetector cd;
    String priceStr;
    double price =0;

    public SharedPreferences settings;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        getSupportActionBar().setTitle("Order Details");
        recyclerView = findViewById(R.id.order_detail_recycler);
        settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
        site_settings = new HashMap<String,String>();
        try {
            site_settings = (HashMap<String,String>) ObjectSerializer.deserialize(settings.getString("site_settings", ObjectSerializer.serialize(new HashMap<String, String>())));
        }catch (IOException e) {
            e.printStackTrace();
        }
        LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(NewOrderDetailActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        mainList = new ArrayList();
        orderItemList = new ArrayList<>();
        myOrderModel = (MyOrderModel) getIntent().getSerializableExtra("my_order");
        Log.d("PromoBalacne",myOrderModel.promoPrice);

        mainList.add(new MainOrderModel(0,myOrderModel));

        adapter = new OrderDetailAdapter(NewOrderDetailActivity.this,mainList);
        recyclerView.setAdapter(adapter);
        hitUrlForOrderDetail();
        initializeDialog();
        cd = new ConnectionDetector(this);
    }

    private void hitUrlForOrderDetail(){
        String urlstring = ConstValue.JSON_ORDER_DETAIL+"&order_id="+myOrderModel.orderId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlstring, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject itemObj = jsonArray.getJSONObject(i);
                        OrderItemModel itemModel = new OrderItemModel();
                        itemModel.orderId = itemObj.getString("order_id");
                        itemModel.image = itemObj.getString("image");
                        itemModel.orderTitle = itemObj.getString("title");
                        itemModel.productId = itemObj.getString("product_id");
                        itemModel.price = itemObj.getString("price");
                        itemModel.quantity = itemObj.getString("qty");
                        mainList.add(new MainOrderModel(1,itemModel,"123"));
                        orderItemList.add(itemModel);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(NewOrderDetailActivity.this);
        requestQueue.add(stringRequest);
    }

    @SuppressWarnings("deprecation")
    private class OrderDetailAdapter extends RecyclerView.Adapter{

        private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
        private ArrayList<MainOrderModel> mainSetList;
        Context context;
        DisplayImageOptions options;
        ImageLoaderConfiguration imgconfig;

        public OrderDetailAdapter(Context context,ArrayList data){
            this.context = context;
            this.mainSetList = data;
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

            imgconfig = new ImageLoaderConfiguration.Builder(context).build();
            ImageLoader.getInstance().init(imgconfig);
        }

        public class HeaderViewHolder extends RecyclerView.ViewHolder{
            TextView scheduledDeliveryText,priceText,statusTv,payLabel ,payLabelDesc,onDeliveryLabel,proSaveTv;
            RelativeLayout rescheduleLayout,repeatLayout,cancelLayout;
            public HeaderViewHolder(View itemView) {
                super(itemView);
                scheduledDeliveryText = itemView.findViewById(R.id.delivery_date_time);
                priceText = itemView.findViewById(R.id.price_text);
                rescheduleLayout = itemView.findViewById(R.id.reschedule_layout);
                repeatLayout = itemView.findViewById(R.id.repeat_layout);
                cancelLayout = itemView.findViewById(R.id.cancle_order);
                statusTv = itemView.findViewById(R.id.status_tv);
                payLabel = itemView.findViewById(R.id.pay_label);
                payLabelDesc =itemView.findViewById(R.id.pay_lable_desc);
                onDeliveryLabel = itemView.findViewById(R.id.pay_lable_desc_end);
                proSaveTv = itemView.findViewById(R.id.pro_save_tv);
                proSaveTv.setText(myOrderModel.promoPrice);
                repeatLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (cd.isConnectingToInternet()) {
                            hitUrlForRepeatOrder();
                            showProgressDialog();
                        }
                    }
                });

                rescheduleLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if ((Integer.parseInt(mainSetList.get(getAdapterPosition()).myOrder.orderStatus))==0){
                            Intent rescheduleIntent = new Intent(NewOrderDetailActivity.this,RescheduleActivity.class);
                            rescheduleIntent.putExtra("my_order",myOrderModel);
                            startActivityForResult(rescheduleIntent,1001);
                        }else {
                            showSnackBar(view,"Not able to Reschedule Order!!!!!!");
                        }
            }
        });
                cancelLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("Cancel","CancelClicked");
                        if (mainSetList.get(getAdapterPosition()).myOrder.cod){
                            if (Integer.parseInt(mainSetList.get(getAdapterPosition()).myOrder.orderStatus)==0 ||Integer.parseInt(mainSetList.get(getAdapterPosition()).myOrder.orderStatus)==1)
                                hitUrlForCancelOrder(view,statusTv);
                            else if (Integer.parseInt(mainSetList.get(getAdapterPosition()).myOrder.orderStatus)==2)
                                showSnackBar(view,"Order all ready Cancelled");
                            else if (Integer.parseInt(mainSetList.get(getAdapterPosition()).myOrder.orderStatus)==3)
                                showSnackBar(view,"Order has been delivered");
                            else if (Integer.parseInt(mainSetList.get(getAdapterPosition()).myOrder.orderStatus)==4)
                                showSnackBar(view,"Order has been completed");
                        }else {
                            showSnackBar(view,"Order is not cancelable");
                        }
                    }
                });
            }
        }
        public class ListViewHolder extends RecyclerView.ViewHolder{
            ImageView productImage;
            TextView productTitle,productPrice,productQuantityAndPrice;

            public ListViewHolder(View itemView) {
                super(itemView);
                productImage = itemView.findViewById(R.id.pro_image);
                productTitle = itemView.findViewById(R.id.proTitle);
                productPrice = itemView.findViewById(R.id.proTotalPrice);
                productQuantityAndPrice = itemView.findViewById(R.id.proQtyPrice);
            }
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType==0){
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_order_detail_header,parent,false);
                return new HeaderViewHolder(view);
            }else if (viewType==1){
                view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_order_detail,parent,false);
                return new ListViewHolder(view);
        }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            MainOrderModel mainOrderModel = mainSetList.get(position);

            if (mainOrderModel.type==0){

                ((HeaderViewHolder) holder).scheduledDeliveryText.setText("("+myOrderModel.deliveryDate+")"+myOrderModel.deliveryTime);

               // ((HeaderViewHolder) holder).priceText.setText(mainOrderModel.myOrder.orderPrice);

               if (Float.parseFloat(mainOrderModel.myOrder.orderPrice) < Float.parseFloat(site_settings.get("Max Purchase Order"))){

                   ((HeaderViewHolder) holder).priceText.setText(String.valueOf(Float.parseFloat(mainOrderModel.myOrder.orderPrice)));
               }
               else {
                   ((HeaderViewHolder) holder).priceText.setText(mainOrderModel.myOrder.orderPrice);
               }
                if (mainOrderModel.myOrder.cod){
                    ((HeaderViewHolder) holder).cancelLayout.setVisibility(View.VISIBLE);
                }else {
                   ((HeaderViewHolder) holder).cancelLayout.setVisibility(View.GONE);
                }

                ((HeaderViewHolder) holder).statusTv.setVisibility(View.VISIBLE);

                if(mainOrderModel.myOrder.orderStatus.equalsIgnoreCase("0")){

                    ((HeaderViewHolder) holder).statusTv.setText("Order Pending");

                    ((HeaderViewHolder) holder).statusTv.setTextColor(Color.parseColor("#d1cece"));

                }
                else if(mainOrderModel.myOrder.orderStatus.equalsIgnoreCase("1")){

                    ((HeaderViewHolder) holder).statusTv.setText("Order Confirmed");
                    ((HeaderViewHolder) holder).statusTv.setTextColor(Color.parseColor("#24af20"));
                }
                else if(mainOrderModel.myOrder.orderStatus.equalsIgnoreCase("2")){
                    ((HeaderViewHolder) holder).statusTv.setText("Order Cancelled");
                    ((HeaderViewHolder) holder).statusTv.setTextColor(Color.parseColor("#f36f2a"));
                }
                else if(mainOrderModel.myOrder.orderStatus.equalsIgnoreCase("3")){
                    ((HeaderViewHolder) holder).statusTv.setText("Order Delevered");
                    ((HeaderViewHolder) holder).statusTv.setTextColor(Color.parseColor("#24af20"));
                }
                else if(mainOrderModel.myOrder.orderStatus.equalsIgnoreCase("4")){
                    ((HeaderViewHolder) holder).statusTv.setText("Order Completed");
                    ((HeaderViewHolder) holder).statusTv.setTextColor(Color.parseColor("#24af20"));
                }
                if (mainOrderModel.myOrder.cod){
                    ((HeaderViewHolder) holder).payLabel.setText("Pay On Delivery");
                    ((HeaderViewHolder) holder).payLabelDesc.setText("You Have to Pay ");
                    ((HeaderViewHolder) holder).onDeliveryLabel.setText(" on Delivery");
                }else {
                    ((HeaderViewHolder) holder).payLabel.setText("Amount Paid");
                    ((HeaderViewHolder) holder).payLabelDesc.setText("You Paid ");
                    ((HeaderViewHolder) holder).onDeliveryLabel.setText("");
                }
            }else if (mainOrderModel.type == 1){
                ImageLoader.getInstance().displayImage(ConstValue.PRO_IMAGE_BIG_PATH+mainOrderModel.orderItemList.image, ((ListViewHolder) holder).productImage, options, animateFirstListener);
                price =price + Double.parseDouble(mainOrderModel.orderItemList.price);
                int qty = Integer.parseInt(mainOrderModel.orderItemList.quantity);
               // price = price * qty ;
              //  ((ListViewHolder) holder).productPrice.setText(String.valueOf(price));
                // Log.d("Price",String.valueOf(price));

                ((ListViewHolder) holder).productPrice.setText(String.valueOf(Math.round(Float.parseFloat(mainOrderModel.orderItemList.price))));

                ((ListViewHolder) holder).productTitle.setText(mainOrderModel.orderItemList.orderTitle);

                ((ListViewHolder) holder).productQuantityAndPrice.setText(mainOrderModel.orderItemList.quantity+ " unit ");
            }
        }
        @Override
        public int getItemViewType(int position) {
            switch (mainSetList.get(position).type) {
                case 0:
                    return 0;
                case 1:
                    return 1;
                default:
                    return -1;
            }
        }
        @Override
        public int getItemCount() {
            return mainList.size();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){

            myOrderModel = (MyOrderModel) data.getSerializableExtra("my_order");

            Log.d("myOrder",myOrderModel.deliveryDate+","+myOrderModel.deliveryTime);

            adapter.notifyItemChanged(0);
        }
    }
    private void hitUrlForRepeatOrder(){
        String urlString = ConstValue.REPEAT_ORDER+"&orderid="+myOrderModel.orderId+"&userId="+settings.getString("userid".toString(), "");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hideProgressDialog();
                Intent intent = new Intent(NewOrderDetailActivity.this, NewViewCartActivity.class);
                startActivity(intent);
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

        RequestQueue requestQueue = Volley.newRequestQueue(NewOrderDetailActivity.this);
        requestQueue.add(stringRequest);
    }


    private void hitUrlForCancelOrder(final View view,final TextView statusTv){
        String urlString = ConstValue.JSON_CANCLE_ORDER+"&orderid="+myOrderModel.orderId;
        Log.d("cancelUrlString",urlString);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
             Log.d("CancelResponse",response);


                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("responce").equalsIgnoreCase("success")) {
                        showSnackBar(view, "Order Cancelled Successfully");
                        statusTv.setText("Order Cancelled");
                        statusTv.setTextColor(Color.parseColor("#f36f2a"));
                    }
                    else
                        showSnackBar(view,"Order could not be Cancelled!!!!!!!!");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

        RequestQueue requestQueue = Volley.newRequestQueue(NewOrderDetailActivity.this);
        requestQueue.add(stringRequest);
    }

    private void initializeDialog(){
        dialog = new ProgressDialog(NewOrderDetailActivity.this);
        dialog.setMessage("Loading Please wait... ");
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

    private void showSnackBar(View view,String message){
        Snackbar snackbar;
        snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(R.color.orange_color));
        snackbar.show();
    }
}
