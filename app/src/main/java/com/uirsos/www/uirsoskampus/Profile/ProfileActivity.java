package com.uirsos.www.uirsoskampus.Profile;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.uirsos.www.uirsoskampus.Adapter.AdapterHistory;
import com.uirsos.www.uirsoskampus.Adapter.AdapterStatus;
import com.uirsos.www.uirsoskampus.POJO.Status_PostList;
import com.uirsos.www.uirsoskampus.R;
import com.uirsos.www.uirsoskampus.SignUp.LoginActivity;
import com.uirsos.www.uirsoskampus.StatusInfo.MainActivity;
import com.uirsos.www.uirsoskampus.Utils.BottomNavigationHelper;
import com.uirsos.www.uirsoskampus.Utils.GridImageDecoration;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final int ACTIVITY_NUM = 1;

    //widget
    private CircleImageView imgProfile;
    private TextView textNpm, textNama, textGender, textStatus, textFakultas;
    private ImageView statusLine;
    private RecyclerView listHistori;
    private List<Status_PostList> postHistory;
    private AdapterHistory adapterHistory;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;

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
        textStatus = findViewById(R.id.txtStatusUser);
        textFakultas = findViewById(R.id.txtFakultas);
        listHistori = findViewById(R.id.historyUser);
        statusLine = findViewById(R.id.image_button);

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

        statusLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentStatus = new Intent(ProfileActivity.this, StatusActivity.class);
                startActivity(intentStatus);
            }
        });

        setupBottomNavigation();

    }

    private void loadHistory() {

        firebaseFirestore.collection("post_user")
                .whereEqualTo("user_id", user_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("Profile", document.getId() + " pajako => " + document.getData());

                                Status_PostList dataitem = new Status_PostList();
                                dataitem.setUser_id(document.getString("user_id"));
                                dataitem.setImage_post(document.getString("image_post"));
                                dataitem.setDesc(document.getString("desc"));
                                postHistory.add(dataitem);
                            }
                            adapterHistory.notifyDataSetChanged();
                        }
                    }
                });

    }

    private void loadProfile() {

        firebaseFirestore.collection("Users").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            String npm = task.getResult().getString("npm");
                            String nama = task.getResult().getString("nama_user");
                            String gender = task.getResult().getString("jenis_kelamin");
                            String fakultas = task.getResult().getString("fakultas");
                            String image = task.getResult().getString("image");

                            textNpm.setText(npm);
                            textNama.setText(nama);
                            textGender.setText(gender);
                            textFakultas.setText(fakultas);

//                            RequestOptions placeholderrequest = new RequestOptions();
//                            placeholderrequest.placeholder(R.drawable.defaulticon);
//                            Glide.with(getApplicationContext())
//                                    .setDefaultRequestOptions(placeholderrequest)
//                                    .load(image)
//                                    .into(imgProfile);
                        }
                    }
                });

    }


    private void setupBottomNavigation() {

        BottomNavigationViewEx bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.bottomNavBar);
        BottomNavigationHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNavigationHelper.enableNavigation(ProfileActivity.this, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        firebaseFirestore.collection("Users").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String verifikasi = task.getResult().getString("verifikasi");
                        if (!verifikasi.equals("valid")) {
                            getMenuInflater().inflate(R.menu.verifikasi_menu, menu);
                        } else {

                            getMenuInflater().inflate(R.menu.profile_menu, menu);

                        }
                    }
                });


        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case R.id.verifikasi:

                Toast.makeText(this, "Verifikasi Data", Toast.LENGTH_SHORT).show();

                return true;
            case R.id.accountSetting:

                Toast.makeText(this, "Setting Account", Toast.LENGTH_SHORT).show();

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

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToLogin();
        }
    }

    private void sendToLogin() {

        Intent loginIntent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

}
