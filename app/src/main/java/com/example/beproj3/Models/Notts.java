package com.example.beproj3.Models;

public class Notts {
    public String uid ,time ,type;
    public boolean seen;

    public  Notts(){}

    public Notts(String uid,  boolean seen ,String time,String type) {
        this.uid = uid;
        this.time = time;
        this.type = type;
        this.seen = seen;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
