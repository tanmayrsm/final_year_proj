package com.example.beproj3.Models;

public class UserStatus {
    String time ,status ,uid;
    public UserStatus(){}

    public UserStatus(String time, String status, String uid) {
        this.time = time;
        this.status = status;
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
