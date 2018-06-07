package com.uirsos.www.uirsoskampus.Profile;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uirsos.www.uirsoskampus.R;

import java.util.HashMap;
import java.util.Map;

public class StatusActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView btnSimpan;
    private EditText bioText;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //inisialisasi yang ada di XML
        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnSimpan = (TextView) findViewById(R.id.btn_simpan);
        btnSimpan.setEnabled(false);
        bioText = (EditText) findViewById(R.id.bioText);

        bioText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().equals("")) {
                    btnSimpan.setEnabled(false);
                } else {
                    btnSimpan.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    btnSimpan.setEnabled(false);
                } else {
                    btnSimpan.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String textStatus = bioText.getText().toString();

                if (!TextUtils.isEmpty(textStatus)) {


                    final String user_id = mAuth.getCurrentUser().getUid();

                    firebaseFirestore.collection("users/" + user_id + "/status").document(user_id).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (!task.getResult().exists()) {

                                        Map<String, Object> statusMap = new HashMap<>();
                                        statusMap.put("status", textStatus);
                                        firebaseFirestore.collection("users/" + user_id + "/status").document(user_id).set(statusMap);

                                        Intent backProfile = new Intent(StatusActivity.this, ProfileActivity.class);
                                        startActivity(backProfile);
                                        finish();

                                    } else {

                                        Map<String, Object> statusMap = new HashMap<>();
                                        statusMap.put("status", textStatus);
                                        firebaseFirestore.collection("users/" + user_id + "/status").document(user_id).update(statusMap);

                                        Intent backProfile = new Intent(StatusActivity.this, ProfileActivity.class);
                                        startActivity(backProfile);
                                        finish();

                                    }
                                }
                            });

                }

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(StatusActivity.this, ProfileActivity.class);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(backIntent);
                finish();
            }
        });
    }
}
