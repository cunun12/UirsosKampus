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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.uirsos.www.uirsoskampus.Profile.ProfileActivity;
import com.uirsos.www.uirsoskampus.Profile.SetupActivity;
import com.uirsos.www.uirsoskampus.R;
import com.uirsos.www.uirsoskampus.SignUp.LoginActivity;
import com.uirsos.www.uirsoskampus.SignUp.RegisterActivity;
import com.uirsos.www.uirsoskampus.Utils.BottomNavigationHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.uirsos.www.uirsoskampus.Utils.SectionPagerAdapter;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    private static final int ACTIVITY_NUM = 0;

    BottomNavigationViewEx defaultBottomNav, adminBottomNav;
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
        adminBottomNav = (BottomNavigationViewEx) findViewById(R.id.adminNavbar);
        adminBottomNav.setVisibility(View.GONE);

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
        view.addFragment(new Chat()); // fragment 0

        ViewPager viewPager = (ViewPager) findViewById(R.id.bodyContainer);
        viewPager.setAdapter(view);

        TabLayout tab = (TabLayout) findViewById(R.id.tabAtas);
        tab.setupWithViewPager(viewPager);

        tab.getTabAt(1).setText(getString(R.string.info));
        tab.getTabAt(2).setText(R.string.Chat);
        tab.getTabAt(0).setText(getString(R.string.status));

    }

    private void setupBottomNavigation() {

        firebaseFirestore.collection("users").document(user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d(TAG, "Current data: " + documentSnapshot.getData());

                    String level = documentSnapshot.getString("level");
                    assert level != null;
                    if (level.equals("admin")) {
                        defaultBottomNav.setVisibility(View.GONE);
                        adminBottomNav.setVisibility(View.VISIBLE);
                        BottomNavigationHelper.setupBottomNavigationView(adminBottomNav);
                        BottomNavigationHelper.enableNavigation(MainActivity.this, adminBottomNav);
                        Menu menu = adminBottomNav.getMenu();
                        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
                        menuItem.setChecked(true);
                    } else {
                        //jika level bukan admin maka menu nya cuman ada menu home dan profile
                        adminBottomNav.setVisibility(View.GONE);
                        defaultBottomNav.setVisibility(View.VISIBLE);
                        BottomNavigationHelper.setupBottomNavigationView(defaultBottomNav);
                        BottomNavigationHelper.enableNavigation(MainActivity.this, defaultBottomNav);
                        Menu menu = defaultBottomNav.getMenu();
                        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
                        menuItem.setChecked(true);
                    }

                } else {
                    Log.d(TAG, "Current data: null");
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToMain();
        } else {

            current_user_id = mAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("users").document(current_user_id).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {
                                if (!task.getResult().exists()) {

                                    Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                                    startActivity(setupIntent);
                                    finish();

                                } else {

                                }
                            } else {

                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(MainActivity.this, "Error + " + errorMessage, Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

        }
    }

    private void sendToMain() {
        Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intentLogin);
        finish();
    }

}
