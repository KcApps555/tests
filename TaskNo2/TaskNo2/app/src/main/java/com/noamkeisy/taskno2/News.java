package com.noamkeisy.taskno2;

import java.io.Serializable;

public class News implements Serializable {
    int picSesId;
    String picLink;
    String title;
    String date;
    String summary;
    String link;

    public News() {
    }

    public News(int picSesId, String title, String date, String summary, String link) {
        this.picSesId = picSesId;
        this.title = title;
        this.date = date;
        this.summary = summary;
        this.link = link;
    }

    public News(String picLink, String title, String date, String summary, String link) {
        this.picLink = picLink;
        this.title = title;
        this.date = date;
        this.summary = summary;
        this.link = link;
    }

    public int getPicSesId() {
        return picSesId;
    }

    public void setPicSesId(int picSesId) {
        this.picSesId = picSesId;
    }

    public String getPicLink() {
        return picLink;
    }

    public void setPicLink(String picLink) {
        this.picLink = picLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
