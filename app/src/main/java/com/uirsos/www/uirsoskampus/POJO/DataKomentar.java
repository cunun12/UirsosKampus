package com.uirsos.www.uirsoskampus.POJO;

import java.sql.Blob;
import java.util.Date;

/**
 * Created by cunun12 on 05/05/2018.
 */

public class DataKomentar {

    private String user_id, komentar;
    private String komentarTime;

    public DataKomentar() {
    }

    public DataKomentar(String user_id, String komentar, String komentarTime) {
        this.user_id = user_id;
        this.komentar = komentar;
        this.komentarTime = komentarTime;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }

    public String getKomentarTime() {
        return komentarTime;
    }

    public void setKomentarTime(String komentarTime) {
        this.komentarTime = komentarTime;
    }
}
