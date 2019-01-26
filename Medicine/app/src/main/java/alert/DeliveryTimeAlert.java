package alert;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.medhealth.NewPlaceOrderActivity;
import com.medi.R;
import com.medhealth.RescheduleActivity;

import java.util.ArrayList;

import models.DeliveryTimeModel;

/**
 * Created by Lnkt on 10-02-2018.
 */

public class DeliveryTimeAlert extends AlertDialog implements DialogInterface.OnDismissListener {
    Context context;
    ArrayList<DeliveryTimeModel> deliveryTimeList;
    public DeliveryTimeAlert(@NonNull Context context, ArrayList<DeliveryTimeModel> deliveryTimeList) {
        super(context);
        this.context = context;
        this.deliveryTimeList = deliveryTimeList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_delivery);
        RecyclerView timeRecyclerView = findViewById(R.id.time_recycler_view);
        LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        timeRecyclerView.setLayoutManager(linearLayoutManager);
        TimeAdapter timeAdapter = new TimeAdapter(context,deliveryTimeList);
        timeRecyclerView.setAdapter(timeAdapter);
        Log.d("Sizes",String.valueOf(deliveryTimeList.size()));
        setOnDismissListener(this);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {

    }

    private class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.MyViewHolder>{
        Context context;
        ArrayList<DeliveryTimeModel> deliveryTimeList;

        public TimeAdapter(Context context, ArrayList<DeliveryTimeModel> deliveryTimeList){
            this.context = context;
            this.deliveryTimeList = deliveryTimeList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delivery_time,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
           holder.timeText.setText(deliveryTimeList.get(position).deliveryTime);
        }

        @Override
        public int getItemCount() {

            return deliveryTimeList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{
            TextView timeText;
            public MyViewHolder(View itemView) {
                super(itemView);
                timeText = itemView.findViewById(R.id.time_tv);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DeliveryTimeModel deliveryTimeModel = deliveryTimeList.get(getAdapterPosition());


                        if (context instanceof NewPlaceOrderActivity) {

                            ((NewPlaceOrderActivity) context).setTime(deliveryTimeModel.deliveryTime);
                        }else {
                            ((RescheduleActivity) context).setTime(deliveryTimeModel.deliveryTime);
                        }

                       // ((NewPlaceOrderActivity) context).setTime(deliveryTimeModel.deliveryTime);
                    }
                });
            }
        }
    }
}
