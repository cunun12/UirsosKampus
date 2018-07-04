package com.uirsos.www.uirsoskampus.Profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rey.material.widget.Button;

import android.widget.EditText;

import android.widget.Spinner;

import android.widget.TextView;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.uirsos.www.uirsoskampus.R;
import com.uirsos.www.uirsoskampus.StatusInfo.MainActivity;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    TextWatcher npmEmailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

//            btnSend.setEnabled(!getNpm.isEmpty() && !getEmail.isEmpty());

        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };
    //widget
    private Button btnSimpan;
    private RadioGroup radioGroup;
    private EditText inputNama;
    private CircleImageView setupImage;
    private TextView setKelamin, kategoriFakultas;
    private String[] kategori = {
            "Fakultas Teknik",
            "Fakultas Hukum",
            "Fakultas Pertanian",
            "Fakultas Ilmu Keguruan dan Ilmu Pendidikan",
            "Fakultas Ilmu Sosial dan Politik",
            "Fakultas Agama Islam",
            "Fakultas Ilmu Komunikasi",
            "Fakultas Psikologi",
            "Fakultas Ekonomi"
    };

    private String TAG = "Setup";
    private Spinner spinner;
    private Uri mainImageURI = null;
    private boolean isChanged = false;
    private int requestCode = 1;

    private ProgressBar progressBar;

    /*widget firebase*/
    private String user_id;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;


    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        /*widgetFirebase*/
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user_id = FirebaseAuth.getInstance().getUid();

        //widget
        progressBar = findViewById(R.id.progressBar);
        btnSimpan = findViewById(R.id.btn_simpan);
        inputNama = findViewById(R.id.namaPengguna);
        setupImage = findViewById(R.id.imageProfil);
        setKelamin = findViewById(R.id.gender);
        setKelamin.setText(R.string.laki_laki);
        radioGroup = findViewById(R.id.radioJk);
        spinner = findViewById(R.id.kategorifakultas);
        kategoriFakultas = findViewById(R.id.fakultas_text);
        setSpinner();
        setImage();

        final Intent updateData = getIntent();
        final int update = updateData.getIntExtra("update", 0);
        String updateNama = updateData.getStringExtra("nama");
        String updateGender = updateData.getStringExtra("gender");
        String updateFakultas = updateData.getStringExtra("fakultas");
        String imageUser = updateData.getStringExtra("image_profile");
        Log.d(TAG, "onCreate: image " + imageUser);

        if (update == 1) {
            btnSimpan.setText("Update Data");
            inputNama.setText(updateNama);
            setKelamin.setText(updateGender);
            kategoriFakultas.setText(updateFakultas);
            kategoriFakultas.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.GONE);

            RadioButton laki = findViewById(R.id.laki_laki);
            RadioButton perempuan = findViewById(R.id.perempuan);

            if (updateGender.equals("Laki-laki")) {
                laki.setChecked(true);
            } else {
                perempuan.setChecked(true);
            }

            if (imageUser != null) {
                mainImageURI = Uri.parse(imageUser);
                RequestOptions placeholderrequest = new RequestOptions();
                placeholderrequest.placeholder(R.drawable.defaulticon);
                Glide.with(getApplicationContext())
                        .setDefaultRequestOptions(placeholderrequest)
                        .load(imageUser)
                        .into(setupImage);
            } else {
                mainImageURI = null;
                setupImage.setImageURI(mainImageURI);
            }

        }

        /*Simpan Data user ke Database*/
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (update == 1) {
                    UpdateData();
                } else {
                    TambahData();
                }
            }
        });
    }

    private void UpdateData() {

        final String nama = inputNama.getText().toString();
        final String jenisKelamin = setKelamin.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        if (isChanged) {

            user_id = mAuth.getCurrentUser().getUid();
            StorageReference imageUrl = storageReference.child("profile_image").child(user_id + ".jpg");
            imageUrl.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        updateStore(task, nama, jenisKelamin);
                    }
                }
            });

        } else {
            updateStore(null, nama, jenisKelamin);
        }

    }

    private void updateStore(Task<UploadTask.TaskSnapshot> task, String nama, String jenisKelamin) {

        Uri download_uri;
        if (task != null) {
            download_uri = task.getResult().getDownloadUrl();
        } else {
            download_uri = mainImageURI;
        }

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("nama_user", nama);
        updateMap.put("jenis_kelamin", jenisKelamin);
        updateMap.put("imagePic", String.valueOf(download_uri));

        firestore.collection("users").document(user_id).update(updateMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent mainActivity = new Intent(SetupActivity.this, ProfileActivity.class);
                            startActivity(mainActivity);
                            finish();
                            progressBar.setVisibility(View.GONE);

                        }
                    }
                });
    }

    /*Proses menambahkan data users*/
    private void TambahData() {

        /*get Intent dari RegisterActivity*/
        Intent registData = getIntent();
        final String npm = registData.getStringExtra("npm");
        final String email = registData.getStringExtra("email");
        final String password = registData.getStringExtra("password");
        /*data dari SetupActivity*/
        final String namaUser = inputNama.getText().toString();
        final String jenisKelamin = setKelamin.getText().toString();
        final String fakultas = kategoriFakultas.getText().toString();

        if (!TextUtils.isEmpty(namaUser) && mainImageURI != null) {
            user_id = mAuth.getCurrentUser().getUid();

            StorageReference imageUrl = storageReference.child("profile_image").child(user_id + ".jpg");
            imageUrl.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        addStoreFirebase(task, npm, email, namaUser, jenisKelamin, fakultas);
                    }
                }
            });

        }

    }

    private void addStoreFirebase(Task<UploadTask.TaskSnapshot> task, final String npm, final String email, final String namaUser, final String jenisKelamin, final String fakultas) {
        final Uri downloadUri;
        if (task != null) {
            downloadUri = task.getResult().getDownloadUrl();
        } else {
            downloadUri = mainImageURI;
        }

        String level = "pending";
        String verifikasi = "invalid";
        Map<String, String> userMap = new HashMap<>();
        userMap.put("npm", npm);
        userMap.put("email", email);
        userMap.put("nama_user", namaUser);
        userMap.put("jenis_kelamin", jenisKelamin);
        userMap.put("fakultas", fakultas);
        userMap.put("imagePic", String.valueOf(downloadUri));
        userMap.put("level", level);
        userMap.put("verifikasi", verifikasi);

        firestore.collection("users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    if (task.isSuccessful()) {
                        Intent mainActivity = new Intent(SetupActivity.this, MainActivity.class);
                        startActivity(mainActivity);
                        finish();
                        Toast.makeText(SetupActivity.this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SetupActivity.this, "Data gagal disimpan", Toast.LENGTH_SHORT).show();
                    }


                }

            }
        });

    }


    private void setImage() {

        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);

                    } else {

                        startCropImageActivity();

                    }
                } else {

                    startCropImageActivity();

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

                mainImageURI = result.getUri();
                setupImage.setImageURI(mainImageURI);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Toast.makeText(this, "why?" + error, Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void startCropImageActivity() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .setAutoZoomEnabled(true)
                .start(this);
    }


    private void setSpinner() {

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                String fakultas = kategori[position];
                kategoriFakultas.setText(fakultas);
                kategoriFakultas.setEnabled(true);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter<String> fakultas = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, kategori);
        fakultas.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(fakultas);
    }

    public void gender(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.laki_laki:
                if (checked) {
                    setKelamin.setText(R.string.laki_laki);
                    setKelamin.setEnabled(true);
                } else {
                    setKelamin.setEnabled(false);
                }
                break;

            case R.id.perempuan:
                if (checked) {
                    setKelamin.setText(R.string.perempuan);
                    setKelamin.setEnabled(true);
                } else {
                    setKelamin.setEnabled(false);
                }
                break;
        }
    }
}
