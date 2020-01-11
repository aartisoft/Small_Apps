package com.mothersdayspecial.videostatus2020.gettersetter;

import java.util.ArrayList;

public class HomeData {

    private boolean status;
    private String message;
    private ArrayList<Item_collections> trending_list;
    private ArrayList<Item_collections> latest_list;
    private ArrayList<Item_collections> fullscreen_list;
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

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Item_collections> getTrending_list() {
        return trending_list;
    }

    public void setTrending_list(ArrayList<Item_collections> trending_list) {
        this.trending_list = trending_list;
    }

    public ArrayList<Item_collections> getLatest_list() {
        return latest_list;
    }

    public void setLatest_list(ArrayList<Item_collections> latest_list) {
        this.latest_list = latest_list;
    }

    public ArrayList<Item_collections> getFullscreen_list() {
        return fullscreen_list;
    }

    public void setFullscreen_list(ArrayList<Item_collections> fullscreen_list) {
        this.fullscreen_list = fullscreen_list;
    }


    public String toString() {
        return "ClassPojo [status = " + this.status + ", message = " + this.message + ", trending_list = " + this.trending_list +  ", latest_list = " + this.latest_list + ", fullscreen_list = " + this.fullscreen_list+ ", slider_list = " + this.slider_list + "]";
    }
}
