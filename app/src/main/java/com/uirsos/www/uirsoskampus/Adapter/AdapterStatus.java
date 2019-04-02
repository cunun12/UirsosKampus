package com.uirsos.www.uirsoskampus.Adapter;

import android.annotation.SuppressLint;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
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
import com.uirsos.www.uirsoskampus.POJO.User;
import com.uirsos.www.uirsoskampus.Profile.FriendActivity;
import com.uirsos.www.uirsoskampus.Profile.ProfileActivity;
import com.uirsos.www.uirsoskampus.R;
import com.uirsos.www.uirsoskampus.StatusInfo.KomentarActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

/**
 * Created by cunun12 on 05/05/2018.
 */

public class AdapterStatus extends RecyclerView.Adapter<AdapterStatus.StatusHolder> {

    private static final String LOG_TAG = "Adapterstatus";
    public List<Status_PostList> postLists;
    Context context;

    RequestManager glide;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public AdapterStatus(List<Status_PostList> postList) {
        this.postLists = postList;
    }

    @NonNull
    @Override
    public AdapterStatus.StatusHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_status, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new StatusHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterStatus.StatusHolder holder, final int position) {

        holder.setIsRecyclable(false);

        final String postId = postLists.get(position).PostId;
        final String currentUserId = firebaseAuth.getUid();

        String desc_data = postLists.get(position).getDeskripsi();
        holder.setDescText(desc_data);

        String imageUrl = postLists.get(position).getGambar_posting();
        holder.setPostImage(imageUrl);

        final String user_id = postLists.get(position).getUser_id();

        assert currentUserId != null;

        /*untuk mengambil profile dan nama*/
        firebaseFirestore.collection("User").document(user_id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);

                            assert user != null;
                            String userName = user.getNama_lengkap();
                            String userImage = user.getGambar_profile();
                            holder.setUserData(userName, userImage);
                        } else {
                            Log.d(TAG, "onEvent: Data Null");
                        }

                    }
                });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        String postTimestamp = postLists.get(position).getPostTime();
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
            holder.setTime(postTimestamp);
        }


        /*membuat jumlah komentar*/
        firebaseFirestore.collection("posting/"+postId+"/komentar").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot docSnap, @Nullable FirebaseFirestoreException e) {
                if (docSnap != null){
                    int count = docSnap.size();
                    holder.countKomentar(count);
                } else {
                    holder.countKomentar(0);
                }
            }
        });

        /*Start Fitur Like*/

        //getCount
        firebaseFirestore.collection("posting/" + postId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (documentSnapshots != null) {

                    int count = documentSnapshots.size();
                    holder.updateLikeCount(count);

                } else {

                    holder.updateLikeCount(0);

                }

            }
        });

        //imageLike
        firebaseFirestore.collection("posting/" + postId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if (firebaseAuth.getCurrentUser() != null) {
                    if (e != null) {
                        Log.w(LOG_TAG, ":onEvent", e);
                        return;
                    }
                    if (documentSnapshot.exists()) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            holder.likeImage.setImageDrawable(context.getDrawable(R.mipmap.action_like_red));
                        }

                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            holder.likeImage.setImageDrawable(context.getDrawable(R.mipmap.action_like_grey));
                        }
                    }
                }
            }
        });

        //touch image
        holder.btnLikePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("posting/" + postId + "/Likes").document(currentUserId)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()) {

                            Map<String, Object> likeMap = new HashMap<>();
                            likeMap.put("likesTime", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("posting/" + postId + "/Likes").document(currentUserId).set(likeMap);

                        } else {

                            firebaseFirestore.collection("posting/" + postId + "/Likes").document(currentUserId).delete();

                        }
                    }
                });
            }
        });

        /*End Like*/

        holder.lineUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d(TAG, "onClickUser: " + user_id);
//                Toast.makeText(context, user_id, Toast.LENGTH_SHORT).show();
                String currentUser = firebaseAuth.getCurrentUser().getUid();
//                Log.d(TAG, "onClickActif: " + currentUser);

                if (!currentUser.equals(user_id)) {

                    Log.d(TAG, "onClickFriend: ");
                    Intent komentar = new Intent(v.getContext(), FriendActivity.class);
                    komentar.putExtra("idUser", user_id);
                    v.getContext().startActivity(komentar);
                } else {
                    Log.d(TAG, "onClickProfile: ");
                    Intent komentar = new Intent(v.getContext(), ProfileActivity.class);
                    komentar.putExtra("idUser", user_id);
                    v.getContext().startActivity(komentar);
                }
            }
        });


        /*Tombol Komentar*/

        holder.btnKomentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent komentar = new Intent(view.getContext(), KomentarActivity.class);
                komentar.putExtra("idPost", postLists.get(position).PostId);
                view.getContext().startActivity(komentar);
            }
        });

        /*End Komentar*/
    }


    @Override
    public int getItemCount() {
        return postLists.size();
    }

    public class StatusHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView descView, postDate, postUserName, postLikeCount, countKomentar;
        private ImageView postImage, likeImage;
        private CircleImageView postUserImage;

        private LinearLayout btnLikePost, btnKomentar;
        private LinearLayout lineUser;

        private StatusHolder(View itemView) {
            super(itemView);

            mView = itemView;
            btnLikePost = mView.findViewById(R.id.like_Pengguna);
            btnKomentar = mView.findViewById(R.id.post_komentar);
            likeImage = mView.findViewById(R.id.post_Like);

            lineUser = mView.findViewById(R.id.line_user);
        }

        private void setDescText(String text) {
            descView = mView.findViewById(R.id.desc_post);
            descView.setText(text);
        }

        private void setPostImage(String downloadUri) {

            postImage = mView.findViewById(R.id.post_image);
            Glide.with(context)
                    .load(downloadUri)
                    .into(postImage);

        }

        private void setTime(String date) {
            postDate = mView.findViewById(R.id.date_post);
            postDate.setText(date);
        }

        @SuppressLint("SetTextI18n")
        private void updateLikeCount(int count) {
            postLikeCount = mView.findViewById(R.id.post_likeCount);
            postLikeCount.setText(count + " Suka");
        }

        @SuppressLint("SetTextI18n")
        private void countKomentar(int count) {
            countKomentar = mView.findViewById(R.id.count_komentar);
            countKomentar.setText(count + " Komentar");
        }


        @SuppressLint("CheckResult")
        private void setUserData(String name, String image) {
            postUserName = mView.findViewById(R.id.nama_Pengguna);
            postUserImage = mView.findViewById(R.id.image_pic);

            postUserName.setText(name);

            RequestOptions placeholderrequest = new RequestOptions();
            placeholderrequest.placeholder(R.drawable.defaulticon);
            Glide.with(context.getApplicationContext())
                    .applyDefaultRequestOptions(placeholderrequest)
                    .load(image)
                    .into(postUserImage);
        }
    }
}
