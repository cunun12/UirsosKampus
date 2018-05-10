package com.uirsos.www.uirsoskampus.HandleRequest;

/**
 * Created by cunun12
 */

public class APIServer {
    //Dibawah ini merupakan Pengalamatan dimana Lokasi Skrip CRUD PHP disimpan
    //karena kita membuat localhost maka alamatnya tertuju ke IP komputer dimana File PHP tersebut berada
    //PENTING! JANGAN LUPA GANTI IP SESUAI DENGAN IP KOMPUTER DIMANA DATA PHP BERADA
    public static final String URL   = "http://192.168.100.15/uirsos/v1/";
    public static final String REGISTER     = URL+"registeruser.php";

    /*berita*/
    public static final String Berita = URL+ "berita/vberita.php";

}
