package com.uirsos.www.uirsoskampus.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.uirsos.www.uirsoskampus.POJO.User;
import com.uirsos.www.uirsoskampus.POJO.Verify;
import com.uirsos.www.uirsoskampus.R;
import com.uirsos.www.uirsoskampus.Verifikasi.DetailVerifikasi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

/**
 * Created by cunun12 on 07/06/2018.
 */

public class AdapaterVerify extends RecyclerView.Adapter<AdapaterVerify.HolderVerify> {

    private List<Verify> listVerify;
    private List<User> listUser;
    private Context context;


    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public AdapaterVerify(List<Verify> listVerify, List<User> listUser) {
        this.listVerify = listVerify;
        this.listUser = listUser;
    }

    @NonNull
    @Override
    public HolderVerify onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_verifikasi, parent, false);
        context = parent.getContext();
        return new HolderVerify(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderVerify holder, int position) {

        holder.setIsRecyclable(false);

        final String image = listUser.get(position).getImagePic();
        final String namaPengguna = listUser.get(position).getNama_user();
        holder.setDataUser(namaPengguna, image);

        final String textVerify = listVerify.get(position).getKomentar();
        final String NPM = listVerify.get(position).getNpm();
        final String NamaKTM = listVerify.get(position).getNama_pengguna();
        final String imageKtm = listVerify.get(position).getImage_ktm();
        final String fakultas = listVerify.get(position).getFakultas();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        final String timeVerify = listVerify.get(position).getWaktu();
        Date timestamp;
        long detik = 0;
        long menit = 0;
        long jam = 0;
        long hari = 0;

        try {
            timestamp = sdf.parse(timeVerify);
            Date now = new Date();

            detik = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - timestamp.getTime());
            menit = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - timestamp.getTime());
            jam = TimeUnit.MILLISECONDS.toHours(now.getTime() - timestamp.getTime());
            hari = TimeUnit.MILLISECONDS.toDays(now.getTime() - timestamp.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (detik < 1) {
            Log.d(TAG, "onBindViewHolder: detik" + detik);
            holder.setTime("Baru");
        } else if (detik < 60) {
            Log.d(TAG, "onBindViewHolder: detik" + detik);
            String postDetik = String.valueOf(detik);
            holder.setTime(postDetik + " Detik yang Lalu");
        } else if (menit < 60) {
            Log.d(TAG, "onBindViewHolder: menit" + menit);
            String postMenit = String.valueOf(menit);
            holder.setTime(postMenit + " Menit yang lalu");
        } else if (jam < 24) {
            Log.d(TAG, "onBindViewHolder: jam" + jam);
            String postJam = String.valueOf(jam);
            holder.setTime(postJam + " Jam yang Lalu");
        } else if (hari < jam) {
            Log.d(TAG, "onBindViewHolder: hari" + hari);
            String postHari = String.valueOf(hari);
            holder.setTime(postHari + " Hari yang lalu");
        } else {
            holder.setTime(timeVerify);
        }

        final String verifyId = listVerify.get(position).PostId;
        holder.rlList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("adapterVerify", "onClick: " + verifyId + " " + imageKtm + " " + fakultas + " " + timeVerify);
                Intent detail = new Intent(view.getContext(), DetailVerifikasi.class);
                detail.putExtra("verifyId", verifyId);
                detail.putExtra("npm", NPM);
                detail.putExtra("image", image);
                detail.putExtra("imageKTM", imageKtm);
                detail.putExtra("nama", namaPengguna);
                detail.putExtra("namaKTM", NamaKTM);
                detail.putExtra("waktu", timeVerify);
                detail.putExtra("prodi", fakultas);
                detail.putExtra("komentar", textVerify);
                detail.putExtra("verifyId", verifyId);
                view.getContext().startActivity(detail);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listVerify.size();
    }

    class HolderVerify extends RecyclerView.ViewHolder {

        private View mView;
        private TextView textNamaPengguna, textTime;
        private CircleImageView imgPic;
        private RelativeLayout rlList;

        public HolderVerify(View itemView) {
            super(itemView);

            rlList = itemView.findViewById(R.id.relatif);
            mView = itemView;
        }

        public void setTime(String waktu) {
            textTime = mView.findViewById(R.id.waktu_permintaan);
            textTime.setText(waktu);
        }

        public void setDataUser(String nama, String image) {
            textNamaPengguna = mView.findViewById(R.id.nama_PenggunaVerify);
            textNamaPengguna.setText(nama);
            imgPic = mView.findViewById(R.id.image_Pic);

            RequestOptions placeholderrequest = new RequestOptions();
            placeholderrequest.placeholder(R.drawable.defaulticon);
            Glide.with(context)
                    .applyDefaultRequestOptions(placeholderrequest)
                    .load(image)
                    .into(imgPic);
        }
    }
}
