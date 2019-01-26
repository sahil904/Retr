package models;

import java.io.Serializable;

/**
 * Created by Lnkt on 07-02-2018.
 */

public class MyOrderModel implements Serializable{

    public String orderName;
    public String orderId;
    public String orderImage;
    public String orderDate;
    public String orderPrice;
    public String orderStatus;
    public boolean cod;
    public boolean paymentStatus;
    public String currency;
    public String receiptNumber;
    public String deliveryCharges;
    public String deliveryDate;
    public String deliveryTime;
    public String isInstantDelivery;
    public String promoPrice;

}
