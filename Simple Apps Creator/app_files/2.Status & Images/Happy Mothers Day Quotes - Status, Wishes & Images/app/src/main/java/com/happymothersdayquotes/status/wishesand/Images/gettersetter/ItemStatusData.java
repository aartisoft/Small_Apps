package com.happymothersdayquotes.status.wishesand.Images.gettersetter;

import java.util.ArrayList;

public class ItemStatusData {

    boolean status;
    String message;
    String limit;
    ArrayList<Item_Status> data;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public ArrayList<Item_Status> getData() {
        return data;
    }

    public void setData(ArrayList<Item_Status> data) {
        this.data = data;
    }

    public String toString() {
        return "ClassPojo [status = " + this.status + ", message = " + this.message +  ", limit = " + this.limit + ", data = " + this.data + "]";
    }
}
