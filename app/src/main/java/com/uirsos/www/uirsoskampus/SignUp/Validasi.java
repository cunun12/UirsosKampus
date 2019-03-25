package com.uirsos.www.uirsoskampus.SignUp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.uirsos.www.uirsoskampus.R;

import java.util.ArrayList;

public class Validasi extends AppCompatActivity implements View.OnClickListener {

    private Spinner tahunAngkatan, fakultas, prodi;
    private TextView textViewTA, angkafakultas, angkaprodi;
    private TextView huruffakultas, hurufprodi, nmLengkap;
    private EditText digit;
    private ImageView image_ktm;

    ArrayList<String> listTahun = new ArrayList<String>();
    ArrayAdapter<String> itemTahun;
    String thn, nama;

    private Uri mainImageURI = null;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validasi);

        /*Bagian Upload KTM*/
        Button kamera = findViewById(R.id.btnCamera);
        image_ktm = findViewById(R.id.imageKTM);
        kamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .start(Validasi.this);

            }
        });

        /*Button Regist*/
        Button validasi = findViewById(R.id.btnValidasi);

        /*var spinner*/
        tahunAngkatan = findViewById(R.id.spinner);
        fakultas = findViewById(R.id.spinnerFakultas);
        prodi = findViewById(R.id.spinnerProdi);

        /*nilai yang akan diambil*/
        textViewTA = findViewById(R.id.textTahunAngkatan);
        angkafakultas = findViewById(R.id.angkaFakultas);
        huruffakultas = findViewById(R.id.hurufFakultas);
        angkaprodi = findViewById(R.id.angkaProdi);
        hurufprodi = findViewById(R.id.hurufProdi);
        digit = findViewById(R.id.npmDigit);
        nmLengkap = findViewById(R.id.namaLengkap);

        /*Ambil Data nama dari Regist Pertema*/
        Intent data = getIntent();
        nama = data.getStringExtra("namaLengkap");
        nmLengkap.setText(nama);

        /*Bagian Spinner*/
        setSpinnerTA();
        setSpinnerFakultas();
        validasi.setOnClickListener(Validasi.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                image_ktm.setImageURI(mainImageURI);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Toast.makeText(this, "Why?" + error, Toast.LENGTH_SHORT).show();
            }
        }
    }


    /*Spinner untuk Fakultas*/
    private void setSpinnerFakultas() {

        final ArrayAdapter<CharSequence> adapterFakultas = ArrayAdapter.createFromResource(this, R.array.fakultas, R.layout.support_simple_spinner_dropdown_item);
        adapterFakultas.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        fakultas.setAdapter(adapterFakultas);

        fakultas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String textFakultas = String.valueOf(adapterFakultas.getItem(position));
                String angka = textFakultas.substring(0, 1);
                String huruf = textFakultas.substring(2);

                /*masukan Nilai ke Textview*/
                angkafakultas.setText(angka);
                huruffakultas.setText(huruf);

                if (angka.contentEquals("1")) {
                    spinnerHukum();
                } else if (angka.contentEquals("2")) {
                    spinnerAgama();
                } else if (angka.contentEquals("3")) {
                    spinnerTeknik();
                } else if (angka.contentEquals("4")) {
                    spinnerPertanian();
                } else if (angka.contentEquals("5")) {
                    spinnerEkonomi();
                } else if (angka.contentEquals("6")) {
                    spinnerFkip();
                } else if (angka.contentEquals("7")) {
                    spinnerPolitik();
                } else if (angka.contentEquals("8")) {
                    spinnerPsikologi();
                } else if (angka.contentEquals("9")) {
                    spinnerKomunikasi();
                } else {
                    prodi.setAdapter(null);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /*Spiner Hukum*/
    public void spinnerHukum() {
        final ArrayAdapter<CharSequence> adapterHukum = ArrayAdapter.createFromResource(Validasi.this, R.array.hukum,
                R.layout.support_simple_spinner_dropdown_item);
        adapterHukum.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        adapterHukum.notifyDataSetChanged();
        prodi.setAdapter(adapterHukum);

        prodi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String textHukum = String.valueOf(adapterHukum.getItem(position));
                String angkahukum = textHukum.substring(0, 1);
                String hurufhukum = textHukum.substring(2);

                /*value hukum*/
                angkaprodi.setText(angkahukum);
                hurufprodi.setText(hurufhukum);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /*Spiner Fakultas Agama Islam*/
    private void spinnerAgama() {

        final ArrayAdapter<CharSequence> adapterAgama = ArrayAdapter.createFromResource(Validasi.this, R.array.agamaislam,
                R.layout.support_simple_spinner_dropdown_item);
        adapterAgama.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        adapterAgama.notifyDataSetChanged();
        prodi.setAdapter(adapterAgama);

        prodi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String textAgama = String.valueOf(adapterAgama.getItem(position));
                String angkaagama = textAgama.substring(0, 1);
                String hurufagama = textAgama.substring(2);

                /*value hukum*/
                angkaprodi.setText(angkaagama);
                hurufprodi.setText(hurufagama);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /*Spiner Fakultas Teknik*/
    private void spinnerTeknik() {

        final ArrayAdapter<CharSequence> adapterTeknik = ArrayAdapter.createFromResource(Validasi.this, R.array.teknik,
                R.layout.support_simple_spinner_dropdown_item);
        adapterTeknik.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        adapterTeknik.notifyDataSetChanged();
        prodi.setAdapter(adapterTeknik);

        prodi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String textTeknik = String.valueOf(adapterTeknik.getItem(position));
                String angkateknik = textTeknik.substring(0, 1);
                String hurufteknik = textTeknik.substring(2);

                /*value hukum*/
                angkaprodi.setText(angkateknik);
                hurufprodi.setText(hurufteknik);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /*Spiner FakultasPertanian*/
    private void spinnerPertanian() {

        final ArrayAdapter<CharSequence> adapterPertanian = ArrayAdapter.createFromResource(Validasi.this, R.array.pertanian,
                R.layout.support_simple_spinner_dropdown_item);
        adapterPertanian.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        adapterPertanian.notifyDataSetChanged();
        prodi.setAdapter(adapterPertanian);

        prodi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String textPertanian = String.valueOf(adapterPertanian.getItem(position));
                String angkapertanian = textPertanian.substring(0, 1);
                String hurufpertanian = textPertanian.substring(2);

                /*value hukum*/
                angkaprodi.setText(angkapertanian);
                hurufprodi.setText(hurufpertanian);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /*Spiner FakultasEkonomi*/
    private void spinnerEkonomi() {

        final ArrayAdapter<CharSequence> adapterEkonomi = ArrayAdapter.createFromResource(Validasi.this, R.array.ekonomi,
                R.layout.support_simple_spinner_dropdown_item);
        adapterEkonomi.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        adapterEkonomi.notifyDataSetChanged();
        prodi.setAdapter(adapterEkonomi);

        prodi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String textEkonomi = String.valueOf(adapterEkonomi.getItem(position));
                String angkaekonomi = textEkonomi.substring(0, 1);
                String hurufekonomi = textEkonomi.substring(2);

                /*value hukum*/
                angkaprodi.setText(angkaekonomi);
                hurufprodi.setText(hurufekonomi);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /*Spinner Fkip*/
    private void spinnerFkip() {

        final ArrayAdapter<CharSequence> adapterFkip = ArrayAdapter.createFromResource(Validasi.this, R.array.fkip,
                R.layout.support_simple_spinner_dropdown_item);
        adapterFkip.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        adapterFkip.notifyDataSetChanged();
        prodi.setAdapter(adapterFkip);

        prodi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String textFkip = String.valueOf(adapterFkip.getItem(position));
                String angkafkip = textFkip.substring(0, 1);
                String huruffkip = textFkip.substring(2);

                /*value hukum*/
                angkaprodi.setText(angkafkip);
                hurufprodi.setText(huruffkip);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /*Spiner Fakultas Ilmu Sosial dan Politik*/
    private void spinnerPolitik() {

        final ArrayAdapter<CharSequence> adapterIsp = ArrayAdapter.createFromResource(Validasi.this, R.array.isp,
                R.layout.support_simple_spinner_dropdown_item);
        adapterIsp.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        adapterIsp.notifyDataSetChanged();
        prodi.setAdapter(adapterIsp);

        prodi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String textPolitik = String.valueOf(adapterIsp.getItem(position));
                String angkapolitik = textPolitik.substring(0, 1);
                String hurufpolitik = textPolitik.substring(2);

                /*value hukum*/
                angkaprodi.setText(angkapolitik);
                hurufprodi.setText(hurufpolitik);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /*Spiner Fakultas Psikologi*/
    private void spinnerPsikologi() {

        final ArrayAdapter<CharSequence> adapterPsikologi = ArrayAdapter.createFromResource(Validasi.this, R.array.psikologi,
                R.layout.support_simple_spinner_dropdown_item);
        adapterPsikologi.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        adapterPsikologi.notifyDataSetChanged();
        prodi.setAdapter(adapterPsikologi);

        prodi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String textPsikologi = String.valueOf(adapterPsikologi.getItem(position));
                String angkapsikologi = textPsikologi.substring(0, 1);
                String hurufpsikologi = textPsikologi.substring(2);

                /*value hukum*/
                angkaprodi.setText(angkapsikologi);
                hurufprodi.setText(angkapsikologi);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /*Spiner Fakultas Komunikasi*/
    private void spinnerKomunikasi() {

        final ArrayAdapter<CharSequence> adapterKomunikasi = ArrayAdapter.createFromResource(Validasi.this, R.array.komunikasi,
                R.layout.support_simple_spinner_dropdown_item);
        adapterKomunikasi.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        adapterKomunikasi.notifyDataSetChanged();
        prodi.setAdapter(adapterKomunikasi);

        prodi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String textKomunikasi = String.valueOf(adapterKomunikasi.getItem(position));
                String angkakomunikasi = textKomunikasi.substring(0, 1);
                String hurufkomunikasi = textKomunikasi.substring(2);

                /*value hukum*/
                angkaprodi.setText(angkakomunikasi);
                hurufprodi.setText(hurufkomunikasi);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /*Spinner Tahun Angkatan*/
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setSpinnerTA() {
        Calendar calendar = Calendar.getInstance();
        int thnNow = calendar.get(Calendar.YEAR);
        for (int i = 2010; i <= thnNow; i++) {
            thn = String.valueOf(i);
            listTahun.add(thn);
            itemTahun = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, listTahun);
        }
        tahunAngkatan.setAdapter(itemTahun);
        tahunAngkatan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                String tahun = itemTahun.getItem(position);
                textViewTA.setText(tahun);
                textViewTA.setEnabled(true);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int JP = 1;
        String digit1 = digit.getText().toString();
        String namaLengkap = nmLengkap.getText().toString().trim().toUpperCase();

        /*Nilai Tahun Angkatan*/
        String tahun = textViewTA.getText().toString();
        String pisahTA = tahun.substring(2, 4);

        /*value Fakultas*/
        String angkafak = angkafakultas.getText().toString();
        String huruffak = huruffakultas.getText().toString();

        /*Value Prodi*/
        String angkapro = angkaprodi.getText().toString();
        String hurufPro = hurufprodi.getText().toString();
        if (digit1.equals("") || digit1.length() != 4) {
            Toast.makeText(this, "periksa angka npm anda", Toast.LENGTH_SHORT).show();
            digit.setError("Periksa angka NPM Anda!");
        } else {
            if (mainImageURI != null) {
                validasiAkun();
//                String NPM = pisahTA + angkafak + angkapro + JP + digit1;
//                Toast.makeText(this, "NPM " + NPM + "Nama " + namaLengkap + " Fakultas = " + huruffak + "Prodi = " + mainImageURI, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Silakan masukan gambar KTM Anda!", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void validasiAkun() {
    }
}