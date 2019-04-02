package com.uirsos.www.uirsoskampus.StatusInfo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import com.uirsos.www.uirsoskampus.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.content.ContentValues.TAG;

public class NewPostActivity extends AppCompatActivity implements View.OnClickListener {

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String getKet = inputKeterangan.getText().toString().trim();

            if (!getKet.isEmpty()) {
                btnSimpan.setEnabled(true);
            } else {
                btnSimpan.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    /*widget Firebase*/
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private String user_id;
    private String fakultas;
    private StorageReference storageReference;

    /*Widget layout*/
    private ImageView imageBack, imagePost, cancelImage;
    private CircleImageView imageProfile;
    private Button btnAddPhoto;
    private EditText inputKeterangan;
    private TextView namaUser, btnSimpan;
    private ProgressBar progressBar;
    private Uri postImageURI;
    private Bitmap compresImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        /*widget Layout*/
        imageBack = findViewById(R.id.btn_back);
        imageProfile = findViewById(R.id.img_Profile);
        imagePost = findViewById(R.id.image_post);
        imagePost.setVisibility(View.GONE);
        cancelImage = findViewById(R.id.cancel_image);
        btnAddPhoto = findViewById(R.id.btn_tambahPhoto);
        inputKeterangan = findViewById(R.id.text_Post);
        namaUser = findViewById(R.id.nama_Pengguna);
        btnSimpan = findViewById(R.id.btn_simpan);
        progressBar = findViewById(R.id.progressBar);

        /*widget Firebase*/
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user_id = mAuth.getCurrentUser().getUid();

        /*untuk mengambil profile dan nama*/
        firestore.collection("User").document(user_id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            String image = documentSnapshot.getString("gambar_profile");
//                          Log.d(TAG, "Ko data user: " + documentSnapshot.getData());
                            fakultas = documentSnapshot.getString("Fakultas");
                            namaUser.setText(documentSnapshot.getString("nama_lengkap"));

                            RequestOptions placeholderrequest = new RequestOptions();
                            placeholderrequest.placeholder(R.drawable.defaulticon);
                            Glide.with(getApplicationContext())
                                    .setDefaultRequestOptions(placeholderrequest)
                                    .load(image)
                                    .into(imageProfile);

                            Log.d(TAG, "ko fakultas : " + fakultas);
                        } else {
                            Log.d(TAG, "onEvent: Data Null");
                        }

                    }
                });

        inputKeterangan.addTextChangedListener(textWatcher);
        imageBack.setOnClickListener(this);
        btnAddPhoto.setOnClickListener(this);
        btnSimpan.setOnClickListener(this);
        cancelImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:

                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                finish();

                break;

            case R.id.cancel_image:
                if (imagePost != null) {
                    imagePost.setImageURI(null);
                    imagePost.setImageDrawable(null);
                    imagePost.setVisibility(View.GONE);
                    cancelImage.setVisibility(View.GONE);
                    btnSimpan.setEnabled(false);
                }
                break;

            case R.id.btn_simpan:
                addPost();
                break;

            case R.id.btn_tambahPhoto:

                CropImage.activity()
                        .start(NewPostActivity.this);

                break;

        }
    }

    private void addPost() {

        final String textKet = inputKeterangan.getText().toString();

        progressBar.setVisibility(View.VISIBLE);
        if (postImageURI != null) {

            String randomImage = UUID.randomUUID().toString();

            final StorageReference ref = storageReference.child("post_image").child(randomImage + ".jpg");
            UploadTask uploadTask = ref.putFile(postImageURI);

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
                        Uri downloadUrl = task.getResult();

                        Map<String, Object> postingMap = new HashMap<>();
                        postingMap.put("gambar_posting", String.valueOf(downloadUrl));
                        postingMap.put("deskripsi", textKet);
                        postingMap.put("user_id", user_id);
                        postingMap.put("fakultas", fakultas);
                        postingMap.put("postTime", getTimestamp());

                        firestore.collection("posting").add(postingMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                progressBar.setVisibility(View.GONE);
                                Intent beritaIntent = new Intent(NewPostActivity.this, MainActivity.class);
                                startActivity(beritaIntent);
                                finish();
                            }
                        });

                    } else {
                        Toast.makeText(NewPostActivity.this, "terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {

            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Maaf silakan masukan gambar sebelum memposting", Toast.LENGTH_SHORT).show();

        }

    }

    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        return sdf.format(new Date());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageURI = result.getUri();
                imagePost.setImageURI(postImageURI);
                if (imagePost != null) {
                    cancelImage.setVisibility(View.VISIBLE);
                    imagePost.setVisibility(View.VISIBLE);
                } else {
                    cancelImage.setVisibility(View.GONE);
                    imagePost.setVisibility(View.GONE);
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Toast.makeText(this, "why?" + error, Toast.LENGTH_SHORT).show();

            }
        }
    }
}
