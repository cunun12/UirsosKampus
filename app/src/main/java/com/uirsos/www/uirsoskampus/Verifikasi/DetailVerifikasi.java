package com.uirsos.www.uirsoskampus.Verifikasi;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uirsos.www.uirsoskampus.POJO.Verify;
import com.uirsos.www.uirsoskampus.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class DetailVerifikasi extends AppCompatActivity implements View.OnClickListener {

    LinearLayout bagianAdmin, bagianUser;
    CircleImageView imageUser;
    ImageView ktmImage, buttonBack;
    TextView namaUser, textNPM, textNama, textFakultas, textProdi;
    Button btnCancel, btnVerify;
    AlertDialog dialog;
    String user_id;
    String getVerifyId;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth fAuth;
    StorageReference reference;
    private String TAG = "DetailVerifikasi";
    List<Verify> listVerify;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verifikasi_account);

        bagianAdmin = findViewById(R.id.bagian_admin);
        bagianAdmin.setVisibility(View.VISIBLE);
        bagianUser = findViewById(R.id.bagian_user);
        bagianUser.setVisibility(View.GONE);
        btnCancel = findViewById(R.id.cancel_Verify);
        btnVerify = findViewById(R.id.terima_Verify);
        imageUser = findViewById(R.id.image_user);
        namaUser = findViewById(R.id.nama_user);
        ktmImage = findViewById(R.id.image_KtmVerify);
        textNPM = findViewById(R.id.text_NPM);
        textNama = findViewById(R.id.text_NamaPengguna);
        textFakultas = findViewById(R.id.text_fakultas);
        textProdi = findViewById(R.id.text_prodi);
        buttonBack = findViewById(R.id.backArrow);

        /*firebase*/
        firebaseFirestore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        reference = FirebaseStorage.getInstance().getReference();

        /*diambil dari adapter*/
        Intent data = getIntent();
        String nama = data.getStringExtra("nama");
        String image = data.getStringExtra("image");
        String getNPM = data.getStringExtra("npm");
        String getNama = data.getStringExtra("namaKTM");
        String getImageKTM = data.getStringExtra("imageKTM");
        String getFakultas = data.getStringExtra("fakultas");
        String getProdi = data.getStringExtra("prodi");
        getVerifyId = data.getStringExtra("verifyId");
        Log.d(TAG, "onCreate: coba check" + getVerifyId);
        listVerify = new ArrayList<>();

        namaUser.setText(nama);
        textNPM.setText(getNPM);
        textNama.setText(getNama);
        textFakultas.setText(getFakultas);
        textProdi.setText(getProdi);

        Glide.with(this)
                .load(getImageKTM)
                .into(ktmImage);

        Glide.with(this)
                .load(image)
                .into(imageUser);

        buttonBack.setOnClickListener(this);
        btnVerify.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.backArrow:
                Intent back = new Intent(this, Verifikasi.class);
                startActivity(back);
                finish();
                break;

            /*Perintah ini untuk memberikan informasi verify gagal*/
            case R.id.cancel_Verify:

                final AlertDialog.Builder a = new AlertDialog.Builder(DetailVerifikasi.this);
                a.setTitle("Cancel Verifikasi");
                final View mView = getLayoutInflater().inflate(R.layout.dialog_cancelverify, null);
                final RadioGroup rgVerify = mView.findViewById(R.id.rgCancelVerify);

                user_id = fAuth.getUid();
                Button btnProses = mView.findViewById(R.id.btn_proses);

                btnProses.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int selectedId = rgVerify.getCheckedRadioButtonId();
                        RadioButton rbMessage = mView.findViewById(selectedId);
                        final String message = rbMessage.getText().toString();
                        String npm = textNPM.getText().toString();
                        String invalid = "invalid";

                        Map<String, String> data = new HashMap<>();
                        data.put("message", message);
                        data.put("npm", npm);
                        data.put("validasi", invalid);

                        Log.d("VErifikasi", "onClick: " + getVerifyId);
                        firebaseFirestore.collection("Validasi").document(getVerifyId).set(data)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        StorageReference storage = reference.child("gambar_ktm/" + getVerifyId + ".jpg");
                                        Log.d(TAG, "onComplete: itu storage " + storage);
                                        storage.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                Intent i = new Intent(DetailVerifikasi.this, Verifikasi.class);
                                                startActivity(i);
                                                finish();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                String error = e.getMessage();

                                                Log.d(TAG, "onFailure: " + error);
                                            }
                                        });

                                    }
                                });
                        dialog.dismiss();
                    }
                });

                a.setView(mView);
                dialog = a.create();
                dialog.show();

                break;

            /*perintah ini untuk user yang verify berhasil*/
            case R.id.terima_Verify:

                final AlertDialog.Builder t = new AlertDialog.Builder(DetailVerifikasi.this);
                t.setMessage("Terima Verifikasi")
                        .setPositiveButton("Terima", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Log.d(TAG, "onClick: VerifyId " + getVerifyId);

                                firebaseFirestore.collection("Validasi").document(getVerifyId)
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                listVerify.clear();

                                                Intent verifikasi = new Intent(DetailVerifikasi.this, Verifikasi.class);
                                                Toast.makeText(DetailVerifikasi.this, "Verifikasi Berhasil", Toast.LENGTH_SHORT).show();
                                                verifikasi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(verifikasi);
                                                finish();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        String error = e.getMessage();

                                        Log.d(TAG, "onFailure: " + error);
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Tunggu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                dialog = t.create();
                dialog.show();

                break;
        }
    }
}
