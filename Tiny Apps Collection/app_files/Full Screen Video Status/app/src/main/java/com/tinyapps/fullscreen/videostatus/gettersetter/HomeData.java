package com.tinyapps.fullscreen.videostatus.gettersetter;

import java.util.ArrayList;

public class HomeData {

    private boolean status;
    private String message;
    private String is_banner_shown;
    private String banner_link;
    private String banner_background;
    private ArrayList<Item_collections> trending_list;
    private ArrayList<Item_collections> latest_list;
    private ArrayList<HomeCatData> category_list;
    private ArrayList<Item_collections> slider_list;

    public ArrayList<Item_collections> getSlider_list() {
        return slider_list;
    }

    public void setSlider_list(ArrayList<Item_collections> slider_list) {
        this.slider_list = slider_list;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public String getIs_banner_shown() {
        return is_banner_shown;
    }

    public void setIs_banner_shown(String is_banner_shown) {
        this.is_banner_shown = is_banner_shown;
    }

    public String getBanner_link() {
        return banner_link;
    }

    public void setBanner_link(String banner_link) {
        this.banner_link = banner_link;
    }

    public String getBanner_background() {
        return banner_background;
    }

    public void setBanner_background(String banner_background) {
        this.banner_background = banner_background;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Item_collections> getTrending_list() {
        return trending_list;
    }

    public void setTrending_list(ArrayList<Item_collections> trending_list) {
        this.trending_list = trending_list;
    }

    public ArrayList<HomeCatData> getCategory_list() {
        return category_list;
    }

    public void setCategory_list(ArrayList<HomeCatData> category_list) {
        this.category_list = category_list;
    }

    public ArrayList<Item_collections> getLatest_list() {
        return latest_list;
    }

    public void setLatest_list(ArrayList<Item_collections> latest_list) {
        this.latest_list = latest_list;
    }


    public String toString() {
        return "ClassPojo [status = " + this.status + ", message = " + this.message +  ", is_banner_shown = " + this.is_banner_shown +  ", banner_link = " + this.banner_link +    ", banner_background = " + this.banner_background +  ", trending_list = " + this.trending_list +  ", latest_list = " + this.latest_list + ", category_list = " + this.category_list+ ", slider_list = " + this.slider_list + "]";
    }
}
