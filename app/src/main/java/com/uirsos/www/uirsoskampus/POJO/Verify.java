package com.uirsos.www.uirsoskampus.POJO;

/**
 * Created by cunun12 on 08/06/2018.
 */

public class Verify extends PostId{

    String user_id, nama_pengguna, komentar, image_ktm, fakultas, npm, waktu;

    public Verify() {
    }

    public Verify(String user_id, String nama_pengguna, String komentar, String image_ktm, String fakultas, String npm, String waktu) {
        this.user_id = user_id;
        this.nama_pengguna = nama_pengguna;
        this.komentar = komentar;
        this.image_ktm = image_ktm;
        this.fakultas = fakultas;
        this.npm = npm;
        this.waktu = waktu;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getNama_pengguna() {
        return nama_pengguna;
    }

    public void setNama_pengguna(String nama_pengguna) {
        this.nama_pengguna = nama_pengguna;
    }

    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }

    public String getImage_ktm() {
        return image_ktm;
    }

    public void setImage_ktm(String image_ktm) {
        this.image_ktm = image_ktm;
    }

    public String getFakultas() {
        return fakultas;
    }

    public void setFakultas(String fakultas) {
        this.fakultas = fakultas;
    }

    public String getNpm() {
        return npm;
    }

    public void setNpm(String npm) {
        this.npm = npm;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }
}
