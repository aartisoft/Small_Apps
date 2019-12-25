package com.techcorp.abdulkalam.quotesinhindi.gettersetter;

public class Item_images {

    String id;
    String item_title;
    String item_description;
    String image;
    String image_thumb;
    String total_views;
    String date;
    String cat_id;
    String category_name;
    String category_image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItem_title() {
        return item_title;
    }

    public void setItem_title(String item_title) {
        this.item_title = item_title;
    }

    public String getItem_description() {
        return item_description;
    }

    public void setItem_description(String item_description) {
        this.item_description = item_description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public String getTotal_views() {
        return total_views;
    }

    public void setTotal_views(String total_views) {
        this.total_views = total_views;
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

    public String getCategory_image() {
        return category_image;
    }

    public void setCategory_image(String category_image) {
        this.category_image = category_image;
    }

    public String toString() {
        return "ClassPojo [id = " + this.id + ", item_title = " + this.item_title +  ", item_description = " + this.item_description + ", image = " + this.image + ", image_thumb = " + this.image_thumb +  ", total_views = " + this.total_views + ", date = " + this.date + ", cat_id = " + this.cat_id + ", category_name = " + this.category_name +", category_image = " + this.category_image + "]";
    }
}
