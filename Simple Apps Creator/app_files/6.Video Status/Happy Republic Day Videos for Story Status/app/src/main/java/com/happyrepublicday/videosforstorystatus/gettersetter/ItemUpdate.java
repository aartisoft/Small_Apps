package com.happyrepublicday.videosforstorystatus.gettersetter;

public class ItemUpdate {

    private boolean status;
    private String message;


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

    public String toString() {
        return "ClassPojo [status = " + this.status + ", message = " + this.message + "]";
    }
}
