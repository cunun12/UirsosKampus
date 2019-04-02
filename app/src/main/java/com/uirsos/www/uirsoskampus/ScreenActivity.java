package com.uirsos.www.uirsoskampus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.uirsos.www.uirsoskampus.Profile.SettingAccount;
import com.uirsos.www.uirsoskampus.SignUp.Validasi;
import com.uirsos.www.uirsoskampus.SignUp.WelcomeLogin;
import com.uirsos.www.uirsoskampus.StatusInfo.AdminActivity;
import com.uirsos.www.uirsoskampus.StatusInfo.MainActivity;

import javax.annotation.Nullable;

public class ScreenActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);

        ImageView logo = findViewById(R.id.logo);
        TextView desc = findViewById(R.id.deskripsiUirsos);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.myanim);
        logo.setAnimation(anim);
        desc.setAnimation(anim);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentId = mAuth.getCurrentUser();
//        Log.d("screen", "onStart: currentId" + id_User);

        if (currentId != null) {
            String id_User = mAuth.getUid();
            final String emailpengguna = mAuth.getCurrentUser().getEmail();
            firestore.collection("User").document(id_User).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        String level = documentSnapshot.getString("Level");

                        if (level.equals("mahasiswa")) {
                            Intent mahasiswa = new Intent(ScreenActivity.this, MainActivity.class);
                            Screen(mahasiswa);
                        } else if (level.equals("admin")) {
                            Intent admin = new Intent(ScreenActivity.this, AdminActivity.class);
                            Screen(admin);
                        }
                    }
                }
            });

        } else {
            final Intent i = new Intent(ScreenActivity.this, WelcomeLogin.class);
            Screen(i);
        }

    }

    private void Screen(final Intent i) {
        Thread timer = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(i);
                    finish();
                }
            }
        };

        timer.start();
    }


}
