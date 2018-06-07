package com.uirsos.www.uirsoskampus.POJO;

/**
 * Created by cunun12 on 05/05/2018.
 */

public class User {
    public String imagePic, nama_user;

    public User() {
    }

    public User(String imagePic, String nama_user) {
        this.imagePic = imagePic;
        this.nama_user = nama_user;
    }

    public String getImagePic() {
        return imagePic;
    }

    public void setImagePic(String imagePic) {
        this.imagePic = imagePic;
    }

    public String getNama_user() {
        return nama_user;
    }

    public void setNama_user(String nama_user) {
        this.nama_user = nama_user;
    }
}
