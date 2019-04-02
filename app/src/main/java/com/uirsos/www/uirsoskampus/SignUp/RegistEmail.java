package com.uirsos.www.uirsoskampus.SignUp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.uirsos.www.uirsoskampus.R;

public class RegistEmail extends AppCompatActivity implements View.OnClickListener {
    EditText password, coPassword;
    String depan, belakang, namaLengkap, email;
    ProgressBar progressBar;

    /*Firebase*/
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist_email);

        Intent data = getIntent();
        depan = data.getStringExtra("nmDepan");
        belakang = data.getStringExtra("nmBelakang");
        namaLengkap = depan + " " + belakang;

        TextView setEmail = findViewById(R.id.edtEmail);
        email = depan + belakang + "@student.uir.ac.id";
        setEmail.setText(email);

        /*fungsi Firebase*/
        mAuth = FirebaseAuth.getInstance();

        /*bagian XML*/
        progressBar = findViewById(R.id.progressCreate);
        password = findViewById(R.id.edtPassword);
        coPassword = findViewById(R.id.edt_coPassword);

        Button btnSimpan = findViewById(R.id.btnSimpan);
        btnSimpan.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String pass = password.getText().toString();
        String coPass = coPassword.getText().toString();
        progressBar.setVisibility(View.VISIBLE);

        if (pass.length() == 0) {
            password.setError("Isi password");
            progressBar.setVisibility(View.GONE);
        } else if (coPass.length() == 0) {
            coPassword.setError("Isi konfrim password");
            progressBar.setVisibility(View.GONE);
        } else {
            if (pass.equals(coPass)) {
                createEmail(email, pass);
            } else {
                progressBar.setVisibility(View.GONE);
                coPassword.requestFocus();
                Toast.makeText(this, "Password tidak Cocok", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void createEmail(final String email, String password) {

        Log.w("", "createEmail: " + email);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);

                            Intent validasi = new Intent(RegistEmail.this, Validasi.class);
                            validasi.putExtra("namaLengkap", namaLengkap);
                            validasi.putExtra("email", email);
                            startActivity(validasi);
                            finish();
                        } else {
                            AlertDialog.Builder allert = new AlertDialog.Builder(RegistEmail.this);
                            allert.setTitle("Silakan Login");
                            allert.setMessage("Akun telah terdaftar!");
                            allert.setCancelable(false)
                                    .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(RegistEmail.this, WelcomeLogin.class);
                                            startActivity(intent);
                                            mAuth.signOut();
                                            finish();
                                        }
                                    }).show();
//                            Toast.makeText(RegistEmail.this, "Email anda sudah terdaftar! Silakan Login", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}
