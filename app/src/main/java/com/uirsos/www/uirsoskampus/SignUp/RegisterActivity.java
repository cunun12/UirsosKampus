package com.uirsos.www.uirsoskampus.SignUp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uirsos.www.uirsoskampus.HandleRequest.APIServer;
import com.uirsos.www.uirsoskampus.HandleRequest.RequestHandler;
import com.uirsos.www.uirsoskampus.Profile.ProfileActivity;
import com.uirsos.www.uirsoskampus.Profile.SetupActivity;
import com.uirsos.www.uirsoskampus.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Register";
    //widget
    private Button btnSend;
    private EditText inputNpm, inputEmail, inputPassword, inputRepassword;
    TextWatcher npmEmailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String getNpm = inputNpm.getText().toString().trim();
            String getEmail = inputEmail.getText().toString().trim();

            if (!getNpm.isEmpty()) {
                btnSend.setEnabled(true);
            }

            if (!getEmail.isEmpty()) {
                btnSend.setEnabled(true);
            }
//            btnSend.setEnabled(!getNpm.isEmpty() && !getEmail.isEmpty());

        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };
    private TextView btnMasuk, infoError;
    private LinearLayout lineNpm, lineEmail, linePassword, lineRepassword;
    //firebase
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //firebase
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        //widget
        btnSend = findViewById(R.id.btn_Daftar);
        btnSend.setEnabled(false);
        btnMasuk = findViewById(R.id.txtViewMasuk);
        lineNpm = findViewById(R.id.line_Npm);
        inputNpm = findViewById(R.id.daftar_Npm);
        lineEmail = findViewById(R.id.line_Email);
        inputEmail = findViewById(R.id.daftar_Email);
        linePassword = findViewById(R.id.line_password);
        inputPassword = findViewById(R.id.daftar_password);
        lineRepassword = findViewById(R.id.line_Re_password);
        inputRepassword = findViewById(R.id.daftar_repassword);
        infoError = findViewById(R.id.info_Error);


        btnMasuk.setOnClickListener(this);

        inputNpm.addTextChangedListener(npmEmailTextWatcher);
        btnSend.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_Daftar:
                checkNPM();
                break;

            case R.id.txtViewMasuk:

                Intent intentRegister = new Intent(RegisterActivity.this, LoginActivity.class);
                intentRegister.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentRegister);
                finish();

                break;
        }
    }

    private void checkNPM() {

        final String getNpm = inputNpm.getText().toString();

//        StringRequest requestCheck = new StringRequest(Request.Method.POST, APIServer.check,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.d(TAG, "onResponse: " + response.toString());
//                        try {
//                            JSONObject res = new JSONObject(response);
//
//                            String check = res.getString("check");
//
//                            if (check.equals("1")) {
//                                Log.d(TAG, "onResponse: 1");
//                                Toast.makeText(RegisterActivity.this, "NPM sudah terdaftar", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Log.d(TAG, "onResponse: 0");
//                                Toast.makeText(RegisterActivity.this, "NPM dapat digunakan", Toast.LENGTH_SHORT).show();
//                                //                            lineNpm.setVisibility(View.GONE);
////                            btnSend.setEnabled(false);
////                            lanjutRegist();
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d(TAG, "onErrorResponse: " + error);
//                        Toast.makeText(RegisterActivity.this, "Ini error nya " + error, Toast.LENGTH_SHORT).show();
//                    }
//                }) {
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//
//                Map<String, String> check = new HashMap<>();
//                check.put("npm", getNpm);
//
//                return check;
//            }
//        };
//
//        RequestHandler.getInstance(RegisterActivity.this).addToRequestQueue(requestCheck);
//

        firebaseFirestore.collection("users").whereEqualTo("npm", getNpm).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(RegisterActivity.this, "NPM dapat digunakan", Toast.LENGTH_SHORT).show();
                            lineNpm.setVisibility(View.GONE);
                            btnSend.setEnabled(false);
                            lanjutRegist();
                        } else {
                            Toast.makeText(RegisterActivity.this, "NPM sudah terdaftar", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void lanjutRegist() {

        lineEmail.setVisibility(View.VISIBLE);
        linePassword.setVisibility(View.VISIBLE);
        lineRepassword.setVisibility(View.VISIBLE);

        btnSend.setEnabled(true);
        btnSend.setText("Daftar");

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String getNpm = inputNpm.getText().toString();
                final String getEmail = inputEmail.getText().toString();
                final String getPassword = inputPassword.getText().toString();
                String getRepassword = inputRepassword.getText().toString();


                if (!getEmail.isEmpty() && !getPassword.isEmpty() && !getRepassword.isEmpty()) {

                    if (Patterns.EMAIL_ADDRESS.matcher(getEmail).matches()) {
                        if (getPassword.equals(getRepassword)) {

                            firebaseFirestore.collection("users").whereEqualTo("email", getEmail).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.getResult().isEmpty()) {

                                                createEmailPassword(getNpm, getEmail, getPassword);

                                            } else {
                                                inputEmail.requestFocus();
                                                inputEmail.setError("Maaf email sudah terdaftar");

                                            }
                                        }
                                    });

                        } else {

                            inputRepassword.setError("Password tidak cocok");

                        }

                    } else {
                        inputEmail.requestFocus();
                        inputEmail.setError("Masukan Email dengan benar");
                    }


                } else {

                    infoError.setVisibility(View.VISIBLE);
                    infoError.setText("Masukan semua data");

                }

            }
        });
    }

    private void createEmailPassword(final String npm, final String email, String password) {

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Berhasil Registrasi", Toast.LENGTH_SHORT).show();
                    Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
                    setupIntent.putExtra("npm", npm);
                    setupIntent.putExtra("email", email);
                    startActivity(setupIntent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Gagal registrasi", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {

            sendToMain();

        }
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(RegisterActivity.this, ProfileActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
