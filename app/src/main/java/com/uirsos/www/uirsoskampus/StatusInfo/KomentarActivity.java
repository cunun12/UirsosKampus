package com.uirsos.www.uirsoskampus.StatusInfo;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.uirsos.www.uirsoskampus.Adapter.AdapterKomentar;
import com.uirsos.www.uirsoskampus.POJO.DataKomentar;
import com.uirsos.www.uirsoskampus.POJO.User;
import com.uirsos.www.uirsoskampus.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.Nullable;

public class KomentarActivity extends AppCompatActivity implements View.OnClickListener {

    /*widget*/
    RecyclerView rvKomentar;
    LinearLayoutManager linearLayoutManager;
    LinearLayout lineKomentar;
    List<DataKomentar> listKomentar;
    List<User> listUser;
    AdapterKomentar adapterKomentar;
    private String idPost;
    private ImageView sendKomentar;
    private EditText textKomentar;

    /*Firebase*/
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_komentar);

        /*ambil data Intent*/
        idPost = getIntent().getStringExtra("idPost");

        rvKomentar = findViewById(R.id.list_komentar);
        textKomentar = findViewById(R.id.messageKomentar);
        sendKomentar = findViewById(R.id.send_Komentar);
        sendKomentar.setOnClickListener(this);


        /*firebase*/
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        /*Set Adapter*/
        listKomentar = new ArrayList<>();
        listUser = new ArrayList<>();
        adapterKomentar = new AdapterKomentar(listKomentar, listUser);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.scrollToPosition(listKomentar.size()-1);
        rvKomentar.setLayoutManager(linearLayoutManager);
        rvKomentar.setAdapter(adapterKomentar);

        komentarView();

    }

    private void komentarView() {

        if (firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore.collection("post_user/" + idPost + "/komentar").orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener(KomentarActivity.this, new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {

                            if (!documentSnapshots.isEmpty()) {

                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                        final DataKomentar komentar = doc.getDocument().toObject(DataKomentar.class);

                                        String komentarUserId = doc.getDocument().getString("user_id");
                                        Log.d("komentarActivity", "onEvent: "+komentarUserId);
                                        firebaseFirestore.collection("Users").document(komentarUserId).get()
                                                .addOnCompleteListener(KomentarActivity.this ,new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {

                                                            User user = task.getResult().toObject(User.class);

                                                            Log.d("KomentarActivity", "onComplete: " +user);
                                                            listUser.add(user);
                                                            listKomentar.add(komentar);
                                                            linearLayoutManager.scrollToPosition(listKomentar.size()-1);
                                                        }
                                                        adapterKomentar.notifyDataSetChanged();
                                                    }
                                                });

                                    }
                                }


                            }

                        }
                    });

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.send_Komentar:
                kirimKomentar();
                break;

            default:

        }
    }

    private void kirimKomentar() {

        String getKomentar = textKomentar.getText().toString();

        if (!TextUtils.isEmpty(getKomentar)) {

            HashMap<String, Object> komentar = new HashMap<>();
            komentar.put("user_id", user_id);
            komentar.put("komentar", getKomentar);
            komentar.put("timestamp", getTimestamp());

            firebaseFirestore.collection("post_user/" + idPost + "/komentar").add(komentar)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                textKomentar.setText("");
                                textKomentar.requestFocus();
                                linearLayoutManager.scrollToPosition(listKomentar.size()-1);
                            } else {
                                String Error =  task.getException().getMessage();
                                Log.d("KomentarActivity", "onComplete: "+Error);
                            }
                        }
                    });

        }
    }

    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        return sdf.format(new Date());
    }
}
