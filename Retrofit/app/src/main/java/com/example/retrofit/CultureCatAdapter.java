package com.example.retrofit;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CultureCatAdapter extends RecyclerView.Adapter<CultureCatAdapter.CultureHolder> {
    ArrayList<ModelClassforretro.DataBean>arrayList_culture;

    public CultureCatAdapter( ArrayList<ModelClassforretro.DataBean> arrayList_culture) {
        this.arrayList_culture = arrayList_culture;
    }

    @NonNull
    @Override
    public CultureHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.culture_custom_recyler_item,null);
        return new CultureHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CultureHolder cultureHolder, final int i) {
     //   cultureHolder.date.setText(arrayList_culture.get(i).getUpdated_at());
       // cultureHolder.title.setText(arrayList_culture.get(i).getTitle());
        Picasso.get().load(arrayList_culture.get(i).getImage()).into(cultureHolder.imageView);
//        cultureHolder.new_relative_click.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(context, CultureSub.class);
//                intent.putExtra("content",arrayList_culture.get(i).content_culture);
//                intent.putExtra("update_date",arrayList_culture.get(i).date_culture);
//                intent.putExtra("image",arrayList_culture.get(i).image_culture);
//                Log.d("fdfjhddsw",arrayList_culture.get(i).image_culture);
//                intent.putExtra("self",arrayList_culture.get(i).self_culture);
//                intent.putExtra("title",arrayList_culture.get(i).title_culture);
//
//                context.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return arrayList_culture.size();
    }

    public class CultureHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView date,title;
        LinearLayout new_relative_click;
        public CultureHolder(@NonNull final View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.culture_item_image);
            //date=itemView.findViewById(R.id.culture_item_date);
            //title=itemView.findViewById(R.id.culture_item_title);
          //  new_relative_click=itemView.findViewById(R.id.new_relative_click);
//            new_relative_click.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent=new Intent(itemView.getContext(),MainActivity.class);
//                    intent.putExtra("cat",arrayList_culture.get(getLayoutPosition()).getCategory_id());
//                }
//            });
        }
    }
}
