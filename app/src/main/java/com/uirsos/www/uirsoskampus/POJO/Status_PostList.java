package com.uirsos.www.uirsoskampus.POJO;

/**
 * Created by cunun12 on 05/05/2018.
 */

public class Status_PostList extends PostId{

    private String user_id, deskripsi, gambar_posting;
    private String postTime, fakultas;

    public Status_PostList() {
    }

    public Status_PostList(String user_id, String deskripsi, String gambar_posting, String postTime, String fakultas) {
        this.user_id = user_id;
        this.deskripsi = deskripsi;
        this.gambar_posting = gambar_posting;
        this.postTime = postTime;
        this.fakultas = fakultas;
    }

    public String getGambar_posting() {
        return gambar_posting;
    }

    public void setGambar_posting(String gambar_posting) {
        this.gambar_posting = gambar_posting;
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
