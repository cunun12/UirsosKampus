package com.uirsos.www.uirsoskampus.POJO;

/**
 * Created by cunun12
 * POJO (Plain Old Java Object)
 * Disini tempat semua variable disimpan maupun di panggil
 */

public class DataItemBerita {

    /*Varible Berita*/
    private String info, judul, waktu, image, deskripsi; //untuk postingan Berita

    public DataItemBerita() {
    }

    public DataItemBerita(String info, String judul, String waktu, String image, String deskripsi) {
        this.info = info;
        this.judul = judul;
        this.waktu = waktu;
        this.image = image;
        this.deskripsi = deskripsi;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }
}
