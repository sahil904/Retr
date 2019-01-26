package alert;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.medi.R;

import java.util.ArrayList;

import models.AppointMentitem;

public class AppointMentAlert extends Dialog implements DialogInterface.OnClickListener {

    RecyclerView recyclerView;
    ArrayList<AppointMentitem> al;

    public AppointMentAlert(@NonNull Context context) {
        super(context);
        setContentView(R.layout.appointment_item);
        recyclerView = findViewById(R.id.appointment_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        AppointMentAdapter appointMentAdapter = new AppointMentAdapter(context,al);
        recyclerView.setAdapter(appointMentAdapter);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }


    class  AppointMentAdapter extends RecyclerView.Adapter<AppointMentAdapter.AppointMentHolder>{

        Context context;
        ArrayList<AppointMentitem> al;

        public AppointMentAdapter(Context context, ArrayList<AppointMentitem> al) {
            this.context = context;
            this.al = al;
        }

        @NonNull
        @Override
        public AppointMentAdapter.AppointMentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_delivery_time,parent,false);
            return new AppointMentHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AppointMentAdapter.AppointMentHolder holder, int position) {
            AppointMentitem appointMentitem = al.get(position);
        }

        @Override
        public int getItemCount() {
            return al.size();
        }

        public class AppointMentHolder extends RecyclerView.ViewHolder {
            TextView timeTv;
            public AppointMentHolder(View itemView) {
                super(itemView);
                timeTv  = itemView.findViewById(R.id.time_tv);
            }
        }
    }
}
