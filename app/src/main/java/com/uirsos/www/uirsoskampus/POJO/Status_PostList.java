package com.uirsos.www.uirsoskampus.POJO;

import java.util.Date;

/**
 * Created by cunun12 on 05/05/2018.
 */

public class Status_PostList extends PostId{

    private String user_id, deskripsi, image_thumb, imagePost;
    private String postTime, fakultas;

    public Status_PostList() {
    }

    public Status_PostList(String user_id, String deskripsi, String image_thumb, String imagePost, String postTime, String fakultas) {
        this.user_id = user_id;
        this.deskripsi = deskripsi;
        this.image_thumb = image_thumb;
        this.imagePost = imagePost;
        this.postTime = postTime;
        this.fakultas = fakultas;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public String getImagePost() {
        return imagePost;
    }

    public void setImagePost(String imagePost) {
        this.imagePost = imagePost;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getFakultas() {
        return fakultas;
    }

    public void setFakultas(String fakultas) {
        this.fakultas = fakultas;
    }
}
