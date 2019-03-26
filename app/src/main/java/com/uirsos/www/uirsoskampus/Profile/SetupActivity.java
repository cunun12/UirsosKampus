package com.uirsos.www.uirsoskampus.Profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
import com.google.firebase.auth.FirebaseUser;
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
import com.uirsos.www.uirsoskampus.SignUp.WelcomeLogin;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    //widget
    private Button btnSimpan;
    private RadioGroup radioGroup;
    private EditText inputNama, inputEmail, inputPassword, inputRePassword;
    private CircleImageView setupImage;
    private TextView setKelamin, kategoriFakultas, textSetup;
    private LinearLayout linePassword, lineRepassword, lineEmail;
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
    String user;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;

    /*get Intent dari RegisterActivity*/
    String regNpm;

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
        textSetup = findViewById(R.id.textsetup);
        inputNama = findViewById(R.id.namaPengguna);
        inputEmail = findViewById(R.id.daftar_Email);
        inputPassword = findViewById(R.id.daftar_password);
        inputRePassword = findViewById(R.id.daftar_repassword);
        linePassword = findViewById(R.id.line_password);
        lineRepassword = findViewById(R.id.line_Re_password);
        lineEmail = findViewById(R.id.line_Email);
        setupImage = findViewById(R.id.imageProfil);
        setKelamin = findViewById(R.id.gender);
        setKelamin.setText(R.string.laki_laki);
        radioGroup = findViewById(R.id.radioJk);
        spinner = findViewById(R.id.kategorifakultas);
        kategoriFakultas = findViewById(R.id.fakultas_text);
        setSpinner();
        setImage();

        /*Bagian getIntent tambah users*/
        Intent addNpm = getIntent();
        regNpm = addNpm.getStringExtra("npm");


        /*bagian getIntent update users*/
        final Intent updateData = getIntent();
        final int update = updateData.getIntExtra("update", 0);
        String updateNama = updateData.getStringExtra("nama");
        String imageUser = updateData.getStringExtra("image_profile");
        Log.d(TAG, "onCreate: image " + imageUser);

        if (update == 1) {
            textSetup.setText("Data Profile");
            btnSimpan.setText("Change");
            inputNama.setText(updateNama);
            lineEmail.setVisibility(View.GONE);
            linePassword.setVisibility(View.GONE);
            lineRepassword.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);

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


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin untuk berhenti mendaftar?")
                .setCancelable(false)//tidak bisa tekan tombol back
                //jika pilih yess
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                //jika pilih no
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();

    }

    private void UpdateData() {

        final String nama = inputNama.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        if (isChanged) {

            user_id = mAuth.getCurrentUser().getUid();
            StorageReference imageUrl = storageReference.child("profile_image").child(user_id + ".jpg");
            imageUrl.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        updateStore(task, nama);
                    }
                }
            });

        } else {
            updateStore(null, nama);
        }

    }

    private void updateStore(Task<UploadTask.TaskSnapshot> task, String nama) {

        Uri download_uri;
        if (task != null) {
            download_uri = task.getResult().getUploadSessionUri();
        } else {
            download_uri = mainImageURI;
        }

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("nama_user", nama);
        updateMap.put("imagePic", String.valueOf(download_uri));

        firestore.collection("users").document(user_id).update(updateMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent profilActivity = new Intent(SetupActivity.this, ProfileActivity.class);
                            profilActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(profilActivity);
                            finish();
                            progressBar.setVisibility(View.GONE);

                        }
                    }
                });
    }

    /*Proses menambahkan data users*/
    private void createEmailPassword(final String namaUser, final String email, String password, final String fakultas, final String jenisKelamin) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmailPassword: success");
                    user = mAuth.getCurrentUser().getUid();

                    StorageReference imageUrl = storageReference.child("profile_image").child(user + ".jpg");
                    imageUrl.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                addStoreFirebase(task, regNpm, email, namaUser, jenisKelamin, fakultas);
                            }
                        }
                    });

//                    Map<String, String> datauser = new HashMap<>();
//                    datauser.put("npm", regNpm);
//                    datauser.put("email", email);
//                    firestore.collection("users").document(user).set(datauser).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            Toast.makeText(SetupActivity.this, "Tersimpan", Toast.LENGTH_SHORT).show();
//                        }
//                    });
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(SetupActivity.this, "Email Sudah terdaftar",
                            Toast.LENGTH_SHORT).show();
//                    updateUI(null);
                }
            }
        });
    }

    private void TambahData() {

        /*data dari SetupActivity*/
        final String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();
        String rePassword = inputRePassword.getText().toString();
        final String namaUser = inputNama.getText().toString();
        String jenisKelamin = setKelamin.getText().toString();
        String fakultas = kategoriFakultas.getText().toString();

        if (mainImageURI != null) {
            if (!namaUser.isEmpty() && !email.isEmpty() && !password.isEmpty() && !rePassword.isEmpty()) {
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    if (password.equals(rePassword)) {

                        createEmailPassword(namaUser, email, password, fakultas, jenisKelamin);

                    } else {

                        inputRePassword.requestFocus();
                        Toast.makeText(this, "Password Tidak Cocok", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    inputEmail.requestFocus();
                    inputEmail.setError("Masukan email dengan benar");
                }

            } else {
                Toast.makeText(this, "Mohon Lengkapi Data", Toast.LENGTH_SHORT).show();
            }

        } else {

            Toast.makeText(this, "Gambar tidak ditemukan", Toast.LENGTH_SHORT).show();

        }

    }

    private void addStoreFirebase(Task<UploadTask.TaskSnapshot> task, final String npm, final String email, final String namaUser, final String jenisKelamin, final String fakultas) {
        final Uri downloadUri;
        if (task != null) {
            downloadUri = task.getResult().getUploadSessionUri();
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

        firestore.collection("users").document(user).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    if (task.isSuccessful()) {

                        /*Allert pernyataan register berhasil*/
                        AlertDialog.Builder alert = new AlertDialog.Builder(SetupActivity.this);
                        alert.setTitle("Register Berhasil");
                        alert.setMessage("Silakan login untuk menggunakan aplikasi")
                                .setCancelable(false)
                                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                        Intent intent = new Intent(SetupActivity.this, WelcomeLogin.class);
                                        mAuth.signOut();
                                        finish();
                                    }
                                }).show();
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
