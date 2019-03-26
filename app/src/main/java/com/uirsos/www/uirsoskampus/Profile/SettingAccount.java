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
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.uirsos.www.uirsoskampus.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingAccount extends AppCompatActivity implements View.OnClickListener {
    /*xml*/
    private EditText namaPengguna;
    private CircleImageView imageProfile;
    private String id_user;
    private boolean isChanged = false;

    /*firebase*/
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private StorageReference storage = FirebaseStorage.getInstance().getReference();
    private Uri profileUri = null;

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
                    String nama = documentSnapshot.getString("Nama Lengkap");
                    String profile = documentSnapshot.getString("gambar_profile");

                    if (profile != null){
                        profileUri = Uri.parse(profile);
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

        btnSimpan.setOnClickListener(this);
        imageProfile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageProfil:
                CropImage.activity()
                        .setFixAspectRatio(true)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(SettingAccount.this);
                break;

            case R.id.btnSimpan:
                final String namaLengkap = namaPengguna.getText().toString();
                if (isChanged){
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
                                    Uri downloadUri = task.getResult();
                                    UpdateProfile(downloadUri, namaLengkap);

                                }
                            }
                        });

                    } else {
                        Toast.makeText(this, "Gambar tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    UpdateProfile(null, namaLengkap);
                }

                break;


        }

    }

    private void UpdateProfile(Uri task, String namaLengkap) {
        Uri download_uri;

        if (task != null){
            download_uri =task;
        } else {
            download_uri = profileUri;
        }

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("Nama Lengkap", namaLengkap);
        updateData.put("gambar_profile", String.valueOf(download_uri));

        firestore.collection("User").document(id_user).update(updateData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SettingAccount.this, "Update Data Berhasil", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingAccount.this, "Ada kesalahan!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
}
