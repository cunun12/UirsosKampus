package com.uirsos.www.uirsoskampus.SignUp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.uirsos.www.uirsoskampus.Profile.SettingAccount;
import com.uirsos.www.uirsoskampus.R;
import com.uirsos.www.uirsoskampus.StatusInfo.MainActivity;

public class LoginEmail extends AppCompatActivity implements View.OnClickListener {
    EditText logEmail, logPass;
    ProgressBar progressLogin;

    /*Firebase*/
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        /*XML*/
        logEmail = findViewById(R.id.nama_pengguna);
        logPass = findViewById(R.id.logPass);
        progressLogin = findViewById(R.id.progres_login);
        Button btnLogin = findViewById(R.id.btn_login);

        /*Onclick*/
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        progressLogin.setVisibility(View.VISIBLE);
        String studentMail = "@student.uir.ac.id";
        String email = logEmail.getText().toString().trim().replace(" ", "").toLowerCase();
        String passw = logPass.getText().toString().trim();

        if (email.isEmpty()) {
            progressLogin.setVisibility(View.GONE);
            logEmail.setError("Isi nama Anda!");
            logEmail.requestFocus();
        } else if (passw.isEmpty()) {
            progressLogin.setVisibility(View.GONE);
            logPass.setError("Isi Password");
            logPass.requestFocus();
        } else {
            progressLogin.setVisibility(View.GONE);
            String emailLengkap = email + studentMail;
            signIn(emailLengkap, passw);
//            Toast.makeText(this, "Email = " + emailLengkap + " Password = " + passw, Toast.LENGTH_SHORT).show();
        }

    }

    private void signIn(final String email, final String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginEmail.this, SettingAccount.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(LoginEmail.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                } else {
                    logEmail.setError("Check email!");
                    logPass.setError("Check password!");
                    logEmail.requestFocus();
                    Toast.makeText(LoginEmail.this, "Login gagal! check Email dan Password!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            Intent setting = new Intent(LoginEmail.this, SettingAccount.class);
            startActivity(setting);
            finish();

        } else {

            Log.d("Login", "onStart: LoginActivity.class");

        }
    }
}
