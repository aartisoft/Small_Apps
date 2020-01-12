package com.techcorp.bhagavadgita.keanmolvachan.gettersetter;

import java.util.ArrayList;

public class ItemImageHome {

    boolean status;
    String message;
    String show_intro;
    String show_app_icon;
    String intro_title;
    String intro_description;
    String intro_backcolor;
    String intro_image;
    ArrayList<Item_images> data;


    public String getShow_intro() {
        return show_intro;
    }

    public void setShow_intro(String show_intro) {
        this.show_intro = show_intro;
    }

    public String getShow_app_icon() {
        return show_app_icon;
    }

    public void setShow_app_icon(String show_app_icon) {
        this.show_app_icon = show_app_icon;
    }

    public String getIntro_title() {
        return intro_title;
    }

    public void setIntro_title(String intro_title) {
        this.intro_title = intro_title;
    }

    public String getIntro_description() {
        return intro_description;
    }

    public void setIntro_description(String intro_description) {
        this.intro_description = intro_description;
    }

    public String getIntro_backcolor() {
        return intro_backcolor;
    }

    public void setIntro_backcolor(String intro_backcolor) {
        this.intro_backcolor = intro_backcolor;
    }

    public String getIntro_image() {
        return intro_image;
    }

    public void setIntro_image(String intro_image) {
        this.intro_image = intro_image;
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


    public ArrayList<Item_images> getData() {
        return data;
    }

    public void setData(ArrayList<Item_images> data) {
        this.data = data;
    }

    public String toString() {
        return "ClassPojo [status = " + this.status + ", message = " + this.message +  ", show_intro = " + this.show_intro +   ", show_app_icon = " + this.show_app_icon +   ", intro_title = " + this.intro_title +   ", intro_description = " + this.intro_description +   ", intro_backcolor = " + this.intro_backcolor + ", intro_image = " + this.intro_image + ", data = " + this.data + "]";
    }
}
