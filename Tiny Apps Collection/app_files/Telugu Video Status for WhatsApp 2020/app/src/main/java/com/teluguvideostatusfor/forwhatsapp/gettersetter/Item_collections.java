package com.teluguvideostatusfor.forwhatsapp.gettersetter;

public class Item_collections {


    String id;
    String video_name;
    String video_url;
    String video_image_thumb;
    String total_views;
    String is_type;
    String date;
    String cat_id;
    String category_name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVideo_name() {
        return video_name;
    }

    public void setVideo_name(String video_name) {
        this.video_name = video_name;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getVideo_image_thumb() {
        return video_image_thumb;
    }

    public void setVideo_image_thumb(String video_image_thumb) {
        this.video_image_thumb = video_image_thumb;
    }

    public String getTotal_views() {
        return total_views;
    }

    public void setTotal_views(String total_views) {
        this.total_views = total_views;
    }

    public String getIs_type() {
        return is_type;
    }

    public void setIs_type(String is_type) {
        this.is_type = is_type;
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
        return "ClassPojo [id = " + this.id + ", video_name = " + this.video_name + ", video_url = " + this.video_url + ", video_image_thumb = " + this.video_image_thumb +  ", total_views = " + this.total_views + ", is_type = " + this.is_type + ", date = " + this.date + ", cat_id = " + this.cat_id + ", category_name = " + this.category_name + "]";
    }
}
