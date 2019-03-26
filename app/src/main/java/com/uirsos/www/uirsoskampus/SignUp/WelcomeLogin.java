package com.uirsos.www.uirsoskampus.SignUp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.uirsos.www.uirsoskampus.Profile.SettingAccount;
import com.uirsos.www.uirsoskampus.R;
import com.uirsos.www.uirsoskampus.StatusInfo.MainActivity;

public class WelcomeLogin extends AppCompatActivity {

    private static final String TAG = "Login";

    /*Firebase*/
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
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


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            Intent setting = new Intent(WelcomeLogin.this, SettingAccount.class);
            startActivity(setting);
            finish();

        } else {

            Log.d("Login", "onStart: LoginActivity.class");

        }
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(WelcomeLogin.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
