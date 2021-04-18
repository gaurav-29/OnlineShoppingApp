package com.android_batch_31.designdemo;

public class WishList {

    private String id, title, photo, price;

    public WishList(String id, String title, String photo, String price) {   //we can decide these variables on the bases of select query of wishlist.php webservice.
        this.id = id;
        this.title = title;
        this.photo = photo;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}