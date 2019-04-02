package com.uirsos.www.uirsoskampus.Profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.uirsos.www.uirsoskampus.SignUp.Validasi;
import com.uirsos.www.uirsoskampus.SignUp.WelcomeLogin;
import com.uirsos.www.uirsoskampus.StatusInfo.AdminActivity;
import com.uirsos.www.uirsoskampus.R;
import com.uirsos.www.uirsoskampus.StatusInfo.MainActivity;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingAccount extends AppCompatActivity implements View.OnClickListener {
    /*xml*/
    private EditText namaPengguna;
    private CircleImageView imageProfile;
    private String id_user;
    private boolean isChange = false;

    /*firebase*/
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private StorageReference storage = FirebaseStorage.getInstance().getReference();
    private Uri profileUri, downloadUrl;
    String imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingaccount);

        /*XML*/
        namaPengguna = findViewById(R.id.nama_pengguna);
        imageProfile = findViewById(R.id.imageProfil);
        Button btnSimpan = findViewById(R.id.btnSimpan);

        /*firebase*/
        mAuth = FirebaseAuth.getInstance();
        id_user = mAuth.getCurrentUser().getUid();
        Button logout = findViewById(R.id.btnLogout);

        Intent datauser = getIntent();
        final int update = datauser.getIntExtra("update", 0);
        if (update == 1) {
            btnSimpan.setText("Change");
            btnSimpan.setEnabled(true);
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
            }
        });

        firestore.collection("User").document(id_user).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @SuppressLint("CheckResult")
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("SettingAccount", "Listen failed.", e);
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String nama = documentSnapshot.getString("nama_lengkap");
                    String profile = documentSnapshot.getString("gambar_profile");

                    if (profile != null) {
                        imageUri = profile;
                        RequestOptions placeholderrequest = new RequestOptions();
                        placeholderrequest.placeholder(R.drawable.defaulticon);
                        Glide.with(getApplicationContext())
                                .setDefaultRequestOptions(placeholderrequest)
                                .load(profile)
                                .into(imageProfile);
                    } else {
                        profileUri = null;
                        imageProfile.setImageURI(profileUri);
                    }

                    namaPengguna.setText(nama);
                }
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (update == 1) {
                    updateData();
                } else {
                    tambahData();
                }

            }
        });
        imageProfile.setOnClickListener(this);
    }

    /*Bagian Tambah*/
    private void tambahData() {
        final String namaLengkap = namaPengguna.getText().toString();

        if (profileUri != null) {

            final StorageReference ref = storage.child("profile_image").child(id_user + ".jpg");
            UploadTask uploadTask = ref.putFile(profileUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        downloadUrl = task.getResult();
                        tambahProfile(downloadUrl, namaLengkap);
                    }
                }
            });
        } else {
            tambahProfile(Uri.parse(imageUri), namaLengkap);
        }
    }

    private void tambahProfile(Uri downloadUrl, String namaLengkap) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("nama_lengkap", namaLengkap);
        updateData.put("gambar_profile", String.valueOf(downloadUrl));

        firestore.collection("User").document(id_user).update(updateData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    Intent mainActivity = new Intent(SettingAccount.this, MainActivity.class);
                    startActivity(mainActivity);
                    finish();

                } else {
                    Toast.makeText(SettingAccount.this, "Ada kesalahan!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*End*/

    /*Bagian Update*/
    private void updateData() {

        final String namaLengkap = namaPengguna.getText().toString();
        if (isChange) {

            final StorageReference ref = storage.child("profile_image").child(id_user + ".jpg");
            UploadTask uploadTask = ref.putFile(profileUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        downloadUrl = task.getResult();
                        UpdateProfile(downloadUrl, namaLengkap);
                    }
                }
            });
        } else {
            UpdateProfile(Uri.parse(imageUri), namaLengkap);
        }
    }

    private void UpdateProfile(Uri task, String nama) {

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("nama_lengkap", nama);
        updateData.put("gambar_profile", String.valueOf(task));

        firestore.collection("User").document(id_user).update(updateData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    pilihMenuUpdate();

                } else {
                    Toast.makeText(SettingAccount.this, "Ada kesalahan!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void pilihMenuUpdate() {

        Intent mainActivity = new Intent(SettingAccount.this, ProfileActivity.class);
        startActivity(mainActivity);
        finish();

    }
    /*End*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageProfil:
                CropImage.activity()
                        .setFixAspectRatio(true)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(SettingAccount.this);
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                profileUri = result.getUri();
                imageProfile.setImageURI(profileUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Toast.makeText(this, "Why?" + error, Toast.LENGTH_SHORT).show();
            }
        }

    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        FirebaseUser current_user = mAuth.getCurrentUser();
//        if (current_user != null) {
//
//            String id_user = mAuth.getCurrentUser().getUid();
//            final String emailPengguna = mAuth.getCurrentUser().getEmail();
//
//            firestore.collection("User").document(id_user).addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                @Override
//                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                    if (documentSnapshot != null && documentSnapshot.exists()) {
//
//                        String gambar = documentSnapshot.getString("gambar_profile");
//                        String level = documentSnapshot.getString("Level");
//                        String nama = documentSnapshot.getString("nama_lengkap");
//
//                        if (gambar != null) {
//                            if (level.equals("admin")) {
//                                Intent adminActivity = new Intent(SettingAccount.this, AdminActivity.class);
//                                startActivity(adminActivity);
//                                finish();
//                            } else if (level.equals("mahasiswa")) {
//                                Intent mainActivity = new Intent(SettingAccount.this, MainActivity.class);
//                                startActivity(mainActivity);
//                                finish();
//                            } else {
//
//                                Intent penggunaActivity = new Intent(SettingAccount.this, Validasi.class);
//                                penggunaActivity.putExtra("namaLengkap", nama);
//                                penggunaActivity.putExtra("email", emailPengguna);
//                                startActivity(penggunaActivity);
//                                finish();
//
//                            }
//                        } else {
//                            Intent setting = new Intent(SettingAccount.this, SettingAccount.class);
//                            startActivity(setting);
//                            finish();
//                        }
//
//                    }
//                }
//            });
//
//        } else {
//            Intent welcomeLogin = new Intent(SettingAccount.this, WelcomeLogin.class);
//            startActivity(welcomeLogin);
//            finish();
//        }
//    }
}
