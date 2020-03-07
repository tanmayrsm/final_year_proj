package com.example.beproj3.Models;

import android.net.Uri;

public class PDF {
    String pdf_url,time,type,who_sent;
    public PDF(Uri url, String tym, String pdf, String who_sent){}

    public PDF(String pdf_url, String time, String type, String who_sent) {
        this.pdf_url = pdf_url;
        this.time = time;
        this.type = type;
        this.who_sent = who_sent;
    }

    public String getPdf_url() {
        return pdf_url;
    }

    public void setPdf_url(String pdf_url) {
        this.pdf_url = pdf_url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWho_sent() {
        return who_sent;
    }

    public void setWho_sent(String who_sent) {
        this.who_sent = who_sent;
    }
}
