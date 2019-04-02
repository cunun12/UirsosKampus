package com.uirsos.www.uirsoskampus.StatusInfo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.uirsos.www.uirsoskampus.Profile.ProfileActivity;
import com.uirsos.www.uirsoskampus.Profile.SettingAccount;
import com.uirsos.www.uirsoskampus.Profile.SetupActivity;
import com.uirsos.www.uirsoskampus.R;
import com.uirsos.www.uirsoskampus.SignUp.LoginActivity;
import com.uirsos.www.uirsoskampus.SignUp.LoginEmail;
import com.uirsos.www.uirsoskampus.SignUp.RegisterActivity;
import com.uirsos.www.uirsoskampus.SignUp.Validasi;
import com.uirsos.www.uirsoskampus.Utils.BottomNavigationHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.uirsos.www.uirsoskampus.Utils.SectionPagerAdapter;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    private static final int ACTIVITY_NUM = 0;

    BottomNavigationViewEx defaultBottomNav;
    String user_id, current_user_id;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = mAuth.getUid();

        defaultBottomNav = (BottomNavigationViewEx) findViewById(R.id.defaultBottom);

        setupViewPager();

        if (mAuth.getCurrentUser() != null) {
            setupBottomNavigation();
        }


    }

    /**
     * viewpager berita dan status
     */
    private void setupViewPager() {
        SectionPagerAdapter view = new SectionPagerAdapter(getSupportFragmentManager());
        view.addFragment(new StatusFragment());// fragment 2
        view.addFragment(new InfoFragment());//fragment 1
//        view.addFragment(new Chat()); // fragment 0

        ViewPager viewPager = (ViewPager) findViewById(R.id.bodyContainer);
        viewPager.setAdapter(view);

        TabLayout tab = (TabLayout) findViewById(R.id.tabAtas);
        tab.setupWithViewPager(viewPager);

        tab.getTabAt(1).setText(getString(R.string.info));
//        tab.getTabAt(2).setText(R.string.Chat);
        tab.getTabAt(0).setText(getString(R.string.status));

    }

    private void setupBottomNavigation() {
        //jika level bukan admin maka menu nya cuman ada menu home dan profile
        BottomNavigationHelper.setupBottomNavigationView(defaultBottomNav);
        BottomNavigationHelper.enableNavigation(MainActivity.this, defaultBottomNav);
        Menu menu = defaultBottomNav.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToMain();
        } else {

            current_user_id = mAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("Validasi").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        String validasi = task.getResult().getString("validasi");
                        if (validasi != null && validasi.equals("invalid")) {

                            firebaseFirestore.collection("User").document(current_user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot != null && documentSnapshot.exists()){
                                        String email = documentSnapshot.getString("email");
                                        String namaLengkap = documentSnapshot.getString("nama_lengkap");

                                        Intent mainValid = new Intent(MainActivity.this, Validasi.class);
                                        mainValid.putExtra("valid", 1);
                                        mainValid.putExtra("namaLengkap", namaLengkap);
                                        mainValid.putExtra("email", email);
                                        startActivity(mainValid);
                                        finish();
                                    }
                                }
                            });

                        }
                    } else {

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "tidak ditemukan Koneksi internet", Toast.LENGTH_SHORT).show();
                }
            });


        }
    }

    private void sendToMain() {
        Intent intentLogin = new Intent(MainActivity.this, LoginEmail.class);
        startActivity(intentLogin);
        finish();
    }

}
