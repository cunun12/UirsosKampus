package com.uirsos.www.uirsoskampus.POJO;

/**
 * Created by cunun12 on 05/05/2018.
 */

public class User {
    public String image, nama_user;

    public User() {
    }

    public User(String image, String nama_user) {
        this.image = image;
        this.nama_user = nama_user;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNama_user() {
        return nama_user;
    }

    public void setNama_user(String nama_user) {
        this.nama_user = nama_user;
    }
}
