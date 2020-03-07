package com.example.beproj3.Models;

public class sent_req_model {
    String sent_id;

    public sent_req_model(){}

    public sent_req_model(String sent_id) {
        this.sent_id = sent_id;
    }

    public String getSent_id() {
        return sent_id;
    }

    public void setSent_id(String sent_id) {
        this.sent_id = sent_id;
    }
}
