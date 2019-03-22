package com.uirsos.www.uirsoskampus.SignUp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.uirsos.www.uirsoskampus.R;

public class RegistPertama extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist_pertama);
    }

    public void goIdentitas(View view) {

        Intent identitas = new Intent(this, Identitas.class);
        startActivity(identitas);

    }
}
