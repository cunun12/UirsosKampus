package com.uirsos.www.uirsoskampus.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.uirsos.www.uirsoskampus.Profile.ProfileActivity;
import com.uirsos.www.uirsoskampus.R;
import com.uirsos.www.uirsoskampus.StatusInfo.MainActivity;
import com.uirsos.www.uirsoskampus.Verifikasi.Verifikasi;

/**
 * Created by cunun12 on 03/05/2018.
 */

public class BottomNavigationHelper {

    private static final String TAG = "BottomNavigationViewHel";

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx) {
        Log.d(TAG, "setupBottomNavigationView: Setting Up BottomNavigationView");
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(true);
    }

    public static void enableNavigation(final Context context, BottomNavigationViewEx view) {
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.home:

                        Intent intentHome = new Intent(context, MainActivity.class); //Activity num 0
                        intentHome.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intentHome);

                        break;

                    case R.id.Profile:

                        Intent intentProfile = new Intent(context, ProfileActivity.class); //Activity num 1
                        intentProfile.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intentProfile);

                        break;
                    case R.id.verifikasiUser:
                        Intent intentVerifikasi = new Intent(context, Verifikasi.class); //Activity num 2
                        intentVerifikasi.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intentVerifikasi);

                        break;
                }

                return false;
            }
        });
    }

}
