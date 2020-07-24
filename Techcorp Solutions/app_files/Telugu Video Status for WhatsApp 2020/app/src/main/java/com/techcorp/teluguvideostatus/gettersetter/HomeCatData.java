package com.techcorp.teluguvideostatus.gettersetter;

import java.util.ArrayList;

public class HomeCatData {

    private String cid;
    private String category_name;
    private String category_image_thumb;
    private String language;
    private String type_layout;
    private ArrayList<Item_collections> data;


    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_image_thumb() {
        return category_image_thumb;
    }

    public void setCategory_image_thumb(String category_image_thumb) {
        this.category_image_thumb = category_image_thumb;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getType_layout() {
        return type_layout;
    }

    public void setType_layout(String type_layout) {
        this.type_layout = type_layout;
    }

    public ArrayList<Item_collections> getData() {
        return data;
    }

    public void setData(ArrayList<Item_collections> data) {
        this.data = data;
    }

    public String toString() {
        return "ClassPojo [cid = " + this.cid + ", category_name = " + this.category_name + ", category_image_thumb = " + this.category_image_thumb + ", language = " + this.language + ", type_layout = " + this.type_layout + ", data = " + this.data + "]";
    }
}
