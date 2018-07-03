package com.uirsos.www.uirsoskampus.StatusInfo;

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
import android.widget.Toast;

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
import com.google.firebase.firestore.QuerySnapshot;
import com.uirsos.www.uirsoskampus.Adapter.AdapterStatus;
import com.uirsos.www.uirsoskampus.POJO.Status_PostList;
import com.uirsos.www.uirsoskampus.POJO.User;
import com.uirsos.www.uirsoskampus.R;
import com.uirsos.www.uirsoskampus.SignUp.RegisterActivity;

import java.util.ArrayList;
import java.util.List;

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


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

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

            dataView();

        }

    }

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

        Query firstQuery = firebaseFirestore.collection("posting")
                .orderBy("postTime", Query.Direction.DESCENDING).limit(3);
        firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable final QuerySnapshot documentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                assert documentSnapshots != null;
                if (documentSnapshots.isEmpty()) {
                    Toast.makeText(getActivity(), "Data masih kosong", Toast.LENGTH_SHORT).show();
                } else {
                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String postId = doc.getDocument().getId();

                            final Status_PostList dataPost = doc.getDocument().toObject(Status_PostList.class).withId(postId);

                            if (isFirstPageFirstLoad) {
                                postLists.add(dataPost);
                            } else {
                                postLists.add(0, dataPost);
                            }
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

        next.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable final QuerySnapshot documentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                assert documentSnapshots != null;
                if (documentSnapshots.isEmpty()) {
                    Toast.makeText(getActivity(), "Data masih kosong", Toast.LENGTH_SHORT).show();
                } else {

                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String postId = doc.getDocument().getId();

                            final Status_PostList dataPost = doc.getDocument().toObject(Status_PostList.class).withId(postId);

                            postLists.add(dataPost);

                            adapterStatus.notifyDataSetChanged();
                        }

                    }
                }
            }
        });

    }


}
