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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uirsos.www.uirsoskampus.Profile.ProfileActivity;
import com.uirsos.www.uirsoskampus.R;
import com.uirsos.www.uirsoskampus.SignUp.LoginActivity;
import com.uirsos.www.uirsoskampus.Utils.BottomNavigationHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.uirsos.www.uirsoskampus.Utils.SectionPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private static final int ACTIVITY_NUM = 0;

    BottomNavigationViewEx defaultBottomNav, adminBottomNav;
    String user_id;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

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
        view.addFragment(new StatusFragment());// fragment 1
        view.addFragment(new InfoFragment());//fragment 0

        ViewPager viewPager = (ViewPager) findViewById(R.id.bodyContainer);
        viewPager.setAdapter(view);

        TabLayout tab = (TabLayout) findViewById(R.id.tabAtas);
        tab.setupWithViewPager(viewPager);

        tab.getTabAt(1).setText(getString(R.string.info));
        tab.getTabAt(0).setText(getString(R.string.status));

    }

    private void setupBottomNavigation() {

        firebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String level = task.getResult().getString("level");
                Log.d("MainActivity", "onComplete: adminlevel" + level);
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
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intentLogin);
            finish();
        }
    }

}
