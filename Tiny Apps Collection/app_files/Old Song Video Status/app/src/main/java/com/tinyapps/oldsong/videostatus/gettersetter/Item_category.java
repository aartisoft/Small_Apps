package com.tinyapps.oldsong.videostatus.gettersetter;


public class Item_category {

    String cid;
    String category_name;
    String category_image_thumb;
    String language;
    String type_layout;


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



    public String toString() {
        return "ClassPojo [cid = " + this.cid + ", category_name = " + this.category_name + ", category_image_thumb = " + this.category_image_thumb +  ", language = " + this.language + ", type_layout = " + this.type_layout + "]";
    }
}
