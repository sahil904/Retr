package com.example.viewpagerwithfragment.item_fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.viewpagerwithfragment.R;

public class affairs_item extends Fragment {
    private static String KEY = "KEY";
    public static Fragment  getInstace(int Pos){
        Bundle bundle = new Bundle();
        affairs_item affairs_item = new affairs_item();
        bundle.putInt(KEY,Pos);
        affairs_item.setArguments(bundle);
        return affairs_item;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.child_affais,container,false);
        Bundle bundle  = getArguments();
        int pos = bundle.getInt(KEY);
        TextView textView = view.findViewById(R.id.setText);
        textView.setText("Gddhe ki gand "+pos);

        return view;
    }
}


