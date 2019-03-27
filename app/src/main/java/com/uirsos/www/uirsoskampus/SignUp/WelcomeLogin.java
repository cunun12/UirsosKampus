package com.uirsos.www.uirsoskampus.SignUp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uirsos.www.uirsoskampus.StatusInfo.AdminActivity;
import com.uirsos.www.uirsoskampus.R;
import com.uirsos.www.uirsoskampus.StatusInfo.MainActivity;

public class WelcomeLogin extends AppCompatActivity {

    private static final String TAG = "Login";

    /*Firebase*/
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String id_user;

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
        id_user = mAuth.getCurrentUser().getUid();

        if (currentUser != null) {

            firestore.collection("User").document(id_user).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot != null && documentSnapshot.exists()) {

                        String level = documentSnapshot.getString("Level");

                        if (level.equals("mahasiswa")) {
                            Intent mainActivity = new Intent(WelcomeLogin.this, MainActivity.class);
                            startActivity(mainActivity);
                            finish();
                        } else {
                            Intent mainActivity = new Intent(WelcomeLogin.this, AdminActivity.class);
                            startActivity(mainActivity);
                            finish();
                        }

                    }
                }
            });

        } else {

            Log.d("Login", "onStart: LoginActivity.class");

        }
    }
}
