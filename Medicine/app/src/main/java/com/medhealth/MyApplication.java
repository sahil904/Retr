package com.medhealth;

import android.app.Application;

import util.FontsOverride;


/**
 * Created by subhashsanghani on 9/12/16.
 */
public class MyApplication extends Application {
    private String totalQuantity;
    private String totalPrice;
    private static MyApplication mInstanse;

    @Override public void onCreate() {
        super.onCreate();
        mInstanse = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstanse;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(String totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

}