package com.happymothersdayquotes.status.wishesand.Images.gettersetter;

public class Item_Status {


    String id;
    String status_text;
    String total_views;
    String total_shares;
    String date;
    String cat_id;
    String category_name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus_text() {
        return status_text;
    }

    public void setStatus_text(String status_text) {
        this.status_text = status_text;
    }

    public String getTotal_views() {
        return total_views;
    }

    public void setTotal_views(String total_views) {
        this.total_views = total_views;
    }

    public String getTotal_shares() {
        return total_shares;
    }

    public void setTotal_shares(String total_shares) {
        this.total_shares = total_shares;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }


    public String toString() {
        return "ClassPojo [id = " + this.id + ", status_text = " + this.status_text + ", total_views = " + this.total_views + ", total_shares = " + this.total_shares + ", date = " + this.date + ", cat_id = " + this.cat_id + ", category_name = " + this.category_name + "]";
    }
}
