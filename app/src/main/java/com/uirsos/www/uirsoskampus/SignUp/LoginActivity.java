package com.uirsos.www.uirsoskampus.SignUp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.uirsos.www.uirsoskampus.R;
import com.uirsos.www.uirsoskampus.StatusInfo.MainActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Login";

    //widget
    private Button btnSend;
    private EditText inputNpm, inputEmail;

    /*TextWatcher untuk NPM dan Email*/
    private TextWatcher npmTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String getNpm = inputNpm.getText().toString().trim();
            String getEmail = inputEmail.getText().toString().trim();
            btnSend.setEnabled(!getNpm.isEmpty() && !getEmail.isEmpty());

        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };
    private ProgressBar loginProgres;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //firebase
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        //widget
        TextView btnDaftar = findViewById(R.id.txtViewRegist);
        btnSend = findViewById(R.id.btn_Login);
        inputNpm = findViewById(R.id.login_Npm);
        inputEmail = findViewById(R.id.login_Email);
        loginProgres = findViewById(R.id.progressBar);

        //onclick
        btnSend.setEnabled(false);
        inputNpm.addTextChangedListener(npmTextWatcher);
        inputEmail.addTextChangedListener(npmTextWatcher);
        btnSend.setOnClickListener(this);
        btnDaftar.setOnClickListener(this);


    }

    /*OnClick*/
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_Login:

                checkNpm();
                break;

            case R.id.txtViewRegist:

                Intent intentRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                intentRegister.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intentRegister);

                break;
        }
    }

    /*Login Menggunakan pengecekan NPM dan email*/
    private void checkNpm() {

        Log.d("Login", "checkNpm: check NPM");
        loginProgres.setVisibility(View.VISIBLE);
        btnSend.setEnabled(false);
        final String getNpm = inputNpm.getText().toString();
        final String getEmail = inputEmail.getText().toString();


        if (!getNpm.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(getEmail).matches()) {

            firebaseFirestore.collection("users").whereEqualTo("npm", getNpm).whereEqualTo("email", getEmail)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            loginProgres.setVisibility(View.GONE);
                            if (!task.getResult().isEmpty()) {

                                Intent npmEmail = new Intent(LoginActivity.this, Password.class);
                                npmEmail.putExtra("email", getEmail);
                                startActivity(npmEmail);
                                finish();
//   bagian tertunda
//                                Toast.makeText(LoginActivity.this, "NPM dan Email Cocok", Toast.LENGTH_SHORT).show();
//                                textLogin.setText("Isi Password dan Login");
//                                btnSend.setEnabled(false);
//                                lineNpm.setVisibility(View.GONE);
//                                lineEmail.setVisibility(View.GONE);
//                                linePassword.setVisibility(View.VISIBLE);
//                                btnSend.setEnabled(true);
//                                btnSend.setText("Masuk");
//                                btnSend.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//
//                                        loginProgres.setVisibility(View.VISIBLE);
//                                        String email = inputEmail.getText().toString().trim();
//                                        String getPassword = inputPassword.getText().toString();
//
//                                        firebaseAuth.signInWithEmailAndPassword(email, getPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<AuthResult> task) {
//
//                                                Intent intentProfile = new Intent(LoginActivity.this, MainActivity.class);
//                                                startActivity(intentProfile);
//                                                finish();
//
//                                                loginProgres.setVisibility(View.GONE);
//
//                                            }
//
//                                        }).addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                                Toast.makeText(LoginActivity.this, "Maaf Password yang anda masukan salah", Toast.LENGTH_SHORT).show();
//                                                loginProgres.setVisibility(View.GONE);
//                                            }
//                                        });
//                                    }
//                                });
                            } else {
                                loginProgres.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, "Login gagal mohon perika kembali NPM dan Email anda", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            btnSend.setEnabled(true);
            loginProgres.setVisibility(View.GONE);
            Toast.makeText(this, "Email tidak benar", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {

            sendToMain();

        } else {

            Log.d(TAG, "onStart: LoginActivity.class");

        }
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
