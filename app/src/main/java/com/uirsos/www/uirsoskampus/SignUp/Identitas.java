package com.uirsos.www.uirsoskampus.SignUp;

import android.annotation.SuppressLint;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.uirsos.www.uirsoskampus.R;

import java.util.ArrayList;

public class Identitas extends AppCompatActivity {

    Spinner ta;
    Button regist;

    ArrayList<String> listTahun = new ArrayList<String>();
    ArrayAdapter<String> itemTahun;
    String thn;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identitas);

        regist = findViewById(R.id.btnRegister);
        ta = findViewById(R.id.tahun);
        ta.setAdapter(getTahun());


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayAdapter<String> getTahun(){
        Calendar calendar = Calendar.getInstance();
        int thnNow = calendar.get(Calendar.YEAR);

        for (int i = 2010; i <= thnNow; i++){
            thn = String.valueOf(i);
            listTahun.add(thn);
            itemTahun = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, listTahun);
        }
        return itemTahun;
    }

}
