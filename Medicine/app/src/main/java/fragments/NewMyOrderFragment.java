package fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.medhealth.NewOrderDetailActivity;
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
import models.MyOrderModel;
import util.ObjectSerializer;


@SuppressWarnings("deprecation")
public class NewMyOrderFragment extends Fragment{

    RecyclerView orderRecycler;
    MyOrderAdapter myOrderAdapter;
    ArrayList<MyOrderModel> myOrderList;
    public SharedPreferences settings;
    RelativeLayout progressLayout,noItemsLayout;
    HashMap<String,String> site_settings;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_order,container,false);
        myOrderList = new ArrayList<>();
        settings = getActivity().getSharedPreferences(ConstValue.MAIN_PREF, 0);
        try {
            site_settings = (HashMap<String,String>) ObjectSerializer.deserialize(settings.getString("site_settings", ObjectSerializer.serialize(new HashMap<String, String>())));
        }catch (IOException e) {
            e.printStackTrace();
        }

        orderRecycler = view.findViewById(R.id.my_order_recycler);
        LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        orderRecycler.setLayoutManager(linearLayoutManager);
        progressLayout = view.findViewById(R.id.progress_layout);
        noItemsLayout = view.findViewById(R.id.no_items_layout);
        progressLayout.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Resume","Resume Called");
        progressLayout.setVisibility(View.VISIBLE);
        hitUrlForProductHistory();
    }

    private void hitUrlForProductHistory(){
        myOrderList = new ArrayList<>();
        String url = ConstValue.JSON_ORDER_HISTORY_URL+"&user_id="+settings.getString("userid","00");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("MyOrders",String.valueOf(response));
                    JSONObject responseObject  = new JSONObject(response);
                    if (responseObject.getInt("count")>0) {
                        JSONArray orderArray = responseObject.getJSONArray("data");
                        for (int i = 0; i < orderArray.length(); i++) {
                            JSONObject orderObject = orderArray.getJSONObject(i);
                            MyOrderModel orderModel = new MyOrderModel();
                            orderModel.orderName = orderObject.getString("title");
                            orderModel.orderImage = orderObject.getString("image");
                            orderModel.orderDate = orderObject.getString("order_date");
                            orderModel.orderId = orderObject.getString("order_id");
                            orderModel.orderPrice = orderObject.getString("total_price");
                            orderModel.orderStatus = orderObject.getString("order_status");
                            orderModel.cod = orderObject.getBoolean("cod");
                            orderModel.paymentStatus = orderObject.getBoolean("payment_status");
                            orderModel.currency = orderObject.getString("currency");
                            orderModel.receiptNumber = orderObject.getString("recipt_no");
                            orderModel.deliveryCharges = orderObject.getString("delivery_charge");
                            orderModel.deliveryDate = orderObject.getString("delivery_date");
                            orderModel.deliveryTime = orderObject.getString("delivery_time");
                            orderModel.promoPrice = orderObject.getString("discount");
                            orderModel.isInstantDelivery = orderObject.getString("delivery_instant");
                            myOrderList.add(orderModel);
                        }
                    }

                    if (myOrderList.size()>0) {
                        myOrderAdapter = new MyOrderAdapter(getActivity(), myOrderList);
                        orderRecycler.setAdapter(myOrderAdapter);
                    }else {
                        noItemsLayout.setVisibility(View.VISIBLE);
                    }
                    progressLayout.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } ,new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", String.valueOf(error));
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.OrderViewHolder>{
        Context context;
        ArrayList<MyOrderModel> orderList;
        private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
        DisplayImageOptions options;
        ImageLoaderConfiguration imgconfig;

        public MyOrderAdapter(Context context, ArrayList<MyOrderModel> orderList){
           this.context = context;
           this.orderList = orderList;
           Log.d("OrderList",String.valueOf(orderList.size()));
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

        public class OrderViewHolder extends RecyclerView.ViewHolder{

            TextView nameTv,scheduleText,orderPriceTv,receiptNum,totalAmountTv,productDeliveryPrice,statusTv;
            ImageView orderImage;
            RelativeLayout delivery_charge_layout;
            public OrderViewHolder(View itemView) {
                super(itemView);
                delivery_charge_layout = itemView.findViewById(R.id.delivery_charge_layout);
                nameTv = itemView.findViewById(R.id.pro_title);
                scheduleText = itemView.findViewById(R.id.scheduled_for_text);
                orderPriceTv = itemView.findViewById(R.id.pro_price);
                totalAmountTv = itemView.findViewById(R.id.total_amount);
                orderImage = itemView.findViewById(R.id.proimage);
                receiptNum = itemView.findViewById(R.id.receipt_num);
                productDeliveryPrice = itemView.findViewById(R.id.pro_delivery_price);
                statusTv = itemView.findViewById(R.id.status_tv);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(),NewOrderDetailActivity.class);
                        intent.putExtra("order_id", myOrderList.get(getAdapterPosition()).orderId);
                        intent.putExtra("status", myOrderList.get(getAdapterPosition()).orderStatus);
                        intent.putExtra("position",getAdapterPosition());
                        intent.putExtra("cod",myOrderList.get(getAdapterPosition()).cod);
                        intent.putExtra("my_order",myOrderList.get(getAdapterPosition()));
                        startActivityForResult(intent,100);
                    }
                });
            }
        }
        @NonNull
        @Override
        public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history,parent,false);
            return new OrderViewHolder(view);
        }
        @Override
        public void onBindViewHolder(OrderViewHolder holder, int position)  {
            holder.nameTv.setText(orderList.get(position).orderName);
            if (orderList.get(position).orderDate.equals("")){

            }else {
                String[] splited = orderList.get(position).orderDate.split("\\s+");
//                holder.dateTv.setText(splited[0]);
            }
            ImageLoader.getInstance().displayImage(ConstValue.PRO_IMAGE_BIG_PATH+orderList.get(position).orderImage, holder.orderImage, options, animateFirstListener);
           try {
               holder.orderPriceTv.setText(orderList.get(position).orderPrice);//+orderList.get(position).currency
               holder.productDeliveryPrice.setText(orderList.get(position).deliveryCharges);//+orderList.get(position).currency
               holder.orderPriceTv.setText(orderList.get(position).orderPrice);
               holder.totalAmountTv.setText(String.valueOf(Double.parseDouble(orderList.get(position).orderPrice)));// + Double.parseDouble(orderList.get(position).deliveryCharges)));
           }catch (Exception e){e.printStackTrace();}
            holder.receiptNum.setText(orderList.get(position).receiptNumber);
            holder.scheduleText.setText("("+orderList.get(position).deliveryDate+") "  + orderList.get(position).deliveryTime);

            holder.statusTv.setVisibility(View.VISIBLE);

                    if(orderList.get(position).orderStatus.equalsIgnoreCase("0")){

                    holder.statusTv.setText("Order Pending");
                    holder.statusTv.setTextColor(Color.parseColor("#d1cece"));
                    }
                    else if(orderList.get(position).orderStatus.equalsIgnoreCase("1")){
                    holder.statusTv.setText("Order Confirmed");
                    holder.statusTv.setTextColor(Color.parseColor("#24af20"));
                    }
                    else if(orderList.get(position).orderStatus.equalsIgnoreCase("2")){
                    holder.statusTv.setText("Order Cancelled");
                    holder.statusTv.setTextColor(Color.parseColor("#f36f2a"));
                    }
                    else if(orderList.get(position).orderStatus.equalsIgnoreCase("3")){
                    holder.statusTv.setText("Order Delivered");
                    holder.statusTv.setTextColor(Color.parseColor("#24af20"));
                    }
                    else if(orderList.get(position).orderStatus.equalsIgnoreCase("4")){
                    holder.statusTv.setText("Order Completed");
                    holder.statusTv.setTextColor(Color.parseColor("#24af20"));
                    }

           /*
           if(orderList.get(position).orderStatus.equalsIgnoreCase("0")){

                holder.orderStatus.setText("Pending");
                holder.orderStatus.setBackgroundResource(R.drawable.xml_gray_button);

            }
            else if(orderList.get(position).orderStatus.equalsIgnoreCase("1")){

                holder.orderStatus.setText("Confirm");
                holder.orderStatus.setBackgroundResource(R.drawable.xml_green_button);
            }
            else if(orderList.get(position).orderStatus.equalsIgnoreCase("2")){
                holder.orderStatus.setText("Cancelled");
                holder.orderStatus.setBackgroundResource(R.drawable.xml_red_button);
                holder.orderStatus.setTextColor(Color.WHITE);
            }
            else if(orderList.get(position).orderStatus.equalsIgnoreCase("3")){
                holder.orderStatus.setText("Delevered");
                holder.orderStatus.setBackgroundResource(R.drawable.xml_green_button);
            }
            else if(orderList.get(position).orderStatus.equalsIgnoreCase("4")){
                holder.orderStatus.setText("Completed");
                holder.orderStatus.setBackgroundResource(R.drawable.xml_white_button);
            }
*/
        }
        @Override
        public int getItemCount() {
            return orderList.size();
        }
    }
}
