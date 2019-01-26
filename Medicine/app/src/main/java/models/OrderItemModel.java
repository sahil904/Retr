package models;

import com.android.volley.toolbox.StringRequest;

import java.io.Serializable;

/**
 * Created by Lnkt on 13-02-2018.
 */

public class OrderItemModel implements Serializable{

    public String orderId;
    public String orderTitle;
    public String productId;
    public String price;
    public String quantity;
    public String image;

}
