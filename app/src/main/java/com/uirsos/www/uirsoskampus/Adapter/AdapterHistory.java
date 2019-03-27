package com.uirsos.www.uirsoskampus.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.uirsos.www.uirsoskampus.POJO.Status_PostList;
import com.uirsos.www.uirsoskampus.Profile.ViewPostActivity;
import com.uirsos.www.uirsoskampus.R;
import com.uirsos.www.uirsoskampus.StatusInfo.KomentarActivity;
import com.uirsos.www.uirsoskampus.Utils.SquareImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by cunun12 on 02/04/2018.
 */

public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.HistoryHolder> {

    private static final String LOG_TAG = "AdapterHistory";
    private List<Status_PostList> listHistory;
    private Context mContext;


    private FirebaseFirestore firebaseFirestore;

    private FirebaseAuth firebaseAuth;

    public AdapterHistory(List<Status_PostList> listHistory) {
        this.listHistory = listHistory;
    }

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_imageview, parent, false);
        mContext = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new HistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HistoryHolder holder, final int position) {

        holder.setIsRecyclable(false);

        final String postId = listHistory.get(position).PostId;
        Log.d(TAG, "onBindViewHolder: postid" +postId);

        final String imageUrl = listHistory.get(position).getGambar_posting();
        final String desc = listHistory.get(position).getDeskripsi();
        final String postTime = listHistory.get(position).getPostTime();
        Log.d(TAG, "onBindViewHolder: deskripsi " + desc);
        final String userid = listHistory.get(position).getUser_id();
        holder.setPostImage(imageUrl);


        holder.rela_Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {


                firebaseFirestore.collection("User").document(userid).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {

                                    String userNamePost = task.getResult().getString("nama_lengkap");
                                    String imageUser = task.getResult().getString("gambar_profile");

                                    Intent viewIntent = new Intent(v.getContext(), ViewPostActivity.class);
                                    viewIntent.putExtra("postId", postId);
                                    viewIntent.putExtra("userId", userid);
                                    viewIntent.putExtra("username", userNamePost);
                                    viewIntent.putExtra("imageuser", imageUser);
                                    viewIntent.putExtra("imagepost", imageUrl);
                                    viewIntent.putExtra("posttime", postTime);
                                    viewIntent.putExtra("desc", desc);
                                    v.getContext().startActivity(viewIntent);


                                }
                            }
                        });

            }
        });


    }

    @Override
    public int getItemCount() {
        return listHistory.size();
    }

    public class HistoryHolder extends RecyclerView.ViewHolder {

        RelativeLayout rela_Post;
        private View iView;
        private SquareImageView postImage;


        public HistoryHolder(View itemView) {
            super(itemView);

            iView = itemView;

            rela_Post = iView.findViewById(R.id.relapost);
        }


        public void setPostImage(final String downloadUri) {
            postImage = iView.findViewById(R.id.image_grid);
            Glide.with(mContext)
                    .load(downloadUri)
                    .into(postImage);

        }
    }
}
