package com.uirsos.www.uirsoskampus.POJO;

/**
 * Created by cunun12 on 08/06/2018.
 */

public class Verify extends PostId {

    private String user_id, NPM, nama_lengkap, Fakultas, Prodi, imageKtm, waktu;

    public Verify() {
    }

    public Verify(String user_id, String NPM, String nama_lengkap, String Fakultas, String Prodi, String imageKtm, String waktu) {
        this.user_id = user_id;
        this.NPM = NPM;
        this.nama_lengkap = nama_lengkap;
        this.Fakultas = Fakultas;
        this.Prodi = Prodi;
        this.imageKtm = imageKtm;
        this.waktu = waktu;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getNPM() {
        return NPM;
    }

    public void setNPM(String NPM) {
        this.NPM = NPM;
    }

    public String getNama_lengkap() {
        return nama_lengkap;
    }

    public void setNama_lengkap(String nama_lengkap) {
        this.nama_lengkap = nama_lengkap;
    }

    public String getFakultas() {
        return Fakultas;
    }

    public void setFakultas(String fakultas) {
        Fakultas = fakultas;
    }

    public String getProdi() {
        return Prodi;
    }

    public void setProdi(String prodi) {
        Prodi = prodi;
    }

    public String getImageKtm() {
        return imageKtm;
    }

    public void setImageKtm(String imageKtm) {
        this.imageKtm = imageKtm;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }
}
