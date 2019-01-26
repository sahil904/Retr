package models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by AVASKM on 2/8/2018.
 */

public class FruitsModel implements Serializable {
    public String catId;
  public String id ;
  public String cod ;
  public String date ;
  public String deliverycharge ;
  public String description ;
  public String emi ;
  public String image;
  public String on_date ;
  public String slug ;
  public String status ;
  public String tax ;
  public String title;
  public String name;

 public ArrayList<NewModelArray> productArray;

  public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDeliverycharge() {
        return deliverycharge;
    }

    public void setDeliverycharge(String deliverycharge) {
        this.deliverycharge = deliverycharge;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public String getEmi() {
        return emi;
    }

    public void setEmi(String emi) {
        this.emi = emi;
    }


    public String getOn_date() {
        return on_date;
    }

    public void setOn_date(String on_date) {
        this.on_date = on_date;
    }


    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
