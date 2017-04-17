package com.asu.mc.digitalassist.activities.models;

/**
 * Created by Siddharth on 4/17/2017.
 */

public class Restaurant {

    private String name;
    private String mobileUrl;
    private String rating;
    private String phone;
    private String category;

    public Restaurant(String name, String mobileUrl, String rating, String phone, String category) {
        this.name = name;
        this.mobileUrl = mobileUrl;
        this.rating = rating;
        this.phone = phone;
        this.category = category;
    }

    public Restaurant() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileUrl() {
        return mobileUrl;
    }

    public void setMobileUrl(String mobileUrl) {
        this.mobileUrl = mobileUrl;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
