package com.uirsos.www.uirsoskampus.StatusInfo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uirsos.www.uirsoskampus.R;

public class DetailBerita extends AppCompatActivity {

    private TextView textJudul, textTanggal, textInfo, textDeskripsi;
    private ImageView imageBerita, back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_berita);

        /*Variabel*/
        textJudul = findViewById(R.id.judul_Berita);
        textTanggal = findViewById(R.id.tanggalBerita);
        textInfo = findViewById(R.id.info_Berita);
        textDeskripsi = findViewById(R.id.tvDeskripsiBerita);
        imageBerita = findViewById(R.id.imgBerita);
        back = findViewById(R.id.backArrow);

        Intent get = getIntent();
        String judul = get.getStringExtra("getJudul");
        String info = get.getStringExtra("getInfo");
        String tanggal = get.getStringExtra("getTanggal");
        String deskripsi = get.getStringExtra("getDeskripsi");
        String imgBerita = get.getStringExtra("getImageBerita");

        textJudul.setText(judul);
        textTanggal.setText(tanggal);
        textInfo.setText(info);
        textDeskripsi.setText(deskripsi);
        Glide.with(getApplicationContext())
                .load(imgBerita)
                .into(imageBerita);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailBerita.super.onBackPressed();
            }
        });

    }

}
