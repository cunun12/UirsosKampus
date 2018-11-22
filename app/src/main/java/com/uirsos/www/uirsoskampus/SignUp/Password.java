package com.uirsos.www.uirsoskampus.SignUp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.uirsos.www.uirsoskampus.R;
import com.uirsos.www.uirsoskampus.StatusInfo.MainActivity;

public class Password extends AppCompatActivity {

    private EditText passwordLogin;
    String email;
    private ProgressBar loginProgres;

    /*Firebase*/
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        Intent get = getIntent();
        email = get.getStringExtra("email");

        passwordLogin = findViewById(R.id.login_password);
        Button btnLogin = findViewById(R.id.btn_Login);
        loginProgres = findViewById(R.id.progressBar);

        /*firebase*/
        firebaseAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String getPassword = passwordLogin.getText().toString();
                if (!getPassword.isEmpty()) {
                    loginProgres.setVisibility(View.VISIBLE);

                    firebaseAuth.signInWithEmailAndPassword(email, getPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Intent mainIntent = new Intent(Password.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                            loginProgres.setVisibility(View.GONE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Password.this, "Maaf Password yang anda masukan salah", Toast.LENGTH_SHORT).show();
                            finish();
                            loginProgres.setVisibility(View.GONE);
                        }
                    });
                } else {
                    Toast.makeText(Password.this, "Kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
