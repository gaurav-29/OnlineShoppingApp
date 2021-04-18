package com.android_batch_31.designdemo;

public class Category {

    private String id;
    private String title;
    private String photo;

    public Category(String id, String title, String photo) {
        // Variables in this constructor are taken from Category cat = new Category(id,title,photo) in CategoryContainer.java
        this.id = id;
        this.title = title;
        this.photo = photo;
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

}
