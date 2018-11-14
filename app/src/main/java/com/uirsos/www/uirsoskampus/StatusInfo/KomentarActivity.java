package com.uirsos.www.uirsoskampus.StatusInfo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
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
    AdapterKomentar adapterKomentar;
    List<String> strings;
    private String idPost;
    private ImageView btnBack;
    private EditText textKomentar1;
    private EditText textKomentar2;
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
        textKomentar1 = findViewById(R.id.messageKomentar1);
        textKomentar2 = findViewById(R.id.messageKomentar2);
        ImageView sendKomentar = findViewById(R.id.send_Komentar);
        sendKomentar.setOnClickListener(this);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        /*firebase*/
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        /*Set Adapter*/
        listKomentar = new ArrayList<>();
        strings = new ArrayList<>();
        adapterKomentar = new AdapterKomentar(listKomentar);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rvKomentar.setLayoutManager(linearLayoutManager);
        rvKomentar.setAdapter(adapterKomentar);


        komentarView();

    }


    private void komentarView() {

        if (firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore.collection("posting/" + idPost + "/komentar").orderBy("komentarTime", Query.Direction.ASCENDING)
                    .addSnapshotListener(KomentarActivity.this, new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {

                            if (!documentSnapshots.isEmpty()) {

                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                    if (doc.getType() == DocumentChange.Type.ADDED) {

                                        String komentarId = doc.getDocument().getId();
                                        final DataKomentar komentar = doc.getDocument().toObject(DataKomentar.class).withId(komentarId);

                                        listKomentar.add(komentar);

                                    }
                                }
                                // membuat RecyclerView otomatis
                                // scroll ke bawah setelah nama baru ditambahkan
                                rvKomentar.scrollToPosition(listKomentar.size() - 1);
                                adapterKomentar.notifyDataSetChanged();


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

            case R.id.btnBack:

                Intent backStatus = new Intent(this, MainActivity.class);
                backStatus.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backStatus);
                finish();

                break;

            default:

        }
    }

    /*mengirim komentar user*/
    private void kirimKomentar() {

        String getKomentar1 = textKomentar1.getText().toString();
        String getKomentar2 = textKomentar2.getText().toString();

        if (!TextUtils.isEmpty(getKomentar1)) {
            textKomentar1.setVisibility(View.GONE);
            textKomentar2.setVisibility(View.VISIBLE);

            HashMap<String, Object> komentar1 = new HashMap<>();
            komentar1.put("user_id", user_id);
            komentar1.put("komentar", getKomentar1);
            komentar1.put("komentarTime", getTimestamp());

            firebaseFirestore.collection("posting/" + idPost + "/komentar")
                    .add(komentar1)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                        }
                    });
            textKomentar1.setText("");
            textKomentar2.requestFocus();
        } else if (!TextUtils.isEmpty(getKomentar2)) {
            textKomentar1.setVisibility(View.VISIBLE);
            textKomentar2.setVisibility(View.GONE);

            HashMap<String, Object> komentar2 = new HashMap<>();
            komentar2.put("user_id", user_id);
            komentar2.put("komentar", getKomentar2);
            komentar2.put("komentarTime", getTimestamp());

            firebaseFirestore.collection("posting/" + idPost + "/komentar")
                    .add(komentar2)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                        }
                    });
            textKomentar2.setText("");
            textKomentar1.requestFocus();
        }

    }

    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        return sdf.format(new Date());
    }
}
