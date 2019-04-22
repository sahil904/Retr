package com.example.pag;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomHolder> {
    ArrayList<Pojo>arrayList;
    Context c;

    public CustomAdapter(ArrayList<Pojo> arrayList, Context c) {
        this.arrayList = arrayList;
        this.c = c;
    }

    @NonNull
    @Override
    public CustomHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom,null);
        return new CustomHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomHolder customHolder, int i) {
        Pojo p=arrayList.get(i);
        Picasso.with(c).load(p.getImage()).into(customHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class CustomHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public CustomHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageview);
        }
    }
}
