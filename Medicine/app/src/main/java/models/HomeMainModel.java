package models;

import java.util.ArrayList;

/**
 * Created by Lnkt on 29-01-2018.
 */

public class HomeMainModel {

    public int type;
    public int spanCount;

    public ArrayList<BannarModel> bannarList;
    public HomeModel homeModel;

    public HomeMainModel(int type,int spanCount, ArrayList<BannarModel> bannarList)
    {
        this.type=type;
        this.spanCount = spanCount;
        this.bannarList=bannarList;

    }
    public HomeMainModel(int type,int spanCount, HomeModel homeModel)
    {
        this.type=type;
        this.spanCount = spanCount;
        this.homeModel=homeModel;
    }
}
