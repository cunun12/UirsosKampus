package com.uirsos.www.uirsoskampus.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.uirsos.www.uirsoskampus.POJO.Status_PostList;
import com.uirsos.www.uirsoskampus.Profile.ViewPostActivity;
import com.uirsos.www.uirsoskampus.R;
import com.uirsos.www.uirsoskampus.Utils.SquareImageView;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by cunun12 on 02/04/2018.
 */

public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.HistoryHolder> {

    public List<Status_PostList> listHistory;
    public Context mContext;

    FirebaseFirestore firebaseFirestore;

    public AdapterHistory(List<Status_PostList> listHistory) {
        this.listHistory = listHistory;
    }

    @Override
    public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_imageview, parent, false);
        mContext = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new HistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryHolder holder, int position) {

        final String imageUrl = listHistory.get(position).getImage_post();
        final String desc = listHistory.get(position).getDesc();
        final String userid = listHistory.get(position).getUser_id();
        Log.d(TAG, "onBindViewHolder user: " + userid + " image : " + imageUrl);
        holder.setPostImage(imageUrl);


        holder.rela_Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                firebaseFirestore.collection("post_user").whereEqualTo("image_post", imageUrl)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (DocumentSnapshot doc : task.getResult()) {

                                Log.d(TAG, doc.getId() + "<=ID cobacoy: " + doc.getData());

                                final String postId = doc.getId();

                                firebaseFirestore.collection("Users").document(userid).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {

                                                    String userNamePost = task.getResult().getString("nama_user");
                                                    String imageUser = task.getResult().getString("image");

                                                    Log.d(TAG, "Bagian PostUser : " + imageUrl + " " + desc + " User : " + userNamePost + " " + imageUser);


                                                    Intent viewIntent = new Intent(v.getContext(), ViewPostActivity.class);
                                                    viewIntent.putExtra("postId", postId);
                                                    viewIntent.putExtra("userId", userid);
                                                    viewIntent.putExtra("username", userNamePost);
                                                    viewIntent.putExtra("imageuser", imageUser);
                                                    viewIntent.putExtra("imagepost", imageUrl);
                                                    viewIntent.putExtra("desc", desc);
                                                    v.getContext().startActivity(viewIntent);


                                                }
                                            }
                                        });

                            }
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
