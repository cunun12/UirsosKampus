package com.uirsos.www.uirsoskampus.StatusInfo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.uirsos.www.uirsoskampus.Profile.SettingAccount;
import com.uirsos.www.uirsoskampus.R;
import com.uirsos.www.uirsoskampus.SignUp.LoginEmail;
import com.uirsos.www.uirsoskampus.Utils.BottomNavigationHelper;
import com.uirsos.www.uirsoskampus.Utils.SectionPagerAdapter;

public class AdminActivity extends AppCompatActivity {

    private static final int ACTIVITY_NUM = 0;

    BottomNavigationViewEx adminBottomNav;
    String user_id, current_user_id;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = mAuth.getUid();

        adminBottomNav = (BottomNavigationViewEx) findViewById(R.id.adminNavbar);

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
        //jika level bukan admin maka menu nya cuman ada menu home dan profile
        BottomNavigationHelper.setupBottomNavigationView(adminBottomNav);
        BottomNavigationHelper.enableNavigationAdmin(AdminActivity.this, adminBottomNav);
        Menu menu = adminBottomNav.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToMain();
        } else {

            current_user_id = mAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("User").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        String gambar = task.getResult().getString("gambar_profile");
                        if (gambar == null) {
                            Intent setting = new Intent(AdminActivity.this, SettingAccount.class);
                            startActivity(setting);
                            finish();
                        }
                    }
                }
            });


        }
    }


    private void sendToMain() {
        Intent intentLogin = new Intent(AdminActivity.this, LoginEmail.class);
        startActivity(intentLogin);
        finish();
    }
}
