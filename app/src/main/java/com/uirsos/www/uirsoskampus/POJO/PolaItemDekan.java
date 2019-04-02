package com.uirsos.www.uirsoskampus.POJO;

/**
 *  POJO (Plain Old Java Object
 *  Disini tempat semua variable disimpan maupun di panggil
 */
public class PolaItemDekan {

    private String judul_berita, info, waktu, isi_berita, gambar;

    public PolaItemDekan(){

    }

    public PolaItemDekan(String judul_berita, String info, String waktu, String isi_berita, String gambar) {
        this.judul_berita = judul_berita;
        this.info = info;
        this.waktu = waktu;
        this.gambar = gambar;
        this.isi_berita = isi_berita;
    }

    public String getJudul_berita() {
        return judul_berita;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public void setJudul_berita(String judul_berita) {
        this.judul_berita = judul_berita;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getIsi_berita() {
        return isi_berita;
    }

    public void setIsi_berita(String isi_berita) {
        this.isi_berita = isi_berita;
    }
}
