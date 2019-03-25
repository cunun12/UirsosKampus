package com.uirsos.www.uirsoskampus.SignUp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.uirsos.www.uirsoskampus.R;

public class RegistPertama extends AppCompatActivity {

    private EditText namaDepan, namaBelakang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist_pertama);

        namaDepan = findViewById(R.id.edtDepan);
        namaBelakang = findViewById(R.id.edtBelakang);

    }

    public void goRegistEmail(View view) {

        String depan = namaDepan.getText().toString().trim().replace(" ", "").toLowerCase();
        String belakang = namaBelakang.getText().toString().trim().replace(" ", "").toLowerCase();

        if (depan.length() == 0) {
            namaDepan.setError("Isi Nama Depan");
        } else if (belakang.length() == 0) {
            namaBelakang.setError("Isi Nama Belakang");
        } else {
            Intent registEmail = new Intent(this, RegistEmail.class);
            registEmail.putExtra("nmDepan",depan);
            registEmail.putExtra("nmBelakang",belakang);
            startActivity(registEmail);
        }

    }

}
