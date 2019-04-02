package com.uirsos.www.uirsoskampus.Profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.uirsos.www.uirsoskampus.Adapter.AdapterHistory;
import com.uirsos.www.uirsoskampus.POJO.PostId;
import com.uirsos.www.uirsoskampus.POJO.Status_PostList;
import com.uirsos.www.uirsoskampus.R;
import com.uirsos.www.uirsoskampus.SignUp.LoginActivity;
import com.uirsos.www.uirsoskampus.SignUp.WelcomeLogin;
import com.uirsos.www.uirsoskampus.StatusInfo.MainActivity;
import com.uirsos.www.uirsoskampus.Verifikasi.VerifikasiAccount;
import com.uirsos.www.uirsoskampus.Utils.BottomNavigationHelper;
import com.uirsos.www.uirsoskampus.Utils.GridImageDecoration;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final int ACTIVITY_NUM = 1;

    //widget
    BottomNavigationViewEx defaultBottomNav, adminBottomNav;
    private CircleImageView imgProfile;
    private TextView textNpm, textNama, textGender, textFakultas;
    private RecyclerView listHistori;
    private List<Status_PostList> postHistory;
    private AdapterHistory adapterHistory;

    //firebase
    String gambarProfile;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id, email;
    private String TAG = "profileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        //widget
        imgProfile = findViewById(R.id.photoProfil);
        textNpm = findViewById(R.id.txtNPM);
        textNama = findViewById(R.id.txtNamaPengguna);
        textGender = findViewById(R.id.txtJenisKelamin);
        textFakultas = findViewById(R.id.txtFakultas);
        listHistori = findViewById(R.id.historyUser);
        defaultBottomNav = (BottomNavigationViewEx) findViewById(R.id.defaultBottom);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        loadProfile();

        postHistory = new ArrayList<>();
        adapterHistory = new AdapterHistory(postHistory);
        listHistori.setLayoutManager(new GridLayoutManager(this, 4));
        listHistori.addItemDecoration(new GridImageDecoration(getApplicationContext(), 2, 2, 2, 2));
        listHistori.setAdapter(adapterHistory);

        loadHistory();

        setupBottomNavigation();

    }

    private void loadHistory() {

        firebaseFirestore.collection("posting")
                .whereEqualTo("user_id", user_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("Profile", document.getId() + " pajako => " + document.getData());

                                Status_PostList dataitem = new Status_PostList().withId(document.getId());
                                dataitem.setUser_id(document.getString("user_id"));
                                dataitem.setGambar_posting(document.getString("gambar_posting"));
                                dataitem.setDeskripsi(document.getString("deskripsi"));
                                dataitem.setPostTime(document.getString("postTime"));
                                postHistory.add(dataitem);
                            }
                            adapterHistory.notifyDataSetChanged();
                        }
                    }
                });

    }

    private void loadProfile() {

        firebaseFirestore.collection("User").document(user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @SuppressLint("CheckResult")
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String nama = documentSnapshot.getString("nama_lengkap");
                    String npm = documentSnapshot.getString("NPM");
                    String prodi = documentSnapshot.getString("Prodi");
                    gambarProfile = documentSnapshot.getString("gambar_profile");

                    textNama.setText(nama);
                    textNpm.setText(npm);
                    textFakultas.setText(prodi);
                    RequestOptions placeholderrequest = new RequestOptions();
                    placeholderrequest.placeholder(R.drawable.defaulticon);
                    Glide.with(getApplicationContext())
                            .setDefaultRequestOptions(placeholderrequest)
                            .load(gambarProfile)
                            .into(imgProfile);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setupBottomNavigation() {
        //jika level bukan admin maka menu nya cuman ada menu home dan profile
        BottomNavigationHelper.setupBottomNavigationView(defaultBottomNav);
        BottomNavigationHelper.enableNavigation(ProfileActivity.this, defaultBottomNav);
        Menu menu = defaultBottomNav.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        getMenuInflater().inflate(R.menu.profile_menu, menu);

//        firebaseFirestore.collection("users").document(user_id).get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        String verifikasi = task.getResult().getString("verifikasi");
//
//                        if (!verifikasi.equals("valid")) {
//                            getMenuInflater().inflate(R.menu.verifikasi_menu, menu);
//                        } else {
//                            getMenuInflater().inflate(R.menu.profile_menu, menu);
//
//                        }
//
//                    }
//                });

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.accountSetting:

                Intent setupActivity = new Intent(ProfileActivity.this, SettingAccount.class);
                setupActivity.putExtra("update", 1);
                setupActivity.putExtra("nama", textNama.getText());
                setupActivity.putExtra("image_profile", gambarProfile);
                startActivity(setupActivity);

                return true;

            case R.id.Logout:

                logout();

                return true;

            default:

                return false;
        }

    }


    private void logout() {

        mAuth.signOut();
        finish();
        sendToLogin();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser == null) {
//            sendToLogin();
//        }
//    }

    private void sendToLogin() {

        Intent loginIntent = new Intent(ProfileActivity.this, WelcomeLogin.class);
        startActivity(loginIntent);
        finish();
    }

}
