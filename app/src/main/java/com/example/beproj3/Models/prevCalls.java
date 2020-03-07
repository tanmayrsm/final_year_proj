package com.example.beproj3.Models;

public class prevCalls {
    Chats Chats;
    String end ,start ,uid;

    public prevCalls(){}

    public prevCalls(com.example.beproj3.Models.Chats chats, String end, String start, String uid) {
        Chats = chats;
        this.end = end;
        this.start = start;
        this.uid = uid;
    }

    public com.example.beproj3.Models.Chats getChats() {
        return Chats;
    }

    public void setChats(com.example.beproj3.Models.Chats chats) {
        Chats = chats;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
