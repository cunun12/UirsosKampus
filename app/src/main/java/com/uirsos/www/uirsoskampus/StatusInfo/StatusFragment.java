package com.uirsos.www.uirsoskampus.StatusInfo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uirsos.www.uirsoskampus.Adapter.AdapterStatus;
import com.uirsos.www.uirsoskampus.POJO.Status_PostList;
import com.uirsos.www.uirsoskampus.POJO.User;
import com.uirsos.www.uirsoskampus.R;
import com.uirsos.www.uirsoskampus.SignUp.RegisterActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatusFragment extends Fragment {

    /*List Data*/
    List<Status_PostList> postLists;
    AdapterStatus adapterStatus;
    private RecyclerView listStatus;
    private FloatingActionButton btnAddPost;
    /*widget Firebase*/
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String users_id;
    private String fakultas_users;
    private LinearLayout linearNoPost;
    private TextView textFakultas;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true; // untuk membuka halaman pertama diambil

    public StatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_status, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listStatus = view.findViewById(R.id.statusPost);
        btnAddPost = view.findViewById(R.id.btn_Post);
        linearNoPost = view.findViewById(R.id.noPost);
        textFakultas = view.findViewById(R.id.txtFakultas);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        users_id = firebaseAuth.getCurrentUser().getUid();

        btnAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent postIntent = new Intent(getActivity(), NewPostActivity.class);
                getActivity().startActivity(postIntent);
            }
        });

        postLists = new ArrayList<>();
        adapterStatus = new AdapterStatus(postLists);
        listStatus.setLayoutManager(new LinearLayoutManager(getContext()));
        listStatus.setAdapter(adapterStatus);

        if (firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore.collection("users").document(users_id)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {

                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }

                            if (documentSnapshot != null && documentSnapshot.exists()) {

                                fakultas_users = documentSnapshot.getString("fakultas");
//                                Log.d(TAG, "ko fakultas : " + fakultas_users);
                                dataView();

                            } else {
                                Log.d(TAG, "onEvent: Data Null");
                            }

                        }
                    });

        }

    }

    @SuppressLint("NewApi")
    private void dataView() {

        listStatus.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean rechadBottom = !recyclerView.canScrollVertically(1);

                if (rechadBottom) {
                    if (firebaseAuth.getCurrentUser() != null) {

                        loadMorePost();

                    }

                }
            }
        });

        Query firstPost = firebaseFirestore.collection("posting")
                .orderBy("postTime", Query.Direction.DESCENDING);
        firstPost.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot orderTime, @javax.annotation.Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                assert orderTime != null;
                if (orderTime.isEmpty()) {

                    linearNoPost.setVisibility(View.VISIBLE);
                    textFakultas.setText(fakultas_users);
//                    Toast.makeText(getActivity(), "titik", Toast.LENGTH_SHORT).show();

                } else {

                    lastVisible = orderTime.getDocuments().get(orderTime.size() - 1);

                    for (DocumentChange doc : orderTime.getDocumentChanges()) {

                        switch (doc.getType()) {

                            case ADDED:

                                String fakultas = doc.getDocument().getString("fakultas");

                                if (fakultas != null) {


                                    if (fakultas.equals(fakultas_users)) {

                                        Log.d(TAG, "cubo: " + doc.getDocument().getData());
                                        String postId = doc.getDocument().getId();

                                        Status_PostList post = doc.getDocument().toObject(Status_PostList.class).withId(postId);

                                        if (isFirstPageFirstLoad) {
                                            postLists.add(post);
                                        } else {
                                            postLists.add(0, post);
                                        }

                                    }

                                } else {
                                    linearNoPost.setVisibility(View.GONE);
                                }

                                break;
                        }

                    }

                    adapterStatus.notifyDataSetChanged();
                }

            }
        });
    }

    private void loadMorePost() {

        Query next = firebaseFirestore.collection("posting")
                .orderBy("postTime", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(3);

        next.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot orderTime, @javax.annotation.Nullable FirebaseFirestoreException e) {

                assert orderTime != null;
                if (!orderTime.isEmpty()) {

                    lastVisible = orderTime.getDocuments().get(orderTime.size() - 1);

                    for (DocumentChange doc : orderTime.getDocumentChanges()) {

                        switch (doc.getType()) {

                            case ADDED:

                                String fakultas = doc.getDocument().getString("fakultas");

                                if (fakultas != null) {

                                    if (fakultas.equals(fakultas_users)) {

                                        Log.d(TAG, "cubo: " + doc.getDocument().getData());
                                        String postId = doc.getDocument().getId();

                                        Status_PostList post = doc.getDocument().toObject(Status_PostList.class).withId(postId);

                                        postLists.add(post);

                                        adapterStatus.notifyDataSetChanged();
                                    }

                                }

                                break;
                            case MODIFIED:
                                Log.d(TAG, "Modified city: " + doc.getDocument().getData());
                                break;
                            case REMOVED:
                                Log.d(TAG, "Removed city: " + doc.getDocument().getData());
                                break;

                        }

                    }

                }

            }
        });

    }


}
