package com.techcorp.teluguvideostatus.gettersetter;

import java.util.ArrayList;

public class CategoryData {

    private boolean status;
    private String message;
    private ArrayList<Item_category> data;

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

    public ArrayList<Item_category> getData() {
        return data;
    }

    public void setData(ArrayList<Item_category> data) {
        this.data = data;
    }


    public String toString() {
        return "ClassPojo [status = " + this.status + ", message = " + this.message + ", data = " + this.data + "]";
    }
}
