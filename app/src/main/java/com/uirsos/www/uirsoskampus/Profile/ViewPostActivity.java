package com.uirsos.www.uirsoskampus.Profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.uirsos.www.uirsoskampus.Adapter.AdapterStatus;
import com.uirsos.www.uirsoskampus.POJO.Status_PostList;
import com.uirsos.www.uirsoskampus.POJO.User;
import com.uirsos.www.uirsoskampus.R;
import com.uirsos.www.uirsoskampus.StatusInfo.KomentarActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewPostActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = "ViewPost";
    /*widget*/
    CircleImageView profileImage;
    private TextView textNama, textWaktu, textDesc, postLikeCount, hapus;
    private ImageView postImage, likeImage, backIntent;
    private LinearLayout lineLike, lineKomentar;

    private RecyclerView rvPostUser;

    /*Firebase*/
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String currentUserId, userId;
    private String userImage, postUserImage, descImage, userName, postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        /*Widget*/
        profileImage = findViewById(R.id.imageProfil);
        textNama = findViewById(R.id.nama_Pengguna);
        textWaktu = findViewById(R.id.date_post);
        textDesc = findViewById(R.id.desc_post);
        postImage = findViewById(R.id.post_image);
        lineLike = findViewById(R.id.like_Pengguna);
        lineKomentar = findViewById(R.id.post_komentar);
        postLikeCount = findViewById(R.id.post_likeCount);
        likeImage = findViewById(R.id.post_Like);
        backIntent = findViewById(R.id.backArrow);
        hapus = findViewById(R.id.hapus);

//        rvPostUser = findViewById(R.id.rvPost);

        /*Firebase*/
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();

        Intent post = getIntent();
        userName = post.getStringExtra("username");
        userImage = post.getStringExtra("imageuser");
        postUserImage = post.getStringExtra("imagepost");
        descImage = post.getStringExtra("desc");
        postId = post.getStringExtra("postId");
        userId = post.getStringExtra("userId");

        textNama.setText(userName);
        textDesc.setText(descImage);

        Glide.with(getApplicationContext())
                .load(postUserImage)
                .into(postImage);


        lineKomentar.setOnClickListener(this);
        lineLike.setOnClickListener(this);
        backIntent.setOnClickListener(this);
        hapus.setOnClickListener(this);
    }

    private void likeToggle() {

            /*Start Fitur Like*/

        //getCount
        firebaseFirestore.collection("post_user/" + postId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (documentSnapshots != null) {

                    int count = documentSnapshots.size();
                    postLikeCount.setText(count + " " + "Likes");

                } else {

                    postLikeCount.setText(0);

                }

            }
        });

        //imageLike
        firebaseFirestore.collection("post_user/" + postId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if (firebaseAuth.getCurrentUser() != null) {
                    if (e != null) {
                        Log.w(LOG_TAG, ":onEvent", e);
                        return;
                    }
                    if (documentSnapshot.exists()) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            likeImage.setImageDrawable(getDrawable(R.mipmap.action_like_red));
                        }

                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            likeImage.setImageDrawable(getDrawable(R.mipmap.action_like_grey));
                        }
                    }
                }
            }
        });

        //touch image
        lineLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("post_user/" + postId + "/Likes").document(currentUserId)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()) {

                            Map<String, Object> likeMap = new HashMap<>();
                            likeMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("post_user/" + postId + "/Likes").document(currentUserId).set(likeMap);

                        } else {

                            firebaseFirestore.collection("post_user/" + postId + "/Likes").document(currentUserId).delete();

                        }
                    }
                });
            }
        });

    /*End Like*/

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.post_komentar:
                Intent i = new Intent(ViewPostActivity.this, KomentarActivity.class);
                i.putExtra("idPost", postId);
                startActivity(i);
                finish();
                break;

            case R.id.like_Pengguna:
                likeToggle();
                break;

            case R.id.backArrow:
                if (currentUserId.equals(userId)) {
                    Intent back = new Intent(ViewPostActivity.this, ProfileActivity.class);
                    back.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(back);
                    finish();
                } else {
                    Intent back = new Intent(ViewPostActivity.this, FriendActivity.class);
                    back.putExtra("idUser", userId);
                    back.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(back);
                    finish();
                }

                break;

            case R.id.hapus:
                if (currentUserId.equals(userId)) {
                    firebaseFirestore.collection("User_Post").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().exists()) {
                                Log.d(LOG_TAG, "Datanya: " + task.getResult().getData());

                                firebaseFirestore.collection("User_Post").document(postId).delete();
                                Intent iProfile = new Intent(ViewPostActivity.this, ProfileActivity.class);
                                startActivity(iProfile);
                                finish();
                            }
                        }
                    });
                }

                break;

        }
    }

    @SuppressLint("SetTextI18n")
    private void tanggalDate(String tanggal) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        String postTimestamp = tanggal;
        Date timestamp;
        long detik = 0;
        long menit = 0;
        long jam = 0;
        long hari = 0;

        try {
            timestamp = sdf.parse(postTimestamp);
            Date now = new Date();

            detik = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - timestamp.getTime());
            menit = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - timestamp.getTime());
            jam = TimeUnit.MILLISECONDS.toHours(now.getTime() - timestamp.getTime());
            hari = TimeUnit.MILLISECONDS.toDays(now.getTime() - timestamp.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (detik < 60) {
            Log.d(LOG_TAG, "onBindViewHolder: detik" + detik);
            String postDetik = String.valueOf(detik);
            textWaktu.setText(postDetik + " Detik yang lalu");
        } else if (menit < 60) {
            Log.d(LOG_TAG, "onBindViewHolder: menit" + menit);
            String postMenit = String.valueOf(menit);
            textWaktu.setText(postMenit + " Menit yang lalu");
        } else if (jam < 24) {
            Log.d(LOG_TAG, "onBindViewHolder: jam" + jam);
            String postJam = String.valueOf(jam);
            textWaktu.setText(postJam + " Jam yang lalu");
        } else if (hari < jam) {
            Log.d(LOG_TAG, "onBindViewHolder: hari" + hari);
            String postHari = String.valueOf(hari);
            textWaktu.setText(postHari + " Hari yang lalu");
        } else {
            textWaktu.setText(postTimestamp);
        }
    }
}
