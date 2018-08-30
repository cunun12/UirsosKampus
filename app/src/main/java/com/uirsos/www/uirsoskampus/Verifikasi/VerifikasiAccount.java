package com.uirsos.www.uirsoskampus.Verifikasi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.uirsos.www.uirsoskampus.Profile.ProfileActivity;
import com.uirsos.www.uirsoskampus.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class VerifikasiAccount extends AppCompatActivity implements View.OnClickListener {
    String komentar = "INGIN VERIFIKASI";
    private EditText inputNama;
    private LinearLayout bagianAdmin, bagianUser;
    private TextView btnPhoto, btnKirim;
    TextWatcher text = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String getInputNama = inputNama.getText().toString().toUpperCase();

            if (getInputNama.isEmpty()) {
                btnPhoto.setVisibility(View.VISIBLE);
                btnKirim.setVisibility(View.GONE);
            } else {
                btnPhoto.setVisibility(View.GONE);
                btnKirim.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private ProgressBar progressBar;
    private ImageView imageKTM;
    private Uri mainImageURI = null;
    private int requestCode = 0;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private String user_id;
    private UploadTask uploadTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verifikasi_account);

        bagianAdmin = findViewById(R.id.bagian_admin);
        bagianAdmin.setVisibility(View.GONE);
        bagianUser = findViewById(R.id.bagian_user);
        btnPhoto = findViewById(R.id.btn_photo);
        btnKirim = findViewById(R.id.btn_Kirim);
        imageKTM = findViewById(R.id.image_KTM);
        inputNama = findViewById(R.id.input_Nama);
        progressBar = findViewById(R.id.progressVerify);

        inputNama.addTextChangedListener(text);
        inputNama.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        btnPhoto.setOnClickListener(this);
        btnKirim.setOnClickListener(this);

        //firebase
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user_id = mAuth.getCurrentUser().getUid();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_Kirim:
                btnKirim.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                if (mainImageURI != null) {

                    prosesKirim();

                } else {

                    progressBar.setVisibility(View.GONE);
                    btnKirim.setVisibility(View.GONE);
                    btnPhoto.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Mohon lengkapi dengan memasuki gambar KTM", Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.btn_photo:
                if (ContextCompat.checkSelfPermission(VerifikasiAccount.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(VerifikasiAccount.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ActivityCompat.requestPermissions(VerifikasiAccount.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
                    }

                } else {

                    startCropImageActivity();

                }
                break;
        }
    }

    private void prosesKirim() {

        Intent npmFakultas = getIntent();
        final String npm = npmFakultas.getStringExtra("npm");
        final String fakultas = npmFakultas.getStringExtra("fakultas");
        final String nama = inputNama.getText().toString();

        Log.d("verifikasi", "prosesKirim: " + mainImageURI);
        Toast.makeText(this, "npm : " + npm + " nama : " + nama + " fakultas : " + fakultas, Toast.LENGTH_SHORT).show();

        final StorageReference imageVerify = storageReference.child("verify").child(user_id + ".jpg");
        uploadTask = imageVerify.putFile(mainImageURI);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageVerify.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    sendDatabase(task, npm, fakultas, nama);
                }
            }
        });


    }

    private void sendDatabase(Task<Uri> task, String npm, String fakultas, String nama) {

        final Uri downloadUri;
        if (task != null) {
            downloadUri = task.getResult();
        } else {
            downloadUri = mainImageURI;
        }

        Map<String, String> data = new HashMap<>();
        data.put("npm", npm);
        data.put("nama_pengguna", nama);
        data.put("fakultas", fakultas);
        data.put("image_ktm", String.valueOf(downloadUri));
        data.put("waktu", getTimestamp());
        data.put("komentar", komentar);

        firestore.collection("verifikasi").document(user_id).set(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        progressBar.setVisibility(View.GONE);
                        Intent backtoProfile = new Intent(VerifikasiAccount.this, ProfileActivity.class);
                        startActivity(backtoProfile);
                        finish();

                    }
                });
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

                mainImageURI = result.getUri();
                imageKTM.setImageURI(mainImageURI);
                if (imageKTM != null) {
                    btnKirim.setVisibility(View.VISIBLE);
                    btnPhoto.setVisibility(View.GONE);
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Toast.makeText(this, "why?" + error, Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void startCropImageActivity() {
        CropImage.activity()
//                .setCropShape(CropImageView.CropShape.OVAL)
                .setAllowFlipping(false)
                .setAutoZoomEnabled(true)
                .setActivityTitle("Gambar KTM")
                .start(this);
    }
}
