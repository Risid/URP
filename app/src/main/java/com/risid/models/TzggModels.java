package com.risid.models;

/**
 * Created by risid on 2015/9/22.
 */
public class TzggModels {
    private String title;
    private String url;
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public TzggModels(String title, String time, String url) {
        this.title = title;
        this.time = time;
        this.url = url;
    }
}
