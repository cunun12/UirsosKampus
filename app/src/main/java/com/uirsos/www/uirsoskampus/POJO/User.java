package com.uirsos.www.uirsoskampus.POJO;

/**
 * Created by cunun12 on 05/05/2018.
 */

public class User {
    public String gambar_profile, nama_lengkap;

    public User() {
    }

    public User(String gambar_profile, String nama_lengkap) {
        this.gambar_profile = gambar_profile;
        this.nama_lengkap = nama_lengkap;
    }

    public String getGambar_profile() {
        return gambar_profile;
    }

    public void setGambar_profile(String gambar_profile) {
        this.gambar_profile = gambar_profile;
    }

    public String getNama_lengkap() {
        return nama_lengkap;
    }

    public void setNama_lengkap(String nama_lengkap) {
        this.nama_lengkap = nama_lengkap;
    }

}
