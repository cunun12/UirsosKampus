package com.uirsos.www.uirsoskampus.SignUp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.uirsos.www.uirsoskampus.R;

public class WelcomeLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_login);

    }

    public void goLogin(View view) {
        Intent login = new Intent(this, LoginEmail.class);
        startActivity(login);
    }

    public void goRegis(View view) {
        Intent regist = new Intent(this, RegistPertama.class);
        startActivity(regist);
    }
}
