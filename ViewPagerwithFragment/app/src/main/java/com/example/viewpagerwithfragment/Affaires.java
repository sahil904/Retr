package com.example.viewpagerwithfragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.viewpagerwithfragment.adapter.AffairsSliderAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class Affaires extends Fragment {
    private ViewPager viewPager;
    private AffairsSliderAdapter affairsSliderAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_pager_fragment, container, false);
        viewPager = view.findViewById(R.id.Viewpager);
        viewPager.setAdapter(new AffairsSliderAdapter(getFragmentManager()));
        return view;
    }

}
