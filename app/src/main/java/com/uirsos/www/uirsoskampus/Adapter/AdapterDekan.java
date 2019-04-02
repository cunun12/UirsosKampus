package com.uirsos.www.uirsoskampus.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.uirsos.www.uirsoskampus.POJO.PolaItemDekan;
import com.uirsos.www.uirsoskampus.R;

import java.util.List;

public class AdapterDekan extends RecyclerView.Adapter<AdapterDekan.DekanHolder> {

    Context context;
    List<PolaItemDekan> item;

    public AdapterDekan(Context context, List<PolaItemDekan> item) {
        this.context = context;
        this.item = item;
    }

    @NonNull
    @Override
    public DekanHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_dekan, null);
        return new DekanHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DekanHolder holder, int position) {
        holder.judulBerita.setText(item.get(position).getJudul_berita());
        holder.tglBerita.setText(item.get(position).getWaktu());

        Glide.with(context)
                .load(item.get(position).getGambar())
                .into(holder.imageBerita);
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    class DekanHolder extends RecyclerView.ViewHolder{

        /*
        Class ini untuk mengkasting sebuah variable untuk objek dengan id tertentu
        */
        ImageView imageBerita;
        TextView judulBerita,  tglBerita;

        public DekanHolder(View itemView) {
            super(itemView);

            imageBerita = itemView.findViewById(R.id.gambar_dekan);
            judulBerita = itemView.findViewById(R.id.judul_Berita);
            tglBerita = itemView.findViewById(R.id.tgl_berita);
        }
    }
}
