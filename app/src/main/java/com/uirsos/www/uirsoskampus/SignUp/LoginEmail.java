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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uirsos.www.uirsoskampus.Profile.SettingAccount;
import com.uirsos.www.uirsoskampus.R;
import com.uirsos.www.uirsoskampus.StatusInfo.AdminActivity;
import com.uirsos.www.uirsoskampus.StatusInfo.MainActivity;

public class LoginEmail extends AppCompatActivity implements View.OnClickListener {
    EditText logEmail, logPass;
    ProgressBar progressLogin;

    /*Firebase*/
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

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
            public void onComplete(@NonNull final Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser user_id = mAuth.getCurrentUser();
                    if (user_id != null) {
                        String userID = mAuth.getCurrentUser().getUid();
                        firestore.collection("User").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot != null && documentSnapshot.exists()) {

                                    String gambar = documentSnapshot.getString("gambar_profile");
                                    String level = documentSnapshot.getString("Level");

                                    assert level != null;
                                    if (level.equals("admin")) {
                                        if (gambar != null) {
                                            Intent adminActivity = new Intent(LoginEmail.this, AdminActivity.class);
                                            startActivity(adminActivity);
                                            finish();
                                            Toast.makeText(LoginEmail.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Intent intent = new Intent(LoginEmail.this, SettingAccount.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    } else if (level.equals("mahasiswa")) {
                                        if (gambar != null) {
                                            Intent mainActivity = new Intent(LoginEmail.this, MainActivity.class);
                                            startActivity(mainActivity);
                                            finish();
                                            Toast.makeText(LoginEmail.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Intent intent = new Intent(LoginEmail.this, SettingAccount.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                            }
                        });
                    } else {
                        Toast.makeText(LoginEmail.this, "Belum berhasil Login", Toast.LENGTH_SHORT).show();
                    }

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

            String user_id = mAuth.getUid();
            final String emailPengguna = mAuth.getCurrentUser().getEmail();
            firestore.collection("User").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        String level = task.getResult().getString("Level");
                        String nama = task.getResult().getString("nama_lengkap");

                        assert level != null;
                        switch (level) {
                            case "admin":
                                Intent adminActivity = new Intent(LoginEmail.this, AdminActivity.class);
                                startActivity(adminActivity);
                                finish();
                                break;
                            case "mahasiswa":
                                Intent mainActivity = new Intent(LoginEmail.this, MainActivity.class);
                                startActivity(mainActivity);
                                finish();
                                break;
                        }
                    }

                }
            });
            Intent setting = new Intent(LoginEmail.this, SettingAccount.class);
            startActivity(setting);
            finish();

        } else {

            Log.d("Login", "onStart: LoginActivity.class");

        }
    }
}
