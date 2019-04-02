package com.uirsos.www.uirsoskampus.StatusInfo;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uirsos.www.uirsoskampus.Adapter.AdapterBerita;
import com.uirsos.www.uirsoskampus.Adapter.AdapterDekan;
import com.uirsos.www.uirsoskampus.HandleRequest.APIServer;
import com.uirsos.www.uirsoskampus.HandleRequest.RequestHandler;
import com.uirsos.www.uirsoskampus.POJO.DataItemBerita;
import com.uirsos.www.uirsoskampus.POJO.PolaItemDekan;
import com.uirsos.www.uirsoskampus.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {

    private RecyclerView listBerita, listBeritaDekan;
    private GridLayoutManager gridBerita;
    private LinearLayoutManager linear;
    private List<DataItemBerita> itemsberita;
    private List<PolaItemDekan> PidBerita;
    private AdapterBerita adapterBerita;
    private AdapterDekan adapterDekan;
    private SwipeRefreshLayout refreshBerita;
    private TextView infoKoneksi;


    /**/
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String idUser = mAuth.getUid();
    String fakultas;

    public InfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        refreshBerita = view.findViewById(R.id.refresh);
        infoKoneksi = view.findViewById(R.id.koneksi);

        /*Bagian Berita Dekan*/
        listBeritaDekan = view.findViewById(R.id.rvDekan);
        linear = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        listBeritaDekan.setLayoutManager(linear);
        PidBerita = new ArrayList<>();
        adapterDekan = new AdapterDekan(getContext(), PidBerita);
        listBeritaDekan.setAdapter(adapterDekan);


        /*Bagian Berita Kampus*/
        listBerita = view.findViewById(R.id.rvBerita);
        itemsberita = new ArrayList<>();
        adapterBerita = new AdapterBerita(itemsberita);
        gridBerita = new GridLayoutManager(getActivity(), 2);
        listBerita.setLayoutManager(gridBerita);
        listBerita.setAdapter(adapterBerita);

//        refreshBerita.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        itemsberita.clear();
//                        refreshBerita.setRefreshing(false);
////                        adapterBerita.notifyDataSetChanged();
//                        loadBerita();
//                    }
//                }, 5000);
//            }
//        });

        /*memanggil berita dekan sesuai fakultas*/
        firestore.collection("User").document(idUser).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null && documentSnapshot.exists()) {

                    fakultas = documentSnapshot.getString("Fakultas");
                    Log.d(TAG, "onSuccess: Fakultas " + fakultas);

                }
            }
        });
        loadDekan();

        /*memanggil berita semua fakultas*/
        loadBerita();
    }

    private void loadDekan() {

        StringRequest requestDekan = new StringRequest(Request.Method.GET, APIServer.Berita,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d(TAG, "onResponse: " + response);

                        try {

                            JSONArray array = new JSONArray(response);

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject data = array.getJSONObject(i);

                                String webFakultas = data.getString("fakultas");
                                String webLevel = data.getString("level");

                                Log.d(TAG, "onResponse: fakultas" + webFakultas + "level = "+webLevel);

                                if (webLevel.equals("dekan")){
                                    if (webFakultas.equals(fakultas)){

                                        PolaItemDekan beritaDekan = new PolaItemDekan();
                                        beritaDekan.setJudul_berita(data.getString("judul"));
                                        beritaDekan.setIsi_berita(data.getString("isi_berita"));
                                        beritaDekan.setWaktu(data.getString("tanggal"));
                                        beritaDekan.setGambar(data.getString("gambar"));
                                        PidBerita.add(beritaDekan);

                                    } else{
                                        Log.d(TAG, "onResponse: tidak sama");
                                    }
                                }

                            }

                            adapterDekan.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.getMessage();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestDekan.setRetryPolicy(new DefaultRetryPolicy(
                1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestHandler.getInstance(getActivity()).addToRequestQueue(requestDekan);
    }

    private void loadBerita() {

        StringRequest request = new StringRequest(Request.Method.GET, APIServer.Berita,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        infoKoneksi.setVisibility(View.GONE);
                        Log.d(TAG, "onResponse: " + response);
                        try {
                            JSONArray array = new JSONArray(response);

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject data = array.getJSONObject(i);
                                Log.d(TAG, "onResponse: " + data.toString());

                                DataItemBerita berita = new DataItemBerita();
                                berita.setJudul(data.getString("judul"));
                                berita.setDeskripsi(data.getString("isi_berita"));
                                berita.setImage(data.getString("gambar"));
                                berita.setWaktu(data.getString("tanggal"));
                                berita.setInfo(data.getString("info"));
                                itemsberita.add(berita);
                            }
                            adapterBerita.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.getMessage();
                            e.printStackTrace();
                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ini kesalahannya", error.toString());

                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            // Is thrown if there's no network connection or server is down
                            infoKoneksi.setVisibility(View.VISIBLE);
                            infoKoneksi.setText("Tidak terhubung dengan server");
                            // We return to the last fragment
//                            if (getFragmentManager().getBackStackEntryCount() != 0) {
//                                getFragmentManager().popBackStack();
//                            }

                        } else {
                            // Is thrown if there's no network connection or server is down
                            infoKoneksi.setVisibility(View.VISIBLE);
                            infoKoneksi.setText("Tidak terhubung dengan server");
                            // We return to the last fragment
                            if (getFragmentManager().getBackStackEntryCount() != 0) {
                                getFragmentManager().popBackStack();
                            }
                        }
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);

    }

}
