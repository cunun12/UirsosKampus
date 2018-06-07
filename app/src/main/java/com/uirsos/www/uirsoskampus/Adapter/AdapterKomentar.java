package com.uirsos.www.uirsoskampus.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uirsos.www.uirsoskampus.POJO.DataKomentar;
import com.uirsos.www.uirsoskampus.POJO.User;
import com.uirsos.www.uirsoskampus.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

/**
 * Created by cunun12 on 05/05/2018.
 */

public class AdapterKomentar extends RecyclerView.Adapter<AdapterKomentar.ViewKomentar>{

    private List<DataKomentar> listKomentar;
    private List<User> listUser;
    Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public AdapterKomentar(List<DataKomentar> listKomentar, List<User> listUser) {
        this.listKomentar = listKomentar;
        this.listUser = listUser;
    }

    @NonNull
    @Override
    public ViewKomentar onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_komentar, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewKomentar(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewKomentar holder, int position) {

        String getKomentar = listKomentar.get(position).getKomentar();
        holder.setKomentar(getKomentar);

        String username = listUser.get(position).getNama_user();
        String image = listUser.get(position).getImagePic();
        holder.setUser(username, image);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        String postTimestamp = listKomentar.get(position).getKomentarTime();
        Date timestamp;
        long detik = 0;
        long menit = 0;
        long jam = 0;
        long hari = 0;

        try {
            timestamp = sdf.parse(postTimestamp);
            Date now = new Date();

            detik = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - timestamp.getTime());
            menit = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - timestamp.getTime());
            jam = TimeUnit.MILLISECONDS.toHours(now.getTime() - timestamp.getTime());
            hari = TimeUnit.MILLISECONDS.toDays(now.getTime() - timestamp.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (detik<1) {
            Log.d(TAG, "onBindViewHolder: detik" + detik);
            holder.setTime("Baru");
        } else if (detik < 60 && detik>0) {
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
            holder.setTime(String.valueOf(postTimestamp));
        }
    }

    @Override
    public int getItemCount() {
        return listKomentar.size();
    }

    public class ViewKomentar extends RecyclerView.ViewHolder {

        private View mView;
        private TextView textKomentar, textWaktu, textUsername;
        private CircleImageView komentarPic;

        public ViewKomentar(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setUser(String username, String image){

            textUsername = mView.findViewById(R.id.nama_User);
            komentarPic  = mView.findViewById(R.id.image_pic);
            textUsername.setText(username);

            RequestOptions placeholderrequest = new RequestOptions();
            placeholderrequest.placeholder(R.drawable.defaulticon);
            Glide.with(context)
                    .applyDefaultRequestOptions(placeholderrequest)
                    .load(image)
                    .into(komentarPic);

        }

        public void setTime(String waktu){
            textWaktu = mView.findViewById(R.id.time_Komentar);
            textWaktu.setText(waktu);
        }

        public void setKomentar(String komentar){
            textKomentar = mView.findViewById(R.id.komentar_User);
            textKomentar.setText(komentar);
        }
    }
}
