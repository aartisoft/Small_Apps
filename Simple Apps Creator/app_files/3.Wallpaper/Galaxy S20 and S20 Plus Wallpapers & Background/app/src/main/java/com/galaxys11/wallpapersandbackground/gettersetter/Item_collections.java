package com.galaxys11.wallpapersandbackground.gettersetter;

public class Item_collections {

    String id;
    String wall_name;
    String wallpaper_image;
    String wallpaper_image_thumb;
    String total_views;
    String total_download;
    String date;
    String wall_tags;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWall_name() {
        return wall_name;
    }

    public void setWall_name(String wall_name) {
        this.wall_name = wall_name;
    }

    public String getWallpaper_image() {
        return wallpaper_image;
    }

    public void setWallpaper_image(String wallpaper_image) {
        this.wallpaper_image = wallpaper_image;
    }

    public String getWallpaper_image_thumb() {
        return wallpaper_image_thumb;
    }

    public void setWallpaper_image_thumb(String wallpaper_image_thumb) {
        this.wallpaper_image_thumb = wallpaper_image_thumb;
    }

    public String getTotal_views() {
        return total_views;
    }

    public void setTotal_views(String total_views) {
        this.total_views = total_views;
    }

    public String getTotal_download() {
        return total_download;
    }

    public void setTotal_download(String total_download) {
        this.total_download = total_download;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWall_tags() {
        return wall_tags;
    }

    public void setWall_tags(String wall_tags) {
        this.wall_tags = wall_tags;
    }

    public String toString() {
        return "ClassPojo [id = " + this.id + ", wall_name = " + this.wall_name + ", wallpaper_image = " + this.wallpaper_image + ", wallpaper_image_thumb = " + this.wallpaper_image_thumb +  ", total_views = " + this.total_views + ", total_download = " + this.total_download + ", date = " + this.date + ", wall_tags = " + this.wall_tags + "]";
    }
}
