package models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Lnkt on 13-02-2018.
 */

public class MainOrderModel implements Serializable{

    public int type;
    public MyOrderModel myOrder;
    public OrderItemModel orderItemList;

    public MainOrderModel(int type,MyOrderModel myOrder){
        this.type = type;
        this.myOrder = myOrder;
    }
    public MainOrderModel(int type,OrderItemModel orderItemList,String xxx){
        this.type = type;
        this.orderItemList = orderItemList;
    }
}
