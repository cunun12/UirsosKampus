package com.uirsos.www.uirsoskampus.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
//import com.uirsos.www.uirsoskampus.StatusInfo.DetailBerita;
import com.codesgood.views.JustifiedTextView;
import com.uirsos.www.uirsoskampus.POJO.DataItemBerita;
import com.uirsos.www.uirsoskampus.R;
import com.uirsos.www.uirsoskampus.StatusInfo.DetailBerita;

import java.util.List;


/**
 * Created by cunun12
 */

public class AdapterBerita extends RecyclerView.Adapter<AdapterBerita.HolderBerita> {

    Context mContext;
    List<DataItemBerita> itemBerita;

    public AdapterBerita(List<DataItemBerita> itemBerita) {
        this.itemBerita = itemBerita;
    }

    @Override
    public HolderBerita onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_berita, parent, false);
        mContext = parent.getContext();
        return new HolderBerita(v);
    }

    @Override
    public void onBindViewHolder(HolderBerita holder, final int position) {

        holder.judulBerita.setText(itemBerita.get(position).getJudul());
        holder.deskripsiBerita.setText(itemBerita.get(position).getDeskripsi());
        holder.tanggalBerita.setText(itemBerita.get(position).getWaktu());
        holder.infoBerita.setText(itemBerita.get(position).getInfo());
        //        holder.tanggal.setText(itemBerita.get(position).getTanggal());
//        holder.fakultas.setText(itemBerita.get(position).getFakultas());
//        holder.judul.setText(itemBerita.get(position).getJudul());
//        holder.hari.setText(itemBerita.get(position).getHari());
//        holder.deskripsi.setText(itemBerita.get(position).getIsi_berita());
//
        Glide.with(mContext)
                .load(itemBerita.get(position).getImage())
                .into(holder.imageBerita);

        holder.mItemBerita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), DetailBerita.class);
                i.putExtra("getTanggal", itemBerita.get(position).getWaktu());
                i.putExtra("getJudul", itemBerita.get(position).getJudul());
                i.putExtra("getDeskripsi", itemBerita.get(position).getDeskripsi());
                i.putExtra("getInfo", itemBerita.get(position).getInfo());
                i.putExtra("getImageBerita", itemBerita.get(position).getImage());
                view.getContext().startActivity(i);

            }
        });

//        holder.mItemBerita.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(v.getContext(), DetailBerita.class);
//                i.putExtra("id_tanggal", itemBerita.get(position).getTanggal());
//                i.putExtra("id_hari", itemBerita.get(position).getHari());
//                i.putExtra("id_fakultas", itemBerita.get(position).getFakultas());
//                i.putExtra("id_judul", itemBerita.get(position).getJudul());
//                i.putExtra("id_deskripsi", itemBerita.get(position).getIsi_berita());
//                i.putExtra("id_img", itemBerita.get(position).getImg());
//                v.getContext().startActivity(i);
//            }
//        });


    }

    @Override
    public int getItemCount() {
        return itemBerita.size();
    }

    class HolderBerita extends RecyclerView.ViewHolder {

        /**
         * class ini untuk casting sebuah variable untuk object dengan id tertentu.
         *
         * @param itemView
         */

        private TextView infoBerita, judulBerita, tanggalBerita;
        private JustifiedTextView deskripsiBerita;
        private ImageView imageBerita;
        private CardView mItemBerita;

        public HolderBerita(View itemView) {
            super(itemView);

            mItemBerita = itemView.findViewById(R.id.itemBerita);
            imageBerita = itemView.findViewById(R.id.image_berita);
            infoBerita = itemView.findViewById(R.id.info_Berita);
            judulBerita = itemView.findViewById(R.id.judul_Berita);
            tanggalBerita = itemView.findViewById(R.id.waktu_berita);
            deskripsiBerita = itemView.findViewById(R.id.deskripsi_berita);

        }
    }
}
