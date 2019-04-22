package com.example.viewpagerwithfragment.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.viewpagerwithfragment.item_fragment.affairs_item;

import java.util.ArrayList;

public class AffairsSliderAdapter extends FragmentPagerAdapter {

    public AffairsSliderAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return new affairs_item().getInstace(i + 1);
    }

    @Override
    public int getCount() {
        return 50;
    }
}
