package com.uirsos.www.uirsoskampus.Verifikasi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.uirsos.www.uirsoskampus.Adapter.AdapaterVerify;
import com.uirsos.www.uirsoskampus.POJO.User;
import com.uirsos.www.uirsoskampus.POJO.Verify;
import com.uirsos.www.uirsoskampus.R;
import com.uirsos.www.uirsoskampus.SignUp.LoginActivity;
import com.uirsos.www.uirsoskampus.SignUp.LoginEmail;
import com.uirsos.www.uirsoskampus.StatusInfo.AdminActivity;
import com.uirsos.www.uirsoskampus.Utils.BottomNavigationHelper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class Verifikasi extends AppCompatActivity {

    private static final int ACTIVITY_NUM = 2;
    private static final String TAG = "Verifikasi";
    BottomNavigationViewEx adminBottomNav;
    String user_id;
    RecyclerView verifyRv;
    List<Verify> listVerify;
    List<User> listUser;
    AdapaterVerify verifyAdapter;
    LinearLayoutManager linear;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true; // untuk membuka halaman pertama diambil


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifikasi);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        adminBottomNav = (BottomNavigationViewEx) findViewById(R.id.adminNavbar);
        verifyRv = findViewById(R.id.rvVerifikasi);

        listVerify = new ArrayList<>();
        listUser = new ArrayList<>();

        verifyAdapter = new AdapaterVerify(listVerify, listUser);
        linear = new LinearLayoutManager(getApplicationContext());
        verifyRv.setLayoutManager(linear);
        verifyRv.setAdapter(verifyAdapter);

        viewVerifikasi();

        setupBottomNavigation();
    }

    private void viewVerifikasi() {

        firebaseFirestore.collection("Validasi").orderBy("waktu", Query.Direction.DESCENDING)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if (documentSnapshots.isEmpty()) {
                            Toast.makeText(Verifikasi.this, "Data masih kosong", Toast.LENGTH_SHORT).show();
                        } else {
                            if (e != null) {
                                Log.w(TAG, "listen:error", e);
                                return;
                            }

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    Log.d(TAG, "New city: " + doc.getDocument().getData());

                                    String verifyId = doc.getDocument().getId();

                                    final Verify verify = doc.getDocument().toObject(Verify.class).withId(verifyId);

                                    String user_id = doc.getDocument().getId();
                                    firebaseFirestore.collection("User").document(user_id)
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {

                                                String level = task.getResult().getString("Level");
                                                Log.d(TAG, "onComplete: " + level);

                                                if (level.equals("admin")) {
                                                    Log.d(TAG, "onComplete: " + level);
                                                } else {
                                                    User user = task.getResult().toObject(User.class);
                                                    listUser.add(user);
                                                    listVerify.add(verify);

                                                    verifyAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        }
                                    });
                                }
                            }

                        }
                    }
                });


    }

    private void setupBottomNavigation() {

        //jika level bukan admin maka menu nya cuman ada menu home dan profile
        BottomNavigationHelper.setupBottomNavigationView(adminBottomNav);
        BottomNavigationHelper.enableNavigationAdmin(Verifikasi.this, adminBottomNav);
        Menu menu = adminBottomNav.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intentLogin = new Intent(Verifikasi.this, LoginEmail.class);
            startActivity(intentLogin);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
