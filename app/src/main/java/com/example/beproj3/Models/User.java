package com.example.beproj3.Models;

public class User {
    public String name,email,password,userid,my_lang,fb_val,lang,image_url;

    public User(){}

    public User(String name, String email, String password, String userid, String my_lang, String fb_val, String lang, String image_url) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.userid = userid;
        this.my_lang = my_lang;
        this.fb_val = fb_val;
        this.lang = lang;
        this.image_url = image_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMy_lang() {
        return my_lang;
    }

    public void setMy_lang(String my_lang) {
        this.my_lang = my_lang;
    }

    public String getFb_val() {
        return fb_val;
    }

    public void setFb_val(String fb_val) {
        this.fb_val = fb_val;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
